package SystemTest.process;

import SystemTest.process.stream.ProcessBuilderExecutor;
import SystemTest.process.task.ProcessMonitor;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ProcessBuilderExecutorShortTermProcessTest extends ProcessBaseTest {

    public static void main(String[] args) throws InterruptedException, IOException {
        // override default value
        // disable redirect into file.
        ParentProcessCommand.redirect = false;
        // turn LongTermProcess into a ShortTermProcess
        ParentProcessCommand.count = 1;

        ProcessBuilder processBuilder = childProcessBuilderPrepare(args);

        // 在关闭重定向的基础上，手动重新向 stdin 到 File 中
        // 用于防止 LongTermProcess 进程阻塞在 java.io.InputStream.read()  上，使其可以快速退出，成为 ShortTermProcess
        File tempStdinFile = File.createTempFile("process-", "-stdin.log", processBuilder.directory());
        System.out.println(tempStdinFile);
        System.out.println(tempStdinFile.canRead());
        processBuilder.redirectInput(ProcessBuilder.Redirect.from(tempStdinFile));

        System.out.println("################################ ProcessExecutorTest ################################");
        //使用 ProcessBuilderExecutor 运行
        ProcessBuilderExecutor processBuilderExecutor = new ProcessBuilderExecutor("process", processBuilder);
        //控制 ProcessBuilderExecutor 的行为
        processBuilderExecutor.setRedirectStream(ParentProcessCommand.redirect);
        processBuilderExecutor.setCaptureStream(ParentProcessCommand.capture);

        ExecutorService executorService = Executors.newFixedThreadPool(5);

        Future<Process> submit = executorService.submit(processBuilderExecutor);

        try {
            Process process = submit.get();
            System.out.println(process);

            ProcessMonitor processMonitor = new ProcessMonitor(process);
            executorService.submit(processMonitor);

            ProcessBuilderExecutorOperate processBuilderExecutorOperate = new ProcessBuilderExecutorOperate(processBuilderExecutor);
            executorService.submit(processBuilderExecutorOperate);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        parentProcessExit(executorService);
    }

    private static class ProcessBuilderExecutorOperate implements Runnable {

        private final ProcessBuilderExecutor processBuilderExecutor;

        public ProcessBuilderExecutorOperate(ProcessBuilderExecutor processBuilderExecutor) {
            this.processBuilderExecutor = processBuilderExecutor;
        }

        @Override
        public void run() {
            try {
                // 不等待数据收集，用于测试一个短期进程执行时，在其快速结束的场景下，我们是否能够读到进程的全部输出
                // 这里我们测试是存在问题由于 ProcessStreamCollector.readInterval 的默认值是 100ms ，而这个短期进程的执行时间仅仅是 5ms 左右
                // 所以有可能 ProcessStreamCollector 在 sleep readInterval 期间，短期进程就执行完毕了，其就会直接使用 waitFor 后面的方法了。
                // 然后 parent process 在随后的获取 message 时就会拿不到相应的 stdout/stderr 中的消息。
                // 这里的解决方案如下
                // 1. 检索 ProcessStreamCollector.readInterval 为 0 ，使其能够快速读取，缺点是 cpu 空转，且获取 message 时也可能不一定读取完毕
                // 2. //TODO 确保 java.io.InputStream.read(byte[], int, int) 返回 -1 时，也即流读取结束时，我们再调用获取 message 的相关方法。
//                TimeUnit.SECONDS.sleep(1);

                // waitFor 短期进程结束，使得我们可以完全的读取进程中的所有数据,
                // 这里注意即使 child process 已经 exit 了，其 stdout 对于 parent process 来说依旧是可读的。
                Process process = processBuilderExecutor.getProcess();
                boolean waitFor = process.waitFor(10, TimeUnit.SECONDS);

                // 注意这里是不能直接关闭 child process 的 stdout 的, 因为此时我们的 ProcessStreamCollector.collect 线程还在执行对该流的数据收集
                // 如果我们在这里提前将 child process 的 stdout 关闭了，jdk 会将其内部 java.io.BufferedInputStream.getInIfOpen 所用的input stream 置为 null
                // 由于我们这里还没有调用 ProcessBuilderExecutor.stopCapture, 所以我们的 ProcessStreamCollector.collect 在随后的执行中，就会抛出如下异常
                // Caused by: java.io.IOException: Stream closed
                // 这里我们所注释的代码中，使用了 trr-with-resource 语句，其回自动的关闭流。
                System.out.println("################################ line ################################");
//                try (InputStreamReader inputStreamReader = new InputStreamReader(process.getInputStream());
//                     BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
//                    String line;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        System.out.println(line);
//                        System.out.flush();
//                    }
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }

                System.out.println("################################ message ################################");
                processBuilderExecutor.stopCapture();

                //正确的输出长度应为 320 = 10(data prefix) + 128(data) + 2(windows println 的 crlf)
                //                      + 24(System.in.read 的信息输出) + 2(windows println 的 crlf)
                //                      + 2(2个流的统计) * 【 75(PrintTask 的统计输出) + 2(windows println 的 crlf) 】
                //                   = 140 + 26 + 154
                String stdoutMessage = processBuilderExecutor.getStdoutMessage();
                System.out.println("stdoutMessage=" + stdoutMessage);
                System.out.println("stdoutMessage.length()=" + stdoutMessage.length());
                System.out.flush();

                //正确的输出长度应为 140 = 10(data prefix) + 128(data) + 2(windows println 的 crlf)
                String stderrMessage = processBuilderExecutor.getStderrMessage();
                System.err.println("stderrMessage=" + stderrMessage);
                System.err.println("stderrMessage.length()=" + stderrMessage.length());
                System.err.flush();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

}


