package regularexpression;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpressionInverse {

    public static void main(String[] args) {

        testInverse();

    }

    private static void testInverse() {

        //原始的集合
        List<String> stringList = new ArrayList<>();
        stringList.add("org.apache.jasper.servlet.JasperInitializer");
        stringList.add("org.apache.tomcat.websocket.server.WsSci");
        stringList.add("org.springframework.web.SpringServletContainerInitializer");

        //需要排除的
        String className = "org.apache.jasper.servlet.JasperInitializer";

        //由于没有捕获匹配字符的出现，所以最后的匹配结果都是 空字符串
        String regular = "(?!" + className + ")";

        //补充了一个匹配字符，由于正向肯定预查不会消耗匹配字符，而 【.】(dot) 则只消耗一个字符，
        //所以对于原始字符串中的每一个字符，都会进行预查的检测，导致匹配效率低下，
        //我们可以使用量词来增加一次匹配的字符消耗数量，从而加快匹配效率 【.*】(匹配 任意个dot字符，其默认为贪婪模式，可一次性消耗所有的字符)
        regular = ".(?!" + className + ")";
        matchList(stringList, regular);

        //而根据情况，对于我们这个检测，可以让正则在开头位置检测预查，所以只有一个位置符合正则，此时只匹配一次执行效率就高多了。
        regular = "^(?!" + className + ")";
        matchList(stringList, regular);

        //而当我们需要匹配的结果时，可以通过添加捕获匹配字符【.*】，直接将整行直接全部消耗掉。
        //其中 【^】消耗了开头，随后【?!】部分断言了本行是否符合条件，最后【.*】将本行结果全部捕获匹配掉。
        regular = "^(?!" + className + ").*";
        matchList(stringList, regular);

    }

    private static void matchList(List<String> stringList, String regular) {
        System.out.println("###############################");
        for (String string : stringList) {
            System.out.println("-------------------------------");
            System.out.println(String.format("match [%s] by [%s]", string, regular));
            printResult(regular, string);
        }
    }

    private static void printResult(String regular, String string) {
        Pattern compile = Pattern.compile(regular);
        Matcher matcher = compile.matcher(string);
        //如果关心正则每次回溯产生的结果，可以使用 while 来输出对一个输入字符串的所有匹配结果
        while (matcher.find()) {
            MatchResult matchResult = matcher.toMatchResult();
            System.out.println("matched:" + matchResult.group());
        }
    }

}
