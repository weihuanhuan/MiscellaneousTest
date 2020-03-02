package regularexpression;

import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JasonFitch on 1/29/2018.
 */
public class RegularExpressionMatcherResult {

    public static void main(String[] args) {
        MatcherResultTest();
    }

    public static void MatcherResultTest() {
        String ipLists = "192.168.9.177:28001,192.168.9.177:28002,192.168.9.177:28003,192.168.9.177:28004";
        System.out.println("#####ipLists.length()=" + ipLists.length());

        //正则匹配 包含冒号在内开头的至少一个数字的子序列，并提取其中的数字部分作为一个单独的捕获分组
        printMatchResult(ipLists, ":(\\d+)");
    }


    public static void printMatchResult(String string, String regularExpression) {
        System.out.println("#####[ " + string + "] matched by [" + regularExpression + " ]");
        Pattern pattern = Pattern.compile(regularExpression);
        System.out.println();

        Matcher matcher = pattern.matcher(string);
        //匹配 source 是否整体符合 regular 的规则。
        boolean matches = matcher.matches();
        System.out.println("#####matcher.matches():" + matches);
        System.out.println();

        System.out.println("#####matcher.find():" + matches);
        //匹配 source 中部分符合 regular 的规则的子字符串。
        while (matcher.find()) {
            // matcher 的状态，包括
            //  处理源的信息 匹配的正则，源的匹配范围，最后一次的匹配结果。
            //  匹配到的信息 本次匹配后，捕获组的数量，开始结束位置，和匹配内容。
            System.out.println(matcher);
            System.out.println(matcher.groupCount());
            System.out.println(matcher.group());
            System.out.println(matcher.start());
            System.out.println(matcher.end());

            //如果一个匹配中存在的捕获组，那么将这些捕获组逐一输出。
            int count = matcher.groupCount();
            for (int i = 1; i <= count; ++i) {
                System.out.println("group=" + i + " value=" + matcher.group(i));
            }

            MatchResult matchResult = matcher.toMatchResult();
            //MatchResult 代表了，此时此刻的 matcher 的内部状态,
            //  相当于上面 matcher 状态的一个快照，所以同一匹配操作后，他们的对应属性都是相同的。
            System.out.println(matchResult);
            System.out.println(matchResult.groupCount());
            System.out.println(matchResult.group());
            System.out.println(matchResult.start());
            System.out.println(matchResult.end());

            System.out.println();
        }

        //matcher 可以重复使用
        System.out.println("#####matcher.reset()");
        matcher.reset();
        //如果没有进行匹配，那么 MatchResult 的状态中是没有状态信息的，此时获取状态会抛异常
        //matcher.find();
//        MatchResult matchResult = matcher.toMatchResult();
//        System.out.println(matchResult);
//        System.out.println(matchResult.groupCount());
//        System.out.println(matchResult.group());
//        System.out.println(matchResult.start());
//        System.out.println(matchResult.end());

    }

}
