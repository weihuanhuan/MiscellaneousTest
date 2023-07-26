package SystemTest.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParentProcessCommand {

    public static String WORK_DIR = System.getProperty("user.dir");

    public static String TMP_DIR = System.getProperty("java.io.tmpdir");

    public static boolean autoExit = false;

    public static int seconds = 3;

    public static boolean shutdownExecutor = false;

    public static boolean redirect = true;

    public static boolean capture = true;

    public static int block = 128;

    public static int count = 8 * 1024 * 8; //mb

    public static List<String> parseCommand(String[] args) {
        System.out.println("################################ ParentProcessCommand ################################");
        System.out.println("System.getProperty(\"user.dir\")=" + WORK_DIR);
        System.out.println("System.getProperty(\"java.io.tmpdir\")=" + TMP_DIR);

        File workDirFile = new File(WORK_DIR);

        File featureJar = new File(workDirFile, "feature/target/feature.jar");
        String featureJarAbsolutePath = featureJar.getAbsolutePath();

        // using default value from field value
        List<String> cmds = new ArrayList<>();
        cmds.add("autoExit");
        cmds.add(String.valueOf(autoExit));
        cmds.add("seconds");
        cmds.add(String.valueOf(seconds));
        cmds.add("shutdownExecutor");
        cmds.add(String.valueOf(shutdownExecutor));
        cmds.add("redirect");
        cmds.add(String.valueOf(redirect));
        cmds.add("capture");
        cmds.add(String.valueOf(capture));
        cmds.add("java");
//        cmds.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=28031");
        cmds.add("-cp");
        cmds.add(featureJarAbsolutePath);
        cmds.add("SystemTest.process.LongTermProcess");
        cmds.add(String.valueOf(block));
        cmds.add(String.valueOf(count));

        // using config value from args value
        if (args != null && args.length > 0) {
            cmds = Arrays.asList(args);
        }
        System.out.println("args=" + Arrays.deepToString(args));
        System.out.println("cmds=" + cmds);

        //just test, rough handle parent process auto exit config
        int offset = 0;
        List<String> finalCMD = cmds;
        if ("autoExit".equals(cmds.get(0))) {
            String autoExitStr = cmds.get(1);
            autoExit = Boolean.parseBoolean(autoExitStr);
            offset = offset + 2;

            if ("seconds".equals(cmds.get(2))) {
                String shutdownThreadPoolStr = cmds.get(3);
                seconds = Integer.parseInt(shutdownThreadPoolStr);
                offset = offset + 2;
            }

            if ("shutdownExecutor".equals(cmds.get(4))) {
                String shutdownThreadPoolStr = cmds.get(5);
                shutdownExecutor = Boolean.parseBoolean(shutdownThreadPoolStr);
                offset = offset + 2;
            }

            if ("redirect".equals(cmds.get(6))) {
                String redirectStr = cmds.get(7);
                redirect = Boolean.parseBoolean(redirectStr);
                offset = offset + 2;
            }

            if ("capture".equals(cmds.get(8))) {
                String captureStr = cmds.get(9);
                capture = Boolean.parseBoolean(captureStr);
                offset = offset + 2;
            }
        }
        finalCMD = cmds.subList(offset, cmds.size());

        String argumentFormat = String.format("autoExit=[%s], seconds=[%s], shutdownExecutor=[%s], redirect=[%s], capture=[%s]"
                , autoExit, seconds, shutdownExecutor, redirect, capture);
        System.out.println(argumentFormat);
        System.out.println("finalCMD=" + finalCMD);
        return finalCMD;
    }

}


