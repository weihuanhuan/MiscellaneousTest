package ssl.analyser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Created by JasonFitch on 10/24/2018.
 */
public class ImportScanner {

    public static void main(String[] args) {

        String workDir = System.getProperty("user.dir");
        String clazzPathStr = workDir + "/src/main/java/ssl/gm";
        Path clazzPath = Paths.get(clazzPathStr);

        String fileRegex = "(?i)^.*\\.java$";
        ContextScaner.regex = "^(//)?import\\s+((?!java).*)";

        FileScanner fileScanner = new FileScanner(fileRegex);
        try {
            Files.walkFileTree(clazzPath, fileScanner);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


