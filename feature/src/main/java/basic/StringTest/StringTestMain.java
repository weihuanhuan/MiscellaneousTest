package basic.StringTest;

import java.util.Arrays;
import java.util.StringTokenizer;

/**
 * Created by JasonFitch on 1/7/2019.
 */
public class StringTestMain {


    public static void main(String[] args) {

        System.out.println("-------- stringBuilderTest -------------");
        stringBuilderTest();

        System.out.println("-------- stringTokenizerTest -------------");
        stringTokenizerTest();

        System.out.println("-------- stringSplitTest -------------");
        stringSplitTest();
    }

    public static void stringBuilderTest() {
        String string1 = "ABC.DEF";
        StringBuilder stringBuilder = new StringBuilder(string1);

        stringBuilder.insert(string1.indexOf('.'), "XXXX");
        stringBuilder.insert(string1.indexOf('.'), "YYYY");
        System.out.println(stringBuilder.toString());
    }

    public static void stringTokenizerTest() {
        String message = "info :: file:///root/path :: user ";
        String delimiters = " :: ";

        //注意 tokenizer 的 delim 参数含义是将 delim 中的每一个字符作为 token ，而不是将 delim 这个完整的字符串作为 token
        //故这里实际的分割符是 colon 或者是 space，虽然有4个字符，但是他们中只有两个是不同的。
        //所以这里的结果并不是 3个域，而是分割成了 4个 域，其中的 file 后面的单个 : 也是分割符。
        StringTokenizer stringTokenizer = new StringTokenizer(message, delimiters);
        while ((stringTokenizer.hasMoreTokens())) {
            System.out.println(stringTokenizer.nextToken());
        }
    }

    private static void stringSplitTest() {
        String listString = "com.xyz.,*,com.abc";

        //Exception in thread "main" java.util.regex.PatternSyntaxException: Dangling meta character '*' near index 0
//        String[] splitWithoutSpace = listString.split("*, *");
//        String stringWithoutSpace = Arrays.toString(splitWithoutSpace);
//        System.out.println(stringWithoutSpace);

        //对于正则表达式来说 * asterisk 是一个 meta character ，其拥有特殊的含义。
        //其含义是 重复匹配其前面的表达式出现零次或者是多次。
        //而 String.split 的参数是一个正则表达式，其实其参数不能直接是 * 开头，这样子作为量词的 * 就会缺少签名的表达式，导致正则语法错误
        //下面例子中的正确用法是用 逗号包括其前后任何数量空格 的字符串作为分割符来分割原始的字符串。
        //在正则表达式中，如果我们要匹配 【*】 分割，那么对其进行转义处理为 【\\*】 就可以了。
        String[] splitWithSpace = listString.split(" *, *");
        String stringWithSpace = Arrays.toString(splitWithSpace);
        System.out.println(stringWithSpace);

    }
}
