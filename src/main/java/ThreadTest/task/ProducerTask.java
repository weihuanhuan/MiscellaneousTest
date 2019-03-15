package ThreadTest.task;

import ThreadTest.filter.UpdateFilter;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static ThreadTest.MainThreadTest.MAX_SESSION_QUANTITY;

/**
 * Created by JasonFitch on 3/14/2019.
 */
public class ProducerTask<T> implements Runnable {

    private static Random random = new Random(System.currentTimeMillis());

    private T[] simpleSessions;

    private UpdateFilter<T, Runnable> updateFilter;

    public static AtomicInteger totalCount = new AtomicInteger(0);

    public ProducerTask(T[] simpleSessions, UpdateFilter<T, Runnable> updateFilter) {
        this.simpleSessions = simpleSessions;
        this.updateFilter = updateFilter;
    }

    @Override
    public void run() {

        try {
            while (true) {

                T simpleSession = simpleSessions[random.nextInt(MAX_SESSION_QUANTITY)];
                updateFilter.put(simpleSession, new CustomTask(simpleSession));
                totalCount.incrementAndGet();

                //take a rest
                TimeUnit.MILLISECONDS.sleep(1);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
            throw new NullPointerException();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            throw new NullPointerException();
        }
    }
}
