package FloatTest;

import java.util.Date;

public class FloatTest {

    public static void main(String[] args) {

        System.out.println("################### testRange ###################");
        testRange();

        System.out.println("################### testPrecision ###################");
        testPrecision();

        System.out.println("################### testRepresentDate ###################");
        testRepresentDate();

        System.out.println("################### testRepresentInteger ###################");
        testRepresentInteger();
    }

    private static void testRange() {
        System.out.println("---- build-in bound value ----");
        System.out.println("Float.SIZE" + " ---> " + Float.SIZE);
        System.out.println("Float.MAX_VALUE" + " ---> " + Float.MAX_VALUE);

        //最小值存在 normal 和 subnormal 两种情况
        System.out.println("Float.MIN_NORMAL" + " ---> " + Float.MIN_NORMAL);
        System.out.println("Float.MIN_VALUE" + " ---> " + Float.MIN_VALUE);

        System.out.println(Float.floatToIntBits(Float.MIN_VALUE));
        System.out.println(Float.floatToRawIntBits(Float.MIN_VALUE));
        System.out.println(Float.toHexString(Float.MIN_VALUE));
    }

    private static void testPrecision() {
        //不超出精度时，其值可以准确的表达，
        System.out.println("---- 6 digits float with 1 decimal ----");
        float floatWithOneDecimal = 123456.1f;
        System.out.println(floatWithOneDecimal);

        //小数部分在超出精度时，其值无法准确的表达，
        System.out.println("---- 6 digits float with 6 decimal ----");
        float floatWithSixDecimal = 123456.123456f;
        System.out.println(floatWithSixDecimal);

        //即使完全是整数，在超出精度时，其值也无法准确的表达，
        System.out.println("---- 21 digits integer string bigger ----");
        String stringBigger = "123456789012345678909";
        float parseFloatBigger = Float.parseFloat(stringBigger);
        System.out.println(stringBigger + " ---> " + parseFloatBigger);

        //这里导致两个大小不一样的数字在转化为 float 一样了。
        System.out.println("---- 21 digits integer string smaller ----");
        String stringSmaller = "123456789012345678901";
        float parseFloatSmaller = Float.parseFloat(stringSmaller);
        System.out.println(stringSmaller + " ---> " + parseFloatSmaller);
    }

    private static void testRepresentDate() {
        //这里导致两个大小明显差距巨大的数字在转化为 date 后，date 的时间一样的
        //所以虽然 float 能容纳这么大的数字，但是使用时精度的缺失会导致时间的误差特别大。
        float smaller = 1234567890_1234567890_1234567890_000000000f;
        System.out.println(smaller);
        printDateValue((long) smaller);

        float bigger = 1234567890_1234567890_1234567890_123456789f;
        System.out.println(bigger);
        printDateValue((long) bigger);
    }

    private static void printDateValue(long value) {
        Date powDate = new Date(value);
        System.out.println(powDate);
    }

    private static void testRepresentInteger() {
        //float 第一个不能精确表达的整数
        double pow = Math.pow(2, 24) + 1;
        System.out.println(pow);
        printDateValue((long) pow);

        //float 的第一个不能准确表达的整数，
        // 这是因为 float 的 significand 为只有显示的 23bits + 隐式的 1bits = 24bits 导致的，
        //和 jdk 的结果不一致， jdk 的为 0x1.0p24
        //Round toward Positive Infinity
        //Decimal value: 16777217.0
        //Normalized binary value: 1.000000000000000000000001b24
        //Normal	0 (+)	10010111 (+24)	1.00000000000000000000001 (1.00000011920928955078125)
        float powFloat = 16777216 + 1;
        System.out.println(powFloat);
        System.out.println(Float.toHexString(powFloat));

        //这里可以看见，每当数字增大一点，优先增加有效值部分，如果有效值部分不够才需要用指数来进行扩大范围
        //和 jdk 的结果一致
        //Round toward Positive Infinity
        //Decimal value: 16777216.0
        //Normalized binary value: 1.000000000000000000000000b24
        //Normal	0 (+)	10010111 (+24)	1.00000000000000000000000 (1.0)
        powFloat = 16777216;
        System.out.println(powFloat);
        System.out.println(Float.toHexString(powFloat));

        //这里发现 2^23 * 1.99999988079071044921875 约等于 16777215.0，
        // 即通过指数的部分来倍化，包含扩大或缩小，有效值的部分用来表示真实的结果的，所以表示值和有效值有倍率关系
        //和 jdk 的结果一致
        //Round toward Positive Infinity
        //Decimal value: 16777215.0
        //Normalized binary value: 1.11111111111111111111111b23
        //Normal	0 (+)	10010110 (+23)	1.11111111111111111111111 (1.99999988079071044921875)
        powFloat = 16777216 - 1;
        System.out.println(powFloat);
        System.out.println(Float.toHexString(powFloat));

        //和 jdk 的结果不一致， jdk 的为 0x1.0p25
        //Round toward Positive Infinity
        //Decimal value: 33554432.0
        //Normalized binary value: 1.0000000000000000000000000b25
        //Normal	0 (+)	10011000 (+25)	1.00000000000000000000001 (1.00000011920928955078125)
        powFloat = 16777216 * 2;
        System.out.println(powFloat);
        System.out.println(Float.toHexString(powFloat));
        //随后发现，如果我们使用 Rounding Mode 为 Round toward Negative Infinity，就和 jdk 的一致了，
        //Round toward Negative Infinity
        //Decimal value: 33554432.0
        //Normalized binary value: 1.0000000000000000000000000b25
        //Normal	0 (+)	10011000 (+25)	1.00000000000000000000000 (1.0)

        /**
         * {@link java.lang.Float#valueOf(java.lang.String)}
         *      * this exact numerical value is then
         *      * conceptually converted to an "infinitely precise"
         *      * binary value that is then rounded to type {@code float}
         *      * by the usual round-to-nearest rule of IEEE 754 floating-point
         *      * arithmetic, which includes preserving the sign of a zero
         *      * value.
         *      *
         *      * Note that the round-to-nearest rule also implies overflow and
         *      * underflow behaviour; if the exact value of {@code s} is large
         *      * enough in magnitude (greater than or equal to ({@link
         *      * #MAX_VALUE} + {@link Math#ulp(float) ulp(MAX_VALUE)}/2),
         *      * rounding to {@code float} will result in an infinity and if the
         *      * exact value of {@code s} is small enough in magnitude (less
         *      * than or equal to {@link #MIN_VALUE}/2), rounding to float will
         *      * result in a zero.
         */
    }

}
