package ThreadPoolExecutorTest.future;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 4/10/2019.
 */
public class FutureTest {


    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        Future<?> future = executorService.submit(new TestTask());
        System.out.println("start");
        try {
            //阻塞直到结果完成或着异常。
            future.get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        System.out.println("end");

    }

    private static class TestTask implements Runnable {

        @Override
        public void run() {
            try {
                TimeUnit.MILLISECONDS.sleep(1000 * 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
