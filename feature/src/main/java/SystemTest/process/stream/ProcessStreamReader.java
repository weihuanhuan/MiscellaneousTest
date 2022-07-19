package SystemTest.process.stream;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class ProcessStreamReader implements Runnable {

    private static final int DEFAULT_BUFFER_SIZE = 8192;

    private boolean stopped;
    private final InputStream inputStream;

    private long readInterval = 1000;

    private byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

    public ProcessStreamReader(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        while (!stopped && Thread.currentThread().isInterrupted()) {

            try {
                int available = inputStream.available();
                if (available > 0) {
                    int min = Math.min(available, buffer.length);
                    int read = inputStream.read(buffer, 0, min);
                }

                TimeUnit.MILLISECONDS.sleep(readInterval);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void stop() {
        stopped = true;
    }

    public long getReadInterval() {
        return readInterval;
    }

    public void setReadInterval(long readInterval) {
        this.readInterval = readInterval;
    }
}


