package ThreadPoolExecutorTest;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by JasonFitch on 3/13/2019.
 */
public class CustomBlockingQueue<T> extends ArrayBlockingQueue<T> {

    public CustomBlockingQueue(int capacity) {
        super(capacity);
    }

    @Override
    public boolean offer(T t) {
        return super.offer(t);
    }
}
