package io.serializtion;

import com.caucho.hessian.io.Hessian2Input;
import com.caucho.hessian.io.Hessian2Output;
import com.caucho.hessian.io.SerializerFactory;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Created by JasonFitch on 2/3/2021.
 */
public class EmptyListTest {

    public static void main(String[] args) {

        System.out.println("############# newInstanceTest ##################");
        try {
            newInstanceTest();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        System.out.println("############# javaSerializeTest ##################");
        try {
            javaSerializeTest();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("############# deserializeObject ##################");
        try {
            hessianSerializeTest();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //这里 Collections.emptyList() 返回的是一个具体类 Collections$EmptyList 的实例,而不是一个没有元素的某种实际使用的 List 实现.
    //private static class EmptyList<E> extends AbstractList<E> implements RandomAccess, Serializable {...}

    //这个 Collections$EmptyList 是一个内部静态类，访问符是 private ，所以外部无法直接 new instance，
    //同时其内部的一些来自其父类 AbstractList 的方法要不是没有实现，要不就是固定返回为等价空或直接抛出异常，
    //所以如果调用者要直接修改这个 emptyList 本身时就会出现问题.
    private static void newInstanceTest() throws IllegalAccessException, InstantiationException {
        List<String> emptyList = Collections.emptyList();

        Class<? extends List> emptyListClass = emptyList.getClass();
        System.out.println(emptyListClass);

        List newInstance = emptyListClass.newInstance();
        System.out.println(newInstance);
    }

    // jdk 动态生成 deserialize 时使用的 constructor .
    //"main@1" prio=5 tid=0x1 nid=NA runnable
    //    java.lang.Thread.State: RUNNABLE
    //        at sun.reflect.MethodAccessorGenerator.generate(MethodAccessorGenerator.java:141)
    //        at sun.reflect.MethodAccessorGenerator.generateSerializationConstructor(MethodAccessorGenerator.java:112)
    //        at sun.reflect.ReflectionFactory.generateConstructor(ReflectionFactory.java:398)
    //        at sun.reflect.ReflectionFactory.newConstructorForSerialization(ReflectionFactory.java:360)
    //        at java.io.ObjectStreamClass.getSerializableConstructor(ObjectStreamClass.java:1589)
    //        at java.io.ObjectStreamClass.access$1500(ObjectStreamClass.java:79)
    //        at java.io.ObjectStreamClass$3.run(ObjectStreamClass.java:519)
    //        at java.io.ObjectStreamClass$3.run(ObjectStreamClass.java:494)
    //        at java.security.AccessController.doPrivileged(AccessController.java:-1)
    //        at java.io.ObjectStreamClass.<init>(ObjectStreamClass.java:494)
    //        at java.io.ObjectStreamClass.lookup(ObjectStreamClass.java:391)
    //        at java.io.ObjectOutputStream.writeObject0(ObjectOutputStream.java:1134)
    //        at java.io.ObjectOutputStream.writeObject(ObjectOutputStream.java:348)
    //        at serializtion.EmptyListTest.main(EmptyListTest.java:23)

    // jdk 的 java 序列化对于实现了 Serializable 接口的对象调用其 readResolve() 方法来还原 deserialize 后的对象.
    //"main@1" prio=5 tid=0x1 nid=NA runnable
    //    java.lang.Thread.State: RUNNABLE
    //        at java.util.Collections$EmptyList.readResolve(Collections.java:4489)
    //        at sun.reflect.NativeMethodAccessorImpl.invoke0(NativeMethodAccessorImpl.java:-1)
    //        at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
    //        at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
    //        at java.lang.reflect.Method.invoke(Method.java:498)
    //        at java.io.ObjectStreamClass.invokeReadResolve(ObjectStreamClass.java:1275)
    //        at java.io.ObjectInputStream.readOrdinaryObject(ObjectInputStream.java:2134)
    //        at java.io.ObjectInputStream.readObject0(ObjectInputStream.java:1624)
    //        at java.io.ObjectInputStream.readObject(ObjectInputStream.java:464)
    //        at java.io.ObjectInputStream.readObject(ObjectInputStream.java:422)
    //        at serializtion.EmptyListTest.main(EmptyListTest.java:30)
    private static void javaSerializeTest() throws IOException, ClassNotFoundException {
        List<String> emptyList = Collections.emptyList();

        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream outputStream = new ObjectOutputStream(arrayOutputStream);
        outputStream.writeObject(emptyList);

        byte[] bytes = arrayOutputStream.toByteArray();

        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream inputStream = new ObjectInputStream(arrayInputStream);
        Object deserializeObject = inputStream.readObject();

        System.out.println(deserializeObject.getClass());
    }

    //而在反序列化时，
    //JDK自带的 java 序列化也会通过 Collections$EmptyList.readResolve 将其解析为原始的 Collections$EmptyList 实例，
    //而这个实例对于同一个 jvm 来说，其就是一个单例对象，所以反序列后和反序列化前他们是一个对象， 因此依旧是不能修改的。

    //而使用 hessian 反序列化时，
    //其使用 com.caucho.hessian.io.CollectionDeserializer 来反序列化符合 Collection.class.isAssignableFrom(cl) 条件的类。
    //故而是的 Collections$EmptyList 被反序列化为了 ArrayList 类的实例，所以反序列后的结果是可以修改的。
    //参考 com.caucho.hessian.io.CollectionDeserializer#createList
    private static void hessianSerializeTest() throws IOException {
        List<String> emptyList = Collections.emptyList();
        SerializerFactory serializerFactory = new SerializerFactory();

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        Hessian2Output h2out = new Hessian2Output(bout);
        h2out.setSerializerFactory(serializerFactory);
        h2out.writeObject(emptyList);

        //保证序列化后的数据从某些可能存在的 write buffer 缓冲中 flush 到保存结果的 byte array 中，以供接下来反序列化时使用。
        h2out.flush();
        byte[] body = bout.toByteArray();

        ByteArrayInputStream input = new ByteArrayInputStream(body, 0, body.length);
        Hessian2Input h2in = new Hessian2Input(input);
        h2in.setSerializerFactory(serializerFactory);
        Serializable deserializeObject = (Serializable) h2in.readObject();

        System.out.println(deserializeObject.getClass());
    }

}


