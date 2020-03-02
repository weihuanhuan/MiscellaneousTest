package regularexpression;

import java.util.Arrays;

/**
 * Created by JasonFitch on 1/29/2018.
 */
public class RegularExpressionLineSeparator {

    public static void main(String[] args) {
        lineSeparatorTest();
    }

    public static void lineSeparatorTest() {
        System.out.println("---------------------regular abstract end of line position test--------------------------------");
        //$ is not a end of line for RegularExpression
        //  $在正则中抽象的表示一行的末尾位置, 所以按照 @$ 分割表示的是按照 行末尾(位置)为@(字符) 来分割，
        //  由于 $ 是抽象的, 只产生位置匹配，不发生字符匹配，而 @ 会产生字符匹配。
        //  所以分割实际按照了 末尾的@ 进行, 导致 \n 还是保留下来, 且结果集中有2个元素, 只匹配到了 末尾的@ 出现了一次
        //  这里结果集中的第二个元素是 \n ，为换行，而不是 空字符串，所以结果得以保留。
        String strUnix = "1@ 0@\n2@\n3@\n@\n";
        String[] splitUnix = strUnix.split("(@$)");
        System.out.println(Arrays.deepToString(splitUnix) + "#splitUnix.length=" + splitUnix.length);
        System.out.println("--------------");

        String strWindows = "1@ 0@\r\n2@\r\n3@\r\n@\r\n";
        String[] splitRegEnd = strWindows.split("(@$)");
        System.out.println(Arrays.deepToString(splitRegEnd) + "#splitRegEnd.length=" + splitRegEnd.length);

        System.out.println("---------------------regular linefeed test--------------------------------");
        //deal with dos and unix linefeed at the same time
        //  这里的 \n(unix) 和 \r\n(windows) 是正则中真正表示换行的 元字符，所以使用他们来分割表示 按照 每一个新行来分割，
        //  由于 \n \r\n 是具体的，可以产生字符匹配，
        //  所以分割后的结果中 \n 就没有了，且结果集中有3个元素，匹配到了 4个新行元字符 \n,
        //  本来结果元素集中应该有5个元素，但是元素为 空字符串 的结果被丢弃了, 即最后一组 @\n 两侧的元素。
        String[] splitLinefeed = strWindows.split("(@\\n)|(@\\r\\n)");
        System.out.println(Arrays.deepToString(splitLinefeed) + "#splitLinefeed.length=" + splitLinefeed.length);
        System.out.println("--------------");
        for (int i = 0; i < splitLinefeed.length; ++i) {
            splitLinefeed[i] = splitLinefeed[i].replaceAll("(^\\s+)|(\\s+$)|(@$)", "");
        }
        System.out.println(Arrays.deepToString(splitLinefeed) + "#splitLinefeed.length=" + splitLinefeed.length);

        System.out.println("---------------------regular dot sign test--------------------------------");
        // 正则的 .（dot）是特殊字符匹配需要转义，java的 \ (backward slash) 也是特殊字符，需转义
        // 所以匹配正则的.（dot）需要二次转义
        // / (forward slash)
        String strDot = "   @n.u.l.l@  \n";
        System.out.println(Arrays.deepToString(strDot.split(".")) + "#");
        System.out.println(Arrays.deepToString(strDot.split("\\.")) + "#");

        System.out.println("---------------------escape percent sign test--------------------------------");
        //百分号无需转义
        System.out.println(" \" \"%%" + "#");
    }
}
