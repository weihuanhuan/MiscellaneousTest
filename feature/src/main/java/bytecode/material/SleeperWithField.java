package bytecode.material;

/**
 * Created by JasonFitch on 8/3/2020.
 */
public class SleeperWithField {

    private static final long CLAZZ_FINAL_LONG = 1000;

    private static long CLAZZ_LONG = 1000;

    private final long instanceFinalLong = 1000;

    private long instanceLong = 1000;

    public void sleep() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(CLAZZ_LONG);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    public void anotherSleep() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread.sleep(instanceLong);
        long end = System.currentTimeMillis();
        System.out.println(end - start);
    }

    public static long getClazzFinalLong() {
        return CLAZZ_FINAL_LONG;
    }

    public static long getClazzLong() {
        return CLAZZ_LONG;
    }

    public static void setClazzLong(long clazzLong) {
        CLAZZ_LONG = clazzLong;
    }

    public long getInstanceFinalLong() {
        return instanceFinalLong;
    }

    public long getInstanceLong() {
        return instanceLong;
    }

    public void setInstanceLong(long instanceLong) {
        this.instanceLong = instanceLong;
    }
}
