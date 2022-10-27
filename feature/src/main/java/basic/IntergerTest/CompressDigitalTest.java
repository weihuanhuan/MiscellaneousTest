package basic.IntergerTest;

public class CompressDigitalTest {


    public static void main(String[] args) {

        int compressDigitalLength =2;
        String regex = "\\d{" + compressDigitalLength + ",}";
        System.out.println(regex);

        String strWithDigital= "asdasdasd 123123123";
        System.out.println(strWithDigital);

        String s1 = strWithDigital.replaceAll(regex, "[COMPRESSED_DIGITAL]");
        System.out.println(s1);

        //JF java.lang.String.replaceAll 在解释他的参数 replacement 时，要替换的值里面的符号 $ 是有特殊含义的，
        // 具体看方法的注释,如果要使用的话需要进行转义处理
        String s2 = strWithDigital.replaceAll(regex, "\\${COMPRESSED_DIGITAL}");
        System.out.println(s2);

        // $ 代表引用先前被 regex 所匹配到的内容，支持正则表达式的匹配组模式
        String s3 = strWithDigital.replaceAll(regex, "$0{COMPRESSED_DIGITAL}");
        System.out.println(s3);

    }

}
