package listener.simple;

/**
 * Created by JasonFitch on 5/14/2019.
 */
public class Pipeline {

    private int threadCount;

    private Listener listener;

    public void removeThread() {
        threadCount--;
        listener.removeThread();
    }

    public void addThread() {
        threadCount++;
        listener.addThread();
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public int getThreadCount() {
        return threadCount;
    }
}
