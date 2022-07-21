package SystemTest.process.stream;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProcessStreamRedirect {

    private static final Logger LOGGER = Logger.getLogger(ProcessStreamRedirect.class.getName());

    private static final String tempDir = System.getProperty("java.io.tmpdir");

    private static final String STDIN_LOG = "stdin.log";
    private static final String STDOUT_LOG = "stdout.log";
    private static final String STDERR_LOG = "stderr.log";

    private final ProcessBuilderExecutor processBuilderExecutor;

    private boolean redirectIn = false;
    private boolean redirectOut = true;
    private boolean redirectErr = true;
    private boolean mergeStderrAndStdout = false;

    private File stdinFile;
    private File stdoutFile;
    private File stderrFile;

    public ProcessStreamRedirect(ProcessBuilderExecutor processBuilderExecutor) {
        this.processBuilderExecutor = processBuilderExecutor;
    }

    public void redirect() {
        String processName = processBuilderExecutor.getProcessName();
        ProcessBuilder processBuilder = processBuilderExecutor.getProcessBuilder();

        File directory = processBuilder.directory();
        if (directory == null) {
            directory = new File(tempDir);
        }

        processBuilder.redirectErrorStream(mergeStderrAndStdout);

        if (redirectIn) {
            stdinFile = createFile(processName, STDIN_LOG, directory);
            processBuilder.redirectInput(ProcessBuilder.Redirect.from(stdinFile));
        }
        if (redirectOut) {
            stdoutFile = createFile(processName, STDOUT_LOG, directory);
            processBuilder.redirectOutput(ProcessBuilder.Redirect.to(stdoutFile));
        }
        if (redirectErr && !mergeStderrAndStdout) {
            stderrFile = createFile(processName, STDERR_LOG, directory);
            processBuilder.redirectError(ProcessBuilder.Redirect.to(stderrFile));
        }
    }

    private File createFile(String processName, String logfile, File directory) {
        String absolutePath = directory.getAbsolutePath();
        String fileName = processName + "-" + logfile;
        Path path = Paths.get(absolutePath, fileName);
        return path.toFile();
    }

    public void clean() {
        if (redirectIn) {
            deleteFile(stdinFile);
        }
        if (redirectOut) {
            deleteFile(stdoutFile);
        }
        if (redirectErr && !mergeStderrAndStdout) {
            deleteFile(stderrFile);
        }
    }

    private void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }

        if (!file.isFile()) {
            String format = String.format("not allow to delete directory named [%s], it should be a file!", file.getAbsolutePath());
            LOGGER.log(Level.WARNING, format);
            return;
        }

        boolean delete = file.delete();
        if (!delete) {
            String format = String.format("failed to delete log file named [%s]!", file.getAbsolutePath());
            LOGGER.log(Level.WARNING, format);
        }
    }

    public boolean isRedirectIn() {
        return redirectIn;
    }

    public void setRedirectIn(boolean redirectIn) {
        this.redirectIn = redirectIn;
    }

    public boolean isRedirectOut() {
        return redirectOut;
    }

    public void setRedirectOut(boolean redirectOut) {
        this.redirectOut = redirectOut;
    }

    public boolean isRedirectErr() {
        return redirectErr;
    }

    public void setRedirectErr(boolean redirectErr) {
        this.redirectErr = redirectErr;
    }

    public boolean isMergeStderrAndStdout() {
        return mergeStderrAndStdout;
    }

    public void setMergeStderrAndStdout(boolean mergeStderrAndStdout) {
        this.mergeStderrAndStdout = mergeStderrAndStdout;
    }

    public File getStdinFile() {
        return stdinFile;
    }

    public File getStdoutFile() {
        return stdoutFile;
    }

    public File getStderrFile() {
        return stderrFile;
    }

}


