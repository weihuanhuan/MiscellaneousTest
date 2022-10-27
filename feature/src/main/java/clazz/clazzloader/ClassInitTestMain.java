package clazz.clazzloader;

import java.util.logging.Logger;

/**
 * Created by JasonFitch on 3/8/2019.
 */
public class ClassInitTestMain {

    private static Logger logger = Logger.getLogger(ClassInitTestMain.class.getName());

    public static void main(String[] args) {

        logger.info(ClassInitTest.finalStaticStr);
        //JF 实际的static init在这里执行，
        //   因为对于static final的变量是一个静态常量，所以其直接在常量池中读取而无需初始化类
        //   所以staticFinalStr可以直接输出，而下面的staticStr则需要先初始化类，之后才能使用
        //   前者由于final修饰不会改变，而后者运行时可能会变化，但他们都是属于类级的变量。
        logger.info(ClassInitTest.staticStr);

        ClassInitTest.setStart(true);
        logger.info(String.valueOf(ClassInitTest.getStart()));

        new ClassInitTest();


    }


}
