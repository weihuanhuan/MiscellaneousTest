package ThreadPoolExecutorTest.task;

import ThreadPoolExecutorTest.VersionWrapper;
import ThreadPoolExecutorTest.tracker.VersionTracker;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static ThreadPoolExecutorTest.MainExecutorTest.MAX_SESSION_QUANTITY;

/**
 * Created by JasonFitch on 3/14/2019.
 */
public class ProducerTask<T> implements Runnable {

    private static Random random = new Random(System.currentTimeMillis());

    private T[] simpleSessions;

    private VersionTracker<T> sessionVersionTracker;

    private BlockingQueue<Runnable> workerQueue;

    public static AtomicInteger totalCount = new AtomicInteger(0);

    public ProducerTask(VersionTracker<T> sessionVersionTracker, T[] simpleSessions, BlockingQueue<Runnable> blockingQueue) {
        this.sessionVersionTracker = sessionVersionTracker;
        this.simpleSessions = simpleSessions;
        this.workerQueue = blockingQueue;
    }

    @Override
    public void run() {

        while (true) {
            try {

                T simpleSession = simpleSessions[random.nextInt(MAX_SESSION_QUANTITY)];

                Integer version = sessionVersionTracker.incrementVersion(simpleSession);

                VersionWrapper<T> sessionVersionWrapper = new VersionWrapper<>(simpleSession);
                sessionVersionWrapper.setVersion(version);

                workerQueue.put(sessionVersionWrapper);
                totalCount.incrementAndGet();

                //take a rest
                TimeUnit.MILLISECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new NullPointerException();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new NullPointerException();
            }
        }
    }
}
