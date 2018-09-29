package regularexpression;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JasonFitch on 1/29/2018.
 */
public class RegularExpression {

    public static void main(String[] args) {

        String   str2    = "1, 0,\n2,\n3,\n,";
        String[] result2 = str2.split("(,$)");
        System.out.println(Arrays.deepToString(result2) + result2.length);
        System.out.println("-----------------------------------------------------");
        String   str3    = "1, 0,\r\n2,\r\n3,\r\n,";
        String[] result3 = str3.split("(,$)");
        System.out.println(Arrays.deepToString(result3) + result3.length);
        System.out.println("-----------------------------------------------------");
//      \r\n and \n is not a end of line for RegularExpression

        String   str1    = "1, 0,\r\n2,\r\n3,\r\n,";
        String[] result1 = str1.split("(,\\n)|(,\\r\\n)");
        System.out.println(Arrays.deepToString(result1) + result1.length);
        for (int i = 0; i < result1.length; ++i) {
            result1[i] = result1[i].replaceAll("(^\\s+)|(\\s+$)|(,$)", "");
        }
        System.out.println(Arrays.deepToString(result1) + result1.length);
        System.out.println("-----------------------------------------------------");
//      deal with dos and unix linefeed at the same time

        String str = "   ,n.u.l.l,  \n";
        System.out.println(Arrays.deepToString(str.split(".")) + "#");
        System.out.println(Arrays.deepToString(str.split("\\.")) + "#");
        // 正则的 .（dot）是特殊字符匹配需要转义，java的 \ (backward slash) 也是特殊字符，需转义
        // 所以匹配正则的.（dot）需要二次转义
        // / (forward slash)
        System.out.println(" \" \"%%" + "#");
        //百分号无需转义
        System.out.println("-----------------------------------------------------");

        System.out.println(System.getProperty("java.class.path"));


        System.out.println("-------------jvmStr---------------------");
        String  jvmStr1  = "JVM_OPTIONS=%JVM_OPTIONS% -Dkey=value";
        String  regex1  = ".*JVM_OPTIONS=.*JVM_OPTIONS.*\\s+-D(.*)=(.*)";
        //尤其贪婪匹配,会将最后一个等号之前的内容全部捕获。
        String  jvmStr2  = "JVM_OPTIONS=%JVM_OPTIONS% -Dkey=value=";
        String  regex2   = ".*JVM_OPTIONS=.*JVM_OPTIONS.*\\s+-D(((?!=).)*)=(.*)";
        //使用正向否定预查，将不包括 = 的第一个捕获组取出。
        boolean isMatch = jvmStr1.matches(regex1);
        System.out.println(isMatch);

        Pattern pattern = Pattern.compile(regex1);
        Matcher matcher = pattern.matcher(jvmStr2);
        //注意将 regex1 来匹配 jvmStr2 时匹配的内容
        if (matcher.find()) {
            int count = matcher.groupCount();
            System.out.println(matcher.groupCount());
            for (int i = 0; i < count + 1; ++i)
                System.out.println("i="+i+":"+matcher.group(i));
        }


        System.out.println("-------------appendReplacement---------------------");
        StringBuffer stringBuffer = new StringBuffer();
        matcher.reset();
        while (matcher.find()) {
                matcher.appendReplacement(stringBuffer,matcher.group().toUpperCase());
        }
        matcher.appendTail(stringBuffer);
        System.out.println(stringBuffer.toString());

        System.out.println("-------------Scanner---------------------");
        Scanner scanner = new Scanner(new BufferedReader(new StringReader(jvmStr1)));
        scanner.useDelimiter(System.lineSeparator());
        //注意修改定界符，因为Scanner只会依据 定界符 来读取下一个分词，
        // 如果正则表达式中含有定界符的匹配，那么Scanner 的 hasNext() 方法将永远返回false
        while (scanner.hasNext(pattern)) {
            scanner.next(pattern);
            MatchResult matchResult = scanner.match();
            System.out.println(matchResult.group());
        }
        
    }
}
