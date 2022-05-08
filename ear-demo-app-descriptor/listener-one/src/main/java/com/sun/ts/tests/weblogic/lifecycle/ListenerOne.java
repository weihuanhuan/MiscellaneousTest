package com.sun.ts.tests.weblogic.lifecycle;

//这些 import 的类是在官方的 glassfish/nucleus/common/glassfish-api 工程中自行添加的
//所以为了正确的编译本类，我们需要先行 mvn clean install 修改后的 glassfish-api 工程
//其相关的依赖 pom 坐标可以看本工程的 pom 文件
import org.glassfish.api.deployment.lifecycle.ApplicationContext;
import org.glassfish.api.deployment.lifecycle.ApplicationLifecycleListener;
import org.glassfish.api.deployment.lifecycle.ApplicationLifecycleEvent;
import org.glassfish.api.deployment.lifecycle.ApplicationLifecycleException;
import org.glassfish.api.deployment.lifecycle.DeploymentOperationType;

public class ListenerOne implements ApplicationLifecycleListener {

    private Integer value = 0;

    public void preStart(ApplicationLifecycleEvent event) throws ApplicationLifecycleException {
        System.out.println(String.format("ListenerOne#preStart %s", ++value));
        printEventInfo(event);
    }

    public void postStart(ApplicationLifecycleEvent event) throws ApplicationLifecycleException {
        System.out.println(String.format("ListenerOne#postStart %s", ++value));
        printEventInfo(event);
    }

    public void preStop(ApplicationLifecycleEvent event) throws ApplicationLifecycleException {
        System.out.println(String.format("ListenerOne#preStop %s", ++value));
        printEventInfo(event);
    }

    public void postStop(ApplicationLifecycleEvent event) throws ApplicationLifecycleException {
        System.out.println(String.format("ListenerOne#postStop %s", ++value));
        printEventInfo(event);
    }

    private void printEventInfo(ApplicationLifecycleEvent event) {
        DeploymentOperationType deploymentOperation = event.getDeploymentOperation();
        ApplicationContext applicationContext = event.getApplicationContext();
        System.out.println(String.format("deploymentOperation: %s", deploymentOperation));
        System.out.println(String.format("applicationContext.getApplicationName(): %s", applicationContext.getApplicationName()));
        System.out.println(String.format("applicationContext.getAppClassLoader(): %s", applicationContext.getAppClassLoader()));
    }

}
