package SystemTest;

/**
 * Created by JasonFitch on 5/22/2019.
 */
public class SystemPropertyOrder {


    public static void main(String[] args) {

        //JF 在启动命令行中同时设置两个相同的系统属性时，以最后面的设置为准。
        //java command line args -Dtest=a -Dtest=b
        String property = System.getProperty("test", "def");
        System.out.println(property);

    }

}
