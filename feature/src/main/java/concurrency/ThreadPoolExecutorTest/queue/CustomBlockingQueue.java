package concurrency.ThreadPoolExecutorTest.queue;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 3/13/2019.
 */
public class CustomBlockingQueue<T> extends LinkedBlockingQueue<T> {

    public CustomBlockingQueue(int capacity) {
        super(capacity);
    }

    @Override
    public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
//        System.out.println("offer with time:"+t.toString());
        return super.offer(t, timeout, unit);
    }

    @Override
    public boolean offer(T t) {
//        System.out.println("offer:"+t.toString());
        return super.offer(t);
    }

    @Override
    public T take() throws InterruptedException {
        //JF ConcurrentHashMap 执行这里 HaspMap 不执行，why？
        //   同时如果将 ThreadPoolExecutor 的 submit 改为 execute 时，ConcurrentHashMap 也不执行这里，他们的区别是？
//        System.out.println("take");
//        TimeUnit.SECONDS.sleep(100);
        return super.take();
    }
}
