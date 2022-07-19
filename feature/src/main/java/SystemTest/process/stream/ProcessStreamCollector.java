package SystemTest.process.stream;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

public class ProcessStreamCollector implements Runnable {

    private static final int DEFAULT_BUFFER_SIZE = 4096;

    private final InputStream inputStream;
    private final ByteArrayOutputStream outputStream;

    private long readInterval = 100;
    private boolean flushImmediately = true;

    private byte[] buffer;
    private boolean stopped;

    public ProcessStreamCollector(InputStream inputStream) {
        this.inputStream = inputStream;
        this.outputStream = new ByteArrayOutputStream();
    }

    @Override
    public void run() {
        try {
            if (inputStream == null) {
                return;
            }

            buffer = new byte[DEFAULT_BUFFER_SIZE];

            while (!isFinish()) {
                collect();
            }
        } finally {
            closeSilent(outputStream);
            closeSilent(inputStream);
        }
    }

    public void stop() {
        stopped = true;
    }

    private boolean isFinish() {
        return stopped || Thread.currentThread().isInterrupted();
    }

    private void collect() {
        try {
            int available = inputStream.available();
            if (available > 0) {
                int read = read(available);
                write(read);
            } else {
                TimeUnit.MILLISECONDS.sleep(readInterval);
            }
        } catch (IOException e) {
            throw new ProcessStreamException(e);
        } catch (InterruptedException ignored) {
            Thread.currentThread().interrupt();
        }
    }

    private int read(int available) throws IOException {
        int min = Math.min(available, buffer.length);
        return inputStream.read(buffer, 0, min);
    }

    private synchronized void write(int read) throws IOException {
        outputStream.write(buffer, 0, read);

        if (flushImmediately) {
            outputStream.flush();
        }
    }

    public synchronized String getOutputStreamAsString(Charset charset) {
        try {
            return outputStream.toString(charset.name());
        } catch (UnsupportedEncodingException e) {
            throw new ProcessStreamException(e);
        }
    }

    private void closeSilent(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException ignored) {
        }
    }

    public boolean isFlushImmediately() {
        return flushImmediately;
    }

    public void setFlushImmediately(boolean flushImmediately) {
        this.flushImmediately = flushImmediately;
    }

    public long getReadInterval() {
        return readInterval;
    }

    public void setReadInterval(long readInterval) {
        this.readInterval = readInterval;
    }

}