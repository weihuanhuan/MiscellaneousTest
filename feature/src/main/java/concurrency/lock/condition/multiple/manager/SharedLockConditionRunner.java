package concurrency.lock.condition.multiple.manager;

public class SharedLockConditionRunner<E> {

    private final SharedLockConditionManager<E> manager;

    public SharedLockConditionRunner(SharedLockConditionManager<E> manager) {
        this.manager = manager;
    }

    public void sharedRun(Runnable runnable) {
        String name = Thread.currentThread().getName();

        do {
            try {
                while (!manager.anyDequeHasWork()) {
                    String format = String.format("Runner: name=[%s], signalCheckWorkAndAwaitAllowRun.", name);
                    System.out.println(format);
                    manager.signalCheckWorkAndAwaitAllowRun();
                }

                //这里先前使用 AtomicBoolean hasWork 状态变量的方式是不对的，当该值为 true 后，如果此时所有的 checker 均 await checkWork ，
                // 那么就没有人能 update hasWork 为 false 了，就导致这里的 runnable.run(); 无限执行了，即使当前队列的真实 hasTakeWaiters 已经为 empty 了
                // 但是由于我们判断的是 AtomicBoolean hasWork 状态变量 ，所以无法感到到他。因此判断是否 hasWork 还是应该直接判断 hasTakeWaiters 的
                // 另外由于我们直接修改为判断 hasTakeWaiters 后， runnable.run() 知道了正确的 anyDequeHasWork 状态了，也就不用先前的 shutdown 了。
                while (manager.anyDequeHasWork()) {
                    runnable.run();
                }
            } catch (InterruptedException e) {
                // restore the interrupt message from sharedAwait to make the sharedRun finish
                Thread.currentThread().interrupt();
                String format = String.format("Runner: name=[%s], restore interrupt mark with InterruptedException=[%s].", name, e.getMessage());
                System.out.println(format);
            }
        } while (!Thread.currentThread().isInterrupted());

        String format = String.format("Runner: name=[%s], finish.", name);
        System.out.println(format);
    }

}
