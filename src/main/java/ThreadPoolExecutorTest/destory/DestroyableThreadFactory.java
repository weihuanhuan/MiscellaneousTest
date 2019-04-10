package ThreadPoolExecutorTest.destory;

import java.util.concurrent.ThreadFactory;

/**
 * Created by JasonFitch on 4/9/2019.
 */
public class DestroyableThreadFactory implements ThreadFactory {
    @Override
    public Thread newThread(Runnable r) {
        return new DestroyThread(r);
    }
}
