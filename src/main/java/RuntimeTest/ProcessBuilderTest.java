package RuntimeTest;

import java.io.File;
import java.io.IOException;

public class ProcessBuilderTest {

    public static void main(String[] args) throws InterruptedException {
        try {
            String subOutputFileStr = "subOutput.log";
            File   subOutputFile    = new File(subOutputFileStr);
            if (!subOutputFile.exists()) {
                subOutputFile.createNewFile();
            }

            String         cmdExecute     = "\"cmd\", \"/C\", \"tree C:/ /f\"";
            String         execute        = "F:/JetBrains/IntelliJ IDEA/BEStest/maven/executor/start.bat";
            ProcessBuilder processBuilder = new ProcessBuilder(execute);
            processBuilder.redirectErrorStream(true);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.to(subOutputFile));
            System.out.println(processBuilder.redirectOutput());

            Process process = processBuilder.start();
//            主进程结束后子进程依旧运行

//            process.waitFor();
//            主进程阻塞，等待子进程结束或异常中止
//            int exitCode = process.exitValue();
//            System.out.println(exitCode);

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("main over");
    }

}