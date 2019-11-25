package log.jdk;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

/**
 * Created by JasonFitch on 11/22/2019.
 */
public class LoggerHandlerTest {


    public static void main(String[] args) throws IOException {
        try {
            String logfileName = System.getProperty("user.dir") + "/logs/handler.log";
            String logfilePath = new File(logfileName).getAbsolutePath();
            checkFileDir(logfileName);

            FileHandler fileHandler = new FileHandler(logfilePath, true);

//          这里设置的时间字符串格式化后的效果是这样的: ####|2019-11-24T20:54:42.329-0800|INFO|_ThreadID=1|test=1574657682328|####
            // Z 时区
            fileHandler.setFormatter(getFormatter("yyyy-MM-dd'T'HH:mm:ss.SSZ"));

            Logger logger = Logger.getLogger(LoggerHandlerTest.class.getCanonicalName());

            System.out.println("################## 移除原先的Handler #######################");
            removeHandlers(logger);

            System.out.println("################## 添加自定义Handler #######################");
            logger.addHandler(fileHandler);
            logger.setLevel(Level.INFO);
            logger.setUseParentHandlers(false);
            System.out.println("Redirect log to file: " + logfilePath.toString());

            logger.info("test=" + System.currentTimeMillis());

            System.out.println("################## 移除现在的Handler #######################");
            removeHandlers(logger);

        } catch (Exception e) {
            throw new RuntimeException("Failed to inital logger handler!", e);
        }
    }

    public static void removeHandlers(Logger logger) {
        Handler[] originalHandlers = logger.getHandlers();
        for (Handler handler : originalHandlers) {
            System.out.println(handler);
            //JF 注意Handler的移除和关闭顺序
            logger.removeHandler(handler);
            //JF 关闭Handler来释放锁定的文件
            handler.close();
        }
    }

    public static Formatter getFormatter(String dateFormatString) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormatString);
        simpleDateFormat.setTimeZone((TimeZone.getTimeZone("America/Los_Angeles")));

        Formatter formatter = new Formatter() {
            @Override
            public String format(LogRecord record) {
                StringBuilder sb = new StringBuilder();

                sb.append("####|").append(simpleDateFormat.format(new Date(record.getMillis())));

                sb.append("|").append(record.getLevel().getName());
                sb.append("|").append("_ThreadID=" + record.getThreadID());
                sb.append("|").append(record.getMessage());

                if (record.getThrown() != null) {
                    StringWriter sw = new StringWriter();
                    PrintWriter pw = new PrintWriter(sw);
                    record.getThrown().printStackTrace(pw);
                    sb.append("|");
                    sb.append(sw.toString());
                }

                sb.append("|####");
                sb.append("\n");
                return sb.toString();
            }
        };
        return formatter;
    }

    public static void checkFileDir(String logfileName) {
        File logFile = new File(logfileName);
        File parentFile = logFile.getParentFile();
        if (parentFile.exists() && parentFile.isFile()) {
            parentFile.delete();
        }
        if (!parentFile.exists()) {
            parentFile.mkdirs();
        }
    }

}

