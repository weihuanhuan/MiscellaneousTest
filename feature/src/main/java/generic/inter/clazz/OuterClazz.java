package generic.inter.clazz;

public class OuterClazz<T> {

    private final String enabled = "true";

    public ObjectPool<T> createObjectPool() {
        ObjectPool<T> valuePool = new ObjectPool<>();

        valuePool.setPolicyByObject(new InterPolicy<>());

        //通过类名来使用反射的方式也是可以创建内部类的对象的，只不过是比较麻烦一些。
        //注意内部类名和外围类名在反射时使用 $ 来分隔，而不是一般的包名 dot 来分隔
        //另外由于非静态的内部类在实例化其对象时都需要持有其外围类的对象引用，所以这里必须要提供其外围类对象的引用才能实例化
        valuePool.setPolicyClass("generic.inter.clazz.OuterClazz$InterPolicy", this);
        return valuePool;
    }

    //注意这里 InterPolicy 类使用了另外一个泛型 Y 来代替 OuterClazz 类的泛型 T
    //这是为了防止在同一个域中，相同的泛型名导致的泛型类型屏蔽问题
    private class InterPolicy<Y> extends DefaultPolicy<Y> {

        @Override
        public void printThis() {
            super.printThis();
            System.out.println(enabled);
        }
    }

}






