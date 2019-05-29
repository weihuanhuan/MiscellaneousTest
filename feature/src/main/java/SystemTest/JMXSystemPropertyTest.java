package SystemTest;

import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * Created by JasonFitch on 5/29/2019.
 */
public class JMXSystemPropertyTest {


    static {

        //JF 注意当在启动是成功开启jmx后，是不会去加载PrivateLogManager，而是依旧使用系统默认的java.util.logging.LogManager
        //   因为JMX的加载是在Main类加载前由jvm处理的，所以不论将 java.util.logging.manager 系统属性设置在Main类的何处，
        //   只要不是JVM启动时可读取的，那么JMX就会先一步使用习系统默认的LogManager去初始化日志系统，接下来变不会再二次初始化了。
        //   具体JVM的处理流程如下所示：

//        "main@1" prio=5 tid=0x1 nid=NA runnable
//        java.lang.Thread.State: RUNNABLE
//        at SystemTest.JMXSystemPropertyTest$PrivateLogManager.<clinit>(JMXSystemPropertyTest.java:43)
//        at sun.reflect.NativeConstructorAccessorImpl.newInstance0(NativeConstructorAccessorImpl.java:-1)
//        at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
//        at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
//        at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
//        at java.lang.Class.newInstance(Class.java:442)
//        at java.util.logging.LogManager$1.run(LogManager.java:192)
//        at java.util.logging.LogManager$1.run(LogManager.java:181)
//        at java.security.AccessController.doPrivileged(AccessController.java:-1)
//        at java.util.logging.LogManager.<clinit>(LogManager.java:181)
//        at java.util.logging.Logger.demandLogger(Logger.java:448)
//        at java.util.logging.Logger.getLogger(Logger.java:502)
//        at com.sun.jmx.remote.util.ClassLogger.<init>(ClassLogger.java:55)
//        at sun.management.jmxremote.ConnectorBootstrap.<clinit>(ConnectorBootstrap.java:846)
//        at sun.management.Agent.startAgent(Agent.java:262)
//        at sun.management.Agent.startAgent(Agent.java:452)

        /*
          -Dcom.sun.management.jmxremote
          -Dcom.sun.management.jmxremote.hostname=127.0.0.1
          -Dcom.sun.management.jmxremote.port=9008
          -Dcom.sun.management.jmxremote.authenticate=false
          -Dcom.sun.management.jmxremote.ssl=false

          -Djava.util.logging.manager=SystemTest.JMXSystemPropertyTest$PrivateLogManager
        */

        System.out.println("JMXSystemPropertyTest static block");

        //JF 所以再启动了Java程序后所设置JMX也是无效的，因为JMX的初始阶段在Main类加载之前
        System.setProperty("com.sun.management.jmxremote", System.getProperty("com.sun.management.jmxremote", "true"));
        System.setProperty("com.sun.management.jmxremote.hostname", System.getProperty("com.sun.management.jmxremote.hostname", "127.0.0.1"));
        System.setProperty("com.sun.management.jmxremote.port", System.getProperty("com.sun.management.jmxremote.port", "9008"));
        System.setProperty("com.sun.management.jmxremote.authenticate", System.getProperty("com.sun.management.jmxremote.authenticate", "false"));
        System.setProperty("com.sun.management.jmxremote.ssl", System.getProperty("com.sun.management.jmxremote.ssl", "false"));

        System.setProperty("java.util.logging.manager", System.getProperty("java.util.logging.manager", PrivateLogManager.class.getName()));

        //JF 这里可以想到Java程序的真正入口应该是从JVM(c/c++)的Main类的main方法算起的，
        // 有些操作在执行到Java的Main类时已经结束了，对于这种情况设置时要注意顺序问题。

    }


    public static void main(String[] args) {

        System.out.println("main static method");

        Logger logger = Logger.getLogger(JMXSystemPropertyTest.class.getName());

        System.out.println(LogManager.getLogManager());
    }

    public static class PrivateLogManager extends LogManager {
        static {
            System.out.println("PrivateLogManager static block");
        }

        public PrivateLogManager() {
            super();
            System.out.println("PrivateLogManager constructor method");
        }
    }
}
