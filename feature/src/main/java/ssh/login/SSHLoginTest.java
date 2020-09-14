package ssh.login;

import com.trilead.ssh2.Connection;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by JasonFitch on 7/11/2020.
 */
public class SSHLoginTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        String hostname = "192.168.88.10";
        int port = 22;

        String username = "root";
        String password = "123456";

        Connection connection = new Connection(hostname, port);
        connection.connect(null, 1000 * 5, 1000 * 5);
        connection.authenticateWithPassword(username, password);

        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        SSHConnection SSHConnection = new SSHConnection(connection);

        System.out.println("---------------- login connection login -----------------------");
        String term = "vt100";
        String commandPTY = "ls -l";
        SSHConnection.loginWithPTYExec(term, commandPTY, arrayOutputStream);
        System.out.print(arrayOutputStream.toString());
        arrayOutputStream.reset();

        System.out.println("---------------- login connection login -----------------------");
        String commandLogin = "echo hello-login";
        SSHConnection.loginExec(commandLogin, arrayOutputStream);
        System.out.print(arrayOutputStream.toString());
        arrayOutputStream.reset();

        System.out.println("---------------- login connection non-login -----------------------");
        String commandNonLogin = "echo hello-non-login";
        SSHConnection.nonLoginExec(commandNonLogin, arrayOutputStream);
        System.out.print(arrayOutputStream.toString());
        arrayOutputStream.reset();

        System.out.println("---------------- login connection close -----------------------");
        SSHConnection.close();
    }
}
