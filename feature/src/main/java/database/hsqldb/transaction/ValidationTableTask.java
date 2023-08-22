package database.hsqldb.transaction;

import java.util.concurrent.CountDownLatch;

public class ValidationTableTask implements Runnable {

    private final CountDownLatch countDownLatch;

    public ValidationTableTask(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            long count = countDownLatch.getCount();
            String format = String.format("ValidationTableTask: java.util.concurrent.CountDownLatch.getCount=[%s]", count);
            System.out.println(format);

            HSQLDBTransactionHangTest.validationTable();

            // 检测是否在事务执行时， ValidationTableTask 可以成功的完成，如果可以，那么说明此种事务模型是不会将其他连接卡住的，
            // 此时就可以通知 TransactionConnectionTask 去提交了，让其继续等待就没有意义了。
            countDownLatch.countDown();
            System.out.println("ValidationTableTask: java.util.concurrent.CountDownLatch.countDown");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}