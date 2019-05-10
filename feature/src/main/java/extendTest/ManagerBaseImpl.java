package extendTest;

import java.util.logging.Logger;

/**
 * Created by JasonFitch on 2/23/2019.
 */
public class ManagerBaseImpl extends ManagerBase {

    protected Logger logger = Logger.getLogger(ManagerBaseImpl.class.getName());

    @Override
    public void doSomeThing() {
        logger.info("Impl:" + logger.getName());
        logger.info("Impl:" + super.logger.getName());
    }
}
