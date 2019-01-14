package reflect.InterfaceStatic;

import java.util.logging.Logger;

/**
 * Created by JasonFitch on 1/9/2019.
 */
public interface LogFactory {

    //JF 接口中可以有field
    String name = "LogFactory";

    //JF 接口中的static方法需要有实现,同时其实现类中不需要也无法覆盖static方法。
    static Logger getInstance(String name) {
        return Logger.getLogger(name);
    }

    Logger getLogInterface(String name);
}
