package concurrency;

import java.nio.file.Paths;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by JasonFitch on 10/8/2018.
 */
public class FutureTest {

    public static void main(String[] args) {

        ExecutorService executorService = Executors.newCachedThreadPool();

        Callable<Integer> task = new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                TimeUnit.SECONDS.sleep(2);
                return Integer.valueOf("0");
            }
        };

        Future<Integer> future = executorService.submit(task);

        try {
            Integer result = future.get(3, TimeUnit.SECONDS);
//            这个方法将阻塞一定的时间，直到时间耗尽时抛出 TimeoutException，或者时间未到时的其他执行异常。
//            Integer result = future.get();
//            这个方法将会阻塞直到任务完成,或者是抛出任务执行时的异常。
            System.out.println("wait for result...");
            System.out.println(result);

            long start = System.currentTimeMillis();
            System.out.println("wait for shutdown...");
            executorService.shutdown();
//              显示的告诉线程池中的线程值任务结束后尽快退出，否则他们会等待一定的时间后才会结束自己
//              那样会照成，虽然任务都已经执行完毕了，main线程本应该退出，但是却不得不等待池中的线程结束后才能退出。
            while (!executorService.isShutdown())
                ;
            long end = System.currentTimeMillis();
            System.out.println("shutdown waiting time:"+TimeUnit.MILLISECONDS.toSeconds(end-start));

        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

    }

}
