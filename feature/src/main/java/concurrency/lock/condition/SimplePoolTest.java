package concurrency.lock.condition;

import concurrency.lock.condition.role.SimpleInvoker;
import concurrency.lock.condition.role.SimplePool;
import concurrency.lock.condition.task.Creator;
import concurrency.lock.condition.entity.Entity;
import concurrency.lock.condition.task.Invoker;
import concurrency.lock.condition.util.ThreadPoolUtility;

import java.util.concurrent.TimeUnit;

public class SimplePoolTest {

    // pool config
    // fairness 和 transfer 都能巨大的影响性能
    public static final boolean USE_FAIRNESS_LOCK = false;
    public static final boolean USE_TRANSFER_QUEUE = false;
    public static final int FIXED_CREATOR_THREAD = 8;

    // invoker config
    public static final int MAX_INVOKER_QUEUE_LENGTH = 32;
    public static final int MAX_INVOKER_THREAD = 16;

    public static void main(String[] args) throws InterruptedException {
        // pool
        SimplePool simplePool = new SimplePool(USE_FAIRNESS_LOCK, USE_TRANSFER_QUEUE, FIXED_CREATOR_THREAD);

        //invoker
        SimpleInvoker simpleInvoker = new SimpleInvoker(simplePool, MAX_INVOKER_QUEUE_LENGTH, MAX_INVOKER_THREAD);
        //JF TODO jdk 的超时等待好像很影响性能，只要超时不为 0 时，就会明显的慢几个数量级,原因未知
        simpleInvoker.addInvokerWithInterval(0, TimeUnit.MICROSECONDS);

        //running time statistic
        statisticTimeHook();

        //keeping main alive some time.
        ThreadPoolUtility.sleep(10, TimeUnit.SECONDS);

        simpleInvoker.shutdown(10, TimeUnit.SECONDS);
        simplePool.shutdown(10, TimeUnit.SECONDS);

        String invokerFormat = String.format("Invoker: executeCount=[%s], processCount=[%s], retryCount=[%s].", Invoker.executeCount, Invoker.processCount, Invoker.retryCount);
        System.out.println(invokerFormat);
        String creatorFormat = String.format("Creator: executeCount=[%s], processCount=[%s], retryCount=[%s].", Creator.executeCount, Creator.processCount, Creator.retryCount);
        System.out.println(creatorFormat);
        String entityFormat = String.format("Entity: createCount=[%s], processCount=[%s].", Entity.createCount, Entity.processCount);
        System.out.println(entityFormat);
        String poolFormat = String.format("SimplePool: transferAddCount=[%s], transferBorrowCount=[%s].", SimplePool.transferAddCount, SimplePool.transferBorrowCount);
        System.out.println(poolFormat);

    }

    private static void statisticTimeHook() {
        long startTime = System.currentTimeMillis();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            long duration = System.currentTimeMillis() - startTime;
            System.out.println("Total Running Time:" + TimeUnit.MILLISECONDS.toSeconds(duration));
        }));
    }

}
