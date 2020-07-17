package escape;

/**
 * Created by JasonFitch on 7/17/2020.
 */
public class EscapeTest {

    public static void main(String[] args) {

        String className = "escape$1";

        String codeEscape  = "escape\\$1";
        boolean matches = className.matches(codeEscape);
        System.out.println(matches);

        //当被转移的字符已经是保存在 String 中后，他们们就已经是转后的含义了。
        //转义只有在我们写 String 字面量的时候为了区别 转义 和 非转义的字符时才有意义
        //所以字符串内部的值为 "escape\\$1" 时，其等价的字面量 String 是 "escape\\\\$1"

        //这点对于从文件中读取数据并构建 String 来说是很有意义的，当文件中包含了 "\" 字符时，
        //其直接按照转义后的字符存于 String 对象的内部，而我需我们进行二次转义了，
        //这和在 源代码 中需要我们使用 "\" 来转义 "\" 是有很大区别的。
        System.out.println(codeEscape.toCharArray());

        String codeEscape2  = "escape\\\\$1";
        boolean matches2 = className.matches(codeEscape2);
        System.out.println(matches2);

        System.out.println(codeEscape2.toCharArray());

    }

}
