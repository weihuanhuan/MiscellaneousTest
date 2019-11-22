package FileTest.JarFile;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by JasonFitch on 10/28/2019.
 */
public class JarFileListTest {


    public static void main(String[] args) throws IOException {

        String path = "F:\\JetBrains\\IntelliJ IDEA\\MiscellaneousTest\\springmvc\\target\\springmvc.war";
        path.replaceAll("\\\\","/");
        System.out.println(path);

        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            System.out.println("error");
        }

        JarFile jarFile = new JarFile(file);

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            System.out.println(name);
        }


    }


}
