package SystemTest.process;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ParentProcessCommand {

    public static String WORK_DIR = System.getProperty("user.dir");

    public static boolean autoExit = false;

    public static int seconds = 3;

    public static boolean shutdownExecutor = false;

    public static boolean redirect = true;

    public static boolean capture = true;

    public static List<String> parseCommand(String[] args) {
        File workDirFile = new File(WORK_DIR);

        File featureJar = new File(workDirFile, "feature/target/feature.jar");
        String featureJarAbsolutePath = featureJar.getAbsolutePath();

        List<String> cmds = new ArrayList<>();
        cmds.add("autoExit");
        cmds.add(String.valueOf(autoExit));
        cmds.add("seconds");
        cmds.add(String.valueOf(seconds));
        cmds.add("shutdownExecutor");
        cmds.add(String.valueOf(shutdownExecutor));
        cmds.add("redirect");
        cmds.add(String.valueOf(redirect));
        cmds.add("java");
//            cmds.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=28031");
        cmds.add("-cp");
        cmds.add(featureJarAbsolutePath);
        cmds.add("SystemTest.process.LongTermProcess");
        cmds.add(String.valueOf(128));
        cmds.add(String.valueOf(8 * 1024 * 8));//mb

        if (args != null && args.length >= 3) {
            cmds = Arrays.asList(args);
        }

        //just test, rough handle parent process auto exit config
        int offset = 0;
        List<String> finalCMD = cmds;
        if (cmds.size() > 4 && "autoExit".equals(cmds.get(0))) {
            String autoExitStr = cmds.get(1);
            autoExit = Boolean.parseBoolean(autoExitStr);
            String secondsStr = cmds.get(3);
            seconds = Integer.parseInt(secondsStr);
            offset = offset + 4;

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
        return finalCMD;
    }

}


