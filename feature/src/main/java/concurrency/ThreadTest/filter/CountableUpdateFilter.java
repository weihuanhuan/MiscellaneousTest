package concurrency.ThreadTest.filter;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by JasonFitch on 3/14/2019.
 */
public class CountableUpdateFilter<T, K> extends AbstractUpdateFilter<T, K> {

    public static AtomicInteger skipCount = new AtomicInteger(0);

    public CountableUpdateFilter(int maxQuantity) {
        super(maxQuantity);
    }

    @Override
    public synchronized boolean put(T key, K value) {
        boolean exist = super.put(key, value);
        if (exist) {
            skipCount.incrementAndGet();
        }
        return exist;
    }
}
