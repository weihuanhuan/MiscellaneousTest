package nio;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class NIOTest {

    static int PORT = 28001;

    public static void main(String[] args) throws Exception {
        ExecutorService executorService = Executors.newCachedThreadPool();

        Thread threadServer = createServer(PORT);
        Thread threadClient = createClient(PORT);

        threadServer.start();
        threadClient.start();

        TimeUnit.SECONDS.sleep(100);
    }

    public static Thread createServer(int port) {
        Thread threadServer = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    System.out.println("server started...");
                    new NIOServer().start(port);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "thread-server");
        return threadServer;
    }

    public static Thread createClient(int port) {
        Thread threadClient = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    TimeUnit.SECONDS.sleep(2);
                    new NIOClient().start(port);
                } catch (Exception e) {
                    Thread.currentThread().interrupt();
                }
            }
        }, "thread-client");
        return threadClient;
    }

}
