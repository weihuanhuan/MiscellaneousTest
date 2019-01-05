package extendTest;

import java.util.ArrayList;
import java.util.List;
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

        System.out.println(asyncRecordExtend.getThreadID());
        System.out.println(asyncRecordExtend.getThreadName());


        LogRecord logRecord = new LogRecord(Level.OFF, "LogMessage");
        AsyncRecordCompositionaAndExtend asyncRecordComposition = new AsyncRecordCompositionaAndExtend(logRecord, "TN");
        asyncRecordComposition.setThreadID(100);

        System.out.println(asyncRecordComposition.getThreadID());
        System.out.println(asyncRecordComposition.getThreadName());


    }
}
