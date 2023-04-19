package concurrency.lock.condition.multiple.task;

import concurrency.lock.condition.queue.LinkedBlockingDeque;

import java.util.concurrent.TimeUnit;

public class PoolTask implements Runnable {

    private final LinkedBlockingDeque<Runnable> one;
    private final LinkedBlockingDeque<Runnable> two;

    public PoolTask(LinkedBlockingDeque<Runnable> one, LinkedBlockingDeque<Runnable> two) {
        this.one = one;
        this.two = two;
    }

    @Override
    public void run() {
        try {
            System.out.println("one poll");
            Runnable onePoll = one.poll(1, TimeUnit.SECONDS);
            System.out.println("two poll");
            //时间长一点，来验证 monitor 线程多次异常结束的情况，看能否自动补充结束的线程
            Runnable twoPoll = two.poll(10, TimeUnit.SECONDS);
            System.out.println("over poll");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
