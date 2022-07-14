package SystemTest.Runtime;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class ProcessBuilderTest {

    public static String WORK_DIR = System.getProperty("user.dir");

    public static void main(String[] args) throws InterruptedException {
        File workDirFile = new File(WORK_DIR);

        File startBat = new File(workDirFile, "script/maven/executor/start.bat");
        String startBatAbsolutePath = startBat.getAbsolutePath();
        System.out.println("startBat.getAbsolutePath()=" + startBatAbsolutePath);

        try {
            String subOutputFileStr = "subOutput.log";
            File subOutputFile = new File(subOutputFileStr);
            if (!subOutputFile.exists()) {
                subOutputFile.createNewFile();
            }
            String subOutputFileAbsolutePath = subOutputFile.getAbsolutePath();
            System.out.println("subOutputFile.getAbsolutePath()=" + subOutputFileAbsolutePath);

            String cmdExecute = "\"cmd\", \"/C\", \"tree C:/ /f\"";
            String executable = startBatAbsolutePath;
            ProcessBuilder processBuilder = new ProcessBuilder(executable);

            Map<String, String> envs = processBuilder.environment();
            envs.put("ENVKEY", "ENVVALUE");

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