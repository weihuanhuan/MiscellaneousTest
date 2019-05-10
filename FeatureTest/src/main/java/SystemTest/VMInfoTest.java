package SystemTest;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

/**
 * Created by JasonFitch on 1/27/2019.
 */
public class VMInfoTest {


    public static void main(String[] args) {

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();

        //the Java virtual machine specification version
        String specVersion = runtimeMXBean.getSpecVersion();
        System.out.println(specVersion);

        //JVM规范版本
        String javaVmSpecificationVersion = System.getProperty("java.vm.specification.version");
        System.out.println(javaVmSpecificationVersion);

        //the Java virtual machine implementation version
        String vmVersion = runtimeMXBean.getVmVersion();
        System.out.println(vmVersion);

        //JVM实现版本
        String javaVmVersion = System.getProperty("java.vm.version");
        System.out.println(javaVmVersion);

        System.out.println("---------------------------------------------------------------------");

        //Java API规范版本
        String javaSpecificationVersion = System.getProperty("java.specification.version");
        System.out.println(javaSpecificationVersion);

        //Java API实现版本
        String javaVersion = System.getProperty("java.version");
        System.out.println(javaVersion);


    }
}
