package basic.StringTest;

import basic.StringTest.object.PropertyInfo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StringMemoryTest {

    private List<String> stringListLong = new ArrayList<>();

    private List<String> stringListShort = new ArrayList<>();

    private List<String> stringListRecorder = new ArrayList<>();

    private List<Object> objectListRecorder = new ArrayList<>();

    public static void main(String[] args) throws InterruptedException, IOException, ClassNotFoundException {

        StringMemoryTest stringMemoryTest = new StringMemoryTest();

        System.out.println("-------- initStringList -------------");
        stringMemoryTest.initStringList();

        System.out.println("-------- newStringTest -------------");
        stringMemoryTest.newStringTest();

        System.out.println("-------- newStringWithClassTest -------------");
        stringMemoryTest.newStringWithClassTest();

        System.out.println("-------- newStringWithSerializableTest -------------");
        stringMemoryTest.newStringWithSerializableTest();

        System.out.println("-------- sleep -------------");
        TimeUnit.SECONDS.sleep(60 * 60);

        System.out.println("-------- printStringList -------------");
        stringMemoryTest.printStringList();
    }

    /**
     * 在 java 中，数组也是一个对象，其内部元素的多少不影响其自身对象的大小，其内部元素都是通过引用来指向真实元素的。
     * 另外 java 的数组有一些额外的内存开销，比如对象头信息，数组长度信息，数组元素的引用信息等。
     */
    private void initStringList() {
        int longCount = 1;
        fillStringList(longCount, stringListLong);

        int shortCount = 50;
        fillStringList(shortCount, stringListShort);
    }

    private void fillStringList(int count, List<String> stringList) {
        int index = count;
        while (index-- > 0) {
            stringList.add(String.valueOf(index));
        }
    }

    private void printStringList() {
        printStringList(stringListLong);
        printStringList(stringListShort);

        printStringList(stringListRecorder);
    }

    private void printStringList(List<String> stringList) {
        if (stringList == null) {
            System.out.println("stringList ==null");
            return;
        }

        int size = stringList.size();
        System.out.println("stringList.size:");
        System.out.println(size);

        String toString = stringList.toString();
        System.out.println("stringList.toString:");
        System.out.println(toString);

        String deepToString = Arrays.deepToString(stringList.toArray());
        System.out.println("Arrays.deepToString:");
        System.out.println(deepToString);
    }

    /**
     * 对于数组来说，
     * 1.字面量的字符串会使用字符串常量池中的字符串对象
     * 2.字面量拼接的字符串在编译为 class 时便会被合并，合并后等效一个完整的字面量字符串。
     * 3.当我们直接调用 new String 时，该字符串对象对在堆中从新分配，因此其不会使用字符串常量池的字符串对象，
     * 即使被构造的字符串的值与某个字符串常量池中的字符串相同时也会重新分配。
     * <p>
     * 所以下面的测试中堆中只会有 5 个不同的 "test-string" 字符串对象。
     */
    private void newStringTest() {
        String string11 = "test-string";
        String string12 = "test-string";

        String string13 = "test-" + "string";
        String string14 = "test-" + "string";

        String string21 = new String("test-string");
        String string31 = new String("test-string");

        String string41 = new String("test-" + "string");
        String string51 = new String("test-" + "string");

        stringListRecorder.add(string11);
        stringListRecorder.add(string12);
        stringListRecorder.add(string13);
        stringListRecorder.add(string14);
        stringListRecorder.add(string21);
        stringListRecorder.add(string31);
        stringListRecorder.add(string41);
        stringListRecorder.add(string51);
    }

    /**
     * 当我们直接在 new 一个对对象时，其内部的 字符串常量 是可以直接从 字符串常量池中获取的。
     * 此时 new 出来的不同对象的对应的字符串对象就只是共用一个。
     * <p>
     * 比如这里 new 的俩个 PropertyInfo 对象，他们的 v_isPrimitive 域都引用的同一个字符串对象.
     */
    private void newStringWithClassTest() {
        PropertyInfo propertyInfo11 = new PropertyInfo();
        PropertyInfo propertyInfo12 = new PropertyInfo();

        objectListRecorder.add(propertyInfo11);
        objectListRecorder.add(propertyInfo12);
    }

    /**
     * 这是 jdk 的序列化在读取 string 时的调用栈，其中使用 StringBuild 来构建反序列化的字符串。
     * <p>
     * "main@1" prio=5 tid=0x1 nid=NA runnable
     * java.lang.Thread.State: RUNNABLE
     * at java.lang.StringBuilder.toString(StringBuilder.java:407)
     * at java.io.ObjectInputStream$BlockDataInputStream.readUTFBody(ObjectInputStream.java:3495)
     * at java.io.ObjectInputStream$BlockDataInputStream.readUTF(ObjectInputStream.java:3282)
     * at java.io.ObjectInputStream.readString(ObjectInputStream.java:1961)
     * at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1606)
     * at java.io.ObjectInputStream.defaultReadFields(ObjectInputStream.java:2343)
     * at java.io.ObjectInputStream.readSerialData(ObjectInputStream.java:2267)
     * at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2125)
     * at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1624)
     * at java.io.ObjectInputStream.readObject(ObjectInputStream.java:464)
     * at java.io.ObjectInputStream.readObject(ObjectInputStream.java:422)
     * at StringTest.StringMemoryTest.serializeObject(StringMemoryTest.java:137)
     * at StringTest.StringMemoryTest.newStringWithSerializableTest(StringMemoryTest.java:122)
     * at StringTest.StringMemoryTest.main(StringMemoryTest.java:39)
     * <p>
     * 而下面是 StringBuilder 最终返回 string 的实现，其通过 new String 来处理，这样子就无法使用 常量池中的 string 了。
     * 这在 jdk 的 StringBuilder 类的 toString 方法中就有所说明了。
     * <p>
     * 因此当我们反序列化一个对象后，其字符串常量的对象就会增加了，比如这里对 propertyInfo13 的反序列化测试。
     * 对于这种情况，如果序列化是我们可控的实现，那么我们可以使用 java.lang.String#intern 来进行优化。
     *
     * @Override public String toString() {
     * // Create a copy, don't share the array
     * return new String(value, 0, count);
     * }
     * @see java.lang.StringBuilder#toString
     */
    private void newStringWithSerializableTest() throws IOException, ClassNotFoundException {
        PropertyInfo propertyInfo13 = new PropertyInfo();
        Object deserializePropertyInfo21 = serializeObject(propertyInfo13);

        objectListRecorder.add(propertyInfo13);
        objectListRecorder.add(deserializePropertyInfo21);
    }

    private static Object serializeObject(Object object) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(arrayOutputStream);
        outputStream.writeObject(object);

        byte[] bytes = arrayOutputStream.toByteArray();

        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
        Object deserializeObject = inputStream.readObject();

        return deserializeObject;
    }

}
