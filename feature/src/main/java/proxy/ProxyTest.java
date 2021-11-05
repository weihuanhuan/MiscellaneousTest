package proxy;

import proxy.handler.ObjectHandler;
import proxy.object.ProxyThread;
import proxy.object.ProxyThreadFactory;

import java.lang.reflect.Proxy;
import java.util.concurrent.ThreadFactory;

public class ProxyTest {

    //动态代理是基于接口的,而 java.lang.Thread 是一个类，其不能用来执行 jdk 的动态代理。
    private static final Class<?>[] THREAD_CLASS = new Class<?>[]{Thread.class};

    //动态代理的类型是一个动态生成的类 `com.sun.proxy.$Proxy0` ，其会实现相应的代理接口类，
    // 而这里的 Runnable 是 Thread 的接口类，故生成的代理类不能被强转为 Thread。
    private static final Class<?>[] RUNNABLE_INTERFACES = new Class<?>[]{Runnable.class};

    private static final Class<?>[] FACTORY_INTERFACES = new Class<?>[]{ThreadFactory.class};

    public static void main(String[] args) {

        try {
            proxyThreadTest();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        try {
            proxyThreadFactoryTest();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void proxyThreadTest() {
        Thread thread = new ProxyThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("proxyThreadTest");
            }
        });

        ClassLoader classLoader = thread.getClass().getClassLoader();
        ObjectHandler objectHandler = new ObjectHandler(thread);
        Thread proxyInstance = (Thread) Proxy.newProxyInstance(classLoader, RUNNABLE_INTERFACES, objectHandler);
        proxyInstance.start();
    }

    private static void proxyThreadFactoryTest() {
        ThreadFactory threadFactory = new ProxyThreadFactory();

        ClassLoader classLoader2 = threadFactory.getClass().getClassLoader();
        ObjectHandler threadFactoryHandler = new ObjectHandler(threadFactory);
        ThreadFactory proxyInstance2 = (ThreadFactory) Proxy.newProxyInstance(classLoader2, FACTORY_INTERFACES, threadFactoryHandler);

        Thread newThread = proxyInstance2.newThread(new Runnable() {
            @Override
            public void run() {
                System.out.println("proxyThreadFactoryTest");
            }
        });
        newThread.start();
    }

}
