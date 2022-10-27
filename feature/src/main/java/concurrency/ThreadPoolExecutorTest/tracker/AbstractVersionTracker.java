package concurrency.ThreadPoolExecutorTest.tracker;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by JasonFitch on 3/15/2019.
 */
abstract public class AbstractVersionTracker<T> implements VersionTracker<T> {

    private static final int DEFAULT_INITIAL_CAPACITY = 12800;

    private static final float DEFAULT_LOAD_FACTOR = 0.80f;

    private static final int DEFAULT_VERSION = 0;

    protected Map<T, Integer> trackerMap;

    public AbstractVersionTracker() {
        this(new HashMap<>(DEFAULT_INITIAL_CAPACITY, DEFAULT_LOAD_FACTOR));
    }

    public AbstractVersionTracker(Map<T, Integer> trackerMap) {
        this.trackerMap = trackerMap;
    }

    @Override
    public synchronized Integer incrementVersion(T target) {
        Integer version = trackerMap.getOrDefault(target, DEFAULT_VERSION);
        trackerMap.put(target, ++version);
        return version;
    }

    @Override
    public synchronized Integer getVersion(T target) {
        Integer version = trackerMap.get(target);
        if (null == version) {
            version = DEFAULT_VERSION;
        }
        return version;
    }

    @Override
    public Integer trackingAmount() {
        return trackerMap.size();
    }

    @Override
    abstract public void trackingClear();

}
