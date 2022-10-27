package clazz.bytecode.material;

/**
 * Created by JasonFitch on 7/28/2020.
 */
public class SleeperParameterChild extends SleeperParameter{

    public SleeperParameterChild(long timeout, Long timeoutL, MyInteger timeoutCTI) {
        super(timeout, timeoutL, timeoutCTI);
    }

    public SleeperParameterChild(long timeout, Long timeoutL, MyLong timeoutCPL) {
        super(timeout, timeoutL, timeoutCPL);
    }

    @Override
    public void SleeperParameter(long timeout, Long timeoutL, MyInteger timeoutCTI) {
        super.SleeperParameter(timeout, timeoutL, timeoutCTI);
    }

    @Override
    public void sleep(long timeout, Long timeoutL, MyLong timeoutPL) throws InterruptedException {
        super.sleep(timeout, timeoutL, timeoutPL);
    }

    @Override
    public void sleep(long[] timeoutA, Long[] timeoutLA, MyLong[] timeoutPLA) throws InterruptedException {
        super.sleep(timeoutA, timeoutLA, timeoutPLA);
    }
}
