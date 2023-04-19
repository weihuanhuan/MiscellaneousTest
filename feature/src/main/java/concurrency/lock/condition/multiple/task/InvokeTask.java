package concurrency.lock.condition.multiple.task;

import concurrency.lock.condition.util.ThreadPoolUtility;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class InvokeTask implements Runnable {

    private final AtomicInteger atomicInteger = new AtomicInteger(0);

    @Override
    public void run() {
        String name = Thread.currentThread().getName();
        int sum = atomicInteger.incrementAndGet();
        String format = String.format("Task: name=[%s], run once, sum=[%s].", name, sum);
        System.out.println(format);

        ThreadPoolUtility.sleep(1, TimeUnit.SECONDS);
    }

}
