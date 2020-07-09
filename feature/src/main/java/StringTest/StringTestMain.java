package StringTest;

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
}
