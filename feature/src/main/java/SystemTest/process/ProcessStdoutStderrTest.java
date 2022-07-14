package SystemTest.process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ProcessStdoutStderrTest {

    private static boolean autoExit = false;

    private static int seconds = 3;

    private static boolean shutdownExecutor = false;

    public static void main(String[] args) throws InterruptedException {
        try {
            List<String> cmds = new ArrayList<>();
            cmds.add("autoExit");
            cmds.add(String.valueOf(autoExit));
            cmds.add("seconds");
            cmds.add(String.valueOf(seconds));
            cmds.add("shutdownExecutor");
            cmds.add(String.valueOf(shutdownExecutor));
            cmds.add("java");
//            cmds.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=28031");
            cmds.add("-cp");
            cmds.add("F:/JetBrains/IntelliJ IDEA/MiscellaneousTest/feature/target/feature.jar");
            cmds.add("SystemTest.process.LongTermProcess");
            cmds.add(String.valueOf(128));
            cmds.add(String.valueOf(8 * 1024 * 8));//mb

            if (args != null && args.length >= 3) {
                cmds = Arrays.asList(args);
            }

            //just test, rough handle parent process auto exit config
            int offset = 0;
            List<String> finalCMD = cmds;
            if (cmds.size() > 4 && "autoExit".equals(cmds.get(0))) {
                String autoExitStr = cmds.get(1);
                autoExit = Boolean.parseBoolean(autoExitStr);
                String secondsStr = cmds.get(3);
                seconds = Integer.parseInt(secondsStr);
                offset = offset + 4;

                if ("shutdownExecutor".equals(cmds.get(4))) {
                    String shutdownThreadPoolStr = cmds.get(5);
                    shutdownExecutor = Boolean.parseBoolean(shutdownThreadPoolStr);
                    offset = offset + 2;
                }
            }
            finalCMD = cmds.subList(offset, cmds.size());

            ProcessBuilder processBuilder = new ProcessBuilder(finalCMD);

            List<String> command = processBuilder.command();
            System.out.println("processBuilder.command()=" + command);
            File directory = processBuilder.directory();
            System.out.println("processBuilder.directory()=" + directory);
            String property = System.getProperty("java.io.tmpdir");
            System.out.println("System.getProperty(\"java.io.tmpdir\")=" + property);

            //directory 是 null 安全的，其为 null 时，内部使用 java.io.File.TempDirectory.location 来获取 "java.io.tmpdir" 的值
            File tempStdoutFile = File.createTempFile("process-", "-stdout.log", directory);
            System.out.println(tempStdoutFile);
            System.out.println(tempStdoutFile.canWrite());
            //和下面的方法是等价的，本方法内部会构造 Redirect 对象。
            //processBuilder.redirectOutput(tempStdoutFile);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.to(tempStdoutFile));

            File tempStderrFile = File.createTempFile("process-", "-stderr.log", directory);
            System.out.println(tempStderrFile);
            System.out.println(tempStderrFile.canWrite());
            processBuilder.redirectError(ProcessBuilder.Redirect.to(tempStderrFile));

            File tempStdinFile = File.createTempFile("process-", "-stdin.log", directory);
            System.out.println(tempStdinFile);
            System.out.println(tempStdinFile.canRead());
            processBuilder.redirectInput(ProcessBuilder.Redirect.from(tempStdinFile));

            //使用当前进程的 stdout 和 stderr 作为子进程的 stdout 和 stderr
            //但是这样子我们不好区分 stdout 和 stderr 中哪些内容是父进程的，哪些是子进程的，所以这种场景下我们需要考虑别的方案
//            processBuilder.inheritIO();

            ExecutorService executorService = Executors.newFixedThreadPool(3);

            ProcessStarter processStarter = new ProcessStarter(processBuilder);
            Future<Process> submit = executorService.submit(processStarter);

            try {
                Process process = submit.get();
                System.out.println(process);

                ProcessMonitor processMonitor = new ProcessMonitor(process);
                executorService.submit(processMonitor);
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if (autoExit) {
                ParentProcessExit parentProcessExit = new ParentProcessExit(executorService, seconds);
                executorService.submit(parentProcessExit);
            }

            //控制主进程的退出与否
            System.in.read();

            //关闭现场池以结束进程，否则在 read 后，但是由于线程池中还有线程在运行，导致主线程无法退出
            executorService.shutdown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ProcessStarter implements Callable<Process> {

        private final ProcessBuilder processBuilder;

        private Process process;

        public ProcessStarter(ProcessBuilder processBuilder) {
            this.processBuilder = processBuilder;
        }

        @Override
        public Process call() throws Exception {
            process = processBuilder.start();

            //子进程的 stdout， 重定向后为 java.lang.ProcessBuilder$NullInputStream
            InputStream inputStream = process.getInputStream();
            System.out.println(inputStream);
            //子进程的 stderr， 重定向后为 java.lang.ProcessBuilder$NullInputStream
            InputStream errorStream = process.getErrorStream();
            System.out.println(errorStream);
            //子进程的 stdin， 重定向后为 java.lang.ProcessBuilder$NullOutputStream
            OutputStream outputStream = process.getOutputStream();
            System.out.println(outputStream);

            //avoid block main thread
            //process.waitFor();
            //long term process never terminate
            //int exitCode = process.exitValue();
            //System.out.println(exitCode);

            return process;
        }
    }

    private static class ProcessMonitor implements Runnable {

        private final Process process;

        public ProcessMonitor(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            while (true) {
                boolean alive = process.isAlive();
                long currentTimeMillis = System.currentTimeMillis();
                System.out.println("process.isAlive()=" + alive + ", System.currentTimeMillis()=" + currentTimeMillis);
                if (!alive) {
                    break;
                }

                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static class ParentProcessExit implements Runnable {

        private final ExecutorService executorService;

        private final int seconds;

        public ParentProcessExit(ExecutorService executorService, int seconds) {
            this.executorService = executorService;
            this.seconds = seconds;
        }

        @Override
        public void run() {
            try {
                TimeUnit.SECONDS.sleep(seconds);

                if (shutdownExecutor) {
                    executorService.shutdown();
                }

                System.exit(0);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

}


