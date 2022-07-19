package SystemTest.process.stream;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class ProcessBuilderExecutor {

    private final String processName;
    private final ProcessBuilder processBuilder;

    private boolean redirectStream = true;
    private boolean captureStream = true;

    private boolean waitFor = false;
    private long timeout = -1;

    private ProcessStreamRedirect processStreamRedirect;
    private ProcessStreamCapture processStreamCapture;

    private Process process;

    public ProcessBuilderExecutor(String processName, ProcessBuilder processBuilder) {
        Objects.requireNonNull(processBuilder, "process builder cannot be null!");

        this.processName = processName;
        this.processBuilder = processBuilder;
    }

    public Process start() throws IOException, InterruptedException {
        if (redirectStream) {
            processStreamRedirect = new ProcessStreamRedirect(processName, processBuilder);
            processStreamRedirect.redirect();
        }

        process = processBuilder.start();
        return process;
    }

    public void startCaptureStream() throws IOException {
        if (!captureStream) {
            return;
        }
        processStreamCapture = new ProcessStreamCapture(process);
        processStreamCapture.capture(processStreamRedirect);
    }

    public void waitFor() throws InterruptedException {
        if (!waitFor) {
            return;
        }

        if (timeout > -1) {
            boolean waitBoolean = process.waitFor(timeout, TimeUnit.MILLISECONDS);
        } else {
            int waitInt = process.waitFor();
        }
    }

    public void stopCaptureStream() {
        if (!captureStream) {
            return;
        }
        processStreamCapture.stop();
    }

    public String readStdout() {
        return processStreamCapture.readStdout();
    }

    public String readStderr() {
        return processStreamCapture.readStderr();
    }

    public void cleanRedirectFile() {
        if (!redirectStream) {
            return;
        }
        processStreamRedirect.clean();
    }

    public boolean isRedirectStream() {
        return redirectStream;
    }

    public void setRedirectStream(boolean redirectStream) {
        this.redirectStream = redirectStream;
    }

    public boolean isCaptureStream() {
        return captureStream;
    }

    public void setCaptureStream(boolean captureStream) {
        this.captureStream = captureStream;
    }

    public boolean isWaitFor() {
        return waitFor;
    }

    public void setWaitFor(boolean waitFor) {
        this.waitFor = waitFor;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

}


