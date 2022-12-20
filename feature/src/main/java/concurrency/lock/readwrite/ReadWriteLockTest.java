package concurrency.lock.readwrite;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ReadWriteLockTest {

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        ReadWriteLockChecker readWriteLockChecker = new ReadWriteLockChecker();

        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
        threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());

        int count = 5000;
        while (count-- > 0) {
            Runnable worker = new Worker(readWriteLockChecker);
            executorService.submit(worker);
        }

        try {
            executorService.shutdown();
            boolean awaitTermination = executorService.awaitTermination(1000 * 60 * 60, TimeUnit.MILLISECONDS);
            System.out.println("awaitTermination=" + awaitTermination);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        long end = System.currentTimeMillis();
        long duration = end - start;
        //当前存在问题，程序跑着跑着就自行结束了，没有抛出意料之外的异常，这是为什么呢？
        //[ReadWriteLockResult]-[16] ---> duration=80103
        //[ReadWriteLockResult]-[9] ---> duration=45114
        //[ReadWriteLockResult]-[11] ---> duration=55091
        //[ReadWriteLockResult]-[18] ---> duration=90115
        System.out.println("duration=" + duration);
    }

    private static class Worker implements Runnable {

        private final ReadWriteLockChecker readWriteLockChecker;

        public Worker(ReadWriteLockChecker readWriteLockChecker) {
            this.readWriteLockChecker = readWriteLockChecker;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            boolean check = readWriteLockChecker.check();
            long end = System.currentTimeMillis();

            String name = Thread.currentThread().getName();
            String format = String.format("name=[%s], run, check=[%s], end=[%s], duration=[%s]", name, check, end, end - start);
//            System.out.println(format);
        }
    }

}
