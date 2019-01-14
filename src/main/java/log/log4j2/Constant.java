package log.log4j2;

/**
 * Created by JasonFitch on 12/25/2018.
 */
public class Constant {

    public static String WORK_DIR = System.getProperty("user.dir");
    public static String MAVEN_REPOSITORY = "InnerEnum:/Temporary/repository";

    public static String HANDLE_FILE = WORK_DIR + "/target/classes/fileHandleTestFile.txt";
    public static String CONF_FILE = WORK_DIR + "/target/classes/log4j2.xml";
    public static String MAIN_CLASS_NAME = "log.log4j2.BESBug.RollingFileInvokedMainClass";

    //JF 类加载器中的url路径应该是一个jar包，或者是一个目录(目录内可以放独立的文件去加载)，但是不能直接指定一个文件
    //   类加载器会按照 指定的目录 作为根目录去查找资源文件或class文件，对于jar包则将jar包本身视为根目录然后查看里面的内容。
    //   ex:查找类 log.log4j2.BESBug.RollingFileInvokedMainClass ，类路径是 下示变量，则相对于这个classes目录下边应该存在目录结构及文件。
    //            ./log/log4j2/BESBug/RollingFileInvokedMainClass.class，
    //   类加载器在加载一个类时，会索要其 全限定类名，在查找前会自动把 . 换成 / 并添加 .class 后缀，然后才在其所有的类路径中按照这个相对位置去查找。
    public static String CLASS_PATH = WORK_DIR + "/target/classes";
    public static String DISRUPTOR_JAR_FILE = MAVEN_REPOSITORY + "/com/lmax/disruptor/3.4.2/disruptor-3.4.2.jar";
    public static String LOG4J_CORE_JAR_FILE = MAVEN_REPOSITORY + "/org/apache/logging/log4j/log4j-core/2.11.1/log4j-core-2.11.1.jar";
    public static String LOG4J_API_JAR_FILE = MAVEN_REPOSITORY + "/org/apache/logging/log4j/log4j-api/2.11.1/log4j-api-2.11.1.jar";

//    mkdir -p /f/JetBrains/IntelliJ\ IDEA/BEStest/logs  ; echo 'original'> '/f/JetBrains/IntelliJ IDEA/BEStest/logs/log4j2.log'  && touch -t 201812241150 '/f/JetBrains/IntelliJ IDEA/BEStest/logs/log4j2.log'

}
