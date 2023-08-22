package database.hsqldb.transaction;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

public class TransactionConnectionTask implements Runnable {

    private final Connection connection;
    private final CountDownLatch countDownLatch;

    public TransactionConnectionTask(Connection connection, CountDownLatch countDownLatch) {
        this.connection = connection;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            boolean oldAutoCommit = connection.getAutoCommit();
            connection.setAutoCommit(false);

            boolean newAutoCommit = connection.getAutoCommit();
            int transactionIsolation = connection.getTransactionIsolation();
            String infoFormat = String.format("TransactionConnectionTask: oldAutoCommit=[%s], newAutoCommit=[%s], transactionIsolation=[%s]",
                    oldAutoCommit, newAutoCommit, transactionIsolation);
            System.out.println(infoFormat);

            Statement statement = connection.createStatement();

            int executeUpdate = statement.executeUpdate("update odd_table set column2 = 11 where column1 = 1;");
            String executeFormat = String.format("TransactionConnectionTask: executeUpdate=[%s]", executeUpdate);
            System.out.println(executeFormat);

            // 改任务刚刚启动是，由于没有人调用 java.util.concurrent.CountDownLatch.countDown ,导致事务一致无法被提交
            countDownLatch.await();

            connection.commit();

            System.out.println("TransactionConnectionTask: java.sql.Connection.commit");
        } catch (SQLException e) {
            try {
                connection.rollback();
                System.out.println("TransactionConnectionTask: java.sql.Connection.rollback");
            } catch (SQLException ex) {
                throw new RuntimeException("TransactionConnectionTask: java.sql.Connection.rollback", ex);
            }
        } catch (Exception e) {
            throw new RuntimeException("TransactionConnectionTask: java.lang.InterruptedException", e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                    System.out.println("TransactionConnectionTask: java.sql.Connection.close");
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}