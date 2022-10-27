package concurrency.ThreadTest.filter;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by JasonFitch on 3/15/2019.
 */
public class AbstractUpdateFilter<T, K> implements UpdateFilter<T, K> {

    private static int DEFAULT_NOTIFY_THRESHOLD = 2;

    private Map<T, K> filterMap;

    private int notifyThreshold;

    public AbstractUpdateFilter(int maxQuantity) {
        this(new LinkedHashMap<>(maxQuantity), DEFAULT_NOTIFY_THRESHOLD);
    }

    public AbstractUpdateFilter(Map<T, K> filterMap, int notifyThreshold) {
        this.filterMap = filterMap;
        this.notifyThreshold = notifyThreshold;
    }

    @Override
    public synchronized boolean put(T key, K value) {
        boolean previouslyExist = false;
        K previously = filterMap.remove(key);
        if (null != previously) {
            previouslyExist = true;
        }
        filterMap.putIfAbsent(key, value);

        if (filterMap.size() < notifyThreshold) {
            this.notifyAll();
        }
        return previouslyExist;

    }


    public synchronized K get() {
        try {
            while (true) {
                if (filterMap.isEmpty()) {
                    this.wait();
                } else {
                    break;
                }
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }

        Iterator<Map.Entry<T, K>> iterator = filterMap.entrySet().iterator();
        K runnable = iterator.next().getValue();
        iterator.remove();
        return runnable;
    }

    @Override
    public int size() {
        return filterMap.size();
    }

}
