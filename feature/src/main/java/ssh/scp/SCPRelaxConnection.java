package ssh.scp;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;
import ssh.login.SSHConnection;

import java.io.IOException;

public class SCPRelaxConnection extends SSHConnection {

    public SCPRelaxConnection(Connection connection) {
        super(connection);
    }

    /**
     * Create a modified SCPClient, named SCPRelaxClient, that is a relax scp protocol client.
     * This method also create original SCPClient in order to check status for origin connection.
     *
     * @return A SCPRelaxClient object.
     * @see SCPClient
     */
    public synchronized SCPRelaxClient createSCPRelaxClient(boolean onlyLineImpurity) throws IOException {
        Connection connection = getConnection();
        if (connection == null) {
            throw new IllegalArgumentException("original connection cannot null!");
        }
        return new SCPRelaxClient(connection, onlyLineImpurity);
    }

    public synchronized SCPRelaxClient createSCPRelaxClient() throws IOException {
        return createSCPRelaxClient(true);
    }

}
