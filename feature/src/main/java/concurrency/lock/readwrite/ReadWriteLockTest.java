package concurrency.lock.readwrite;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ReadWriteLockTest {

    //并发指标,由于某些 wait 可能发生在 call 已经开始了一段时间后，所以单次等待时间是 <= 150
    // wait 总调用轮数 = 500 / 10 = 50
    // wait 总等待时间 = 50 * 150 <= 7500
    // call 总调用次数 = 7500 / 500 <= 15
    private static final int WORKER_SUBMIT_COUNT = 500;
    private static final int WORKER_THREAD_COUNT = 10;

    //长时间调用，模拟系统线程操作缓慢
    public static final int CALL_TIME = 500;
    //短时间等待，模拟用户线程等待查询
    public static final int WAIT_TIMEOUT = 150;
    //冗余等待时间，模拟系统内部的消耗
    public static final int WAIT_TIMEOUT_TOLERANT = (int) (150 * 0.1f);

    private static final AtomicLong appropriateWaitCounter = new AtomicLong();

    public static void main(String[] args) {
        long start = System.currentTimeMillis();

        ReadWriteLockChecker readWriteLockChecker = new ReadWriteLockChecker();

        ExecutorService executorService = Executors.newFixedThreadPool(WORKER_THREAD_COUNT);

        if (executorService instanceof ThreadPoolExecutor) {
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executorService;
            threadPoolExecutor.prestartAllCoreThreads();
            threadPoolExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        }

        int index = WORKER_SUBMIT_COUNT;
        while (index-- > 0) {
            Runnable worker = new Worker(readWriteLockChecker, appropriateWaitCounter);
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
        // 注意，这里的 result 线程数量，和其对应的 duration 数值，和当前运行数据比较是没有意义的，
        // 此组数据运行的 time 参数和 lock 实现已经变更了，留着他只是一个记录。
        //[ReadWriteLockResult]-[16] ---> duration=80103
        //[ReadWriteLockResult]-[9] ---> duration=45114
        //[ReadWriteLockResult]-[11] ---> duration=55091
        //[ReadWriteLockResult]-[18] ---> duration=90115
        System.out.println("duration=" + duration);

        //后面我们对比 completedTaskCount 和 submitTaskCount 发现他们时相同的，所以这里的自行结束，也是在所有任务执行完之后结束的
        //如此巨大的执行线程数量区别，应该是每个 worker 自身执行时的等待时间开销并不是确定的而导致
        //1. 大部分 worker 需要等待的时间为 duration=[1501] 左右，
        // 这是我们配置的 waitTimeout 值，当 call 刚刚执行时，一般最多就是等这么久
        //2. 另外一部分可能小于 waitTimeout 值，分布很随机，任何值都有。
        // 发生在当 call 的执行已经过了一段时间时，他就至多等待 【waitTimeout - call已经执行的时间】。
        //3. 另外一部分可能大于 waitTimeout 值，分布较固定，基本都是 3003 左右，为 waitTimeout 的二倍。
        // 发生在当先前线程所触发的上次 result 的 readLock 正在被占用，而本次线程触发了新的 result 对象构建，
        // 由于对于不同的 result 使用的同一个 readLock 和 writeLock 来控制对 result 的创建和移除，所以他们不能进行
        // 故这里本线程需要等待 【上次 result 的 readLock + 本次 result 的 readLock】，即为 waitTimeout 的二倍。
        //4. 最后还有很少部分大于 waitTimeout 值的为 4504 做，其是 waitTimeout 的三倍了。
        // 这个发生的情况和上面的类似 3 中的情况
        // 这说明了我们的程序存在问题，明明最大只允许等待 waitTimeout 时间，结果却变得不可控了，
        // 依据上面分析 3 的出现场景，我们需要避免在执行本次的 writeLock 时，还要等待其他的 readLock ，导致时间被延长。
        // 本问题已经优化完成，分析代码后，将负责的读写锁，优化为了普通的 lock 。
        long completedTaskCount = ((ThreadPoolExecutor) executorService).getCompletedTaskCount();
        String format = String.format("completedTaskCount=[%s], submitTaskCount=[%s], appropriateWaitCounter=[%s]", completedTaskCount, WORKER_SUBMIT_COUNT, appropriateWaitCounter);
        System.out.println(format);
    }

    private static class Worker implements Runnable {

        private final ReadWriteLockChecker readWriteLockChecker;

        private final AtomicLong counter;

        public Worker(ReadWriteLockChecker readWriteLockChecker, AtomicLong counter) {
            this.readWriteLockChecker = readWriteLockChecker;
            this.counter = counter;
        }

        @Override
        public void run() {
            long start = System.currentTimeMillis();
            boolean check = readWriteLockChecker.check();
            long end = System.currentTimeMillis();

            String name = Thread.currentThread().getName();
            long duration = end - start;

            counter.incrementAndGet();

            //关心 worker 的等待时间，和我们设置的 waitTimeout 的关系
            //由于程序执行本身的消耗，这里额外增加 WAIT_TIMEOUT_TOLERANT 时间
            if (duration > WAIT_TIMEOUT + WAIT_TIMEOUT_TOLERANT) {
                String format = String.format("name=[%s], run, check=[%s], duration=[%s]", name, check, duration);
                System.out.println(format);
            }
        }
    }

}
