package generic.inter;

import generic.inter.clazz.ObjectPool;
import generic.inter.clazz.OuterClazz;

import java.io.InputStream;

public class InterClassTest {

    public static void main(String[] args) {

        OuterClazz<InputStream> outerClazz = new OuterClazz<>();
        ObjectPool<InputStream> objectPool = outerClazz.createObjectPool();

        //检测通过 new 对象形式创建的内部类对象
        objectPool.getPolicyByObject().printThis();

        //检测通过 类名 反射形式创建的内部类对象
        objectPool.getPolicyByClass().printThis();
    }

}


