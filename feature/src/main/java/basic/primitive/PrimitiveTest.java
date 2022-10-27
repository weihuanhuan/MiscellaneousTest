package basic.primitive;

/**
 * Created by JasonFitch on 2/25/2019.
 */
public class PrimitiveTest {


    public static void main(String[] args) {

        int i1 = 0xff;
        String binaryString = Integer.toBinaryString(i1);
        System.out.println(i1);
        System.out.println(binaryString);
        //其实0xff是int类型的，所以在转换为char时，系统将32位的int转换为了8位的byte
        byte c1 = (byte) 0xff;
        String cbinaryString = Integer.toBinaryString(c1);
        System.out.println(c1);
        System.out.println(cbinaryString);

        //这里字面量-127是int型，4个字节，会自动转化为byte的一个字节
        byte b = -127;
        System.out.println("不&:" + Integer.toHexString(b));
        //这里b是byte类型，在和0xff，int类型，按位与时，自身需要先按符号补位扩展成int类型，然后在执行 & 运算。
        System.out.println("要&:" + Integer.toHexString(b & 0xff));

//        其实是从数字类型扩展到较宽的类型时，补零扩展还是补符号位扩展的问题
//        这是因为Java中只有有符号数，当byte扩展到short, int时，即正数都一样，
//        因为为符号位是0，所以无论如何都是补零扩展；但负数补零扩展和按符号位扩展结果完全不同。

//        补符号数，原数值不变。
//        补零时，相当于把有符号数看成无符号数，比如-127 = 0x81，看成无符号数就是129， 256 + （- 127）

//        对于有符号数，从小扩展大时，需要用&0xff这样方式来确保是按补零扩展。
//        而从大向小处理，符号位自动无效，所以不用处理。

//        也就是说在byte向int扩展的时候，自动转型是按符号位扩展的，这样子能保证十进制的数值不会变化，
//        而&0xff是补0扩展的，这样子能保证二进制存储的一致性，但是十进制数值已经发生变化了。
//        也就是说按符号位扩展能保证十进制数值不变，补0扩展能保证二进制存储不会变。
//        而正数可以说是既按符号位扩展，又是补0扩展，所以在二进制存储和十进制数值上都能保证一致。
    }
}
