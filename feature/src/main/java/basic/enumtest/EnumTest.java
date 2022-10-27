package basic.enumtest;

/**
 * Created by JasonFitch on 12/19/2018.
 */
public class EnumTest {

    //JF 对于内部枚举类型来说，其本身就是static,其构造方法也是private的
    public static enum InnerEnum {
        APPLE("APPLE"), ORANGE("ORANGE"), DUMMY("DUMMY");

        private final String name;

        private InnerEnum(String name) {
            this.name = name;
        }


        public static InnerEnum getEnumByName(String name) {
            if (name.equalsIgnoreCase(APPLE.name)) {
                return APPLE;
            }
            if (name.equalsIgnoreCase(ORANGE.name)) {
                return ORANGE;
            }
            return DUMMY;
        }
    }

}

