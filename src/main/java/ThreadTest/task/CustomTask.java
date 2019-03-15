package ThreadTest.task;

import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 3/14/2019.
 */
public class CustomTask<T> implements Runnable {

    private T target;

    public CustomTask(T target) {
        this.target = target;
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
}
