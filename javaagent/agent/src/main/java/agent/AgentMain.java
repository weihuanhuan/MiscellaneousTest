package agent;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import util.ClazzInfoUtils;
import util.JarFileHelper;

/**
 * Created by JasonFitch on 12/31/2020.
 */
public class AgentMain {

    public static void main(String[] args) {
        System.out.println("agent.AgentMain.main");
        printClass(null);
    }

    public static void premain(String agentArgs, Instrumentation inst) {
        System.out.println("agent.AgentMain.premain");
        printClass(inst);
    }

    public static void agentmain(String agentArgs, Instrumentation inst) {
        System.out.println("agent.AgentMain.agentmain");
        printClass(inst);
    }

    private static void printClass(Instrumentation inst) {
        try {
            JarFileHelper.addJarToBootstrap(inst);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ClazzInfoUtils.printClassInfo();
    }

//    默认情况 则默认使用 Application，
//    由 sun.misc.Launcher$AppClassLoader 来加载，其余情况则由指定的 类路径对应的 classloader 加载
//    $ java -javaagent:agent.jar -jar application.jar
//    agent.AgentMain.premain
//    application util.ClazzInfoUtils.printClassInfo
//    lib.LibMain
//    sun.misc.Launcher$AppClassLoader@18b4aac2
//    private java.lang.String lib.LibMain.fieldFromApplication
//
//    app.ApplicationMain.main
//    application util.ClazzInfoUtils.printClassInfo
//    lib.LibMain
//    sun.misc.Launcher$AppClassLoader@18b4aac2
//    private java.lang.String lib.LibMain.fieldFromApplication

//    使用 java.lang.instrument.Instrumentation.appendToBootstrapClassLoaderSearch 则优先使用 Agent
//    $ java -javaagent:agent.jar -jar application.jar
//    agent.AgentMain.premain
//    agent util.ClazzInfoUtils.printClassInfo
//    lib.LibMain
//    null
//    private java.lang.String lib.LibMain.fieldFromAgent
//
//    agent util.ClazzInfoUtils.printClassInfo
//    lib.LibMain
//    null
//    private java.lang.String lib.LibMain.fieldFromAgent

//    配置 Boot-Class-Path: agentlib.jar 则优先使用 AgentLib
//    $ java -javaagent:agent.jar -jar application.jar
//    agent.AgentMain.premain
//    agentlib util.ClazzInfoUtils.printClassInfo
//    lib.LibMain
//    null
//    private java.lang.String lib.LibMain.fieldFromLib
//
//    app.ApplicationMain.main
//    agentlib util.ClazzInfoUtils.printClassInfo
//    lib.LibMain
//    null
//    private java.lang.String lib.LibMain.fieldFromLib

//    指定 jvm 选项 -Xbootclasspath/a:agentoption.jar 则优先使用 AgentOption
//    $ java -Xbootclasspath/a:agentoption.jar -javaagent:agent.jar -jar application.jar
//    agent.AgentMain.premain
//    agentOption util.ClazzInfoUtils.printClassInfo
//    lib.LibMain
//    null
//    private java.lang.String lib.LibMain.fieldFromOption
//
//    app.ApplicationMain.main
//    agentOption util.ClazzInfoUtils.printClassInfo
//    lib.LibMain
//    null
//    private java.lang.String lib.LibMain.fieldFromOption

}
