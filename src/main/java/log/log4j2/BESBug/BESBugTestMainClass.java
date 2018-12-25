package log.log4j2.BESBug;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.TimeUnit;
import log.log4j2.Constant;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by JasonFitch on 12/25/2018.
 */
public class BESBugTestMainClass {

    public static void main(String[] args) {

        Logger notAsyncNotRootLogger1 = LogManager.getLogger("NotAsyncNotRootLogger1");

        while (true) {
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            notAsyncNotRootLogger1.error("Hello notAsyncNotRootLogger1, level error!");
        }


    }

}
