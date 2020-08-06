package regularexpression;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JasonFitch on 8/5/2020.
 */
public class RegularExpressionMatchFlag {


    public static void main(String[] args) {

        testFlagControl();

    }

    private static void testFlagControl() {

        //使用内嵌形式的正则表达式匹配标志 (?i) ，其具体含义可以参考 Pattern 各标志变量的 javadoc 来确认。
        String regular = "(?i)app";
        String string = "Apple";

        //正则表达式模式标志是一个整形变量
        int caseInsensitive = Pattern.CASE_INSENSITIVE;
        //另外不是用内嵌标志的话，可以在创建 Pattern 时指定正则表达式匹配标志
        Pattern compile = Pattern.compile(regular, Pattern.CASE_INSENSITIVE);

        //这里在正则表达式中使用 内嵌模式 设置匹配标志和上面使用标志变量在创建模式时设置是等效的。
        compile = Pattern.compile(regular);

        //由于设置了匹配忽略大小写的匹配标志，所以这里可以按照忽略大小写来匹配
        Matcher matcher = compile.matcher(string);
        if (matcher.find()) {
            MatchResult matchResult = matcher.toMatchResult();
            System.out.println(matchResult);
        }

    }

}
