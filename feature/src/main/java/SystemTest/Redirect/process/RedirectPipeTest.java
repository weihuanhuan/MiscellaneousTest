package SystemTest.Redirect.process;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by JasonFitch on 9/19/2018.
 */
public class RedirectPipeTest {

    public static String WORK_DIR = System.getProperty("user.dir");

    /**
     * subprocess stream default to {@link java.lang.ProcessBuilder.Redirect#PIPE}
     * handing by {@link ProcessImpl#start(String[], Map, String, ProcessBuilder.Redirect[], boolean)}
     * <p>
     * {@link SystemTest.process.LongTermProcess#main(java.lang.String[])}
     */
    public static void main(String[] args) throws IOException, InterruptedException {
        File workDirFile = new File(WORK_DIR);
        File featureJar = new File(workDirFile, "feature/target/feature.jar");
        String featureJarAbsolutePath = featureJar.getAbsolutePath();

        List<String> cmds = new ArrayList<>();
        cmds.add("java");
//        cmds.add("-agentlib:jdwp=transport=dt_socket,server=y,suspend=y,address=28031");
        cmds.add("-cp");
        cmds.add(featureJarAbsolutePath);
        cmds.add("SystemTest.process.LongTermProcess");

        // 128 * 8 bytes = 1kb, 最终是 1kb * 1024 * 1024 共 1GB 数据，以保证填充满 父进程 和 子进程 PIPE 之间的缓冲区
        cmds.add(String.valueOf(128));
        cmds.add(String.valueOf(8 * 1024 * 1024));

        System.out.println("workDirFile=" + workDirFile);
        System.out.println("cmds=" + cmds);

        ProcessBuilder processBuilder = new ProcessBuilder(cmds);
        Process subProcess = processBuilder.start();

        // stdout/stderr of subprocess, we can read from it.
        // 该子进程的 main 方法中，其使用异步线程一直向其 stdout/stderr 中写出数据，
        // 而在 PIPE 模式下，这些数据会暂时写到 父进程 的缓冲区中
        // 这保证了我们在 java.lang.ProcessBuilder.start 后，即使没来得急调用 read 也不会丢失子进程的数据。
        // 但是由于 父进程 的缓冲区是存在大小限制的，所以如果在 父进程 中，一直不读取该缓冲区的数据，那么在缓冲区被填满之后，子进程就会卡在 write 操作了。
        InputStream inputStream = subProcess.getInputStream();
        InputStream errorStream = subProcess.getErrorStream();

        // stdin of subprocess, we can write into it.
        OutputStream outputStream = subProcess.getOutputStream();

        //read exit string from stdin of parent process
        String exitString = inputExitString(System.in);
        // 子进程的 main 方法中，调用了 java.io.InputStream.read() 所以要让其退出需要向其 stdin 中写入至少一个字节数据才行。
        System.out.printf("notice subprocess exit by writing [%s] string to its stdin.%n", exitString);
        outputStream.write(exitString.getBytes());
        // 默认情况下 java.lang.Process.getOutputStream 是存在缓冲区的，
        // 所以我们必须调用 java.io.OutputStream.flush 以使得我们写入的 exitString 立即发送到 subprocess ，否则 subprocess 的 stdin 是读不到这个信息的
        outputStream.flush();

        // 默认永久性的阻塞在等待子进程退出
        // 不过这里 子进程 是永远不会退出的，因为
        // 1. 子进程向父进程的 PIPE 写入了大量的数据，而父进程有没有进行读取，这导致父子进程间接的缓冲区满了，所以子进程当前卡在了 write 调用上，无法结束。
        // 2. 子进程执行 write 的线程并没有设置 java.lang.Thread.setDaemon 为 true ，此时虽然 main 线程收到 exitString 退出了，但还存在 非daemon 线程等待结束。
        int waitFor = subProcess.waitFor();
        System.out.println("waitFor=" + waitFor);
    }

    private static String inputExitString(InputStream in) {
        Scanner scanner = new Scanner(in);

        String inputLine = null;
        while (scanner.hasNext()) {
            inputLine = scanner.nextLine();
            if (inputLine == null || inputLine.isEmpty()) {
                continue;
            }

            System.out.println("inputLine=" + inputLine);
            if ("exit".equals(inputLine)) {
                break;
            }
        }
        return inputLine;
    }

}
