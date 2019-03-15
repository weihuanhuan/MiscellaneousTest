package ThreadPoolExecutorTest.queue;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by JasonFitch on 3/15/2019.
 */
public class IteratorRemoveBlockingQueue<T> extends LinkedBlockingQueue<T> {

    public IteratorRemoveBlockingQueue(int capacity) {
        super(capacity);
    }

    @Override
    public T poll() {

        T finalTarget = super.poll();
        if (null == finalTarget) {
            return null;
        }

        int size = super.size();
        Iterator<T> iterator = super.iterator();
        while (size-- > 0 && iterator.hasNext()) {
            T next = iterator.next();
            if (next == finalTarget) {
                finalTarget = next;
                iterator.remove();
            }
        }

        return finalTarget;
    }
}
