package clazz.extendTest;

import java.util.logging.Logger;

/**
 * Created by JasonFitch on 2/23/2019.
 */
public class ManagerBase {

    protected Logger logger = Logger.getLogger(ManagerBase.class.getName());

    public void doSomeThing() {
        logger.info("Base:" + logger.getName());
    }
}
