package regularexpression;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by JasonFitch on 3/21/2020.
 */
public class RegularExpressionMetaChar {

    public static void main(String[] args) {

        testHyphen();
    }

    public static void testHyphen() {
        String str = "eis/app-client";

        System.out.println("---------end/startWith 【-】  可以匹配到 hyphen【-】 -----------------");
        String regular1 = "^[a-zA-Z0-9][a-zA-Z0-9./-]*$";
        executeMatch(regular1, str);

        //JF 只有连字符在字符组【内部】时,并且出现在两个字符之间时,才能表示字符的【范围】;
        //   如果出字符组的 【开头或者结尾】 ,则只能表示连字符【本身】.
        System.out.println("---------contains【-】  无法匹配到 hyphen【-】 -----------------");
        String regular2 = "^[a-zA-Z0-9][a-zA-Z0-9.-/]*$";
        executeMatch(regular2, str);

        // 可以考虑在字符组内部使用转义字符来处理 hyphen 保证可以与位置无关的匹配到
        System.out.println("---------escape  【-】 可以匹配到 hyphen【-】 -----------------");
        String regular3 = "^[a-zA-Z0-9][a-zA-Z0-9.\\-/]*$";
        executeMatch(regular3, str);

        // 而在字符组【 [some char] 】的外部， hyphen 也只表示其【本身】
        System.out.println("---------在字符组外部 【-】 可以匹配到 hyphen【-】 -----------------");
        String regular4 = "^[a-zA-Z0-9/]*-[a-zA-Z0-9]*$";
        executeMatch(regular4, str);

    }

    public static void executeMatch(String reg, String str) {
        Pattern pattern = Pattern.compile(reg);

        Matcher matcher = pattern.matcher(str);

        while (matcher.find()) {
            String group = matcher.group();
            System.out.println(group);
        }
    }


}
