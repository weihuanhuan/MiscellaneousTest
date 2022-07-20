package SystemTest.process.stream;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

public class ProcessStreamCapture {

    private final ProcessBuilderExecutor processBuilderExecutor;

    private ProcessStreamCollector stdoutStreamCollector;
    private ProcessStreamCollector stderrStreamCollector;

    private Charset charset = StandardCharsets.UTF_8;

    public ProcessStreamCapture(ProcessBuilderExecutor processBuilderExecutor) {
        this.processBuilderExecutor = processBuilderExecutor;
    }

    public void capture() throws IOException {
        Process process = processBuilderExecutor.getProcess();
        if (process == null) {
            String processName = processBuilderExecutor.getProcessName();
            String format = String.format("process cannot be null, Please start the process named [%s] first!", processName);
            throw new ProcessStreamException(format);
        }

        ProcessStreamRedirect processStreamRedirect = processBuilderExecutor.getProcessStreamRedirect();

        InputStream inputStream = getInputStream(processStreamRedirect, process);
        InputStream errorStream = getErrorStream(processStreamRedirect, process);

        stdoutStreamCollector = new ProcessStreamCollector(inputStream);
        stderrStreamCollector = new ProcessStreamCollector(errorStream);
    }

    private InputStream getInputStream(ProcessStreamRedirect processStreamRedirect, Process process) throws IOException {
        InputStream inputStream;
        if (processStreamRedirect == null) {
            inputStream = process.getInputStream();
        } else if (!processStreamRedirect.isRedirectOut()) {
            inputStream = process.getInputStream();
        } else {
            File stdoutFile = processStreamRedirect.getStdoutFile();
            inputStream = Files.newInputStream(stdoutFile.toPath(), StandardOpenOption.READ);
        }
        return inputStream;
    }

    private InputStream getErrorStream(ProcessStreamRedirect processStreamRedirect, Process process) throws IOException {
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
        return errorStream;
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