package SystemTest.process.helper;

import org.apache.commons.io.input.ReversedLinesFileReader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

public class ReadFileLastLine {

    private static final Charset CHARSET = Charset.defaultCharset();

    public static String getLastLinesAsString(File file, int lineCount) throws IOException {
        List<String> lastLines = readLines(file, lineCount);
        if (lastLines == null) {
            return null;
        }

        return ReadLastLineHelper.joinLinesAsString(lastLines);
    }

    public static List<String> readLines(File file, int lineCount) throws IOException {
        //注意关闭 reader，使用 try-with-resources 语法来完成
        try (ReversedLinesFileReader reversedLinesFileReader = new ReversedLinesFileReader(file, CHARSET)) {
            return reversedLinesFileReader.readLines(lineCount);
        }
    }

}
