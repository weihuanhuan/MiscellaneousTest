package ThreadPoolExecutorTest.task;

import ThreadPoolExecutorTest.VersionWrapper;
import ThreadPoolExecutorTest.tracker.VersionTracker;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Created by JasonFitch on 3/14/2019.
 */
public class CustomerTask<T> implements Runnable {

    private static Logger logger = Logger.getLogger(CustomerTask.class.getName());

    private static AtomicInteger skipCount = new AtomicInteger(0);

    private static AtomicInteger processCount = new AtomicInteger(0);

    private VersionTracker<T> sessionVersionTracker;

    private BlockingQueue<Runnable> workerQueue;

    public CustomerTask(VersionTracker<T> sessionVersionTracker, BlockingQueue<Runnable> workerQueue) {
        this.sessionVersionTracker = sessionVersionTracker;
        this.workerQueue = workerQueue;
    }

    @Override
    public void run() {


        while (true) {

            try {

                Runnable runnable = workerQueue.poll();
                if (null == runnable) {
                    TimeUnit.MILLISECONDS.sleep(10);
                    continue;
                }

                //shirking
                if (runnable instanceof VersionWrapper) {
                    T target = (T) ((VersionWrapper) runnable).getTarget();
                    int current = ((VersionWrapper) runnable).getVersion();
                    int newest = sessionVersionTracker.getVersion(target);
                    if (current < newest) {
                        skipCount.incrementAndGet();
//                        String skipInfo = "skipping:" + runnable.toString() + " [skipCount: " + skipCount.get() + " ] [workerQueueSize:" + workerQueue.size() + " ]";
//                         logger.info(skipInfo);
                        continue;
                    }
                }

                //handle
                runnable.run();
                processCount.incrementAndGet();
//                String processInfo = "processing:" + runnable.toString() + " [processCount: " + processCount.get() + " ] [workerQueueSize:" + workerQueue.size() + " ]";
//                 logger.info(processInfo);

                //statistic
                String workInfo
                        = "[totalCount:" + ProducerTask.totalCount
                        + ",processCount: " + processCount.get()
                        + ",skipCount: " + skipCount.get()
                        + ",workerQueueSize:" + workerQueue.size() + "]";
                logger.info(workInfo);


            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new NullPointerException();
            }
        }
    }

}
