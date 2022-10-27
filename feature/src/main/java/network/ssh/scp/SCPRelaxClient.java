
package network.ssh.scp;

import com.trilead.ssh2.Connection;
import com.trilead.ssh2.SCPClient;
import com.trilead.ssh2.Session;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Logger;

public class SCPRelaxClient {

    private Logger logger = Logger.getLogger(this.getClass().getName());

    private Connection conn;
    private SCPClient scpClient;

    private boolean cleanImpurity = true;
    private boolean onlyLineImpurity = true;
    private int cleanImpurityTimeout = 3000;
    private int maxImpurityLengthInByte = 1024 * 1024;

    public SCPRelaxClient(Connection connection) throws IOException {
        this.conn = connection;
        this.scpClient = connection.createSCPClient();
    }

    public SCPRelaxClient(Connection connection, boolean onlyLineImpurity) throws IOException {
        this(connection);
        this.onlyLineImpurity = onlyLineImpurity;
        logger.info(String.format("create relax scp client：%s", this));
    }

    /**
     * Modified from  SCPClient, provider ability for relax read response.
     *
     * @see SCPClient#readResponse(java.io.InputStream)
     */
    private void readResponse(InputStream is) throws IOException {
        int c = is.read();
        if (c == 0) {
            return;
        }

        if (c == -1) {
            throw new IOException("Remote scp terminated with error code " + c);
        }

        if (c == 2 || c == 1) {
            String errInfo = receiveLine(is);
            throw new IOException("Remote scp terminated with error (" + errInfo + ").");
        }

        //meeting illegal response code, decide how to do with this case.
        if (!cleanImpurity) {
            throw new IOException("Remote scp terminated with error code " + c);
        }

        //构建杂质的第一个字节的信息
        char cChar = (char) c;
        String impurityLine = new String(new char[]{cChar});

        //when relax case, we should ignore all impurity data
        //这里有两种情况，
        //一种是杂质的数据都是一行一行的，比如命令 echo 之类的输出，【echo ""】输出一个空行，echo 会自动添加换行信息。
        //另一种是杂质数据可能没有行信息，比如系统调用 printf() 函数的输出，【printf a】输出一个 a 字符，printf不会自动换行信息。
        if (onlyLineImpurity) {
            //检测获取到的杂质数据是否是一个行，
            //如果不是，则将这个杂质行的余下杂质数据读取完毕；如果是，则无需继续读取，只要接着处理下一行数据即可。
            if (c != '\n') {
                impurityLine = impurityLine + receiveLine(is);
            }
            logger.info(String.format("read impurity line: %s", impurityLine));
            //递归调用继续处理剩余的数据，直到寻找到真正的 scp 响应数据。
            readResponse(is);
        } else {
            byte[] impurityData = new byte[maxImpurityLengthInByte];
            int impurityLength = readAllImpurityData(is, impurityData, cleanImpurityTimeout);

            //构建其余的所有杂质字节的信息，
            //其中的最后一个字节被认为时 scp 协议的响应报文的首字节，他暗示一个响应代码,所以不因包括在杂质信息中。
            impurityLine = impurityLine + new String(impurityData, 0, impurityLength - 1);
            logger.info(String.format("read all impurity data: %s", impurityLine));

            //由于我们一次性将所有的杂质数据都读了出来，所以我们这里无需递归调用，只需要判断最终的响应代码是否合法即可。
            if (checkAllImpurityData(impurityData, impurityLength)) {
                return;
            }
        }
    }

    private int readAllImpurityData(InputStream is, byte[] bytes, int timeout) throws IOException {
        return readInputStreamWithTimeout(is, bytes, timeout);
    }

    private int readInputStreamWithTimeout(InputStream is, byte[] impurityData, int timeoutMillis)
            throws IOException {
        int bufferOffset = 0;
        long maxTimeMillis = System.currentTimeMillis() + timeoutMillis;
        while (System.currentTimeMillis() < maxTimeMillis && bufferOffset < impurityData.length) {
            int readLength = Math.min(is.available(), impurityData.length - bufferOffset);
            // can alternatively use bufferedReader, guarded by isReady():
            int readResult = is.read(impurityData, bufferOffset, readLength);
            if (readResult == -1)
                throw new IOException("Remote scp terminated unexpectedly.");
            bufferOffset += readResult;
        }
        return bufferOffset;
    }

    private boolean checkAllImpurityData(byte[] impurityData, int impurityLength) throws IOException {
        int lastImpurityByte = impurityData[impurityLength - 1];
        if (lastImpurityByte == 0) {
            return true;
        }

        if (lastImpurityByte == 1 || lastImpurityByte == 2) {
            throw new IOException("Remote scp terminated with error code " + lastImpurityByte);
        }
        return true;
    }

    /**
     * Same as SCPClient.
     *
     * @see SCPClient#receiveLine(java.io.InputStream)
     */
    private String receiveLine(InputStream is) throws IOException {
        StringBuffer sb = new StringBuffer(30);

        while (true) {
            /*
             * This is a random limit - if your path names are longer, then
             * adjust it
             */

            if (sb.length() > 8192)
                throw new IOException("Remote scp sent a too long line");

            int c = is.read();

            if (c < 0)
                throw new IOException("Remote scp terminated unexpectedly.");

            if (c == '\n')
                break;

            sb.append((char) c);

        }
        return sb.toString();
    }

    /**
     * Using original scp client
     *
     * @see SCPClient
     */
    public void putOriginal(String localFile, String remoteTargetDirectory) throws IOException {
        scpClient.put(localFile, remoteTargetDirectory);
    }

    /**
     * Using relax scp client
     *
     * @see SCPClient#put(java.lang.String, java.lang.String)
     */
    public void putRelax(String localFile, String remoteTargetDirectory) throws IOException {
        String[] localFiles = {localFile};
        String mode = "0600";
        String[] remoteFiles = null;
        put(localFiles, remoteFiles, remoteTargetDirectory, mode);
    }

    /**
     * Same as SCPClient, by this method wo can invoke custom sendFiles method.
     *
     * @see SCPClient#put(java.lang.String[], java.lang.String[], java.lang.String, java.lang.String)
     */
    private void put(String[] localFiles, String[] remoteFiles, String remoteTargetDirectory, String mode)
            throws IOException {
        Session sess = null;

        /*
         * remoteFiles may be null, indicating that the local filenames shall be
         * used
         */

        if ((localFiles == null) || (remoteTargetDirectory == null) || (mode == null))
            throw new IllegalArgumentException("Null argument.");

        if (mode.length() != 4)
            throw new IllegalArgumentException("Invalid mode.");

        for (int i = 0; i < mode.length(); i++)
            if (Character.isDigit(mode.charAt(i)) == false)
                throw new IllegalArgumentException("Invalid mode.");

        if (localFiles.length == 0)
            return;

        remoteTargetDirectory = remoteTargetDirectory.trim();
        remoteTargetDirectory = (remoteTargetDirectory.length() > 0) ? remoteTargetDirectory : ".";

        String cmd = "scp -t -d " + remoteTargetDirectory;

        for (int i = 0; i < localFiles.length; i++) {
            if (localFiles[i] == null)
                throw new IllegalArgumentException("Cannot accept null filename.");
        }

        try {
            sess = conn.openSession();
            sess.execCommand(cmd);
            sendFiles(sess, localFiles, remoteFiles, mode);
        } catch (IOException e) {
            throw (IOException) new IOException("Error during SCP transfer.").initCause(e);
        } finally {
            if (sess != null)
                sess.close();
        }
    }

    /**
     * Relax scp client send file implement,
     * by this method wo can invoke custom read response rather than original private method from SCPClient.
     *
     * @see SCPClient#sendFiles(com.trilead.ssh2.Session, java.lang.String[], java.lang.String[], java.lang.String)
     */
    private void sendFiles(Session sess, String[] files, String[] remoteFiles, String mode) throws IOException {
        byte[] buffer = new byte[8192];

        OutputStream os = new BufferedOutputStream(sess.getStdin(), 40000);
        InputStream is = new BufferedInputStream(sess.getStdout(), 512);

        readResponse(is);

        for (int i = 0; i < files.length; i++) {
            File f = new File(files[i]);
            long remain = f.length();

            String remoteName;

            if ((remoteFiles != null) && (remoteFiles.length > i) && (remoteFiles[i] != null))
                remoteName = remoteFiles[i];
            else
                remoteName = f.getName();

            String cline = "C" + mode + " " + remain + " " + remoteName + "\n";

            os.write(cline.getBytes("ISO-8859-1"));
            os.flush();

            readResponse(is);

            FileInputStream fis = null;

            try {
                fis = new FileInputStream(f);

                while (remain > 0) {
                    int trans;
                    if (remain > buffer.length)
                        trans = buffer.length;
                    else
                        trans = (int) remain;

                    if (fis.read(buffer, 0, trans) != trans)
                        throw new IOException("Cannot read enough from local file " + files[i]);

                    os.write(buffer, 0, trans);

                    remain -= trans;
                }
            } finally {
                if (fis != null)
                    fis.close();
            }

            os.write(0);
            os.flush();

            readResponse(is);
        }

        os.write("E\n".getBytes("ISO-8859-1"));
        os.flush();
    }

    @Override
    public String toString() {
        return "SCPRelaxClient{" +
                "cleanImpurity=" + cleanImpurity +
                ", onlyLineImpurity=" + onlyLineImpurity +
//                ", cleanImpurityTimeout=" + cleanImpurityTimeout +
//                ", maxImpurityLengthInByte=" + maxImpurityLengthInByte +
                '}';
    }
}
