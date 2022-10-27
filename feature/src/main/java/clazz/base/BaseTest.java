package clazz.base;

public class BaseTest {

    public static void main(String[] args) {

        Sub sub = new Sub();
        sub.threadStart();
    }

}

class Sub extends Base {

    @Override
    public void back() {

        System.out.println("sub_back");
        super.back();
        //子类覆盖了父类的实现，如果要使父类被覆盖的函数执行需要显示的调用其。
        //否则,即使传递的是父类的引用也会调用子类的实现。
        //因为JVM知道引用的指向的实际对象是子类对象，而不是父类对象，所以调用的是父类的实现。
    }
}


abstract class Base {

    private Thread thread;

    public void back() {

        System.out.println("base_back");
    }

    public void threadStart() {

        String threadName = "BackgroundProcessor[" + toString() + "]";
        thread = new Thread(new BackProcessor(), threadName);
//        thread.setDaemon(true);
//        设置为daemon线程时程序无输出，因为在调用start时，系统中只有daemon线程，
//        此时jvm会退出，导致daemon线程任务无法执行
        thread.start();
    }

    class BackProcessor implements Runnable {

        @Override
        public void run() {

            System.out.println("run");
            process(Base.this);
            //不能直接使用this，应为this指得是BackProcessor，而这里需要的是Base类的实例,
            //内部类可以访问其外部类的成员，所以显示的以 外部类.this 的形式传递外部类的引用
        }

        private void process(Base base) {

            System.out.println("precess");
            base.back();
        }
    }
}


