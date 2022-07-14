package SystemTest.Runtime;


import java.io.File;
import java.io.IOException;

public class ProcessTest {

    public static String WORK_DIR = System.getProperty("user.dir");

    public static void main(String[] args) {
        File workDirFile = new File(WORK_DIR);

        File startBat = new File(workDirFile, "script/maven/executor/start.bat");
        String startBatAbsolutePath = startBat.getAbsolutePath();
        System.out.println("startBat.getAbsolutePath()=" + startBatAbsolutePath);

        try {
            String executable = startBatAbsolutePath;

            if (args.length == 0 || args[0].equals("")) {
                System.out.println("startFile from internal");
                ExecUtils.exec(executable, null, false);

            } else {
                System.out.println("startFile from external");
                String[] newArr = new String[args.length - 1];
                System.arraycopy(args, 1, newArr, 0, args.length - 1);
                ExecUtils.startServer(args[0], newArr);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            System.out.println("please press enter to continue.");
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
