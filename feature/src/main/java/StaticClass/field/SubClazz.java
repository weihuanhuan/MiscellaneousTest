package StaticClass.field;

import java.io.IOException;

/**
 * Created by JasonFitch on 1/7/2020.
 */
public class SubClazz extends SuperClazz {

    public Thread startThread = startThread("SubThread");

    public static Thread startThreadStatic = startThread("SubThreadStatic");

    public Thread getSuperClazzThread() {
        // 在继承结构中，super只是用来代表父类中域的引用方式，是一个虚拟的概念。
        // 实际上，对于一个实例化了的子类，系统中只有子类实例而没有父类实例，所以super不能直接返回作为一个实例来使用。
//        return super;
        return super.startThread;
    }

    public static void main(String[] args) throws IOException {

        SubClazz subClazz = new SubClazz();

        //对于静态域来说，由于其属于类级别,引用的时候也是使用类来引用，因此即使是同名情况下，子类也无法覆盖父类的静态域。
        System.out.println(SuperClazz.startThreadStatic);
        System.out.println(subClazz.startThreadStatic);

        //而对于非静态域来说，其属于实例级别，引用时通过实例名来引用，这样子对于实例的外部是看不见其父类的，
        // 所以对于实例的调用者而言，子类会屏蔽掉其父类的同名域，
        System.out.println(subClazz.startThread);

        // 但是父类这个域并不是不存在，他依旧正常的实例化，只不过需要在子类使用super来使用，或者通过子类将该域暴露给外部使用。
        System.out.println(subClazz.getSuperClazzThread());


    }

}
