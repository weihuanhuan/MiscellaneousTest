package clazz.extendTest;

/**
 * Created by JasonFitch on 2/23/2019.
 */
public class ManagerTest {

    public static void main(String[] args) {

        ManagerBaseImpl manager = new ManagerBaseImpl();
        ((ManagerBase) manager).doSomeThing();
        //  依旧是调用子类的方法，类型转换是多余的,等价于
        ManagerBase managerBase = new ManagerBaseImpl();
        managerBase.doSomeThing();
        //  子类要想调用父类中被Override的方法必须在子类内部使用super来实现，
        //  否则子类不可以直接调用父类中被Override的方法，这不符合面向对象的思想，
        //  子类覆写父类的方法就是要改变父类的行为才有意义，否则就是多余的。
        //  如果直接调用会导致抽象体系的漏洞，比如子类可以直接按照父类的行为来工作，
        //  相当于 有界队列（put处理边界） extends 队列（put不处理边界）
        //  直接可以调用覆盖的put导致有界队列的行为和无界队列相同，打破了对象体系的一致性。
    }
}
