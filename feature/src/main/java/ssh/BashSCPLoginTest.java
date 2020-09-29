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
public class BashSCPLoginTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        File workDir = new File(System.getProperty("user.dir"));
        File testFile = new File(workDir, "feature/src/main/java/ssh/SCPTest.java");
        System.out.println(testFile.getCanonicalPath());
        System.out.println("------------------------------------------------------------------");

        String hostname = "192.168.88.150";
        int port = 22;

        String username = "root";
        String password = "123456";

        String targetDir = "/root/scp_test";
        String sourceFile = testFile.getCanonicalPath();

        Connection connection = new Connection(hostname, port);
        connection.connect(null, 1000 * 5, 1000 * 5);
        connection.authenticateWithPassword(username, password);

        //这里注意不同于 csh 的返回代码，对于 bash 来说，是否执行 exit 命令，其返回结果都是和 test 命令一致的。
        //因此对于 bash 而言，其 shell 执行的返回结果是其执行的最后一条非 exit 命令的执行结果。
        //具体参看 man bash 关于 exit status 以及 exit 命令的描述
        //从 man bash 中我们得到了，bash 真正的 exit status code 是 exit [n] 中的值【n】，
        //但是当【n】缺失时他就会使用其执行的最后一条命令的推出状态来替代了。
        //所以当我们在执行 exit 命令时最好使用 【exit $?】的形式，将我们需要的命令返回结果正确的带回。
        boolean executeExit = true;
        //返回状态是【1】
        CshSCPLoginTest.testFileExist(connection, targetDir, executeExit);
        //返回状态是【1】
        CshSCPLoginTest.testFileExist(connection, targetDir, !executeExit);

        System.out.println("---------------- ssh command -----------------------");

        SCPClient scpClient = connection.createSCPClient();
        scpClient.put(sourceFile, targetDir);
        System.out.println("---------------- scp command -----------------------");

        connection.close();
        System.out.println("---------------- connection close -----------------------");
    }

}
