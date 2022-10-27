package network.ssh;

import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;
import com.trilead.ssh2.Session;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

/**
 * Created by JasonFitch on 7/11/2020.
 */
public class SCPLoginTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        File workDir = new File(System.getProperty("user.dir"));
        File testFile = new File(workDir, "feature/src/main/java/ssh/SCPTest.java");
        System.out.println(testFile.getCanonicalPath());
        System.out.println("------------------------------------------------------------------");

        String hostname = "192.168.88.10";
        int port = 22;

        String username = "root";
        String password = "123456";

        String targetDir = "/root/scp_test";
        String sourceFile = testFile.getCanonicalPath();

        String mkdir = "mkdir";
        String mkdirCommand = mkdir + " " + targetDir;

        Connection connection = new Connection(hostname, port);
        //这里发起对 ssh 的连接，可以在 linux 上通过 pstree 发现 顶层的 sshd 中增加了一个 子sshd 进程
        connection.connect(null, 1000 * 5, 1000 * 5);
        connection.authenticateWithPassword(username, password);

        //打开一个 session 用来和 sshd 交互
        //而 session 本身是一个 sshd 的抽象层，其用来表示在 sshd 的连接上面的某一个会话。
        Session session = connection.openSession();

        //请求一个 pty ，将返回一个类似 xshell 一样的交互式界面，其中包含了提示符，终端显示状态等，如颜色之类的，
        //如果不请求 pty ，那么将仅仅在 stream（in，out，err） 中包含原始的 shell 处理数据
        //这里的请求 pty 其实相当于是启用了一个 interactive shell 。
//        session.requestPTY("vt100", 0, 0, 0, 0, null);

        //开启一个 shell,来执行 bash 命令，可以触发 bash 的 login 脚本，而且执行命令时不是必须需要 pty 的存在。
        //这里其实是在 ssh的 session 中启动了 bash 命令，可以通过在 linux 上的 pstree 发现 sshd 下面增加了 bash 的子进程
        //这个函数实质上是发起了一个 login shell 来执行命令，

        //如果配合上请求 pty 一起使用那么就是 交互式登录 shell 了，这就是和 ssh u@h 的效果一样了。
        session.startShell();

        //执行一个 shell 命令，注意需要我们提供一个换行来标识命令输入完毕，就像在终端中按下回车时一样。
        OutputStream stdin = session.getStdin();
        stdin.write(mkdirCommand.getBytes());
        stdin.write("\n".getBytes());
        stdin.flush();

        //执行另一个 shell 命令，这里是  exit 命令，其可以触发 bash 的 logout 脚本，会结束 ssh channel 进而隐式的结束 stream 。
        //这里其实是在 ssh的 session 中退出了 bash 命令，可以在 linux 通过 pstree 看见 sshd 下面的 bash 进程没了。
        stdin.write("exit".getBytes());
        stdin.write("\n".getBytes());
        stdin.flush();

        //对于 close 的调用相当于 client 突然断掉了连接，所以可以隐式的退出一个 shell，使得从 stream 中的读取数据的动作可以结束，
        //因此只要保证 exit 和 stdin.close 两者至少有一个正常执行，那么 stdout 和 stderr 的读取就可以正常结束
        //同时最好在使用 login shell 时，最后的命令后执行 exit 这样子可以正确的执行从 login 到 logout 的所有 bash 脚本。
        stdin.close();

        //Session.execCommand 模式使用 非交互式非登陆 bash 执行命令，在本地 bash 中他是不会调用 profile 以及 bashrc 系列的文件
        //但是由于他是使用 sshd 远程调用的，那么 bash 在这种特殊情况下会默认加载 .bashrc 的文件。
        //而且使用 Session.startShell 和 Session.execCommand 是相互冲突的，不能一起使用。
        //Exception in thread "main" java.io.IOException: A remote execution has already started.
        //session.execCommand(cmd);

        //读取执行 bash 命令之后 sshd 的 session 所返回的信息以及状态，
        processSessionStream(session);
        printSessionStatus(session);

        //关闭刚刚在 sshd 上面打开的 session，此时 sshd 连接本身并没有断，我们依旧可以在这个连接上继续 connection.openSession 来进行操作
        session.close();
        System.out.println("---------------- ssh command -----------------------");

        SCPClient scpClient = connection.createSCPClient();
        //scp 命令的本质是使用了 非登陆非交互式的 bash 模式
        //scp 的 put 接口会调用 com.trilead.ssh2.Connection.openSession() 以及 com.trilead.ssh2.Session.close()
        scpClient.put(sourceFile, targetDir);
        //在 session 关闭后，session 的状态以及其流中的内容依旧是可以读取的
        processSessionStream(session);
        printSessionStatus(session);
        System.out.println("---------------- scp command -----------------------");


        //这里才会断开 ssh 的连接，可以在 linux 上通过 pstree 发现刚刚 top 层的 sshd 增加的 子sshd进程已经消失了。
        connection.close();
        System.out.println("---------------- connection close -----------------------");
    }

    public static void printSessionStatus(Session session) {
        int waitForCondition = session.waitForCondition(ChannelCondition.EXIT_STATUS, 1000 * 5);
        System.out.println("waitForCondition: " + waitForCondition);

        Integer exitStatus = session.getExitStatus();
        System.out.println("exitStatus: " + exitStatus);
    }

    public static void processSessionStream(Session session) throws IOException {
        InputStream stdout = session.getStdout();
        readStream(stdout);
        System.out.println("---- session.getStdout() ----");

        InputStream stderr = session.getStderr();
        readStream(stderr);
        System.out.println("---- session.getStderr() ----");
    }

    public static void readStream(InputStream inputStream) throws IOException {
        //整行整行的读取，避免 stdout 或者 stderr 中的某行数据被拆分掉，影响结果的观察。
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }
    }

}
