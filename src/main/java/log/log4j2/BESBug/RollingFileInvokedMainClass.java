package log.log4j2.BESBug;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.TimeUnit;

import log.log4j2.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.appender.AbstractManager;

/**
 * Created by JasonFitch on 12/25/2018.
 */
public class RollingFileInvokedMainClass {

    public static void main(String[] args) throws InterruptedException, IOException {

        //JF 这里log4j2 第二次 加载配置，并 再次 打开要轮询的文件log文件，产生第2个文件句柄
        //   此时使用的url classloader不同于第一次所使用的app classloader，且这些资源并不能委托到他们的共同父类加载器，ext，bootstrap
        //   所以即使是同一个类文件，但是他们在系统中是不同的类型，即不能类型转换，赋值这样的操作。
        Logger notAsyncNotRootLogger1 = LogManager.getLogger("NotAsyncNotRootLogger1");

        //File文件对象不会真正的创建文件handle，只是一个java对文件的抽象描述。
        File file = new File(Constant.HANDLE_FILE);
        //打开一次文件流，增加一个该文件的handle
        FileInputStream fileInputStream0 = new FileInputStream(file);
        //关闭一次文件流，减少一个该文件的handle
        fileInputStream0.close();
        FileInputStream fileInputStream1 = new FileInputStream(file);
        StringBuilder stringBuilder = new StringBuilder(Constant.HANDLE_FILE).append(".bak");
        File destination = new File(stringBuilder.toString());
        try {
            //如果一个流没有完全关闭，指当文件handle计数不为0时，如此时handle计数为1，不能执行move操作，
            Files.move(Paths.get(file.getAbsolutePath()), Paths.get(destination.getAbsolutePath()),
                    StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //JF 根本原因
        //   log4j2的一个classloader会初始化一次org.apache.logging.log4j.LogManager
        //   有多少个没有父子关系的classloader就得打开多少次文件流
        //JF 解决方法
        //   1.让LogManager只加载一次，添加JVM参数：-Djava.ext.dirs=ext
        //   org.apache.logging.log4j.core.appender.AbstractManager的AbstractManager.getManager()方法
        //   在取manager时会在其成员变量MAP中优先使用之前创建的已经缓冲的管理器，如果没有找到先前缓冲的管理器才会新创建一个,
        //   这是不是也是log4j2在写入文件数据时，不是对目的地lock，反而是去lock目的地所对应的管理器的原因？


        //当 符合轮询 条件时，当且经当执行 写日志操作 才会处理轮询相关的文件操作，导致出现如下问题：
        // 就是因为之前文件被打开了两次，org.apache.logging.log4j.core.appender.rolling.RollingFileManager.RollingFileManagerFactory.createManager()
        // 但是此时却只关闭了一次,就要改名所造成的，org.apache.logging.log4j.core.appender.rolling.RollingFileManager.rollover()方法
        //2018-12-26 15:59:25,407 main ERROR Unable to move file logs\log4j2.log to logs\log4j2-2018-12-26-12.log:
        // java.nio.file.FileSystemException logs\log4j2.log -> logs\log4j2-2018-12-26-12.log:
        // The process cannot access the file because it is being used by another process.
        //2018-12-26 15:59:25,413 main ERROR Unable to delete file logs\log4j2.log:
        // java.nio.file.FileSystemException logs\log4j2.log:
        // The process cannot access the file because it is being used by another process.
        while (true) {
            TimeUnit.MILLISECONDS.sleep(10);
            notAsyncNotRootLogger1.error("Hello notAsyncNotRootLogger1, level error!");
        }

        //JF AbstractLogger.logMessage()方法触发轮询，关闭日志文件句柄流程
//        "main@1" prio=5 tid=0x1 nid=NA runnable
//        java.lang.Thread.State: RUNNABLE
//        at java.io.FileOutputStream.close(FileOutputStream.java:343)
//        at org.apache.logging.log4j.core.appender.OutputStreamManager.closeOutputStream(OutputStreamManager.java:313)
//        - locked <0x968> (a org.apache.logging.log4j.core.appender.rolling.RollingFileManager)
//        at org.apache.logging.log4j.core.appender.rolling.RollingFileManager.rollover(RollingFileManager.java:403)
//        at org.apache.logging.log4j.core.appender.rolling.RollingFileManager.rollover(RollingFileManager.java:312)
//        at org.apache.logging.log4j.core.appender.rolling.RollingFileManager.checkRollover(RollingFileManager.java:252)
//        at org.apache.logging.log4j.core.appender.RollingFileAppender.append(RollingFileAppender.java:308)
//        at org.apache.logging.log4j.core.config.AppenderControl.tryCallAppender(AppenderControl.java:156)
//        at org.apache.logging.log4j.core.config.AppenderControl.callAppender0(AppenderControl.java:129)
//        at org.apache.logging.log4j.core.config.AppenderControl.callAppenderPreventRecursion(AppenderControl.java:120)
//        at org.apache.logging.log4j.core.config.AppenderControl.callAppender(AppenderControl.java:84)
//        at org.apache.logging.log4j.core.config.LoggerConfig.callAppenders(LoggerConfig.java:464)
//        at org.apache.logging.log4j.core.config.LoggerConfig.processLogEvent(LoggerConfig.java:448)
//        at org.apache.logging.log4j.core.config.LoggerConfig.log(LoggerConfig.java:431)
//        at org.apache.logging.log4j.core.config.LoggerConfig.log(LoggerConfig.java:406)
//        at org.apache.logging.log4j.core.config.AwaitCompletionReliabilityStrategy.log(AwaitCompletionReliabilityStrategy.java:63)
//        at org.apache.logging.log4j.core.Logger.logMessage(Logger.java:146)
//        at org.apache.logging.log4j.spi.AbstractLogger.tryLogMessage(AbstractLogger.java:2170)
//        at org.apache.logging.log4j.spi.AbstractLogger.logMessageTrackRecursion(AbstractLogger.java:2125)
//        at org.apache.logging.log4j.spi.AbstractLogger.logMessageSafely(AbstractLogger.java:2108)
//        at org.apache.logging.log4j.spi.AbstractLogger.logMessage(AbstractLogger.java:2002)
//        at org.apache.logging.log4j.spi.AbstractLogger.logIfEnabled(AbstractLogger.java:1974)
//        at org.apache.logging.log4j.spi.AbstractLogger.error(AbstractLogger.java:731)
//        at log.log4j2.BESBug.RollingFileInvokedMainClass.main(RollingFileInvokedMainClass.java:32)
//        at sun.reflect.NativeMethodAccessorImpl.invoke0(NativeMethodAccessorImpl.java:-1)
//        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
//        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
//        at java.lang.reflect.Method.invoke(Method.java:498)
//        at log.log4j2.BESBug.RollingFileMultiClass.main(RollingFileMultiClass.java:88)

        //JF RollingFileManager.checkRollover()方法触发轮询的文件操作流程
//        "main@1" prio=5 tid=0x1 nid=NA runnable
//        java.lang.Thread.State: RUNNABLE
//        at java.nio.file.Files.move(Files.java:1392)
//        at org.apache.logging.log4j.core.appender.rolling.action.FileRenameAction.execute(FileRenameAction.java:119)
//        at org.apache.logging.log4j.core.appender.rolling.action.FileRenameAction.execute(FileRenameAction.java:66)
//        at org.apache.logging.log4j.core.appender.rolling.RollingFileManager.rollover(RollingFileManager.java:407)
//        at org.apache.logging.log4j.core.appender.rolling.RollingFileManager.rollover(RollingFileManager.java:312)
//        - locked <0x968> (a org.apache.logging.log4j.core.appender.rolling.RollingFileManager)
//        at org.apache.logging.log4j.core.appender.rolling.RollingFileManager.checkRollover(RollingFileManager.java:252)
//        at org.apache.logging.log4j.core.appender.RollingFileAppender.append(RollingFileAppender.java:308)
//        at org.apache.logging.log4j.core.config.AppenderControl.tryCallAppender(AppenderControl.java:156)
//        .....省略同上部分调用栈

    }

}
