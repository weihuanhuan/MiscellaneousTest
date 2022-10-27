package concurrency.ThreadPoolExecutorTest.tracker;

import concurrency.ThreadPoolExecutorTest.task.CustomerTask;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Created by JasonFitch on 3/14/2019.
 */
public class AutoCleanVersionTracker<K> extends AbstractVersionTracker<K> {

    private static Logger logger = Logger.getLogger(CustomerTask.class.getName());

    private static final int DEFAULT_CLEAN_INTERVAL = 60;

    private static final int DEFAULT_CLEAN_THRESHOLD = 10000;

    private String cleanThreadName = "tracker-cleaner-thread";

    private Thread cleanThread;

    private AtomicInteger cleanCount = new AtomicInteger(0);

    public AutoCleanVersionTracker() {
        this(DEFAULT_CLEAN_INTERVAL, DEFAULT_CLEAN_THRESHOLD);
    }

    public AutoCleanVersionTracker(int interval, int threshold) {
        initClearThread(interval, threshold);
    }

    public AutoCleanVersionTracker(int interval, int threshold, Map<K, Integer> map) {
        super(map);
        initClearThread(interval, threshold);
    }

    private void initClearThread(int interval, int threshold) {
        this.cleanThread = new Thread(new Cleaner(interval, threshold), cleanThreadName);
        cleanThread.setDaemon(true);
        cleanThread.start();
    }

    @Override
    public synchronized void trackingClear() {
        this.trackerMap.clear();
    }

    class Cleaner implements Runnable {

        private int interval;

        private int threshold;

        public Cleaner(int interval, int threshold) {
            this.interval = interval;
            this.threshold = threshold;
        }

        @Override
        public void run() {

            int currentClean;
            while (true) {
                try {
                    currentClean = AutoCleanVersionTracker.this.trackingAmount();
                    if (currentClean < threshold) {
                        continue;
                    }

                    AutoCleanVersionTracker.this.trackingClear();
                    cleanCount.addAndGet(currentClean);

                    String cleanInfo = "[currentClean: " + currentClean + " totalClean: " + cleanCount.get() + " ]";
                    logger.info(cleanInfo);

                    TimeUnit.SECONDS.sleep(interval);
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
}
