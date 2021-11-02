package SystemTest.jdk;

public class SystemProperty {

    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            return;
        }

        String systemProperty = JDKUtils.getSystemProperty(args[0]);
        System.out.println(systemProperty);
    }

}
