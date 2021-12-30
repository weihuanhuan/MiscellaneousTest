package util.classloader;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class PrintTCCLTask implements Runnable {

    private static final AtomicInteger counter = new AtomicInteger(0);

    private final int index;

    private final AtomicBoolean internalCreated;

    public PrintTCCLTask() {
        this.index = counter.incrementAndGet();
        this.internalCreated = new AtomicBoolean(false);
    }

    public PrintTCCLTask(PrintTCCLTask task) {
        this.index = task.index;
        this.internalCreated = new AtomicBoolean(true);
    }

    @Override
    public void run() {
        ClassLoader thisClassLoader = this.getClass().getClassLoader();
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        System.out.printf("index [%s] internalCreated [%s] thisClassLoader:[%s]%n", index, internalCreated, thisClassLoader);
        System.out.printf("index [%s] internalCreated [%s] contextClassLoader:[%s]%n", index, internalCreated, contextClassLoader);

        if (!internalCreated.get()) {
            Thread thread = new Thread(new PrintTCCLTask(this));
            thread.start();
        }
    }

}
