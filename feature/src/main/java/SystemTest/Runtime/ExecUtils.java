package SystemTest.Runtime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

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
                System.out.println("execute" + Arrays.deepToString(commands));
                process = Runtime.getRuntime().exec(commands);
            } else {
                System.out.println("execute:" + execute);
                process = Runtime.getRuntime().exec(execute);
//                process 会开启一个子进程来执行任务 ,并将子进程的 IO 定向到本进程，即子进程的父进程。详情产看 class Process
//                如果是嵌套调用也是一样，只要不另开新进程所以的子进程的 IO 都会定向过来。
//                如 Java(exec) -> cmd, 重定向 cmd 到 java
//                如 java(exec) -> cmd -> tomcat, 重定向 tomcat 到 cmd ，cmd 到 java

//                这个任务不是阻塞的执行，除非调用其 java.lang.Process.waitFor() 方法
            }

            StreamPumper inputPumper = new StreamPumper(process.getInputStream(), message, "thread-pumper-input");
            StreamPumper errorPumper = new StreamPumper(process.getErrorStream(), message, "thread-pumper-error");
            inputPumper.start();
            errorPumper.start();
//            即使没有必要读取子进程的IO流，也要将读取IO流的操作放在 if 外面，或者丢到一个线程中单独执行
//            防止在不用阻塞等待子进程返回结果时，子进程的输出流没有及时读取，导致系统缓冲区填满，使得子进程输出阻塞。

//            或者被调用的子进程【不要】将IO流输出定位到 console 上也可以，因为子进程只有console的流会自动定位到父进程
//            如下所示, 不读取子进程的IO流，导致日志打印到console的阻塞问题

//            "main" #1 prio=5 os_prio=0 tid=0x0000000002023800 nid=0x1870 runnable [0x0000000                                                                                                                                                                                               001fde000]
//            java.lang.Thread.State: RUNNABLE
//            at java.io.FileOutputStream.writeBytes(Native Method)
//            at java.io.FileOutputStream.write(FileOutputStream.java:326)
//            at java.io.BufferedOutputStream.write(BufferedOutputStream.java:122)
//            - locked <0x00000000ec521838> (a java.io.BufferedOutputStream)
//            at java.io.PrintStream.write(PrintStream.java:480)
//            - locked <0x00000000ec521818> (a java.io.PrintStream)
//            at sun.nio.cs.StreamEncoder.writeBytes(StreamEncoder.java:221)
//            at sun.nio.cs.StreamEncoder.implFlushBuffer(StreamEncoder.java:291)
//            at sun.nio.cs.StreamEncoder.implFlush(StreamEncoder.java:295)
//            at sun.nio.cs.StreamEncoder.flush(StreamEncoder.java:141)
//            - locked <0x00000000ec5c29d8> (a java.io.OutputStreamWriter)
//            at java.io.OutputStreamWriter.flush(OutputStreamWriter.java:229)
//            at java.util.logging.StreamHandler.flush(StreamHandler.java:259)
//            - locked <0x00000000ec5b9648> (a java.util.logging.ConsoleHandler)
//            at java.util.logging.ConsoleHandler.publish(ConsoleHandler.java:117)
//            at java.util.logging.Logger.log(Logger.java:738)
//            at java.util.logging.Logger.doLog(Logger.java:765)
//            at java.util.logging.Logger.logp(Logger.java:931)
//            at org.apache.juli.logging.DirectJDKLog.log(DirectJDKLog.java:180)
//            at org.apache.juli.logging.DirectJDKLog.info(DirectJDKLog.java:123)


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

        public StreamPumper(InputStream is, StringBuffer message, String name) {
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
