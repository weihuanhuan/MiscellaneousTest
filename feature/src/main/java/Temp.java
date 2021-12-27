import java.text.DecimalFormat;

public class Temp {

    public static void main(String[] args) {

        double d = 00011.1;
        String format = new DecimalFormat("0.00").format(d);
        System.out.println(format);


        long round = Math.round(00123.123d);
        System.out.println(round);

        test1();

        test2();
    }

    private static void test1() {
        boolean a = true;
        boolean b = false;
        boolean c = false;
        b |= a;
        b |= c;
        System.out.println(b);
    }

    private static void test2() {
        boolean a = true;
        boolean b = false;
        boolean c = true;
        b &= a;
        b &= c;
        System.out.println(b);
    }

}
