package enumtest;

import java.util.Arrays;

/**
 * Created by JasonFitch on 7/21/2020.
 */
public class EnumNameTest {

    public static void main(String[] args) {

        nameTests();
    }

    private static void nameTests() {
        Type[] values = Type.values();
        String deepToString = Arrays.deepToString(values);
        System.out.println(deepToString);

        //Enum.name
        //注意每一个 enum 类型的 class 都是 java.lang.Enum 的一个子类，
        //其内部会生成默认的 name 域，并提供 Enum.name() 方法来访问这个 name 的值
        //其默认生成的值就是定义 enum 常量时的实例名字，比如这里的 ANIMAL 其 name() 方法返回 "ANIMAL"
        //当我们要自定以 enum 中的域时一定要避开这个 name 域，防止出现 域同名 的问题，此时 jvm 是不会报错的。
        //但是我们声明的 name 域是无法从 name() 方法中访问的，他被覆盖了，
        //JDK内置的 java.lang.Enum.name
        //我们声明的 enumtest.EnumNameTest.Type.name
        //可以看见 我们声明的 name 域和 jdk 的是两个不同的域，而 name() 方法固定访问 JDK 内置的
        //如果我们要访问自己的 name 域，我们要手动添加相应的方法，当然最好不要重名，我们可以使用其他的域名即可。

        //java.lang.Enum.ordinal 当然这个域也是 JDK 的 enum 实现保留的，我们也不要去重复声明
        String name = values[0].name();
        System.out.println(name);
        int ordinal = values[0].ordinal();
        System.out.println(ordinal);

        //而 jdk 默认的 valueOf 实现也是使用的 jdk 默认的 name 来解析 String 到 enum 常量的对应关系
        Type animalJdkName = Type.valueOf("ANIMAL");
        System.out.println(animalJdkName);

        //如果要使用我们自己的 name 域来解析 String 到 enum 的对应也需要我们手动实现相关的方法。
        //Exception in thread "main" java.lang.IllegalArgumentException: No enum constant enumtest.EnumNameTest.Type.animal
//        Type animalMyName = Type.valueOf("animal");
//        System.out.println(animalMyName);

        //按照我们自定义的域来解析 String 到 enum 实例的对应关系，
        //注意在 我们的子类 enum 实现中我们只能访问到我们自己的 name 对象，
        //因为在 java.lang.Enum 中其域 name 的声明如下,是 private 的
        //private final String name;
        //当然我们在子类中使用 Enum.name()方法还是可以访问父类中有 jdk 定义的 name 域的
        //这个方法是不可覆盖的，所以我们的子类中一定可以调用他，其声明如下，是 final 的
        //public final String name()
        Type enumByName = Type.getEnumByName("animal");
        System.out.println(enumByName);
    }

    private static enum Type {

        ANIMAL("animal"), PLANT("plant"), ABIOTIC("abiotic"), UNKNOWN("unknown");

        private String name;
        private String type;

        Type(String type) {
            this.name = type;
            this.type = type;
        }

        static Type getEnumByName(String name) {
            if (ANIMAL.name.equals(name)) {
                return ANIMAL;
            } else if (PLANT.name.equals(name)) {
                return PLANT;
            } else if (ABIOTIC.name.equals(name)) {
                return ABIOTIC;
            }
            return UNKNOWN;
        }
    }

}
