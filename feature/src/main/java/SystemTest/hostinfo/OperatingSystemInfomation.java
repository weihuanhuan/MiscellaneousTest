package SystemTest.hostinfo;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;

/**
 * Created by JasonFitch on 12/2/2019.
 */
public class OperatingSystemInfomation {


    public static void main(String[] args) {

        //JF JVM 本身相关的 memory 信息
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        int processors = runtime.availableProcessors();

        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        String runtimeMXBeanName = runtimeMXBean.getName();

        //JF OperatingSystem 相关的信息
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        String name = operatingSystemMXBean.getName();
        String version = operatingSystemMXBean.getVersion();
        String arch = operatingSystemMXBean.getArch();
        int availableProcessors = operatingSystemMXBean.getAvailableProcessors();
        double systemLoadAverage = operatingSystemMXBean.getSystemLoadAverage();

        //JF 注意JVM获取的信息可能存在较大误差，做好使用操作系统的，或者三方lib
        System.out.println("");

    }


}
