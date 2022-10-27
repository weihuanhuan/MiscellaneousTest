
package concurrency.ThreadPoolExecutorTest.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 本类来源于 jenkins 维护的 trilead-ssh2 工程的 com.trilead.ssh2.util.TimeoutService 类。
 * <p>
 * <!-- https://mvnrepository.com/artifact/org.jenkins-ci/trilead-ssh2 -->
 * <dependency>
 * <groupId>org.jenkins-ci</groupId>
 * <artifactId>trilead-ssh2</artifactId>
 * <version>build-217-jenkins-27</version>
 * </dependency>
 */

/**
 * TimeoutService (beta). Here you can register a timeout.
 * <p>
 * Implemented having large scale programs in mind: if you open many concurrent SSH connections
 * that rely on timeouts, then there will be only one timeout thread. Once all timeouts
 * have expired/are cancelled, the thread will (sooner or later) exit.
 * Only after new timeouts arrive a new thread (singleton) will be instantiated.
 *
 * @author Christian Plattner, plattner@trilead.com
 * @version $Id: TimeoutService.java,v 1.1 2007/10/15 12:49:57 cplattne Exp $
 */
public class TimeoutService {

    private final static ThreadFactory threadFactory = new ThreadFactory() {

        private AtomicInteger count = new AtomicInteger();

        public Thread newThread(Runnable r) {
            int threadNumber = count.incrementAndGet();
            String threadName = "TimeoutService-" + threadNumber;
            Thread thread = new Thread(r, threadName);
            thread.setDaemon(true);
            return thread;
        }
    };

    private final static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(20, threadFactory);

    /**
     * Cancelled tasks should be immediately removed from the work queue at time of cancellation
     */
    static {
        if (scheduler instanceof ScheduledThreadPoolExecutor) {
            ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) scheduler;

            //automatic remove periodic/delayed task from queue when cancel occur.
            executor.setRemoveOnCancelPolicy(true);

            //when set to true, for the cancel of these tasks you need to use java.util.concurrent.ExecutorService.shutdownNow
            //automatic cancel periodic task when java.util.concurrent.ExecutorService.shutdown,
            executor.setContinueExistingPeriodicTasksAfterShutdownPolicy(false);
            //automatic cancel delayed task when java.util.concurrent.ExecutorService.shutdown
            executor.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        }
    }

    public static class TimeoutToken implements Runnable {
        private Runnable handler;
        private boolean cancelled = false;
        private ScheduledFuture<?> schedule;

        @Override
        public void run() {
            long id = Thread.currentThread().getId();
            boolean initDetected = Thread.currentThread().isInterrupted();
            System.out.println(String.format("TimeoutToken java.lang.Runnable.run"));
            System.out.println(String.format("[%s] interrupted initDetected: [%s].", id, initDetected));

//            test interrupt: case 1: responds to the interrupt of java.util.concurrent.Future.cancel
//            test interrupt: case 1: there is no method can throw java.lang.InterruptedException, we have to display the check is Thread.currentThread().isInterrupted()
//            long count = Long.MAX_VALUE;
//            while (--count > 0) {
//                boolean pollDetected = Thread.currentThread().isInterrupted();
//                if (pollDetected) {
//                    System.out.println(String.format("[%s] interrupted pollDetected: [%s].", id, pollDetected));
//                    break;
//                }
//            }

//            test interrupt: case 2: java.lang.InterruptedException will only appear if java.lang.InterruptedException can be produced
//            test interrupt: case 2: the thread was interrupted in sleep, throwing an InterruptedException and clearly marking for isInterrupted
//            test interrupt: case 2: for the processing of subsequent calls, the clear interrupt is restored here
//            test interrupt: case 2: use the restored state for processing
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                System.out.println(e.getClass());
//                boolean sleepDetected = Thread.currentThread().isInterrupted();
//                System.out.println(String.format("[%s] interrupted sleepDetected: [%s].", id, sleepDetected));
//                Thread.currentThread().interrupt();
//            }
//            boolean restoredDetected = Thread.currentThread().isInterrupted();
//            System.out.println(String.format("[%s] interrupted restoredDetected: [%s].", id, restoredDetected));

            //real timeout handler execute control
            System.out.println("handler.run() cancelled:" + cancelled);
            if (!cancelled) {
                handler.run();
            }
        }
    }

    /**
     * 添加一个 stop 方法用于测试如何对 TimeoutService 进行优雅停机了。
     */
    public static void stop(int timeout, TimeUnit timeUnit) {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(timeout, timeUnit)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * It is assumed that the passed handler will not execute for a long time.
     *
     * @param runTime runTime
     * @param handler handler
     * @return a TimeoutToken that can be used to cancel the timeout.
     */
    public static final TimeoutToken addTimeoutHandler(long runTime, Runnable handler) {
        TimeoutToken token = new TimeoutToken();
        token.handler = handler;
        long delay = runTime - System.currentTimeMillis();
        if (delay < 0) {
            delay = 0;
        }

//        test interrupt: case x: simulation business execution first
//        delay = 10;

        //record ScheduledFuture for future cancellation
        token.schedule = scheduler.schedule(token, delay, TimeUnit.MILLISECONDS);
        return token;
    }

    /**
     * Cancel the timeout callback for the specified token.
     *
     * @param token token to be canceled.
     */
    public static final void cancelTimeoutHandler(TimeoutToken token) {
//        test interrupt: case x: wait scheduler to schedule TimeoutToken
//        try {
//            Thread.sleep(100);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        token.cancelled = true;
        if (token.schedule != null) {
            //manual cancellation ScheduledFuture for TimeoutToken
            //mayInterruptIfRunning==true causes invoke java.lang.Thread.interrupt
            boolean cancel = token.schedule.cancel(true);
            System.out.println("TimeoutService.cancelTimeoutHandler:" + cancel);
        }
    }
}