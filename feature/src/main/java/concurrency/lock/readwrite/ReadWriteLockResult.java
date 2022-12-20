package concurrency.lock.readwrite;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ReadWriteLockResult extends FutureTask<Boolean> {

    public final int waitTimeout = ReadWriteLockTest.WAIT_TIMEOUT;

    private volatile Boolean result;

    public ReadWriteLockResult(Callable<Boolean> callable) {
        super(callable);
    }

    public void waitResult() {
        try {
            result = get(waitTimeout, TimeUnit.MILLISECONDS);
        } catch (InterruptedException | ExecutionException e) {
            String name = Thread.currentThread().getName();
            String format = String.format("name=[%s], waitResult, exception=[%s]!", name, e.getMessage());
//            System.out.println(format);
        } catch (TimeoutException e) {
            String name = Thread.currentThread().getName();
            String format = String.format("name=[%s], waitResult, waitTimeout=[%s]!", name, waitTimeout);
//            System.out.println(format);
        }
    }

    public boolean queryResult() {
        String name = Thread.currentThread().getName();
        String format = String.format("name=[%s], queryResult, result=[%s]!", name, result);
//        System.out.println(format);

        if (result != null) {
            return result;
        }
        //当在 waitResult 中发生了超时和执行异常的情况，由于没有设置 result 对象，所以我们均默认返回 false
        return false;
    }

    @Override
    public boolean isDone() {
        return super.isDone();
    }

}
