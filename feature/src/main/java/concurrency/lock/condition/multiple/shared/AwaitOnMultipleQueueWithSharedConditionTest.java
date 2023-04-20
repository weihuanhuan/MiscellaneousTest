package concurrency.lock.condition.multiple.shared;

import concurrency.lock.condition.multiple.task.InvokeTask;
import concurrency.lock.condition.multiple.task.PoolTask;
import concurrency.lock.condition.queue.LinkedBlockingDeque;
import concurrency.lock.condition.util.ThreadPoolUtility;

import java.util.concurrent.TimeUnit;

public class AwaitOnMultipleQueueWithSharedConditionTest {

    private static final NoticableSharedCondition SHARED_CONDITION = new NoticableSharedCondition(false);
    private static final LinkedBlockingDeque<Runnable> one = new NoticableSharedLinkedBlockingDeque<>(false, SHARED_CONDITION);
    private static final LinkedBlockingDeque<Runnable> two = new NoticableSharedLinkedBlockingDeque<>(false, SHARED_CONDITION);

    public static void main(String[] args) throws InterruptedException {
        ThreadPoolUtility.statisticTimeHook();

        // trigger poll signal
        Runnable poolTask = new PoolTask(one, two);
        Thread poolTaskThread = new Thread(poolTask, "poolTask");
        poolTaskThread.start();

        // trigger invoke task
        Runnable invokeTask = new InvokeTask();
        Thread invokeTaskThread = new RunTaskThread(invokeTask, "invokeTask");
        invokeTaskThread.start();

        // keep running
        ThreadPoolUtility.sleep(15, TimeUnit.SECONDS);

        System.out.println("shutdown");
        // interrupt running thread
        poolTaskThread.interrupt();
        invokeTaskThread.interrupt();

        //我们可以直接中断该线程，此时他也能从 await 中返回，所以对于 SHARED_CONDITION 可以不实现其 interruptTakeWaiters 方法
//        SHARED_CONDITION.interruptTakeWaiters();
    }

    private static class RunTaskThread extends Thread {

        private final Runnable runnable;

        public RunTaskThread(Runnable runnable, String name) {
            super(name);
            this.runnable = runnable;
        }

        @Override
        public void run() {
            String name = Thread.currentThread().getName();
            String startFormat = String.format("Task: name=[%s], thread start.", name);
            System.out.println(startFormat);

            do {
                while (SHARED_CONDITION.hasTakeWaiters()) {
                    runnable.run();
                }

                try {
                    //这里存在问题，所以这个方案实际上是不能使用的会导致程序卡死在这里，问题如下，
                    //当执行线程执行完 runnable.run 后，再调用 SHARED_CONDITION.await() 前，
                    // 假如此时正好业务线程存在新的 poll 操作，则其先触发了 sharedCondition.signalAll(); 然后就在 queue 的 notEmpty.await(); 上等待了
                    // 但是由于此时的本场景中，执行线程只是持有了 shared 的锁，而没有持有 queue 的锁，
                    // 因此执行线程可以在业务线程完成 sharedCondition.signalAll 后，并进入到了 SHARED_CONDITION.await(); 之前时，就能拿到 shared 锁，并进入 SHARED_CONDITION.await() 中。
                    // 而这个之后，业务线程也随之进入了 queue 的 notEmpty.await(); 中，由于系统只有一次操作触发 poll ，所以随后业务线程，和本线程都挂死了，导致系统假锁住了，这是错误的地方了。
                    // 要想解决这个错误，我们考虑如下
                    // 1. 确保不丢失 sharedCondition.signalAll() 的信号，这就可使得我们一定能够正确的从 SHARED_CONDITION.await 中醒来
                    //  但是要想信号不丢失，就要保证在 poll 在调用 sharedCondition.signalAll 时，执行线程必须处于 SHARED_CONDITION.await() 中
                    //  这也就意味着我们的整个执行操作必须在放到 shared 锁中才行，然后当不能继续执行时，执行线程先将自己放入到 SHARED_CONDITION.await(); 中，并释放锁，
                    //  此时业务线程才能拿到 shared 锁来调用 sharedCondition.signalAll  ，虽然这样子业务线程能保证其在调用通知时，一定能够通知到执行线程，
                    //  但是由于我们想要多个执行线程并行的执行，但这种方式却由于 lock 住了整个操作，所以无法并行了，故该方案是不合适的，
                    // 2. 确保没有等待在 queue 的 notEmpty.await() 的线程在进入 SHARED_CONDITION.await
                    //  要想保证执行线程进入等待时是没有 poll 的业务线程的话，我们需要在 SHARED_CONDITION.await(); 中，判断 queue 是否 deque.hasTakeWaiters() ，
                    //  这就意味这我们只要在 SHARED_CONDITION.await(); 的内部调用 deque.hasTakeWaiters() 就行了，而 hasTakeWaiters 内部是会持有 queue 的锁的，所以在调用期间数量是不会变化的
                    //  但是我们这里存在多个队列，由于每个 queue 的锁是不同的，这迫使我们要获取全部的 queue lock 才行。此时我们就可以同时持有这些锁来所有的队列是否真正的 empty 了。
                    //  但是由于 condition.await 只能释放所关联的锁的 lock ，因此即使我们正确的判断队列状态，进入 SHARED_CONDITION.await(); 后，
                    //  执行线程也只会释放 SHARED_CONDITION 对应的 shared 锁，此时该执行线程还是会持有判断队列状态时所用的所有 queue 的锁，
                    //  这导致其余的执行线程，或者业务线程都无法和 queue 交互了，又导致系统进入了假死状态，故该方案也是不行的。
                    //  另外这里还存在一个锁使用时的顺序问题，业务线程是先 queue lock ，然后 shared lock ，执行线程是先 shared lock ，然后 queue 锁，这种不按相同顺序加锁还可能会导致死锁
                    // 所以我们要如何解决这个问题呢？
                    //  其实问题的关键就是我们在处理队列状态时同使用了 queue lock 和 shared lock 两个锁，但是 shared 并不是队列的锁，而 await 机制不能处理两把锁。
                    //  因此我们应该向 queue lock 中增加条件来完成通知，而不是使用独立的锁。
                    //  再加上我们不想将整个操作放入 queue lock 中，所以在进入 【增加条件.await();】前，应该检测下 deque.hasTakeWaiters() 看我们是否应该进入 await 。
                    //  由于业务线程在 poll 时，如果不存在元素，则其必须在拿到 queue lock 的情况下，先【signal 增加条件】然后将自己放入 notEmpty 条件的等待队列中，最后执行 notEmpty.await(); 来释放  queue lock
                    //  而虽然业务线程【signal 增加条件】时我们就唤醒了，但在 【增加条件.await();】 时，要获取到业务线程刚刚释放的 queue lock 才能执行
                    //  所以这里执行线程就必须等到业务线程的  notEmpty.await(); 后执行，并使用 queue lock 的 notEmpty 条件队列来判断是否存在等待线程，并以此决定是否进行 await
                    //  这里由于 业务线程 poll ， 【signal 增加条件】  ，notEmpty await ，；执行线程 【增加条件.await();】，hasTakeWaiters 都使用同一把 queue lock ，
                    //  故我们可以既不会丢失信号，也不会错误看见队列状态，因此这个方案是可以真正的做到正确的使用 【增加条件】来触发执行线程的执行的
                    //  至于对于多个队列的问题，由于不同的队列具有不同的锁，因此我们就只能使用不同的执行线程来专注的处理不同的队列了。
                    SHARED_CONDITION.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            } while (!Thread.currentThread().isInterrupted());

            String finishFormat = String.format("Task: name=[%s], thread finish.", name);
            System.out.println(finishFormat);
        }
    }

}
