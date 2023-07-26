package SystemTest.process;

import SystemTest.process.task.ParentProcessExit;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class ProcessBaseTest {

    public static ProcessBuilder childProcessBuilderPrepare(String[] args) {
        List<String> finalCMD = ParentProcessCommand.parseCommand(args);

        System.out.println("################################ childProcessBuilderPrepare ################################");
        File workDirFile = new File(ParentProcessCommand.WORK_DIR);

        ProcessBuilder processBuilder = new ProcessBuilder(finalCMD);

        //指定 process 的 work dir 防止生成的 redirect log file 默认占用 temp 目录的空间
        processBuilder.directory(workDirFile);

        List<String> command = processBuilder.command();
        System.out.println("processBuilder.command()=" + command);
        File directory = processBuilder.directory();
        System.out.println("processBuilder.directory()=" + directory);
        return processBuilder;
    }

    public static void parentProcessExit(ExecutorService executorService) {
        try {
            ParentProcessExit parentProcessExit;
            if (ParentProcessCommand.autoExit) {
                parentProcessExit = new ParentProcessExit(executorService, ParentProcessCommand.seconds, ParentProcessCommand.shutdownExecutor);
                //自动控制 ParentProcess 是否退出
                executorService.submit(parentProcessExit);
            } else {
                //手动控制 ParentProcess 是否退出
                int read = System.in.read();
                System.out.println(ProcessBaseTest.class.getSimpleName() + ": read=" + read);

                //手动控制时 ParentProcess 立马退出，不进行等待，所以 seconds 固定为 0 值就行了。
                parentProcessExit = new ParentProcessExit(executorService, 0, ParentProcessCommand.shutdownExecutor);
                parentProcessExit.run();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


