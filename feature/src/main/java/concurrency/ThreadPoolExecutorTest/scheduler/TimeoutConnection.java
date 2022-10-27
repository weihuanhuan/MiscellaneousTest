package concurrency.ThreadPoolExecutorTest.scheduler;

/**
 * 本类来源于 jenkins 维护的 trilead-ssh2 工程的 com.trilead.ssh2.Connection 类。
 * <p>
 * <!-- https://mvnrepository.com/artifact/org.jenkins-ci/trilead-ssh2 -->
 * <dependency>
 * <groupId>org.jenkins-ci</groupId>
 * <artifactId>trilead-ssh2</artifactId>
 * <version>build-217-jenkins-27</version>
 * </dependency>
 */

public class TimeoutConnection {

    /**
     * 参考方法 com.trilead.ssh2.Connection#connect(com.trilead.ssh2.ServerHostKeyVerifier, int, int, int)
     */
    public synchronized void connect(int kexTimeout) {

        final class TimeoutState {
            boolean isCancelled = false;
            boolean timeoutSocketClosed = false;
        }

        final TimeoutState state = new TimeoutState();

        TimeoutService.TimeoutToken token = null;
        try {
            if (kexTimeout > 0) {
                final Runnable timeoutHandler = new Runnable() {
                    public void run() {
                        synchronized (state) {
                            if (state.isCancelled)
                                return;
                            state.timeoutSocketClosed = true;
                        }
                    }
                };

                long timeoutHorizont = System.currentTimeMillis() + kexTimeout;
                token = TimeoutService.addTimeoutHandler(timeoutHorizont, timeoutHandler);
            }

        } finally {
            /* Now try to cancel the timeout, if needed */
            if (token != null) {
                TimeoutService.cancelTimeoutHandler(token);
            }
        }
    }

}
