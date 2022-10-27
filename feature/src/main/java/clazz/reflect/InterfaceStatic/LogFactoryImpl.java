package clazz.reflect.InterfaceStatic;

import java.util.logging.Logger;

/**
 * Created by JasonFitch on 1/9/2019.
 */
public class LogFactoryImpl implements LogFactory {

    private static final LogFactory singleton = new LogFactoryImpl();


    private LogFactoryImpl() {
    }

    @Override
    public Logger getLogInterface(String name) {
        return Logger.getLogger(name);
    }


    public static LogFactory getSingleton() {
        return singleton;
    }

    public static Logger getLogStatic(String name) {
        return getSingleton().getLogInterface(name);
    }

}
