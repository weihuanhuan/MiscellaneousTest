package log.log4j2;

import java.io.IOException;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by JasonFitch on 12/11/2018.
 */
public class Log4j2Test {

    static {
//        System.setProperty("log4j2.debug'","true");
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
    }


    public static void main(String[] args) throws IOException, InterruptedException {

        Logger notRootLogger1 = LogManager.getLogger("NotRootLogger1");
        notRootLogger1.error("Hello notRootLogger1, level error!");

        Logger notRootLogger2 = LogManager.getLogger("NotRootLogger2");
        notRootLogger2.error("Hello notRootLogger2, level error!");

        Logger notRootLogger3 = LogManager.getLogger("NotRootLogger3");
        notRootLogger3.error("Hello notRootLogger3, level error!");

        Logger notAsyncNotRootLogger1 = LogManager.getLogger("NotAsyncNotRootLogger1");
        notAsyncNotRootLogger1.error("Hello notAsyncNotRootLogger1, level error!");


        while (true)
        {
            notAsyncNotRootLogger1.error("Hello notAsyncNotRootLogger1, level error!");
        }

        // 默认配置下异步会丢失某些日志，延时让程序写完日志事件
//        TimeUnit.MILLISECONDS.sleep(6000 * 3600);

    }
}
