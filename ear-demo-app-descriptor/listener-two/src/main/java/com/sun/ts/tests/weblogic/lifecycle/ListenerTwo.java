package com.sun.ts.tests.weblogic.lifecycle;

import org.glassfish.api.deployment.lifecycle.ApplicationContext;
import org.glassfish.api.deployment.lifecycle.ApplicationLifecycleListener;
import org.glassfish.api.deployment.lifecycle.ApplicationLifecycleEvent;
import org.glassfish.api.deployment.lifecycle.ApplicationLifecycleException;
import org.glassfish.api.deployment.lifecycle.DeploymentOperationType;

public class ListenerTwo implements ApplicationLifecycleListener {

    private Integer value = 0;

    public void preStart(ApplicationLifecycleEvent event) throws ApplicationLifecycleException {
        System.out.println(String.format("ListenerTwo#preStart %s", ++value));
        printEventInfo(event);
    }

    public void postStart(ApplicationLifecycleEvent event) throws ApplicationLifecycleException {
        System.out.println(String.format("ListenerTwo#postStart %s", ++value));
        printEventInfo(event);
    }

    public void preStop(ApplicationLifecycleEvent event) throws ApplicationLifecycleException {
        System.out.println(String.format("ListenerTwo#preStop %s", ++value));
        printEventInfo(event);
    }

    public void postStop(ApplicationLifecycleEvent event) throws ApplicationLifecycleException {
        System.out.println(String.format("ListenerTwo#postStop %s", ++value));
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
