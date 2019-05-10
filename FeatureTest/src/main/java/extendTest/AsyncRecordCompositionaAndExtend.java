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
    public int getThreadID() {
        return logRecord.getThreadID();
    }

//    @Override
//    public void setThreadID(int threadID) {
//        logRecord.setThreadID(threadID);
//    }

}
