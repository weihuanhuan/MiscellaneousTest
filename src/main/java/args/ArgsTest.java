package args;

import java.util.Arrays;

/**
 * Created by JasonFitch on 1/10/2018.
 */
public class ArgsTest {

    public static void main(String[] args) {

        System.out.println(Arrays.deepToString(args));

        System.out.println("------------");
        System.out.println(Arrays.deepToString(new String("").split(":")));
        System.out.println(new String(":").split(":").length);

    }
}
