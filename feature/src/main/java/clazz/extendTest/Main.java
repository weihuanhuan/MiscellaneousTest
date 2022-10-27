package clazz.extendTest;

import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 * Created by JasonFitch on 1/5/2019.
 */
public class Main {

    public static void main(String[] args) {


        AsyncRecordExtend asyncRecordExtend = new AsyncRecordExtend(Level.OFF, "AsyncMessage");
        asyncRecordExtend.setThreadID(100);
        asyncRecordExtend.setThreadName("TN");
        asyncRecordExtend.setLoggerName(Main.class.getName());

        System.out.println(asyncRecordExtend.getThreadID());
        // 这里拿到threadID 100
        System.out.println(asyncRecordExtend.getThreadName());
        System.out.println(asyncRecordExtend.getLoggerName());

        // 这个 logRecord 传递给了 asyncRecordCompositionAndExtend 的成员 Record
        // LogRecord 在其构造方法中会将当前线程的threadID作为默认值，所以是1
        LogRecord logRecord = new LogRecord(Level.OFF, "LogMessage");
        AsyncRecordCompositionaAndExtend asyncRecordCompositionAndExtend = new AsyncRecordCompositionaAndExtend(logRecord, "TN");
        // 由于，子类没有重写父类的setThreadID的方法，所以实际上是设置给了其父类 logRecord
        asyncRecordCompositionAndExtend.setThreadID(100);
        asyncRecordCompositionAndExtend.setLoggerName(Main.class.getName());

        // 这里拿到threadID 1，因为，实际上asyncRecordCompositionAndExtend含有两份logRecord的数据，
        // 一份是其继承的父类LogRecord 的 threadID ，另一份是其自身的成员 LogRecord 的 threadID
        // 由于，子类没有重写setThreadID的方法，默认会调用其父类的，所以实际设置的threadID，传递给了其父类的threadID
        // 但是这里，getThreadID的却重写了，并且都用过this转发给了其自身成员LogRecord的threadID，
        // 因此这里取得的值是其成员 LogRecord 的threadID 值 1， 而不是父类 LogRecord 的threadID 值 100
        System.out.println(asyncRecordCompositionAndExtend.getThreadID());

        // 所以，为了使得值是正确的
        //1.属性不是父类没有，而是子类自己都有的
        System.out.println(asyncRecordCompositionAndExtend.getThreadName());
        //2 子类重写父类的set与get的一组方法，并转发给其成员logRecord就可以了。
        System.out.println(asyncRecordCompositionAndExtend.getLoggerName());

    }
}
