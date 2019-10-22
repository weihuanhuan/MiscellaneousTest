package spring.external;

import java.util.logging.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by JasonFitch on 10/18/2019.
 */

@RestController
public class ExternalLoadTest {

    public String dirClassName = "external.ExternalLoadDirTest";
    public String jarClassName = "external.ExternalLoadJarTest";

    public String jarInWarClassName = "external.ExternalLoadJarInWarTest";
    public String jarIncarClassName = "external.ExternalLoadJarInCarTest";

    public Logger logger = Logger.getLogger(ExternalLoadTest.class.getName());

    @RequestMapping("/externaldir")
    public String getExternalDir() throws ClassNotFoundException {
        return printInfo(dirClassName);
//        /C:/external/dir/dir.jar
    }

    @RequestMapping("/externaljar")
    public String getExternalJar() throws ClassNotFoundException {
        return printInfo(jarClassName);
//        /C:/external/jar.jar
    }

    @RequestMapping("/externalwar")
    public String getExternalWar() throws ClassNotFoundException {
        return printInfo(jarInWarClassName);
//        file:/C:/external/jarinwar.war*/WEB-INF/lib/war.jar
    }

    @RequestMapping("/externalcar")
    public String getExternalCar() throws ClassNotFoundException {
        return printInfo(jarIncarClassName);
//        file:/C:/external/jarincar.car*/WEB-INF/lib/car.jar
    }

    public String printInfo(String className) throws ClassNotFoundException {

        ClassLoader classLoader = ExternalLoadTest.class.getClassLoader();
        StringBuilder stringBuilder = new StringBuilder();

        Class<?> aClass = classLoader.loadClass(className);
        String file = aClass.getProtectionDomain().getCodeSource().getLocation().getFile();
        logger.info(file);
        stringBuilder.append(file);
        stringBuilder.append("</br>");

        while (classLoader != null) {
            logger.info(classLoader.toString());
            stringBuilder.append(classLoader);
            stringBuilder.append("</br>");
            classLoader = classLoader.getParent();
        }

        return stringBuilder.toString();
    }


}
