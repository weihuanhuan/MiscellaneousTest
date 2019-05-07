package picture;

/**
 * Created by JasonFitch on 5/6/2019.
 */
public class Int2FloatTest {

    public static void main(String[] args) {

        // 型强制转换优先级高于四则运算。
        int a = 85;
        float r = (float) a / 100;
        System.out.println(r);

    }
}
