package ThreadPoolExecutorTest;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by JasonFitch on 3/13/2019.
 */
public class CustomThreadFactory implements ThreadFactory {

    private AtomicInteger createCount = new AtomicInteger(0);

    private String prefix;

    private boolean daemon;

    public CustomThreadFactory(String prefix) {
        this(prefix, false);
    }

    public CustomThreadFactory(String prefix, boolean daemon) {
        this.prefix = prefix;
        this.daemon = daemon;
    }

    @Override
    public Thread newThread(Runnable r) {

        String thradName = prefix + "-" + createCount.addAndGet(1);
        //JF 要运行的任务 Runnable不能丢， Runnable不能丢， Runnable不能丢!
        Thread thread = new Thread(r, thradName);
        thread.setDaemon(daemon);
        return thread;
    }
}
