package extendTest;

import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by JasonFitch on 1/5/2019.
 */
public class AsyncRecordCompositionaAndExtend extends LogRecord {

    private LogRecord logRecord;
    //对父类相关域的访问都转发到这个成员变量上，使用this

    public AsyncRecordCompositionaAndExtend(LogRecord logRecord, String threadName) {
        //JF 注意这里只是把LogRecord的Level和Message这两项传递给了父类，丢失了其他的信息
        // 所以需要以组合的方式，将其他的信息保留在成员变量中，除非在构造方法里面逐个set到父类中。
        // 这里扩展的意义是为了面向接口编程（JDK的接口）,在记录日志时可以传递LogRecord即可，
        // 扩展解决了接口类型为题，组合成员解决了logRecord传递过来的繁多数据项可以不用在逐个set了。
        super(logRecord.getLevel(), logRecord.getMessage());
        this.logRecord = logRecord;
        this.threadName = threadName;
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
        return logRecord.getLoggerName();
    }

    @Override
    public void setLoggerName(String name) {
        logRecord.setLoggerName(name);
    }

    @Override
    public ResourceBundle getResourceBundle() {
        return logRecord.getResourceBundle();
    }

    @Override
    public void setResourceBundle(ResourceBundle bundle) {
        logRecord.setResourceBundle(bundle);
    }

    @Override
    public String getResourceBundleName() {
        return logRecord.getResourceBundleName();
    }

    @Override
    public void setResourceBundleName(String name) {
        logRecord.setResourceBundleName(name);
    }

    @Override
    public Level getLevel() {
        return logRecord.getLevel();
    }

    @Override
    public void setLevel(Level level) {
        logRecord.setLevel(level);
    }

    @Override
    public long getSequenceNumber() {
        return logRecord.getSequenceNumber();
    }

    @Override
    public void setSequenceNumber(long seq) {
        logRecord.setSequenceNumber(seq);
    }

    @Override
    public String getSourceClassName() {
        return logRecord.getSourceClassName();
    }

    @Override
    public void setSourceClassName(String sourceClassName) {
        logRecord.setSourceClassName(sourceClassName);
    }

    @Override
    public String getSourceMethodName() {
        return logRecord.getSourceMethodName();
    }

    @Override
    public void setSourceMethodName(String sourceMethodName) {
        logRecord.setSourceMethodName(sourceMethodName);
    }

    @Override
    public String getMessage() {
        return logRecord.getMessage();
    }

    @Override
    public void setMessage(String message) {
        logRecord.setMessage(message);
    }

    @Override
    public Object[] getParameters() {
        return logRecord.getParameters();
    }

    @Override
    public void setParameters(Object[] parameters) {
        logRecord.setParameters(parameters);
    }

    @Override
    public int getThreadID() {
        return logRecord.getThreadID();
    }

    @Override
    public void setThreadID(int threadID) {
        logRecord.setThreadID(threadID);
    }

    @Override
    public long getMillis() {
        return logRecord.getMillis();
    }

    @Override
    public void setMillis(long millis) {
        logRecord.setMillis(millis);
    }

    @Override
    public Throwable getThrown() {
        return logRecord.getThrown();
    }

    @Override
    public void setThrown(Throwable thrown) {
        logRecord.setThrown(thrown);
    }
}
