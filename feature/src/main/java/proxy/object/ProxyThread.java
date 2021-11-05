package proxy.object;

public class ProxyThread extends Thread {

    public ProxyThread(Runnable r) {
        super(r);
    }

    @Override
    public String toString() {
        return "ProxyThread.toString";
    }
}
