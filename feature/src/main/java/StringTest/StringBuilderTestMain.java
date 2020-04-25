package StringTest;

/**
 * Created by JasonFitch on 1/7/2019.
 */
public class StringTestMain {


    public static void main(String[] args) {

        String string1 = "ABC.DEF";
        StringBuilder stringBuilder = new StringBuilder(string1);

        stringBuilder.insert(string1.indexOf('.'), "XXXX");
        stringBuilder.insert(string1.indexOf('.'), "YYYY");
        System.out.println(stringBuilder.toString());

    }
}
