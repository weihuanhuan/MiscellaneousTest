package SystemTest.process;

import SystemTest.process.helper.ReadFileLastLine;
import SystemTest.process.helper.ReadStringLastLine;
import SystemTest.process.stream.ProcessBuilderExecutor;
import SystemTest.process.stream.ProcessStreamRedirect;
import SystemTest.process.task.ProcessMonitor;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ProcessExecutorTest extends ProcessBaseTest {

    public static void main(String[] args) throws InterruptedException {
        ProcessBuilder processBuilder = childProcessBuilderPrepare(args);

        System.out.println("################################ ProcessExecutorTest ################################");
        //使用 ProcessBuilderExecutor 运行
        ProcessBuilderExecutor processBuilderExecutor = new ProcessBuilderExecutor("process", processBuilder);
        //控制 ProcessBuilderExecutor 的行为
        processBuilderExecutor.setRedirectStream(ParentProcessCommand.redirect);
        processBuilderExecutor.setRedirectStream(ParentProcessCommand.capture);

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
                //等待数据数据收集
                TimeUnit.SECONDS.sleep(1);

                ProcessStreamRedirect processStreamRedirect = processBuilderExecutor.getProcessStreamRedirect();
                System.out.println("##########################################################");
                if (processStreamRedirect != null) {
                    //默认情况下 SystemTest.process.stream.ProcessStreamRedirect.redirectIn 为 false, 所以这里的 stdinFile 为 null ，
                    // 这使得 SystemTest.process.LongTermProcess.main 中的 java.io.InputStream.read() 方法依旧重 parent 中进行读取数据，
                    // 只要当 parent process 不向 child process 的 input 中写数据，那么其就会阻塞在 read 上，并使得 child process 的 main 线程无法结束，
                    // 再加之其 stdout/stderr 的线程的执行，都是非 daemon 的，所以即使其输出任务完成，整个 child process 也不会结束
                    File stdinFile = processStreamRedirect.getStdinFile();
                    System.out.println("processStreamRedirect.getStdinFile()=" + stdinFile);
                    File stdoutFile = processStreamRedirect.getStdoutFile();
                    System.out.println("processStreamRedirect.getStdoutFile()=" + stdoutFile);
                    File stderrFile = processStreamRedirect.getStderrFile();
                    System.out.println("processStreamRedirect.getStderrFile()=" + stderrFile);

                    if (stdoutFile != null) {
                        String stdoutFileLastLinesAsString = ReadFileLastLine.getLastLinesAsString(stdoutFile, 5);
                        System.out.println("stdoutFileLastLinesAsString=" + stdoutFileLastLinesAsString);
                        System.out.flush();
                    }
                    if (stderrFile != null) {
                        String stderrFileLastLinesAsString = ReadFileLastLine.getLastLinesAsString(stderrFile, 5);
                        System.err.println("stderrFileLastLinesAsString=" + stderrFileLastLinesAsString);
                        System.err.flush();
                    }
                }


                System.out.println("##########################################################");
                //第一次查询数据
                String stdoutMessage = processBuilderExecutor.getStdoutMessage();
                String stdoutMessageLastLinesWithLF = ReadStringLastLine.getLastLinesWithLF(stdoutMessage, 5);
                System.out.println("stdoutMessageLastLinesWithLF=" + stdoutMessageLastLinesWithLF);
                System.out.flush();

                String stderrMessage = processBuilderExecutor.getStderrMessage();
                String stderrMessageLastLinesWithLF = ReadStringLastLine.getLastLinesWithLF(stderrMessage, 5);
                System.err.println("stderrMessageLastLinesWithLF=" + stderrMessageLastLinesWithLF);
                System.err.flush();

                //再次等待数据收集
                TimeUnit.SECONDS.sleep(1);

                //停止收集数据
                processBuilderExecutor.stopCapture();

                //这里只是为了消耗时间，理论上 stop capture 之后还回存在一个读取等待周期，这里跳过他
                TimeUnit.SECONDS.sleep(1);

                //第二次查询数据，并比较第二次和第一次的数据，由于此时存在数据收集，他们应该是不相同的数据。
                String stdoutMessage2 = processBuilderExecutor.getStdoutMessage();
                System.out.println("Objects.equals(stdoutMessage, stdoutMessage2)=" + Objects.equals(stdoutMessage, stdoutMessage2));

                String stderrMessage2 = processBuilderExecutor.getStderrMessage();
                System.out.println("Objects.equals(stderrMessage, stderrMessage2)=" + Objects.equals(stderrMessage, stderrMessage2));

                //这里只是为了消耗时间，理论上 stop capture 之后数据不会在被收集了
                TimeUnit.SECONDS.sleep(1);

                //第三次查询数据，并比较第三次和第二次的数据，由于此时没有数据收集了，他们应该是相同的数据。
                String stdoutMessage3 = processBuilderExecutor.getStdoutMessage();
                System.out.println("Objects.equals(stdoutMessage2, stdoutMessage3)=" + Objects.equals(stdoutMessage2, stdoutMessage3));

                String stderrMessage3 = processBuilderExecutor.getStderrMessage();
                System.out.println("Objects.equals(stderrMessage2, stderrMessage3)=" + Objects.equals(stderrMessage2, stderrMessage3));

                //移除重定向文件
                //当重定向的文件正在被进程使用时是无法进行 clean 的，当前的 clean 实现是删除文件
                processBuilderExecutor.cleanRedirect();

            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

}


