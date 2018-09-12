package regularexpression;

import java.util.Arrays;
import java.util.EmptyStackException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JasonFitch on 1/29/2018.
 */
public class RegularExpression {
    public static void main(String[] args) {

        String str2 = "1, 0,\n2,\n3,\n,";
        String[] result2 = str2.split("(,$)");
        System.out.println(Arrays.deepToString(result2) + result2.length);
        System.out.println("-----------------------------------------------------");
        String str3 = "1, 0,\r\n2,\r\n3,\r\n,";
        String[] result3 = str3.split("(,$)");
        System.out.println(Arrays.deepToString(result3) + result3.length);
        System.out.println("-----------------------------------------------------");
//      \r\n and \n is not a end of line for RegularExpression

        String str1 = "1, 0,\r\n2,\r\n3,\r\n,";
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


        System.out.println("-----------------------------------------------------");
        String jvmStr = "JVM_OPTIONS=%JVM_OPTIONS% -Dkey=value";
        String regex = ".*JVM_OPTIONS=.*JVM_OPTIONS.*\\s+-D(.*)=(.*)";
        boolean isMatch = jvmStr.matches(regex);
        System.out.println(isMatch);

        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jvmStr);
        if (matcher.find()) {
            int count = matcher.groupCount();
            System.out.println(matcher.groupCount());
            for (int i = 0; i < count+1;++i)
            System.out.println(matcher.group(i));
        }

    }
}
