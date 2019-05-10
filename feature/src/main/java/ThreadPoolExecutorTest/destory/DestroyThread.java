package ThreadPoolExecutorTest.destory;

/**
 * Created by JasonFitch on 4/9/2019.
 */
public class DestroyThread extends Thread {

    Runnable runnable;

    public DestroyThread(Runnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void run() {
        try {
            runnable.run();
        } catch (ThreadNormalStopException ex) {
            System.out.println("ThreadNormalStopException threadID:" + this.getId());
        }
    }
}
