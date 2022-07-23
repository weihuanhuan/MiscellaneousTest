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
    private static final int DEFAULT_MIX_BYTE_ARRAY_BUFFER_SIZE = 8192;

    private final InputStream inputStream;
    private ByteArrayOutputStream outputStream;

    private long readInterval = 100;
    private boolean readOnly = false;
    private boolean flushImmediately = true;

    private byte[] buffer;

    //volatile 提供可见性的保证，当其被其他线程调用 stop 修改后，其余的线程能够看见最新的状态
    //这可以防止本地线程仅仅使用其 copy 的 stopped 缓冲值，而不从主内存实时读取其他线程潜在更新的值，最后导致本线程不会感知到对 stop 的调用。
    private volatile boolean stopped;

    public ProcessStreamCollector(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    public void run() {
        try {
            if (inputStream == null) {
                return;
            }

            buffer = new byte[DEFAULT_BUFFER_SIZE];
            if (!readOnly) {
                //reduce data copy backed by byte[] object when grow byte[] length.
                outputStream = new ByteArrayOutputStream(DEFAULT_MIX_BYTE_ARRAY_BUFFER_SIZE);
            }

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
            //Returns an estimate of the number of bytes that can be read (or
            //     * skipped over) from this input stream without blocking by the next
            //     * invocation of a method for this input stream. The next invocation
            //     * might be the same thread or another thread.  A single read or skip of this
            //     * many bytes will not block, but may read or skip fewer bytes.
            //Note that while some implementations of {@code InputStream} will return
            //     * the total number of bytes in the stream, many will not.  It is
            //     * never correct to use the return value of this method to allocate
            //     * a buffer intended to hold all data in this stream.
            int available = inputStream.available();
            if (available > 0) {
                int read = read(available);

                //the total number of bytes read into the buffer, or
                //-1 if there is no more data because the end of the stream has been reached.
                if (read < 0) {
                    stop();
                    return;
                }

                if (!readOnly) {
                    write(read);
                }
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
        //Reads up to <code>len</code> bytes of data from the input stream into
        //     * an array of bytes.  An attempt is made to read as many as
        //     * <code>len</code> bytes, but a smaller number may be read.
        //     * The number of bytes actually read is returned as an integer.
        //     *
        //<p> This method blocks until input data is available, end of file is
        //     * detected, or an exception is thrown.
        //<p> If <code>len</code> is zero, then no bytes are read and
        //     * <code>0</code> is returned; otherwise, there is an attempt to read at
        //     * least one byte. If no byte is available because the stream is at end of
        //     * file, the value <code>-1</code> is returned; otherwise, at least one
        //     * byte is read and stored into <code>b</code>.
        int min = Math.min(available, buffer.length);
        return inputStream.read(buffer, 0, min);
    }

    // synchronized 是为了防止在 getOutputStreamAsString 时 write 数据
    private synchronized void write(int read) throws IOException {
        if (outputStream == null) {
            return;
        }

        outputStream.write(buffer, 0, read);

        if (flushImmediately) {
            outputStream.flush();
        }
    }

    public synchronized String getOutputStreamAsString(Charset charset) {
        if (outputStream == null) {
            return null;
        }

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

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

}