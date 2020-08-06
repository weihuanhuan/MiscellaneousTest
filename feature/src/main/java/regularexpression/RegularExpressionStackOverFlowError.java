package regularexpression;

/**
 * Created by JasonFitch on 8/5/2020.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegularExpressionStackOverFlowError {

    public static void main(String[] args) throws IOException {

        System.out.println("################# largeFileTest ####################");
        largeFileTest();
        System.out.println();

        System.out.println("################# smallFileTest ####################");
        smallFileTest();
        System.out.println();

    }

    public static void largeFileTest() throws IOException {
        String string = readResourceFileAsString("regular/large.css");

        //匹配资源内容
        //这个正则表达式来源于：yuicompressor 框架的 css 压缩器的实现方法中
        //com.yahoo.platform.yui.compressor.CssCompressor#compress
        //这里要匹配出由 '' 或者 "" 包围的字符串，即用户的输出内容的提取，这是不可变化的。
        //Exception in thread "main" java.lang.StackOverflowError
        Pattern p = Pattern.compile("(\"([^\\\\\"]|\\\\.|\\\\)*\")|(\'([^\\\\\']|\\\\.|\\\\)*\')");

        //在 dotall 模式 .(period)符号可以匹配任何字符，包括换行符，也就是可以进行跨行匹配了，
        //但是导致 StackOverFlowError 的字符串是一个超长行，所以 dotall 模式不会对他有什么改善的。
        //Exception in thread "main" java.lang.StackOverflowError
        p = Pattern.compile("(\"([^\\\\\"]|\\\\.|\\\\)*\")|(\'([^\\\\\']|\\\\.|\\\\)*\')", Pattern.DOTALL);

        //使用占用模式匹配，来禁止回溯，避免 jdk 递归
        //对于正则表达式而言对于 贪婪匹配 的处理是需要回溯匹配的，
        //而 debug 发现 jdk 的正则表达式在 贪婪量词匹配 时是使用 递归 来处理正则的 回溯匹配 的，
        //那么对于 (.)* 这样的正则结构匹配时就会对 被字符串 中的 每一个字符 进行递归的检测，检测方法是下面的个函数
        //Pattern.CharProperty.match()

        //因此当字符的数量很多时， jdk 的实现就会产生巨大的递归层次导致最终 StackOverFlow
        //我们只要避免 jdk 匹配时的递归处理就可以了，那么我们就需要使用不会回溯的正则匹配模式就可以了。
        //而 jdk 的实现对于正则的量词修饰符 +(占有模式) 的匹配是非递归的，所以我们只要在量词后面添加 + 来限制其不回溯就好了。
        p = Pattern.compile("(\"([^\\\\\"]|\\\\.|\\\\)*+\")|(\'([^\\\\\']|\\\\.|\\\\)*+\')");

        //另外正则对于量词修饰符的 ?(非贪婪模式) 的匹配也是会回溯的，所以使用 ? 来限制 * 的重复匹配依旧会使 jdk 递归产生 StackOverFlow
        p = Pattern.compile("(\"([^\\\\\"]|\\\\.|\\\\)*?\")|(\'([^\\\\\']|\\\\.|\\\\)*?\')");

        //使用 (?:) (非捕获匹配),来去除无用分组，避免 jdk 记录匹配组的信息
        //这里我们最后只使用 Matcher.group() 来获取整个匹配结果，同时也没有使用任何匹配组的信息，
        //为了减少 jdk 处理 regex 时对于组信息的处理，我们可以使用 非捕获组来减少这些处理负担
        p = Pattern.compile("(?:\"(?:[^\\\\\"]|\\\\.|\\\\)*+\")|(?:\'(?:[^\\\\\']|\\\\.|\\\\)*+\')");

        System.out.println(p);
        printResult(p, string);

    }

    public static void smallFileTest() throws IOException {
        String string = readResourceFileAsString("regular/small.css");

        Pattern p = Pattern.compile("(?:\"(?:[^\\\\\"]|\\\\.|\\\\)*\")|(?:\'(?:[^\\\\\']|\\\\.|\\\\)*\')");

        //上面的正则表达式取出调 java 转义后的正则表达式为 (?:"(?:[^\\"]|\\.|\\)*") | (?:'(?:[^\\']|\\.|\\)*')
        //其分为两个部分，其前半部分处理 "" 中的字符串，后半部分处理 '' 中的字符串，他们的匹配规则是相同的，故我们只看前半部分来分析
        // [^\\"] 其匹配了 ""  包围的字符串，该字符串中不允许出现 " ，以保证字符串是闭合的，或者 \ 以保证最后的字符串闭合标志 " 不会被转义到而失效
        // \\. 但是对于 \ 来说，如果是出现了 \. 的形式则是合法的，因为 \ 已经转义了其他的字符对于字符串闭合标记是安全的了。
        // \\ 最后就是这一段有问题，他和前面的 [^\\"] 中禁止 \ 单独出现在产生了冲突，
        // 导致可以匹配到 "va\"lue1\" 这样形式的字符串，而实际上这个字符串是一个非闭合的，
        // 如果去掉 \\ 这一段，那么我们就不会匹配到他了，只有标准的闭合字符串形式，如 "val\"ue" 才能被匹配到，中间的 " 已经被 \ 转义了。

        //去除非闭合  "" 以及 '' 的内容
        //通过上面的分析我们这里为了完美匹配闭合的字符串结构，我们去掉了第三段的 \\\\ 的匹配。
        p = Pattern.compile("(?:\"(?:[^\\\\\"]|\\\\.)*\")|(?:\'(?:[^\\\\\']|\\\\.)*\')");

        //或者直接原始正则对 * 量词修饰符添加上 +(占有匹配) 标记也可以不匹配没有闭合的字符串
        //这里的原因是"\e601\" 这个字符串的 \" 被 \. 占据，而不回溯是的他不能对 \ 进行尝试，所以 \" 的结尾组合被屏蔽了。
        p = Pattern.compile("(?:\"(?:[^\\\\\"]|\\\\.|\\\\)*+\")|(?:\'(?:[^\\\\\']|\\\\.|\\\\)*+\')");

        System.out.println(p);
        printResult(p, string);
    }

    private static void printResult(Pattern pattern, String str) {
        Matcher matcher = pattern.matcher(str);
        for (int i = 1; matcher.find(); i++) {
            System.out.print("Found match #" + i + ":");
            System.out.println(matcher.group());
        }
    }

    private static String readResourceFileAsString(String name) throws IOException {
        //查找资源
        Enumeration<URL> systemResources = ClassLoader.getSystemResources(name);
        boolean has = systemResources.hasMoreElements();
        if (!has) {
            throw new IOException("not found " + name);
        }

        //打开资源
        URL url = systemResources.nextElement();
        URLConnection urlConnection = url.openConnection();

        //读取资源
        String inputLine;
        StringBuilder builder = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
        while ((inputLine = in.readLine()) != null) {
            builder.append(inputLine);
        }
        in.close();

        //转换资源内容为 String
        String String = builder.toString();
        return String;
    }

}
