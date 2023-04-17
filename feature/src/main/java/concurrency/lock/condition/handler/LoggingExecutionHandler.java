package concurrency.lock.condition.handler;

import concurrency.lock.condition.role.SimplePool;
import concurrency.lock.condition.task.BaseTask;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

public class LoggingExecutionHandler implements RejectedExecutionHandler {

    private final SimplePool simplePool;

    public LoggingExecutionHandler(SimplePool simplePool) {
        this.simplePool = simplePool;
    }

    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        if (r instanceof BaseTask) {
            BaseTask baseTask = (BaseTask) r;

            String simpleName = baseTask.getClass().getSimpleName();
            int index = baseTask.getIndex();
            String format = String.format("rejected:name=[%s], index=[%s].", simpleName, index);
            // 由于输出到 console 时会 lock stdout
            // 所以多线程运行时，其会大大的阻碍性能问题，建议不是 debug 问题时关掉输出
//                System.out.println(format);
        }
    }

}
