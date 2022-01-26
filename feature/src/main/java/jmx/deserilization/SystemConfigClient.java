
package jmx.deserilization;

import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;

public class SystemConfigClient {

    public static void main(String[] args) throws IOException, MalformedObjectNameException {
        String url = "service:jmx:rmi:///jndi/rmi://" + SystemConfigConstants.HOST + ":" + SystemConfigConstants.PORT + "/jmxrmi";
        System.out.println(url);

        JMXServiceURL jmxServiceURL = new JMXServiceURL(url);
        JMXConnector jmxConnector = JMXConnectorFactory.connect(jmxServiceURL);
        MBeanServerConnection mbeanServerConnection = jmxConnector.getMBeanServerConnection();

        //ObjectName should be same as your MBean name
        ObjectName mbeanName = new ObjectName(SystemConfigConstants.OBJECT_NAME);

        //Get MBean proxy instance that will be used to make calls to registered MBean
        SystemConfigMBean mbeanProxy = MBeanServerInvocationHandler.newProxyInstance(mbeanServerConnection, mbeanName, SystemConfigMBean.class, true);

        //let's make some calls to mbean through proxy and see the results.
        System.out.println("Current SystemConfig::" + mbeanProxy.doConfig());

        mbeanProxy.setSchemaName("NewSchema");
        mbeanProxy.setThreadCount(5);
        System.out.println("New SystemConfig::" + mbeanProxy.doConfig());

        //let's terminate the mbean by making thread count as 0
        mbeanProxy.setThreadCount(0);

        //close the connection
        jmxConnector.close();
    }
}
