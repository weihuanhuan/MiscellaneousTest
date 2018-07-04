package ClassloaderTest;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

import static java.security.AccessController.doPrivileged;

/**
 * Created by JasonFitch on 7/4/2018.
 */
public class ClassloaderTest
{
    public static void main(String[] args)
    {

        try
        {
            ClassLoader AppClassLoader = ClassLoader.getSystemClassLoader();

            ClassLoader urlloader = new URLClassLoader(new URL[]{new URL("file", null, "class")}, null);
//          parent = null
            ClassLoader childloader = new URLClassLoader(new URL[]{new URL("file", null, "class")}, urlloader);
//          parent = urlloader
            ClassLoader childEmpty = new URLClassLoader(new URL[]{new URL("file", null, "class")});
//          parent = AppClassLoader = ClassLoader.getSystemClassLoader();

            ClassLoader accessNull = createClassloader(true, null);
//          parent = null
            ClassLoader accessParent = createClassloaderLambda(true, urlloader);
//          parent = urlloader
            ClassLoader accessEmpty = createClassloaderLambda(false, null);
//          parent = AppClassLoader = ClassLoader.getSystemClassLoader();

//          调用时，
//          指定 Parent 为 null 则 parent类加载器器 = null，
//          否则是使用默认的 系统类加载器 作为父 AppClassLoader = ClassLoader.getSystemClassLoader();
//          if(hasParent)
//          {
//              new URLClassLoader(new URL[]{new URL("file", null, "class")}, cl);
//          } else {
//              new URLClassLoader(new URL[]{new URL("file", null, "class")});
//          }
//          注意：else 子句里面的 URLClassLoader 构造函数 只有一个参数，没有 ClassLoader 这个参数

            System.in.read();

        } catch (MalformedURLException e)
        {
            e.printStackTrace();

        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    public static ClassLoader createClassloader(boolean hasParent, ClassLoader cl)
    {
        return AccessController.doPrivileged(new PrivilegedAction<URLClassLoader>()
        {
            @Override
            public URLClassLoader run()
            {
                ClassLoader newcl = null;
                try
                {
                    if (hasParent)
                        newcl = new URLClassLoader(new URL[]{new URL("file", null, "class")}, cl);
                    else
                        newcl = new URLClassLoader(new URL[]{new URL("file", null, "class")});

                } catch (MalformedURLException e)
                {
                    e.printStackTrace();
                }

                return (URLClassLoader) newcl;
            }
        });
    }

    public static ClassLoader createClassloaderLambda(boolean hasParent, ClassLoader cl)
    {

        return AccessController.doPrivileged((PrivilegedAction<URLClassLoader>) () ->
        {
            ClassLoader newcl = null;
            try
            {
                if (hasParent)
                    newcl = new URLClassLoader(new URL[]{new URL("file", null, "class")}, cl);
                else
                    newcl = new URLClassLoader(new URL[]{new URL("file", null, "class")});

            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            }

            return (URLClassLoader) newcl;
        });

    }

}
