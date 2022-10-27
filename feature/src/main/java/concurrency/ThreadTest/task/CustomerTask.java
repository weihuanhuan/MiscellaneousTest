package concurrency.ThreadTest.task;

import concurrency.ThreadTest.filter.UpdateFilter;
import concurrency.ThreadTest.filter.CountableUpdateFilter;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

/**
 * Created by JasonFitch on 3/14/2019.
 */
public class CustomerTask<T> implements Runnable {

    private static Logger logger = Logger.getLogger(CustomerTask.class.getName());

    private UpdateFilter<T, Runnable> updateFilter;

    private static AtomicInteger processCount = new AtomicInteger(0);

    public CustomerTask(UpdateFilter<T, Runnable> updateFilter) {
        this.updateFilter = updateFilter;
    }

    @Override
    public void run() {

        while (true) {

            try {

                Runnable runnable = updateFilter.get();
                if (null == runnable) {
                    return;
                }
                runnable.run();

                //statistic
                processCount.incrementAndGet();
                String workInfo
                        = "[totalCount:" + ProducerTask.totalCount
                        + ",processCount: " + processCount.get()
                        + ",skipCount: " + CountableUpdateFilter.skipCount.get()
                        + ",runnableHashMapSize:" + updateFilter.size() + "]";
                logger.info(workInfo);

            } catch (Throwable throwable) {
                throwable.printStackTrace();
                throw new NullPointerException();
            }

        }
    }
}
