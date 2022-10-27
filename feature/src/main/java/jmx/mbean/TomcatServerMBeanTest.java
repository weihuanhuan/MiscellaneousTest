package jmx.mbean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;

/**
 * Created by JasonFitch on 8/3/2020.
 */
public class TomcatServerMBeanTest {

    public static void main(String[] args) throws IOException, MalformedObjectNameException, IntrospectionException, InstanceNotFoundException, ReflectionException, AttributeNotFoundException, MBeanException {
        //server enable jmx jvm options
        //mbean server 开启 jmx connector 监听，并指定端口
        //-Dcom.sun.management.jmxremote.port=6600
        //mbean server 指定 RMI Registry 端口为特定的端口，
        //注意如果不指定为和 jmx connector 使用相同的端口，那么他将使用一个随机的端口开监听，这会造成 tomcat 多开启一个端口。
        //-Dcom.sun.management.jmxremote.rmi.port=6600
        //mbean server 禁用 ssl 认证
        //-Dcom.sun.management.jmxremote.ssl=false
        //mbean server 禁用 任何 认证
        //-Dcom.sun.management.jmxremote.authenticate=false

        //tomcat server debug
        //jdk 1.5+，只会开启一个监听端口，
        //虽然下面的 jdk1.4 形式的还是可以使用的，但是新版本推荐使用这种形式，
        //原因是从 java5 开始，java变更了他的 debug 模式，提供了这里的jdk1.5+的 debug 选项，
        //如果使用jdk1.4形式的选项，会导致使用 -javaagent 启动的程序在 main 执行之前的代码无法 debug 到。
        //-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=28003
        //jdk 1.4，只会开启一个监听端口
        //-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=28003

        String host = "localhost";
        String port = "6600";
        String jmxUrl = "service:jmx:rmi:///jndi/rmi://" + host + ":" + port + "/jmxrmi";

        String objectName = "Catalina:type=Server";
        String attributeName = "serverInfo";

        //credentials info, no required any credentials by default
        Map<String, String[]> env = new HashMap<>();
        //String[] credentials = new String[]{"admin_user", "admin_passwd"};
        //env.put(JMXConnector.CREDENTIALS, credentials);

        //connector
        System.out.println(jmxUrl);
        System.out.println();
        JMXServiceURL url = new JMXServiceURL(jmxUrl);
        JMXConnector jmxConnector = JMXConnectorFactory.connect(url, env);

        //connection
        MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();

        //query attribute value
        //属性值直接从 MBeanServerConnection 中获取，而 MBeanInfo 是用来审视某个 ObjectName 关联的 MBean 的信息的。
        ObjectName mbeanName = new ObjectName(objectName);
        Object value = mbeanServerConnection.getAttribute(mbeanName, attributeName);
        System.out.println(String.format("%s=%s", attributeName, value));
        System.out.println();

        //query mbean info
        System.out.println("############ printMbeanInfo ##############");
        printMbeanInfo(mbeanServerConnection, objectName);

        //close
        jmxConnector.close();
    }

    private static void printMbeanInfo(MBeanServerConnection mbeanServerConnection, String objectName) throws IntrospectionException, ReflectionException, InstanceNotFoundException, IOException, AttributeNotFoundException, MBeanException, MalformedObjectNameException {
        ObjectName mbeanName = new ObjectName(objectName);
        MBeanInfo mBeanInfo = mbeanServerConnection.getMBeanInfo(mbeanName);

        MBeanAttributeInfo[] attributes = mBeanInfo.getAttributes();
        List<String> readableAttributes = new ArrayList<>();
        for (MBeanAttributeInfo attribute : attributes) {
            System.out.println(attribute);
            if (attribute.isReadable() && attribute.getType().equals(String.class.getName())) {
                readableAttributes.add(attribute.getName());
            }
        }

        System.out.println("------ readableAttributes -------");
        for (String readableAttribute : readableAttributes) {
            Object value = mbeanServerConnection.getAttribute(mbeanName, readableAttribute);
            System.out.println(String.format("%s=%s", readableAttribute, value));
        }

    }

}

