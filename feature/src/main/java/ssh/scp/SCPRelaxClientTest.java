package ssh.scp;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.ConnectionInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

public class SCPRelaxClientTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        String hostname = "192.168.88.150";
        int port = 22;

        File workDir = new File(System.getProperty("user.dir"));
        File testFile = new File(workDir, "feature/src/main/java/ssh/scp/SCPRelaxClientTest.java");

        String testFilePath = testFile.getAbsolutePath();
        String remoteFileDir = "/root/scp_testdir";

        //原始连接进行登录认证
        Connection connection = new Connection(hostname, port);
        ConnectionInfo connectionInfo = connection.connect();
        boolean root = connection.authenticateWithPassword("root", "123456");

        //SCP连接用来创建自定义的SCP客户端，用来特殊处理SCP协议。
        //这里主要是用来对 scp 命令执行时，有能力兼容处理 bash 登录或者连接时存在输出内容的情况。
        SCPRelaxConnection relaxConnection = new SCPRelaxConnection(connection);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        int returnCode = relaxConnection.loginExec("mkdir " + remoteFileDir, arrayOutputStream);
        String returnMessage = new String(arrayOutputStream.toByteArray());
        System.out.println("returnCode:" + returnCode);
        System.out.println("returnMessage:" + returnMessage);

        System.out.println("######### testOnlyLineImpurity ###########");
        testOnlyLineImpurity(relaxConnection, testFilePath, remoteFileDir);

        System.out.println("######### testNotOnlyLineImpurity ###########");
        testNotOnlyLineImpurity(relaxConnection, testFilePath, remoteFileDir);

    }

    public static void testOnlyLineImpurity(SCPRelaxConnection relaxConnection, String testFilePath, String remoteFileDir) throws IOException {
        SCPRelaxClient onlyLineImpurityRelaxClient = relaxConnection.createSCPRelaxClient();

        System.out.println("######### onlyLineImpurityRelaxClient.putOriginal ###########");
        //默认 trilead 实现的 SCPClient 在存在 bash 的杂质输出时会传输错误，他将这些杂质当成了 scp 报文来处理了。
        try {
            onlyLineImpurityRelaxClient.putOriginal(testFilePath, remoteFileDir);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("######### onlyLineImpurityRelaxClient.putRelax ###########");
        //而对于 relax 的 SCPClient 来说，默认设置为 OnlyLineImpurity ，那么其只过滤以整行出现的的杂质，
        //当杂质最后不是以换行结尾时，比如 【printf a】产生的杂质，他将阻塞在 SCPRelaxClient.receiveLine() 方法上。
        try {
            onlyLineImpurityRelaxClient.putRelax(testFilePath, remoteFileDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void testNotOnlyLineImpurity(SCPRelaxConnection relaxConnection, String testFilePath, String remoteFileDir) throws IOException {
        SCPRelaxClient scpRelaxClientNotOnlyLine = relaxConnection.createSCPRelaxClient(false);

        //而对于 relax 的 SCPClient 来说，如果设置为非 OnlyLineImpurity ，那么其会过滤所有出现的的杂质，
        //虽然这样子不会由于考虑杂质是否以行结尾造成阻塞，但是由于 printf 可以输出各种奇奇怪怪的数据，使得 scp 协议错误判断的几率大增。
        try {
            System.out.println("######### scpRelaxClientNotOnlyLine.putRelax ###########");
            scpRelaxClientNotOnlyLine.putRelax(testFilePath, remoteFileDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
