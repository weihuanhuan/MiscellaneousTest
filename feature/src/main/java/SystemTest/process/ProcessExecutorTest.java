package SystemTest.process;

import SystemTest.process.stream.ProcessBuilderExecutor;
import SystemTest.process.stream.ProcessStreamRedirect;
import SystemTest.process.task.ParentProcessExit;
import SystemTest.process.task.ProcessMonitor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public class ProcessExecutorTest {

    public static void main(String[] args) throws InterruptedException {
        List<String> finalCMD = ParentProcessCommand.parseCommand(args);
        File workDirFile = new File(ParentProcessCommand.WORK_DIR);

        try {
            ProcessBuilder processBuilder = new ProcessBuilder(finalCMD);

            //指定 process 的 work dir 防止生成的 redirect log file 默认占用 temp 目录的空间
            processBuilder.directory(workDirFile);

            List<String> command = processBuilder.command();
            System.out.println("processBuilder.command()=" + command);
            File directory = processBuilder.directory();
            System.out.println("processBuilder.directory()=" + directory);

            //使用 ProcessBuilderExecutor 运行
            ProcessBuilderExecutor processBuilderExecutor = new ProcessBuilderExecutor("process-", processBuilder);
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

            if (ParentProcessCommand.autoExit) {
                ParentProcessExit parentProcessExit = new ParentProcessExit(executorService, ParentProcessCommand.seconds, ParentProcessCommand.shutdownExecutor);
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
                if (processStreamRedirect != null) {
                    File stdinFile = processStreamRedirect.getStdinFile();
                    System.out.println("processStreamRedirect.getStdinFile()=" + stdinFile);
                    File stdoutFile = processStreamRedirect.getStdoutFile();
                    System.out.println("processStreamRedirect.getStdoutFile()=" + stdoutFile);
                    File stderrFile = processStreamRedirect.getStderrFile();
                    System.out.println("processStreamRedirect.getStderrFile()=" + stderrFile);
                }

                //第一次查询数据
                String stdoutMessage = processBuilderExecutor.getStdoutMessage();
                String stdoutMessageLastLinesAsString = ProcessExecutorHelper.getLastLinesAsString(stdoutMessage, 5);
                System.out.println(stdoutMessageLastLinesAsString);

                String stderrMessage = processBuilderExecutor.getStderrMessage();
                String stderrMessageLastLinesAsString = ProcessExecutorHelper.getLastLinesAsString(stderrMessage, 5);
                System.err.println(stderrMessageLastLinesAsString);

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
            }
        }
    }

}


