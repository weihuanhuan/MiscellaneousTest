package ThreadPoolExecutorTest;

import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 3/13/2019.
 */
public class VersionWrapper<T> implements Runnable {

    private T target;

    private Integer version;

    public VersionWrapper() {
        this(null, 0);
    }

    public VersionWrapper(T target) {
        this(target, 0);
    }

    public VersionWrapper(T target, Integer version) {
        this.target = target;
        this.version = version;
    }

    public T getTarget() {
        return target;
    }

    public void setTarget(T target) {
        this.target = target;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public void run() {
        try {

            //simulate work time
            TimeUnit.MILLISECONDS.sleep(10);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "VersionWrapper{" + "target=" + target + ", version=" + version + '}';
    }
}
