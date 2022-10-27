package concurrency.ThreadPoolExecutorTest.destory;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by JasonFitch on 4/9/2019.
 */
public class DestroyableThreadFactory implements ThreadFactory {

    private static AtomicInteger counter = new AtomicInteger();

    @Override
    public Thread newThread(Runnable r) {
        String name = "DestroyThread-" + counter.incrementAndGet();
        return new DestroyThread(name, r);
    }
}
