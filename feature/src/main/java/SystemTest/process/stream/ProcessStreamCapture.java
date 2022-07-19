package SystemTest.process.stream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class ProcessStreamCapture {

    private final ProcessBuilderExecutor processBuilderExecutor;

    private ProcessStreamCollector stdoutStreamCollector;
    private ProcessStreamCollector stderrStreamCollector;

    private Charset charset = StandardCharsets.UTF_8;

    public ProcessStreamCapture(ProcessBuilderExecutor processBuilderExecutor) {
        this.processBuilderExecutor = processBuilderExecutor;
    }

    public void capture() throws IOException {
        ProcessStreamRedirect processStreamRedirect = processBuilderExecutor.getProcessStreamRedirect();
        Process process = processBuilderExecutor.getProcess();

        InputStream inputStream;
        if (processStreamRedirect == null) {
            inputStream = process.getInputStream();
        } else if (!processStreamRedirect.isRedirectOut()) {
            inputStream = process.getInputStream();
        } else {
            File stdoutFile = processStreamRedirect.getStdoutFile();
            inputStream = Files.newInputStream(stdoutFile.toPath(), StandardOpenOption.READ);
        }

        InputStream errorStream;
        if (processStreamRedirect == null) {
            errorStream = process.getErrorStream();
        } else if (processStreamRedirect.isMergeStderrAndStdout()) {
            errorStream = null;
        } else if (!processStreamRedirect.isRedirectErr()) {
            errorStream = process.getErrorStream();
        } else {
            File stderrFile = processStreamRedirect.getStderrFile();
            errorStream = Files.newInputStream(stderrFile.toPath(), StandardOpenOption.READ);
        }

        stdoutStreamCollector = new ProcessStreamCollector(inputStream);
        stderrStreamCollector = new ProcessStreamCollector(errorStream);
    }

    public void start() {
        String processName = processBuilderExecutor.getProcessName();
        startCollector(stdoutStreamCollector, processName + "-stdout");
        startCollector(stderrStreamCollector, processName + "-stderr");
    }

    private void startCollector(ProcessStreamCollector reader, String threadName) {
        if (reader == null) {
            return;
        }

        Thread thread = new Thread(reader, threadName);
        thread.setDaemon(true);
        thread.start();
    }

    public void stop() {
        stopCollector(stdoutStreamCollector);
        stopCollector(stderrStreamCollector);
    }

    private void stopCollector(ProcessStreamCollector collector) {
        if (collector != null) {
            collector.stop();
        }
    }

    public String readStdout() {
        return stdoutStreamCollector.getOutputStreamAsString(charset);
    }

    public String readStderr() {
        return stderrStreamCollector.getOutputStreamAsString(charset);
    }

    public Charset getCharset() {
        return charset;
    }

    public void setCharset(Charset charset) {
        this.charset = charset;
    }

}