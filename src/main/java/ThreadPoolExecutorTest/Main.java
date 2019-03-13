package ThreadPoolExecutorTest;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by JasonFitch on 3/13/2019.
 */
public class Main {

    private static AtomicLong workCount = new AtomicLong(0);


    public static void main(String[] args) {

        CustomBlockingQueue<Runnable> customBlockingQueue = new CustomBlockingQueue<>(10000);

        ThreadFactory threadFactory = new CustomThreadFactory("work-thread");

        CustomThreadPoolExecutor service = new CustomThreadPoolExecutor(1, 2, 60L, TimeUnit.SECONDS,
                customBlockingQueue, threadFactory);
        service.prestartAllCoreThreads();

        int count = 10;
        while (count > 0) {

//            try {
//                TimeUnit.SECONDS.sleep(1);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }


            Event event = new Event();
            event.setId(String.valueOf((workCount.addAndGet(1) % 10) + 1));

            service.execute(new Runnable() {
                @Override
                public void run() {
                    System.out.println(event.getId() + ":Start");
                    System.out.println(event.getId() + ":End");
                }
            });
        }

        System.out.println("Main End!");
    }
}
