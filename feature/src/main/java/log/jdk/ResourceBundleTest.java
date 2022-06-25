package log.jdk;

import java.text.MessageFormat;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class ResourceBundleTest {

    public static void main(String[] args) {

        //避免由于 class-based 优先加载，导致误将与 log.jdk.ResourceBundleTest 同名的类但不是 ResourceBundle 实现类的本测试类当作 ResourceBundle 而加载
        List<String> formatProperties = ResourceBundle.Control.FORMAT_PROPERTIES;
        ResourceBundle.Control control = ResourceBundle.Control.getControl(formatProperties);

        Locale locale = Locale.US;
        ResourceBundle bundle = ResourceBundle.getBundle("log.jdk.ResourceBundleTest", locale, control);

        Enumeration<String> keys = bundle.getKeys();
        int index = 0;
        while (keys.hasMoreElements()) {
            String key = keys.nextElement();

            String string = bundle.getString(key);
            Object[] generateArgs = generateArgs(++index);
            //格式化 ResourceBundleTest.properties 中形如 {0} {1} 这样的参数占位符。
            String format = MessageFormat.format(string, generateArgs);
            System.out.println(format);
        }
    }

    private static String[] generateArgs(int index) {
        String[] strings = new String[index];
        while (index-- > 0) {
            strings[index] = "substitute-" + index;
        }
        return strings;
    }

}
