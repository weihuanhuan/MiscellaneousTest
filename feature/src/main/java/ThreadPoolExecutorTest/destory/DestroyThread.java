package ThreadPoolExecutorTest.destory;

/**
 * Created by JasonFitch on 4/9/2019.
 */
public class DestroyThread extends Thread {

    Runnable runnable;

    public DestroyThread(Runnable runnable) {
        this.runnable = runnable;
    }

    public DestroyThread(String name, Runnable runnable) {
        this.runnable = runnable;
        setName(name);
    }

    @Override
    public void run() {
        try {
            runnable.run();
        } catch (ThreadNormalStopException ex) {
            //由 DestroyableThreadPoolExecutor.afterExecute 中抛出的异常， 终止了 runWorker 中的循环
            //然后由 java.util.concurrent.ThreadPoolExecutor.processWorkerExit 来善后处理 Worker 的退出
            System.out.println("ThreadNormalStopException threadName:" + this.getName());
        }
    }
}
