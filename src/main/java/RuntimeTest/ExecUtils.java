package RuntimeTest;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class ExecUtils {
    private static final int JOIN_TIMEOUT = 250;

    public static void startServer(String execute, String[] arguments) throws IOException, InterruptedException {
        exec(execute, arguments, false);

        int tryTimes = 0;
        boolean isSuccess = false;

        String pidPath = "";
        File pidFile = new File(pidPath);

        while (tryTimes++ < 15) {
            if (pidFile.exists()) {
                isSuccess = true;
                break;
            }
            Thread.sleep(1000 * 1);
        }
        System.out.println("try to detect the status of server was finished");

        if (!isSuccess) {
//            throw new IOException("Not found PID file!");
        }
    }

    public static void stopServer(String execute, String[] arguments) throws IOException, InterruptedException {
        ExecResult execResult = exec(execute, arguments, true);

        int execCode = execResult.exitCode;
        String message = execResult.message;

        if (0 != execCode) {
            throw new IOException(message);
        }
    }

    public static ExecResult exec(String execute, String[] arguments) throws IOException, InterruptedException {
        return exec(execute, arguments, true);
    }

    public static ExecResult exec(String execute, String[] arguments, boolean redirect) throws IOException, InterruptedException {
        ExecResult execResult = new ExecResult();
        StringBuffer message = new StringBuffer();

        Process process = null;
        try {

            if (null != arguments) {
                String[] commands = new String[arguments.length + 1];
                commands[0] = execute;
                System.arraycopy(arguments, 0, commands, 1, arguments.length);
                process = Runtime.getRuntime().exec(commands);
            } else {
                process = Runtime.getRuntime().exec(execute);
            }

            StreamPumper inputPumper = new StreamPumper(process.getInputStream(), message,"thread-pumper-input");
            StreamPumper errorPumper = new StreamPumper(process.getErrorStream(), message,"thread-pumper-error");
            inputPumper.start();
            errorPumper.start();
            //放在 if 外面，防止在不用阻塞等待子进程返回结果时，子进程的输出流没有及时读取，导致系统缓冲区填满，使得子进程输出阻塞。

            if (redirect) {

                process.waitFor();

                finishPumper(inputPumper);
                finishPumper(errorPumper);

                execResult.exitCode = process.exitValue();
                execResult.message = message.toString();
            }

        } finally {
            if (null != process && redirect) {
                process.destroy();
            }
        }

        return execResult;
    }

    private static void finishPumper(StreamPumper streamPumper) {
        try {
            if (!streamPumper.isAlive()) {
                return;
            }

            streamPumper.close();
            streamPumper.join(JOIN_TIMEOUT);
            while (streamPumper.isAlive()) {
                streamPumper.interrupt();
                streamPumper.join(JOIN_TIMEOUT);
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class StreamPumper extends Thread {
        private InputStream is;
        private StringBuffer message;

        private boolean finish = false;
        private static final int SLEEP_TIME = 10;

        public StreamPumper(InputStream is, StringBuffer message,String name) {
            this.is = is;
            this.message = message;

            this.setName(name);
            this.setDaemon(true);
//            设置成 Daemon 进程，防止主进程结束后，因为子进程还在执行导致JVM无法注销
        }

        @Override
        public void run() {
            final byte[] buffer = new byte[1024];

            int length;
            try {

//                正常情况下（未中断，未完成），循环收集数据块。
                while (true) {
                    waitForInput(is);

                    if (finish || Thread.interrupted()) {
                        break;
                    }

                    length = is.read(buffer);
                    if (length <= 0 || Thread.interrupted()) {
                        break;
                    }

                    byte[] newBytes = new byte[length];
                    System.arraycopy(buffer, 0, newBytes, 0, length);
                    System.out.println(new String(newBytes));
                    message.append(new String(newBytes));
                }

//                接收到 finish 信号后收集所有未读数据。
                if (finish) {

                    while ((length = is.available()) > 0) {
                        if (Thread.interrupted()) {
                            break;
                        }
                        length = is.read(buffer, 0, Math.min(length, buffer.length));
                        if (length < 0) {
                            break;
                        }
                        byte[] newBytes = new byte[length];
                        System.arraycopy(buffer, 0, newBytes, 0, length);
                        System.out.println(new String(newBytes));
                        message.append(new String(newBytes));
                    }
                }

//          可以读取时的中断处理是：读完一轮后停止下轮读取，
//          等待读取时的中断处理是：停止等待，并且不尝试下一轮.
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                finish = false;
                synchronized (this) {
                    notifyAll();
                }
            }
        }

        private void waitForInput(InputStream is) throws IOException, InterruptedException {
            while (!finish && is.available() == 0) {
                if (Thread.interrupted()) {
                    throw new InterruptedException();
                }

                synchronized (this) {
                    this.wait(SLEEP_TIME);
                }
            }
        }

        public synchronized void close() {
            if (!finish) {
                finish = true;
                notifyAll();
            }
        }
    }

    public static class ExecResult {
        public int exitCode = 1;
        public String message;

        public ExecResult() {
        }

        public ExecResult(int exitCode, String message) {
            this.exitCode = exitCode;
            this.message = message;
        }
    }
}
