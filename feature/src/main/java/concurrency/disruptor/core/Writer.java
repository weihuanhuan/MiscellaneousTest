package concurrency.disruptor.core;

import java.io.BufferedWriter;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicInteger;

public class Writer {

    protected PrintWriter writer;
    protected Charset charset = StandardCharsets.ISO_8859_1;
    protected AtomicInteger counter = new AtomicInteger(0);

    public Writer(File file) {
        try {
            writer = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true), charset), 128000),
                    false);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public synchronized void writerMessage(final CharArrayWriter message) {
        try {
            message.writeTo(writer);
            counter.incrementAndGet();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public AtomicInteger getCounter() {
        return counter;
    }

    public synchronized void close() {
        writer.close();
    }

}
