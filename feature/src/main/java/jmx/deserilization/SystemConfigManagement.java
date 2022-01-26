
package jmx.deserilization;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class SystemConfigManagement {


    static {
        //开启 server jmx 监听，需要在启动参数中设置下列的系统属性，具体原因见测试类 SystemTest.JMXSystemPropertyTest
//        System.setProperty("com.sun.management.jmxremote.port", SystemConfigConstants.PORT);
//        System.setProperty("com.sun.management.jmxremote.authenticate", "false");
//        System.setProperty("com.sun.management.jmxremote.ssl", "false");
    }

    public static void main(String[] args) throws MalformedObjectNameException, InterruptedException, InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException {
        //Get the MBean server
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();

        //register the MBean
        SystemConfig mBean = new SystemConfig(SystemConfigConstants.DEFAULT_NO_THREADS, SystemConfigConstants.DEFAULT_SCHEMA);
        ObjectName name = new ObjectName(SystemConfigConstants.OBJECT_NAME);
        mbs.registerMBean(mBean, name);

        do {
            Thread.sleep(3000);
            System.out.println("Thread Count=" + mBean.getThreadCount() + ":::Schema Name=" + mBean.getSchemaName());
        } while (mBean.getThreadCount() != 0);
    }

}
