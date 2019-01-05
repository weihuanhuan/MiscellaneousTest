package extendTest;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by JasonFitch on 1/5/2019.
 */
public class AsyncRecordExtend extends LogRecord{
    /**
     * Construct a LogRecord with the given level and message values.
     * <p>
     * The sequence property will be initialized with a new unique value.
     * These sequence values are allocated in increasing order within a VM.
     * <p>
     * The millis property will be initialized to the current time.
     * <p>
     * The thread ID property will be initialized with a unique ID for
     * the current thread.
     * <p>
     * All other properties will be initialized to "null".
     *
     * @param level a logging level value
     * @param msg   the raw non-localized logging message (may be null)
     */
    public AsyncRecordExtend(Level level, String msg) {
        super(level, msg);
    }

    private String threadName;

    public String getThreadName() {
        return threadName;
    }

    public void setThreadName(String threadName) {
        this.threadName = threadName;
    }

    @Override
    public String getLoggerName() {
        return super.getLoggerName();
    }

    @Override
    public void setLoggerName(String name) {
        super.setLoggerName(name);
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return super.getResourceBundle();
    }

    @Override
    public void setResourceBundle(ResourceBundle bundle) {
        super.setResourceBundle(bundle);
    }

    @Override
    public String getResourceBundleName() {
        return super.getResourceBundleName();
    }

    @Override
    public void setResourceBundleName(String name) {
        super.setResourceBundleName(name);
    }

    @Override
    public Level getLevel() {
        return super.getLevel();
    }

    @Override
    public void setLevel(Level level) {
        super.setLevel(level);
    }

    @Override
    public long getSequenceNumber() {
        return super.getSequenceNumber();
    }

    @Override
    public void setSequenceNumber(long seq) {
        super.setSequenceNumber(seq);
    }

    @Override
    public String getSourceClassName() {
        return super.getSourceClassName();
    }

    @Override
    public void setSourceClassName(String sourceClassName) {
        super.setSourceClassName(sourceClassName);
    }

    @Override
    public String getSourceMethodName() {
        return super.getSourceMethodName();
    }

    @Override
    public void setSourceMethodName(String sourceMethodName) {
        super.setSourceMethodName(sourceMethodName);
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }

    @Override
    public void setMessage(String message) {
        super.setMessage(message);
    }

    @Override
    public Object[] getParameters() {
        return super.getParameters();
    }

    @Override
    public void setParameters(Object[] parameters) {
        super.setParameters(parameters);
    }

    @Override
    public int getThreadID() {
        return super.getThreadID();
    }

    @Override
    public void setThreadID(int threadID) {
        super.setThreadID(threadID);
    }

    @Override
    public long getMillis() {
        return super.getMillis();
    }

    @Override
    public void setMillis(long millis) {
        super.setMillis(millis);
    }

    @Override
    public Throwable getThrown() {
        return super.getThrown();
    }

    @Override
    public void setThrown(Throwable thrown) {
        super.setThrown(thrown);
    }
}
