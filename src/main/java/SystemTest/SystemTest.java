package SystemTest;

import java.util.Enumeration;

public class SystemTest {

    public static void main(String[] args) {

        Enumeration<?> enumeration = System.getProperties().propertyNames();
        while (enumeration.hasMoreElements()) {
            Object name = enumeration.nextElement();
            System.out.println(name + " = " + System.getProperty((String) name));
        }
        System.out.println("------------------------------------------------");
        String userdir = System.getProperty("user.dir");
        System.out.println("#####user.dir:"+userdir);
//        当前工作目录，用户的家目录是user.home
        String JAVA_HOME = System.getenv("JAVA_HOME");
        System.out.println("#####JAVA_HOME:"+JAVA_HOME);
//        环境变量的JAVA_HOME是系统设置的。
        String javahome = System.getProperty("java.home");
//        而要注意java.home的输出是JAVA_HOME/jre, 这里是jre的lib，不是jdk的lib，他们并不相同
        System.out.println("#####java.home:"+javahome);

        System.setProperty("java.home","C:/java");
        String javahomeMod = System.getProperty("java.home");
        System.out.println("#####java.homeMod:"+javahomeMod);

    }
}
