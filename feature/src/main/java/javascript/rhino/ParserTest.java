package javascript.rhino;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.mozilla.javascript.CompilerEnvirons;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Parser;


/**
 * Created by JasonFitch on 8/10/2020.
 */
public class ParserTest {

    public static void main(String[] args) throws IOException, URISyntaxException {

        parserTest();

    }

    public static void parserTest() throws IOException, URISyntaxException {
        URL url = ClassLoader.getSystemResource("js/jquery.dsmorse-gridster.min-beautify.js");

        Path path = Paths.get(url.toURI());
        File file = path.toFile();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

        CompilerEnvirons compilerEnvirons = new CompilerEnvirons();
        compilerEnvirons.setLanguageVersion(Context.VERSION_1_6);

        compilerEnvirons.setReservedKeywordAsIdentifier(true);
//        compilerEnvirons.setAllowMemberExprAsFunctionName(true);
//        compilerEnvirons.setXmlAvailable(true);

        Parser parser = new Parser(compilerEnvirons, compilerEnvirons.getErrorReporter());
        Object parse = parser.parse(bufferedReader, null, 1);
        System.out.println(parse);
    }

}
