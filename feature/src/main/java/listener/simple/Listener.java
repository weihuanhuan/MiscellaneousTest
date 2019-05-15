package listener.simple;

/**
 * Created by JasonFitch on 5/14/2019.
 */
public class Listener {

    private SelectorThread selectorThread;

    public Listener(SelectorThread selectorThread) {
        this.selectorThread = selectorThread;
    }

    public void addThread() {
        selectorThread.addTask();
    }

    public void removeThread() {
        selectorThread.removeTask();
    }
}
