package ssl.analyser;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JasonFitch on 10/24/2018.
 */
public class ContextScaner {

    public static String regex = ".*";
    private Pattern pattern;

    public ContextScaner() {
        this(".*");
    }

    public ContextScaner(String regex) {
        this.regex = regex;
        init();
    }

    private void init() {
        this.pattern = Pattern.compile(regex);
    }


    public static void Scaner(Path file) throws IOException {
        BufferedReader bufferedReader = Files.newBufferedReader(file);
        String line = "";
        while (null != (line = bufferedReader.readLine())) {
            Matcher matcher = Pattern.compile(regex).matcher(line);
            if (matcher.find()) {
                System.out.println("    " + line);
            }
        }
    }
}
