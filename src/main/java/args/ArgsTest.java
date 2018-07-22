package args;

import java.util.Arrays;
import java.util.Properties;

/**
 * Created by JasonFitch on 1/10/2018.
 */
public class ArgsTest {

    public static void main(String[] args) {

        System.out.println(Arrays.deepToString(args));
        System.out.println(args.getClass());
        System.out.println(args.length);
//      调用参数为 java -DhasParemeter=true args.ArgsTest parameter1
//      注意这里的输出为 1 , 与 C 语言不同，java 中 args 的长度即为提供参数的长度，不包括程序名本身
        System.out.println(System.getProperty("hasParameter"));
//      这里输出为 true

        System.out.println("------------");
        System.out.println(Arrays.deepToString(new String(":").split(":")));
        System.out.println(new String(":").split(":").length);

    }
}
