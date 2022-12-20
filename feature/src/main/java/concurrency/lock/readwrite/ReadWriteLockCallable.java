package concurrency.lock.readwrite;

import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

public class ReadWriteLockCallable implements Callable<Boolean> {

    private final int callTime = ReadWriteLockTest.CALL_TIME;

    private final long counter;

    public ReadWriteLockCallable(long counter) {
        this.counter = counter;
    }

    @Override
    public Boolean call() throws SQLException {
        long start = System.currentTimeMillis();
        try {
            TimeUnit.MILLISECONDS.sleep(callTime);
        } catch (InterruptedException e) {
            throw new RuntimeException("call, should neven happen!", e);
        }
        long end = System.currentTimeMillis();

        String name = Thread.currentThread().getName();
        long mod = counter % 4;
        String format = String.format("name=[%s], call, counter=[%s], mod=[%s], start=[%s], duration=[%s]", name, counter, mod, start, end - start);
        System.out.println(format);

        switch ((int) mod) {
            case 0:
                return true;
            case 1:
                return false;
            case 2:
                throw new SQLException();
            default:
                throw new RuntimeException();
        }
    }
}
