package mbean;

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
        //-Dcom.sun.management.jmxremote.port=6600
        //-Dcom.sun.management.jmxremote.ssl=false
        //-Dcom.sun.management.jmxremote.authenticate=false

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

