package concurrency.lock.condition.handler;

import concurrency.lock.condition.role.SimplePool;
import concurrency.lock.condition.task.Creator;
import concurrency.lock.condition.util.ThreadPoolUtility;

public class CreatorThreadFactory extends ThreadPoolUtility.DefaultThreadFactory {

    private final SimplePool simplePool;

    public CreatorThreadFactory(String threadName, boolean daemon, SimplePool simplePool) {
        super(threadName, daemon);
        this.simplePool = simplePool;
    }

    @Override
    public Thread newThread(Runnable ignored) {
        Creator creator = new Creator(simplePool);
        return super.newThread(creator);
    }

}