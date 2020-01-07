package StaticClass.field;

import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 1/7/2020.
 */
public class SuperClazz {

    public static Thread startThreadStatic = startThread("SuperThreadStatic");

    public Thread startThread = startThread("SuperThread");

    public static Thread startThread(String threadName) {
        Runnable runnable = getRunnable(threadName);
        Thread t = new Thread(runnable, threadName);
        t.start();
        return t;
    }

    public static Runnable getRunnable(String threadName) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println(threadName);
            }
        };

        return runnable;
    }


}
