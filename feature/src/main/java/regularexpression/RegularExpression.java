package regularexpression;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JasonFitch on 1/29/2018.
 */
public class RegularExpression {

    public static void main(String[] args) {

//        lineSeparatorTest();

//        System.out.println(System.getProperty("java.class.path"));

//        LANATest();

        IPMatchTest();

    }

    public static void IPMatchTest() {

        String ipLists = "192.168.9.177:28001,192.168.9.177:28002,192.168.9.177:28003,192.168.9.177:28004";

        String ipListsWithForwardSlash = "192.168.9.177:28001,192.168.9.177:28002,192.168.9.177:28003/192.168.9.177:28004";

        //一个错误的示范
        String nodeRegexOriginal = "(\\[[^\\[\\].]*\\]|[^:]+):([\\d]+)";
        String nodeMultiRegexOriginal = nodeRegexOriginal + "(?:(?:\\s+|,)" + nodeRegexOriginal + ")*";
        printMatchesWholeEntry(ipListsWithForwardSlash, nodeMultiRegexOriginal);
        //注意这里由于 / 的处在使得格式错误，所以需要匹配俩轮才能匹配玩整个字符串，
        // NODES_REGEX_Original 的 "(?:(?:\\s+|,)"  部分确定连接的符号必须是 space 或者 comma .
        // 所以分为 192.168.9.177:28001,192.168.9.177:28002,192.168.9.177:28003 和 192.168.9.177:28004 俩个部分
        printMatchEchoGroups(ipListsWithForwardSlash, nodeMultiRegexOriginal);
        System.out.println("----------------------------ipListsWithForwardSlash-----------------------------------");


        String nodeRegexImprove = "(?:(?:\\s+|,)?)(\\[[^\\[\\].]*\\]|[^:]+):([\\d]+)";
        String nodesMultiRegexImprove = nodeRegexImprove + "(?:(?:\\s+|,)" + nodeRegexImprove + ")*";
        printMatchesWholeEntry(ipLists,nodesMultiRegexImprove);
        //这里使用正确的ip地址列表，可以一次性匹配整个IP列表，不需要分拆，
        // NODES_REGEX 中的  "(?:(?:\\s+|,)" + NODE_REGEX + ")*" 部分表示，ip地址可以 再 出现 0-n 次，
        printMatchEchoGroups(ipLists, nodesMultiRegexImprove);
        System.out.println("----------------------------nodesMultiRegexImprove-----------------------------------");

        // 而填充的分组数据则会以最后一次的填充为准，因此这里的4个组的结果为开始的一堆ip和最后的一对ip。
        // 本质其实是由于贪婪匹配，把第二部分的所有可能的分组都迭代了,如若改成非贪婪的则可以每次只便利最少的数量,
        // 注意因为第二部分的量词修饰为 *（0-n次） ,所以仅有第一部分会匹配，第二部分选择了最小的 0 次。
        String nodeRegexImproveNonGreedy = "(\\[[^\\[\\].]*\\]|[^:]+):([\\d]+)";
        String nodesMultiRegexImproveNonGreedy = nodeRegexImproveNonGreedy + "(?:(?:\\s+|,))(" + nodeRegexImproveNonGreedy + ")*?";
        printMatchEchoGroups(ipLists, nodesMultiRegexImproveNonGreedy);
        System.out.println("----------------------------nodesMultiRegexImproveNonGreedy-----------------------------------");

        //这里使用单ip的模板逐个解析IP
        printMatchEchoGroups(ipLists, nodeRegexImprove);
        System.out.println("----------------------------nodeRegexImprove-----------------------------------");

        // 单独ip测试
        String ipOnlyOneEntry = ",192.168.9.177:28001,";
        String ipRegex = "(?:(?:\\s+|,)?)(.*):([\\d]+)";
        printMatchEchoGroups(ipOnlyOneEntry, ipRegex);
        System.out.println("----------------------------ipOnlyOneEntry-----------------------------------");

    }


    public static void LANATest() {
        System.out.println("------------------look ahead negative assert start---------------------");

        String sysPropStrWithoutEqual = "JVM_OPTIONS=%JVM_OPTIONS% -Dkey=value";
        String sysPropRegexGreedy = ".*JVM_OPTIONS=.*JVM_OPTIONS.*\\s+-D(.*)=(.*)";

        //当出现多个匹配分割字符时，贪婪匹配(greedy),会将最后一个等号之前的内容全部捕获，照成匹配错误。
        String sysPropStrWithEqual = "JVM_OPTIONS=%JVM_OPTIONS% -Dkey=value=";
        printMatchEchoGroups(sysPropStrWithEqual, sysPropRegexGreedy);
        System.out.println("---------------------sysPropRegexGreedy-------------------------------");

        // 对于贪婪匹配的问题，可以使用？将其变为非贪婪匹配
        String sysPropRegexNonGreedy = ".*JVM_OPTIONS=.*JVM_OPTIONS.*\\s+-D(.*?)=(.*)";
        printMatchEchoGroups(sysPropStrWithEqual, sysPropRegexNonGreedy);
        System.out.println("---------------------sysPropRegexNonGreedy-------------------------------");

        // 使用正向否定预查(look ahead negative assert)，将不包含第一个 = 之前的内容取出。
        // 但是这里 ((?!=).*) 部分的 .* 依旧有贪婪匹配，所以仍旧出现了问题，也可以通过增加 ？ 变成非贪婪来解决。
        // 这样子，前面 ((?!=).*?) 的非贪婪组匹配到第一个 = ，而后面的贪婪组则匹配余下所有的内容，共俩个分组
        String sysPropRegexLANA_v1 = ".*JVM_OPTIONS=.*JVM_OPTIONS.*\\s+-D((?!=).*?)=(.*)";
        printMatchEchoGroups(sysPropStrWithEqual, sysPropRegexLANA_v1);
        System.out.println("---------------------sysPropRegexLANA_v1-------------------------------");

        // V2的方式中使用  ((?!=).)  对每一个字符逐个判断其前面是否是 = ，不是则保留该字符的匹配，产生一个分组
        // 然后在用     ( ((?!=).) *) 将上述过程中每一个符合的字符累积起来形成最终的结果，产生一个分组
        // 因此在遇见第二个 = 时匹配不满足 ((?!=).) 匹配结束，没有贪婪匹配的问题。
        // 同时注意这里捕获了 3 个分组，第一个是key，第二个是 = 前的最后一个字符，第三个是value
        // 其中 第二组是 y 是第一阶段最后匹配的单字符，第一组是 key是 (((?!=).)*)累加的结果，
        String sysPropRegexLANA_v2 = ".*JVM_OPTIONS=.*JVM_OPTIONS.*\\s+-D(((?!=).)*)=(.*)";
        printMatchEchoGroups(sysPropStrWithEqual, sysPropRegexLANA_v2);
        System.out.println("---------------------sysPropRegexLANA_v2-------------------------------");

        System.out.println("###########################################################################");
        System.out.println("###########################################################################");
        System.out.println("###########################################################################");

        String jvmOptionStrNormal = "JVM_OPTIONS=%JVM_OPTIONS% -server";
        String jvmOptionStrWithD = "JVM_OPTIONS=%JVM_OPTIONS% -ser-Dver";
        String jvmOptionRegexGreedy = ".*JVM_OPTIONS=.*JVM_OPTIONS.*\\s+((?!-D).*)";

        // 注意贪婪匹配(greedy), .*\s+ 会将最后一个空格之前的内容全部捕获。 .* 匹配了 [% -ser]，而 \\s+ 匹配了 [ ]，所以最后换剩下 ver
        String jvmOptionStrWithSpace = "JVM_OPTIONS=%JVM_OPTIONS% -ser ver";
        printMatchEchoGroups(jvmOptionStrWithSpace, jvmOptionRegexGreedy);
        System.out.println("---------------------jvmOptionRegexGreedy-------------------------------");

        // 处理方法一，因为是 .* 部分贪婪匹配了，将其转变为 .*? 非贪婪即可.
        String jvmOptionRegexNonGreedy = ".*JVM_OPTIONS=.*JVM_OPTIONS.*?\\s+((?!-D).*)";
        printMatchEchoGroups(jvmOptionStrWithSpace, jvmOptionRegexNonGreedy);
        System.out.println("---------------------jvmOptionRegexNonGreedy-------------------------------");

        // 处理方法二，通过对每一个字符检查，确保只匹配在 \s 在前面的非\s字符，使得匹配在第一次空格出现时停止，
        // 然后通过非捕获匹配 ( shy group )忽略其组号占用，这样子做是为了使得捕获组的组号变小。
        // 否则 (((?!\s).)*) 如 sysPropRegexLANA_v2 所述这里会会产生俩个分组，一个是内部的 ((?!\s).) 另一个是起累加的 ((A)*)
        // 而 (?:X) 是非捕获的，所以可以利用他来放弃已经捕获的组，(?:(?!\s).) = A 和 (?:(A)*) 合并来便是  (?:(?:(?!\s).)*)
        // 即在捕获组中可以使用?:标记捕获组将其变为非捕获组从而取消不必要的组号 (X) --->  (?:X) ，
        String jvmOptionRegexLANA = ".*JVM_OPTIONS=.*JVM_OPTIONS(?:(?:(?!\\s).)*)\\s+((?!-D).*)";
        printMatchEchoGroups(jvmOptionStrWithSpace, jvmOptionRegexLANA);
        System.out.println("---------------------jvmOptionRegexLANA-------------------------------");

        System.out.println("###########################################################################");
        System.out.println("###########################################################################");
        System.out.println("###########################################################################");

        Pattern pattern = Pattern.compile(jvmOptionRegexLANA);
        Matcher matcher = pattern.matcher(jvmOptionStrWithSpace);
        StringBuffer stringBuffer = new StringBuffer();
        matcher.reset();
        while (matcher.find()) {
            matcher.appendReplacement(stringBuffer, matcher.group().toUpperCase());
        }
        matcher.appendTail(stringBuffer);
        System.out.println(stringBuffer.toString());
        System.out.println("-------------appendReplacement---------------------");

        Scanner scanner = new Scanner(new BufferedReader(new StringReader(sysPropStrWithoutEqual)));
        scanner.useDelimiter(System.lineSeparator());
        //注意修改定界符，因为Scanner只会依据 定界符 来读取下一个分词，
        // 如果正则表达式中含有定界符的匹配，那么Scanner 的 hasNext() 方法将永远返回false
        while (scanner.hasNext(pattern)) {
            scanner.next(pattern);
            MatchResult matchResult = scanner.match();
            System.out.println(matchResult.group());
        }
        System.out.println("-------------Scanner---------------------");

        System.out.println("------------------look ahead negative assert end---------------------");

    }


    public static void lineSeparatorTest() {

        System.out.println("---------------------lineSeparatorTest start--------------------------------");

//      \r\n and \n is not a end of line for RegularExpression
        String str2 = "1, 0,\n2,\n3,\n,";
        String[] result2 = str2.split("(,$)");
        System.out.println(Arrays.deepToString(result2) + "#" + result2.length);
        System.out.println("-----------------------------------------------------");

        String str3 = "1, 0,\r\n2,\r\n3,\r\n,";
        String[] result3 = str3.split("(,$)");
        System.out.println(Arrays.deepToString(result3) + "#" + result3.length);
        System.out.println("-----------------------------------------------------");

//      deal with dos and unix linefeed at the same time
        String str1 = "1, 0,\r\n2,\r\n3,\r\n,";
        String[] result1 = str1.split("(,\\n)|(,\\r\\n)");
        System.out.println(Arrays.deepToString(result1) + "#" + result1.length);
        for (int i = 0; i < result1.length; ++i) {
            result1[i] = result1[i].replaceAll("(^\\s+)|(\\s+$)|(,$)", "");
        }
        System.out.println(Arrays.deepToString(result1) + "#" + result1.length);
        System.out.println("-----------------------------------------------------");

        // 正则的 .（dot）是特殊字符匹配需要转义，java的 \ (backward slash) 也是特殊字符，需转义
        // 所以匹配正则的.（dot）需要二次转义
        // / (forward slash)
        String str = "   ,n.u.l.l,  \n";
        System.out.println(Arrays.deepToString(str.split(".")) + "#");
        System.out.println(Arrays.deepToString(str.split("\\.")) + "#");

        //百分号无需转义
        System.out.println(" \" \"%%" + "#");

        System.out.println("---------------------lineSeparatorTest end--------------------------------");
    }

    public static void printMatchEchoGroups(String string, String regularExpression) {
        System.out.println("#####[ " + string + "] matched by [" + regularExpression + " ]#####");
        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(string);
        // 使用 while 而非 if 可以不断的匹配知道消耗完所有的字符。
        while (matcher.find()) {
            int count = matcher.groupCount();
            System.out.println("#####groupCount:" + matcher.groupCount());
            for (int i = 0; i < count + 1; ++i)
                System.out.println("#####i=" + i + ":" + matcher.group(i));
        }
    }

    public static void printMatchesWholeEntry(String string, String regularExpression) {
        System.out.println("#####[ " + string + "] matched by [" + regularExpression + " ]#####");
        Pattern pattern = Pattern.compile(regularExpression);
        Matcher matcher = pattern.matcher(string);
        // 检测一次正则匹配是否可以匹配全部的内容
        boolean matches = matcher.matches();
        String info = "#####wholeMatches=";
        info = info + matches;
        System.out.println(info);
    }
}
