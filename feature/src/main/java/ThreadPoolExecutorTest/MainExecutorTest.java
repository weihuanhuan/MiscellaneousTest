package ThreadPoolExecutorTest;

import ThreadPoolExecutorTest.task.CustomerTask;
import ThreadPoolExecutorTest.task.ProducerTask;
import ThreadPoolExecutorTest.tracker.AutoCleanVersionTracker;
import ThreadPoolExecutorTest.queue.CustomBlockingQueue;
import ThreadPoolExecutorTest.tracker.VersionTracker;
import java.io.IOException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 3/13/2019.
 */
public class MainExecutorTest {

    public static final int MAX_BLOCKING_QUEUE_QUANTITY = 10000;

    public static final int MAX_SESSION_QUANTITY = 1000;

    public static final int MAX_PRODUCER_THREAD = 10;

    public static final int MIN_PRODUCER_CORE_THREAD = 10;

    public static final int MAX_CUSTOMER_THREAD = 1;

    public static final int MIN_CUSTOMER_CORE_THREAD = 1;

    public static final long KEEP_ALIVE_TIME = 10L;

    public static void main(String[] args) throws IOException {

        //session
        int sessionCount = MAX_SESSION_QUANTITY;
        SimpleSession[] simpleSessions = new SimpleSession[MAX_SESSION_QUANTITY];
        while (sessionCount-- > 0) {
            SimpleSession session = new SimpleSession(String.valueOf(sessionCount));
            simpleSessions[sessionCount] = session;
        }

        //work queue
        CustomBlockingQueue<Runnable> customBlockingQueue = new CustomBlockingQueue<>(MAX_BLOCKING_QUEUE_QUANTITY);

        //tracker
        VersionTracker<SimpleSession> versionTracker = new AutoCleanVersionTracker<>();

        //producer
        ThreadFactory producerThreadFactory = new CustomThreadFactory("producer-thread", true);

        CustomThreadPoolExecutor producerService = new CustomThreadPoolExecutor(MIN_PRODUCER_CORE_THREAD, MAX_PRODUCER_THREAD,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS, customBlockingQueue, producerThreadFactory);

        int producerCount = MAX_PRODUCER_THREAD;
        while (producerCount-- > 0) {
            ProducerTask<SimpleSession> simpleSessionProducerTask = new ProducerTask<>(versionTracker, simpleSessions, customBlockingQueue);
            producerService.execute(simpleSessionProducerTask);
        }

        //customer
        ThreadFactory customerThreadFactory = new CustomThreadFactory("customer-thread", true);

        CustomThreadPoolExecutor customerService = new CustomThreadPoolExecutor(MIN_CUSTOMER_CORE_THREAD, MAX_CUSTOMER_THREAD,
                KEEP_ALIVE_TIME, TimeUnit.SECONDS, customBlockingQueue, customerThreadFactory);

        int customerCount = MAX_CUSTOMER_THREAD;
        while (customerCount-- > 0) {
            CustomerTask<SimpleSession> simpleSessionCustomerTask = new CustomerTask<>(versionTracker, customBlockingQueue);
            customerService.execute(simpleSessionCustomerTask);
        }

        //running time statistic
        long startTime = System.currentTimeMillis();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Total Running Time:" + TimeUnit.MILLISECONDS.toSeconds(duration));
        }));

        //keeping main alive some time.
        int count = 2;
        while (count-- > 0) {
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
                throw new NullPointerException();
            }
        }


    }


}
