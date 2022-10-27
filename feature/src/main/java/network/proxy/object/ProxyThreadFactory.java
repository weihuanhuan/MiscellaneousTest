package network.proxy.object;

import java.util.concurrent.ThreadFactory;

public class ProxyThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        return new ProxyThread(r);
    }

    @Override
    public String toString() {
        return "ProxyThreadFactory.toString";
    }
}
