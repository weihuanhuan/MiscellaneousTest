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

        //对于 rhino 来说其 1.7版本的正式发布，其更新了语法树的解析，
        //而且还变动了 rhino 的发布坐标
        //新版本 1.7.7.2 ---> org.mozilla:rhino:jar:1.7.7.2
        //老版本 1.7R2   ---> rhino:js:jar:1.7R2

        //另外 1.7.7.2 之后的发布版本为 1.7.8 其将 java运行时升级为了 jdk8

        //而且这个设置默认就是 true 了，但是对于老版本的 1.7R2 需要手动设置才行
        compilerEnvirons.setReservedKeywordAsIdentifier(true);
//        compilerEnvirons.setAllowMemberExprAsFunctionName(true);
//        compilerEnvirons.setXmlAvailable(true);

        Parser parser = new Parser(compilerEnvirons, compilerEnvirons.getErrorReporter());
        Object parse = parser.parse(bufferedReader, null, 1);
        System.out.println(parse);
    }

}
