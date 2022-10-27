package collection.ArrayTest;

/**
 * Created by JasonFitch on 3/27/2019.
 */
public class ArrayLengthTest {

    private static StackTraceElement[] stackTraceElements;

    public static void main(String[] args) {

        if (null == stackTraceElements) {
            System.out.println("handling stackTraceElements");
            int depth = getStackTrackElementsDepth();
            stackTraceElements = new StackTraceElement[depth];
            for (int i = 0; i < depth; ++i) {
                stackTraceElements[i] = getStackTrackElement(i);
            }
        }

        //JF 长度为零的数组 length 方法的调用不会NPE，可以用来避免是否 null 的判断。
        System.out.println(stackTraceElements.length);

    }


    private static int getStackTrackElementsDepth() {
        return 0;
    }

    private static StackTraceElement getStackTrackElement(int index) {
        return new StackTraceElement("className","methodName","fileName",16);
    }

}
