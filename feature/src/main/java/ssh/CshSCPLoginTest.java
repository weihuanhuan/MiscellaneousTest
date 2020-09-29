package ssh;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;
import com.trilead.ssh2.Session;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by JasonFitch on 7/11/2020.
 */
public class CshSCPLoginTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        File workDir = new File(System.getProperty("user.dir"));
        File testFile = new File(workDir, "feature/src/main/java/ssh/SCPTest.java");
        System.out.println(testFile.getCanonicalPath());
        System.out.println("------------------------------------------------------------------");

        String hostname = "192.168.88.150";
        int port = 22;

        String username = "whh";
        String password = "123456";

        String targetDir = "/home/whh/scp_test";
        String sourceFile = testFile.getCanonicalPath();

        Connection connection = new Connection(hostname, port);
        connection.connect(null, 1000 * 5, 1000 * 5);
        connection.authenticateWithPassword(username, password);

        //对于 csh 来说其 shell 执行的返回结果是绝对的最后一条命令执行结果，
        //如果执行了 exit 推出 csh，那么其结果也是 exit 命令的结果，这里要注意 csh 的行为和 bash 不同。
        //而对于 bash 而言，其 shell 执行的返回结果是最后一条非 exit 命令的执行结果，
        //具体参看 man csh 关于 exit status 以及 exit 命令的描述
        //从 man csh 中我们了解到了 csh 真正返回值是 exit [expr] 命令形式的 【expr】的计算结果，
        //如果【expr】缺失那么 csh 就返回 【0】作为 csh 的退出状态代码。
        //所以当我们在执行 exit 命令时最好使用 【exit $?】的形式，将我们需要的命令返回结果正确的带回。
        //特别是对于 csh 的这种默认省略【expr】而返回为 【0】的情况
        boolean executeExit = true;
        //返回状态是【0】，这个退出状态和 bash 不一致，其实 exit 命令的状态，其始终为【0】
        testFileExist(connection, targetDir, executeExit);
        //返回状态是【1】，这个退出状态和 bash 是一致的，都是 test 命令的状态，其依据文件的存在情况可能为【0】或者【1】
        testFileExist(connection, targetDir, !executeExit);

        System.out.println("---------------- ssh command -----------------------");

        SCPClient scpClient = connection.createSCPClient();
        scpClient.put(sourceFile, targetDir);
        System.out.println("---------------- scp command -----------------------");

        connection.close();
        System.out.println("---------------- connection close -----------------------");
    }

    public static void testFileExist(Connection connection, String targetDir, boolean executeExit) throws IOException {
        Session session = connection.openSession();
        session.startShell();

        OutputStream stdin = session.getStdin();
        stdin.write(("test -d " + targetDir).getBytes());
        stdin.write("\n".getBytes());
        stdin.flush();

        //在不执行 exit 命令时需要手动模拟临时掉线的状况，即客户端输入流断开，等效终端窗口关闭。
        //否则 csh 会一直阻塞等待，直到 ssh 进程结束，他才能拿到命令执行的结果，
        if (!executeExit) {
            stdin.close();
            //另外注意只有当一条命令执行完之后立即获取该命令的执行结果，我们才能正确的取得命令的返回值。
            SCPLoginTest.processSessionStream(session);
            SCPLoginTest.printSessionStatus(session);
        }

        //而在执行 exit 时，如果我们在 exit 命令之后获取 session 中返回的响应结果时不对的。
        //另外执行了 exit 命令后虽然 stdin 会被服务端关闭的，我们仍然要在客户端执行 stdin.close 来结束流的使用。
        if (executeExit) {
            stdin.write("exit".getBytes());
            stdin.write("\n".getBytes());
            stdin.flush();

            //这里不论真正的 test 命令的执行是否成功，我们都只能取得返回状态代码为【0】零，
            //这是由于返回状态针对的是最后一条命令的结果，因此我们此时取回的是 exit 的结果，而不是上面 test 命令的结果
            stdin.close();
            SCPLoginTest.processSessionStream(session);
            SCPLoginTest.printSessionStatus(session);
        }

        session.close();
    }

}
