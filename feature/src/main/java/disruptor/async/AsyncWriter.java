package disruptor.async;

import disruptor.core.Writer;
import java.io.CharArrayWriter;
import java.io.File;
import java.util.concurrent.TimeUnit;

public class AsyncWriter extends Writer {

    private static final AsyncWriterDisruptor loggerDisruptor = new AsyncWriterDisruptor();

    private final ThreadLocal<RingBufferLogEventTranslator> threadLocalTranslator = new ThreadLocal<>();

    private transient boolean start;

    public AsyncWriter(File file) {
        super(file);
        loggerDisruptor.start();
        start = true;
    }

    private RingBufferLogEventTranslator getCachedTranslator() {
        RingBufferLogEventTranslator result = threadLocalTranslator.get();
        if (result == null) {
            result = new RingBufferLogEventTranslator();
            threadLocalTranslator.set(result);
        }
        return result;
    }

    public void queueMessage(final char[] message) {
        if (!start) {
            return;
        }
        logWithThreadLocalTranslator(message);
    }

    private void logWithThreadLocalTranslator(final char[] message) {
        final RingBufferLogEventTranslator translator = getCachedTranslator();
        translator.setBasicValues(this, message);
        publish(translator);
    }

    private void publish(final RingBufferLogEventTranslator translator) {
        if (!loggerDisruptor.tryPublish(translator)) {
            loggerDisruptor.enqueueLogMessageInfo(translator);
        }
    }

    public void actualWriterMessage(final RingBufferLogEvent event) {
        char[] message = event.getMessage();
        writer.write(message, 0, message.length);
    }

    @Override
    public void writerMessage(CharArrayWriter message) {
        queueMessage(message.toCharArray());
    }

    public synchronized void close() {
        if (start) {
            loggerDisruptor.stop(10, TimeUnit.SECONDS);
            start = false;
        }
        super.close();
    }

}
