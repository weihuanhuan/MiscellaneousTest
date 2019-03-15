package ThreadTest;

import ThreadPoolExecutorTest.CustomThreadFactory;
import ThreadPoolExecutorTest.SimpleSession;
import ThreadTest.filter.CountableUpdateFilter;
import ThreadTest.filter.UpdateFilter;
import ThreadTest.task.CustomerTask;
import ThreadTest.task.ProducerTask;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 3/14/2019.
 */
public class MainThreadTest {

    public static final int MAX_LINKED_HASH_MAP_QUANTITY = 10000;

    public static final int MAX_SESSION_QUANTITY = 1000;

    public static final int MAX_PRODUCER_THREAD = 10;

    public static final int MAX_CUSTOMER_THREAD = 1;

    public static void main(String[] args) {

        //session
        int sessionCount = MAX_SESSION_QUANTITY;
        SimpleSession[] simpleSessions = new SimpleSession[MAX_SESSION_QUANTITY];
        while (sessionCount-- > 0) {
            SimpleSession session = new SimpleSession(String.valueOf(sessionCount));
            simpleSessions[sessionCount] = session;
        }

        //work queue
        UpdateFilter<SimpleSession, Runnable> UpdateFilter = new CountableUpdateFilter<>(MAX_LINKED_HASH_MAP_QUANTITY);

        //producer
        ThreadFactory producerThreadFactory = new CustomThreadFactory("producer-thread", true);

        int producerCount = MAX_PRODUCER_THREAD;
        while (producerCount-- > 0) {
            Thread thread = producerThreadFactory.newThread(new ProducerTask<>(simpleSessions, UpdateFilter));
            thread.start();
        }

        //customer
        ThreadFactory customerThreadFactory = new CustomThreadFactory("customer-thread", true);

        int customerCount = MAX_CUSTOMER_THREAD;
        while (customerCount-- > 0) {
            Thread thread = customerThreadFactory.newThread(new CustomerTask<>(UpdateFilter));
            thread.start();
        }

        //running time statistic
        long startTime = System.currentTimeMillis();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Total Running Time:" + TimeUnit.MILLISECONDS.toSeconds(duration));
        }));

        //keeping main alive some time.
        int count = 2;
        while (count -- > 0) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new NullPointerException();
            }
        }

    }
}
