package clazz.generic.inter.clazz;

import java.lang.reflect.Constructor;

public class ObjectPool<T> {

    private DefaultPolicy<T> policyByObject;

    private DefaultPolicy<T> policyByClass;

    public void setPolicyByObject(DefaultPolicy<T> policyByObject) {
        this.policyByObject = policyByObject;
    }

    public void setPolicyClass(String className, Object outerObject) {
        try {
            Class<?> aClass = Class.forName(className);

            //内部类在编译后，编译器默认会为其生成一个【private generic.inter.clazz.OuterClazz$InterPolicy(generic.inter.clazz.OuterClazz)】 类型的构造器
            //由于该类本身已经存在了构造器了，所以其不会生成默认的无参构造器了，这导致 java.lang.Class.newInstance 方法会出现 NoSuchMethodException
//            this.policyByClass = (DefaultPolicy<T>) aClass.newInstance();

            //由于内部类默认生成的构造器是私有的，所以使用 java.lang.Class.getDeclaredConstructors 来进行获取
            //而内部类由编译器默认生成的构造器需要使用其外围类对象的引用作为参数，所以调用构造器的 java.lang.reflect.Constructor.newInstance 需要提供参数
            for (Constructor<?> constructor : aClass.getDeclaredConstructors()) {
                constructor.setAccessible(true);
                this.policyByClass = (DefaultPolicy<T>) constructor.newInstance(new Object[]{outerObject});
                break;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public DefaultPolicy<T> getPolicyByObject() {
        return policyByObject;
    }

    public DefaultPolicy<T> getPolicyByClass() {
        return policyByClass;
    }
}