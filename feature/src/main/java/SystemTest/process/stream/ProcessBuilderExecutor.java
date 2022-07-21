package SystemTest.process.stream;

import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ProcessBuilderExecutor implements Callable<Process> {

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

        this.processName = processName == null ? "" : processName;
        this.processBuilder = processBuilder;
    }

    @Override
    public Process call() throws Exception {
        return start();
    }

    public Process start() throws IOException, InterruptedException {
        if (redirectStream) {
            redirectStream();
        }

        process = processBuilder.start();

        if (captureStream) {
            startCapture();
        }

        if (waitFor) {
            waitFor();
        }
        return process;
    }

    private void waitFor() throws InterruptedException {
        if (timeout > 0) {
            boolean waitBoolean = process.waitFor(timeout, TimeUnit.MILLISECONDS);
        } else {
            int waitInt = process.waitFor();
        }
    }

    private void redirectStream() {
        processStreamRedirect = new ProcessStreamRedirect(this);
        processStreamRedirect.redirect();
    }

    public void cleanRedirect() {
        if (processStreamRedirect == null) {
            return;
        }
        processStreamRedirect.clean();
    }

    private void startCapture() throws IOException {
        processStreamCapture = new ProcessStreamCapture(this);
        processStreamCapture.capture();
        processStreamCapture.start();
    }

    public void stopCapture() {
        if (processStreamCapture == null) {
            return;
        }
        processStreamCapture.stop();
    }

    public String getStdoutMessage() {
        if (processStreamCapture == null) {
            return null;
        }
        return processStreamCapture.readStdout();
    }

    public String getStderrMessage() {
        if (processStreamCapture == null) {
            return null;
        }
        return processStreamCapture.readStderr();
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

    public String getProcessName() {
        return processName;
    }

    public ProcessBuilder getProcessBuilder() {
        return processBuilder;
    }

    public ProcessStreamRedirect getProcessStreamRedirect() {
        return processStreamRedirect;
    }

    public Process getProcess() {
        return process;
    }

}


