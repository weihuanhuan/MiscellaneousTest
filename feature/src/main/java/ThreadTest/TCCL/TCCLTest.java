package ThreadTest.TCCL;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TCCLTest {

    public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, MalformedURLException {

        String userDir = System.getProperty("user.dir");
        String path = userDir + "/javaagent/agentlib/target/agentlib.jar";
        File file = new File(path);
        System.out.printf("file:[%s], exist:[%s]%n", file.getAbsolutePath(), file.exists());

        List<URL> urls = new ArrayList<>();
        urls.add(file.toURI().toURL());

        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        URLClassLoader urlClassLoader = new URLClassLoader(urls.toArray(new URL[0]), systemClassLoader);

        int availableProcessors = Runtime.getRuntime().availableProcessors();
        ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(availableProcessors);

        //必须要在创建线程前就切换 TCCL 为我们需要的 urlClassLoader 才行, 否则就是 jdk 启动 main 线程时使用的 systemClassLoader 了。
        //注意这里仅仅是一个线程创建 TCCL 时的测试，真实使用时记得需要还原执行线程先前的 TCCL 。
        Thread mainThread = Thread.currentThread();
//        mainThread.setContextClassLoader(urlClassLoader);

        scheduledThreadPoolExecutor.scheduleAtFixedRate(newPrintTCCLTask(urlClassLoader), 100, 100, TimeUnit.MILLISECONDS);
        System.out.println("schedule");

        scheduledThreadPoolExecutor.submit(newPrintTCCLTask(urlClassLoader));
        System.out.println("submit");

        scheduledThreadPoolExecutor.shutdown();
    }

    private static Runnable newPrintTCCLTask(ClassLoader classLoader) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
        Class<?> aClass = classLoader.loadClass("util.classloader.PrintTCCLTask");
        Runnable runnable = (Runnable) aClass.newInstance();
        return runnable;
    }


}
