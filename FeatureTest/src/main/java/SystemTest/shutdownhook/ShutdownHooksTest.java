package SystemTest.shutdownhook;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by JasonFitch on 3/18/2019.
 */
public class ShutdownHooksTest {

    private static AtomicInteger workCount = new AtomicInteger(0);


    public static void main(String[] args) {

        System.out.println("starting");
        //直接关闭 DOS 窗口相当于是 kill -9 pid，不会触发shutdown hook的执行  ---> kill     ,强制关闭JVM
        //而使用 ctrl+C 的形式，则会触发 hook 的执行，相当于 kill -15 pid.    ---> terminal ,正常关闭JVM
        Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownTask()));
        System.out.println("started");

        while (true) {
            try {
                System.out.println("I'm handling work: " + workCount.incrementAndGet());
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }

    static class ShutdownTask implements Runnable {
        @Override
        public void run() {
            System.out.println("executing shutdown hook");
        }
    }
}
