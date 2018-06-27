package StaticClass;

// import com.sun.org.apache.xpath.internal.operations.String;   这个String 会覆盖 java.lang 下的 String 类导致main方法识别失败！！！
import java.io.IOException;

/**
 * Created by JasonFitch on 6/27/2018.
 */
public class StaticClassTest
{
    public static void main(String[] args)
    {
        try
        {
            //System.out.println(StaticInner.innerA);
            System.out.println(outterA);

            /*

            注释输出：
                outterStaticBlock
                outterGetVarA
                0

            未注释输出：
                outterStaticBlock
                outterGetVarA
                innerStaticBlock
                innerGetVarA
                1
                0

            即，静态内部类如果不调用他是不会随着外部类的加载而加载的。

             */



            System.in.read();
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    static
    {
        System.out.println("outterStaticBlock");
    }

    public static int outterA = getOutterVarA();

    public static int getOutterVarA()
    {
        System.out.println("outterGetVarA");
        return 0;
    }

    public static class StaticInner
    {
        static
        {
            System.out.println("innerStaticBlock");
        }

        public static int innerA = getInnerVarA();

        public static int getInnerVarA()
        {
            System.out.println("innerGetVarA");
            return 1;
        }

    }
}
