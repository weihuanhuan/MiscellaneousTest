package util;

import agent.AgentMain;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.net.URLDecoder;
import java.util.jar.JarFile;

public class JarFileHelper {

    public static void addJarToBootstrap(Instrumentation inst) throws IOException {
        String localJarPath = getLocalJarPath();
        inst.appendToBootstrapClassLoaderSearch(new JarFile(localJarPath));
    }

    public static String getLocalJarPath() throws UnsupportedEncodingException {
        URL localUrl = AgentMain.class.getProtectionDomain().getCodeSource().getLocation();
        String localUrlFile = localUrl.getFile();
        String path = URLDecoder.decode(localUrlFile.replace("+", "%2B"), "UTF-8");
        return path;
    }

}
