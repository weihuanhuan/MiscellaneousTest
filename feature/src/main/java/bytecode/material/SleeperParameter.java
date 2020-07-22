package bytecode.material;

/**
 * Created by JasonFitch on 7/22/2020.
 */
public class SleeperParameter<CTI extends Integer> {

    public SleeperParameter() {
    }

    public SleeperParameter(long timeout) {
    }

    public SleeperParameter(Long timeoutL) {
    }

    public SleeperParameter(long timeout, Long timeoutL, CTI timeoutCTI) {
    }

    public <CPL extends Long> SleeperParameter(long timeout, Long timeoutL, CPL timeoutCPL) {
    }

    //注意构造方法没有返回值部分，连 void 都没有，void 也是返回值的一种，代表返回的是 空，
    //所以不要不小心在构造其中添加 void 返回值部分，如果添加了的话他将变为 method ，是一个与构造器重名的方法而已，而不是构造器了。
    public void SleeperParameter(long timeout, Long timeoutL, CTI timeoutCTI) {
    }

    public void sleep(long timeout) throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(timeout);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    public void sleep(Long timeoutL) throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(timeoutL);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    //默认情况下泛型擦除后的类型是 Object，如果 泛型使用了 extends 来声明，那么擦除后的类型就是 super 的类型。
    //比如这里的 sleep 方法的 P 擦除为 java.lang.Object,而下面的 anotherSleep 中的 P 擦除为 java.lang.Long
    public <P> void sleep(P timeoutP) throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep((Long) timeoutP);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    //不可以和 sleep(Long timeout) 使用相同的方法名，因为 Java 的泛型依赖于类型擦除
    //而在类型擦出后，这个泛型方法将和 上面的 Long 类型的 sleep 重载拥有相同的函数签名，导致无法 javac 编译。
    //编译时 方法的返回值是不考虑在签名中的，但是 jvm 是会区分重载方法的返回值的。
    //Error:(22, 34) java: name clash: <T>sleep(T) and sleep(java.lang.Long) have the same erasure
    public <PL extends Long> void anotherSleep(PL timeoutPL) throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(timeoutPL);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    public <PL extends Long> void sleep(long timeout, Long timeoutL, PL timeoutPL) throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(timeout);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    public <PL extends Long> void sleep(long[] timeoutA, Long[] timeoutLA, PL[] timeoutPLA) throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(timeoutA[0]);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }


}
