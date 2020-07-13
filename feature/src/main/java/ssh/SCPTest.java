package ssh;

import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;
import com.trilead.ssh2.Session;
import com.trilead.ssh2.StreamGobbler;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by JasonFitch on 7/11/2020.
 */
public class SCPTest {

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
        String cmd = mkdir + " " + targetDir;

        Connection connection = new Connection(hostname, port);

        //java.lang.IllegalStateException: Cannot create SCP client, you need to establish a connection first.
        connection.connect(null, 1000 * 5, 1000 * 5);

        //java.lang.IllegalStateException: Cannot create SCP client, connection is not authenticated.
        connection.authenticateWithPassword(username, password);

        //java.io.IOException: Remote scp terminated with error (scp: /root/scp_java: No such file or directory).
        Session session = connection.openSession();
        session.execCommand(cmd);
        processSessionStream(session);
        printSessionStatus(session);

        //java.io.IOException: A remote execution has already started.
//        session.execCommand(cmd);
//        processSessionStream(session);
//        printSessionStatus(session);

        session.close();
        System.out.println("---------------- ssh command -----------------------");

        //String cmd = "scp -t -d " + remoteTargetDirectory;
        //Source and sink modes are triggered using -f (from) and -t (to) options, respectively.
        //These options are for internal usage only and aren't documented.
        //There is also the 3rd hidden option, -d, when the target is expected to be a directory.
        //https://chuacw.ath.cx/blogs/chuacw/archive/2019/02/04/how-the-scp-protocol-works.aspx
        SCPClient scpClient = connection.createSCPClient();
        scpClient.put(sourceFile, targetDir);
        processSessionStream(session);
        printSessionStatus(session);
        System.out.println("---------------- scp command -----------------------");
    }

    private static void printSessionStatus(Session session) {
        int waitForCondition = session.waitForCondition(ChannelCondition.EXIT_STATUS, 1000 * 5);
        System.out.println("waitForCondition: " + waitForCondition);

        Integer exitStatus = session.getExitStatus();
        System.out.println("exitStatus: " + exitStatus);
    }


    private static void processSessionStream(Session session) throws IOException {
//        OutputStream stdin = session.getStdin();
        InputStream stdout = session.getStdout();
        InputStream stderr = session.getStderr();

        readStream(stdout);
        System.out.println("---- session.getStdout() ----");
        readStream(stderr);
        System.out.println("---- session.getStderr() ----");
    }

    private static void readStream(InputStream inputStream) throws IOException {
        byte[] bytes = new byte[1024];
        StreamGobbler streamGobbler = new StreamGobbler(inputStream);
        int length;
        while (-1 != (length = streamGobbler.read(bytes, 0, 1024))) {
            System.out.println(new String(bytes, 0, length));
        }
    }

}
