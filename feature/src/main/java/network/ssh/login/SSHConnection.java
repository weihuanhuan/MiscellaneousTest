package network.ssh.login;

import com.trilead.ssh2.ChannelCondition;
import com.trilead.ssh2.Connection;
import com.trilead.ssh2.Session;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

/**
 * Created by JasonFitch on 9/14/2020.
 * 内容参考：com.trilead.ssh2.Connection
 * 工程信息：trilead-ssh2:build-217-jenkins-22-SNAPSHOT
 */
public class SSHConnection {

    private static byte[] BASH_EXIT_BYTES = "exit".getBytes(Charset.forName("ISO-8859-1"));
    private static byte[] NEW_LINE_BYTES = "\n".getBytes(Charset.forName("ISO-8859-1"));

    private Connection connection;

    public SSHConnection(Connection connection) {
        if (connection == null) {
            throw new NullPointerException("Connection cannot null");
        }
        this.connection = connection;
    }

    public void close() {
        if (connection != null) {
            connection.close();
        }
    }

    /**
     * Executes a process with login and pty remotely and blocks until its completion.
     */
    public int loginWithPTYExec(String term, String command, OutputStream output) throws IOException, InterruptedException {
        Session session = connection.openSession();
        try {
            if (term == null || term.isEmpty()) {
                session.requestDumbPTY();
            } else {
                session.requestPTY(term);
            }
            return doLoginExec(session, command, output);
        } finally {
            session.close();
        }
    }

    /**
     * Executes a process with login remotely and blocks until its completion.
     */
    public int loginExec(String command, OutputStream output) throws IOException, InterruptedException {
        Session session = connection.openSession();
        try {
            return doLoginExec(session, command, output);
        } finally {
            session.close();
        }
    }

    /**
     * Executes a process with login remotely and blocks until its completion.
     *
     * @param command the command
     * @param output  The stdout/stderr will be sent to this stream.
     * @return the int
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    private int doLoginExec(Session session, String command, OutputStream output) throws IOException, InterruptedException {
        session.startShell();
        OutputStream stdin = session.getStdin();
        stdin.write(command.getBytes());
        stdin.write(NEW_LINE_BYTES);
        stdin.flush();

        stdin.write(BASH_EXIT_BYTES);
        stdin.write(NEW_LINE_BYTES);
        stdin.flush();
        stdin.close();

        Integer r = processOutAndErrStream(session, output);
        if (r != null) return r.intValue();
        return -1;
    }

    /**
     * Executes a process remotely with non-login and blocks until its completion.
     *
     * @param command the command
     * @param output  The stdout/stderr will be sent to this stream.
     * @return the int
     * @throws IOException          the io exception
     * @throws InterruptedException the interrupted exception
     */
    public int nonLoginExec(String command, OutputStream output) throws IOException, InterruptedException {
        Session session = connection.openSession();
        try {
            session.execCommand(command);

            Integer r = processOutAndErrStream(session, output);
            if (r != null) return r.intValue();
            return -1;
        } finally {
            session.close();
        }
    }

    private Integer processOutAndErrStream(Session session, OutputStream output) throws IOException, InterruptedException {
        PumpThread t1 = new PumpThread(session.getStdout(), output);
        t1.start();
        PumpThread t2 = new PumpThread(session.getStderr(), output);
        t2.start();
        session.getStdin().close();
        t1.join();
        t2.join();

        // wait for some time since the delivery of the exit status often gets delayed
        session.waitForCondition(ChannelCondition.EXIT_STATUS, 3000);
        Integer r = session.getExitStatus();
        return r;
    }

    /**
     * Pumps {@link InputStream} to {@link OutputStream}.
     *
     * @author Kohsuke Kawaguchi
     */
    private static final class PumpThread extends Thread {
        private final InputStream in;
        private final OutputStream out;

        /**
         * Instantiates a new Pump thread.
         *
         * @param in  the in
         * @param out the out
         */
        public PumpThread(InputStream in, OutputStream out) {
            super("pump thread");
            this.in = in;
            this.out = out;
        }

        public void run() {
            byte[] buf = new byte[1024];
            try {
                while (true) {
                    int len = in.read(buf);
                    if (len < 0) {
                        in.close();
                        return;
                    }
                    out.write(buf, 0, len);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Connection getConnection() {
        return connection;
    }
}
