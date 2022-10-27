package basic.IntergerTest;

import javax.sql.DataSource;
import java.sql.Driver;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.concurrent.TimeUnit;

/**
 * Created by JasonFitch on 1/7/2019.
 */
public class FormatDigitalTest {

    public static void main(String[] args) {

        parse();

        System.out.println();

        format(2500);
    }

    public static void parse() {
        //JF java 可以在数字中使用下划线，他们的唯一作用就是给数字分组便于阅读长数字，实际上这些符号相当于不存在的。
        int i = 1_1_1;
        System.out.println(i);
        System.out.println(111 == i);

        //null string 不能解析为 zero
        try {
            String stringInteger = "";
            int stringIntegerValue = Integer.parseInt(stringInteger);
            System.out.println(stringIntegerValue);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void format(int digital) {
        //java Format
        NumberFormat formatter = new DecimalFormat("0000000");
        String number = formatter.format(digital);
        System.out.println("Number with lading zeros: " + number);

        //java String
        String format = String.format("%07d", digital);
        System.out.println(format);

        // C style
        System.out.printf("%07d", digital);
    }
}
