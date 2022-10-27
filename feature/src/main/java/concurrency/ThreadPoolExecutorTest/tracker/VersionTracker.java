package concurrency.ThreadPoolExecutorTest.tracker;

/**
 * Created by JasonFitch on 3/15/2019.
 */
public interface VersionTracker<T> {

    Integer incrementVersion(T target);

    Integer getVersion(T target);

    Integer trackingAmount();

    void trackingClear();
}
