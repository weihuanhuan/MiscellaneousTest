package ssl.analyser;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JasonFitch on 10/24/2018.
 */
class FileScanner extends SimpleFileVisitor<Path> {
    private String regex;
    private Pattern pattern;


    public FileScanner() {
        this(".*");
    }

    public FileScanner(String regex) {
        this.regex = regex;
        init();
    }

    private void init() {
        pattern = Pattern.compile(regex);
    }


    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
        Matcher matcher = pattern.matcher(file.toString());
        if (matcher.find()) {
            System.out.println(file);
            ContextScaner.Scaner(file);
        }
        return super.visitFile(file, attrs);
    }
}
