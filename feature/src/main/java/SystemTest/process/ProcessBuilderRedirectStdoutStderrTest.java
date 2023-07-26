package SystemTest.process;

import SystemTest.process.task.ProcessMonitor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ProcessBuilderRedirectStdoutStderrTest extends ProcessBaseTest {

    public static void main(String[] args) throws InterruptedException {
        ProcessBuilder processBuilder = childProcessBuilderPrepare(args);

        System.out.println("################################ ProcessStdoutStderrTest ################################");
        try {
            File directory = processBuilder.directory();
            if (ParentProcessCommand.redirect) {
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

                //默认情况下 SystemTest.process.stream.ProcessStreamRedirect.redirectIn 为 false, 但这里手动对其 redirectInput 了
                // 这使得 SystemTest.process.LongTermProcess.main 中的 java.io.InputStream.read() 方法是可以有 input 来读取的。
                // 只不过该文件的内容为 0 byte 大小，所以其读到的值为 -1，即 input 结束了 ，故其会使得该 child process 的 main 线程也跟着结束
                // 再加之其 stdout/stderr 的线程的执行，都是非 daemon 的，所以一旦其输出任务完成，整个 child process 就会结束
                File tempStdinFile = File.createTempFile("process-", "-stdin.log", directory);
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

            parentProcessExit(executorService);
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

}


