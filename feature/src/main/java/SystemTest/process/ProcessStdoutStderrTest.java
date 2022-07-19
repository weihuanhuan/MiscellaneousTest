package SystemTest.process;

import SystemTest.process.task.ParentProcessExit;
import SystemTest.process.task.ProcessMonitor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProcessStdoutStderrTest {

    public static void main(String[] args) throws InterruptedException {
        List<String> finalCMD = ParentProcessCommand.parseCommand(args);
        File workDirFile = new File(ParentProcessCommand.WORK_DIR);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(finalCMD);

            processBuilder.directory(workDirFile);

            List<String> command = processBuilder.command();
            System.out.println("processBuilder.command()=" + command);
            File directory = processBuilder.directory();
            System.out.println("processBuilder.directory()=" + directory);
            String property = System.getProperty("java.io.tmpdir");
            System.out.println("System.getProperty(\"java.io.tmpdir\")=" + property);

            if (ParentProcessCommand.redirect) {
                //directory 是 null 安全的，其为 null 时，内部使用 java.io.File.TempDirectory.location 来获取 "java.io.tmpdir" 的值
                File tempStdoutFile = File.createTempFile("process-", "stdout.log", directory);
                System.out.println(tempStdoutFile);
                System.out.println(tempStdoutFile.canWrite());
                //和下面的方法是等价的，本方法内部会构造 Redirect 对象。
                //processBuilder.redirectOutput(tempStdoutFile);
                processBuilder.redirectOutput(ProcessBuilder.Redirect.to(tempStdoutFile));

                File tempStderrFile = File.createTempFile("process-", "stderr.log", directory);
                System.out.println(tempStderrFile);
                System.out.println(tempStderrFile.canWrite());
                processBuilder.redirectError(ProcessBuilder.Redirect.to(tempStderrFile));

                File tempStdinFile = File.createTempFile("process-", "stdin.log", directory);
                System.out.println(tempStdinFile);
                System.out.println(tempStdinFile.canRead());
                processBuilder.redirectInput(ProcessBuilder.Redirect.from(tempStdinFile));
            }

            //使用当前进程的 stdout 和 stderr 作为子进程的 stdout 和 stderr
            //但是这样子我们不好区分 stdout 和 stderr 中哪些内容是父进程的，哪些是子进程的，所以这种场景下我们需要考虑别的方案
//            processBuilder.inheritIO();

            ExecutorService executorService = Executors.newFixedThreadPool(5);

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

            if (ParentProcessCommand.autoExit) {
                ParentProcessExit parentProcessExit = new ParentProcessExit(executorService, ParentProcessCommand.seconds, ParentProcessCommand.shutdownExecutor);
                executorService.submit(parentProcessExit);
            }

            //控制主进程的退出与否
            System.in.read();

            //关闭现场池以结束进程，否则在 read 后，但是由于线程池中还有线程在运行，导致主线程无法退出
            executorService.shutdown();
        } catch (
                IOException e) {
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

}


