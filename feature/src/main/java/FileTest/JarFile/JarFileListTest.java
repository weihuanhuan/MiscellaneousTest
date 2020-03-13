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

        String userDir = System.getProperty("user.dir");
        System.out.println(userDir);

        String path = userDir + "/springmvc/target/springmvc.war";
        System.out.println(path);

        File file = new File(path);
        if (!file.exists() || !file.isFile()) {
            System.out.println("error");
            return;
        }

        System.out.println("################### JarFileGetJarEntrys ###################");
        JarFileGetJarEntrys(file);

    }

    public static void JarFileGetJarEntrys(File file) throws IOException {
        JarFile jarFile = new JarFile(file);

        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String name = jarEntry.getName();
            System.out.println(name);
        }

        System.out.println("-----------------------");
        //JF jar包中的路径不能以 /(根路径) 开始来查找
//        JarEntry jarEntryRoot = jarFile.getJarEntry("");
//        String name = jarEntryRoot.getName();
//        System.out.println(name);

//        JarEntry jarEntryRoot = jarFile.getJarEntry("/");
//        String name = jarEntryRoot.getName();
//        System.out.println(name);

//        JarEntry jarEntryFile = jarFile.getJarEntry("/writerflushclose.jsp");
//        String nameFile = jarEntryFile.getName();
//        System.out.println(nameFile);

        JarEntry jarEntryFile2 = jarFile.getJarEntry("writerflushclose.jsp");
        String nameFile2 = jarEntryFile2.getName();
        System.out.println(nameFile2);

        jarFile.close();
    }
}
