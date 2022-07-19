package SystemTest.process.stream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class ProcessStreamCapture {

    private final Process process;

    private InputStream inputStream;
    private InputStream errorStream;

    public ProcessStreamCapture(Process process) {
        this.process = process;
    }

    public void capture(ProcessStreamRedirect processStreamRedirect) throws IOException {
        if (processStreamRedirect != null && processStreamRedirect.isRedirectOut()) {
            File stdoutFile = processStreamRedirect.getStdoutFile();
            inputStream = Files.newInputStream(stdoutFile.toPath(), StandardOpenOption.READ);
        } else {
            inputStream = process.getInputStream();
        }

        if (processStreamRedirect != null && processStreamRedirect.isRedirectErr()) {
            File stderrFile = processStreamRedirect.getStderrFile();
            errorStream = Files.newInputStream(stderrFile.toPath(), StandardOpenOption.READ);
        } else {
            errorStream = process.getErrorStream();
        }

        new ProcessStreamReader(inputStream);
    }

    public void stop() {
    }

    public String readStdout() {
        return null;
    }

    public String readStderr() {
        return null;
    }


}


