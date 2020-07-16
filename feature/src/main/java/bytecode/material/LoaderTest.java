package bytecode.material;

import bytecode.transformer.ThreadSleepTransformerAround;
import bytecode.transformer.ThreadSleepTransformerModify;
import bytecode.transformer.TransformerClassLoader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * Created by JasonFitch on 7/16/2020.
 */
public class LoaderTest {

    private static String className = "bytecode.material.Sleeper";
    private static String methodName = "sleep";
    private static String anotherMethodName = "anotherSleep";

    public static void main(String[] args) throws ClassNotFoundException, InterruptedException, IllegalAccessException, InstantiationException, MalformedURLException, NoSuchMethodException, InvocationTargetException {
        ClassLoader systemClassLoader = ClassLoader.getSystemClassLoader();
        URL[] systemUrls = ((URLClassLoader) systemClassLoader).getURLs();
        TransformerClassLoader transformerClassLoader = new TransformerClassLoader(systemUrls, systemClassLoader);
        Thread.currentThread().setContextClassLoader(transformerClassLoader);

        transformerClassLoader.addClassFileTransformer(new ThreadSleepTransformerAround());
        transformerClassLoader.addClassFileTransformer(new ThreadSleepTransformerModify());

        Class<?> aClass = transformerClassLoader.loadClass(className);
        Object instance = aClass.newInstance();

        Method sleep = aClass.getDeclaredMethod(methodName);
        sleep.invoke(instance, null);

        Method anotherSleep = aClass.getDeclaredMethod(anotherMethodName);
        anotherSleep.invoke(instance, null);
    }

}
