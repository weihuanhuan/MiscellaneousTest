package reflect.main;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReflectMainTest {

    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {

        if (args != null && args.length > 0) {
            System.out.println(Arrays.deepToString(args));
            return;
        }

        List<String> list = new ArrayList<>();
        list.add("arg1");
        list.add("arg1");
        String[] toArray = list.toArray(new String[0]);

        Method main = ReflectMainTest.class.getMethod("main", String[].class);

        boolean bool = new String[]{} instanceof Object[];
        System.out.println(bool);

        //JF need to wrap String[] in an Object[] due to array covariance
        //Exception in thread "main" java.lang.IllegalArgumentException: wrong number of arguments
        //main.invoke(null, toArray);
        main.invoke(null, new Object[]{toArray});
    }
}
