package database.hsqldb.transaction;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class HSQLDBTransactionHangTest {

    private static final String DRIVER_CLASSNAME = "org.hsqldb.jdbc.JDBCDriver";

    private static final String username = "SA";
    private static final String password = "123456";

    private static String url;

    static {
        try {
            Class.forName(DRIVER_CLASSNAME);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws SQLException, InterruptedException {
        System.out.println("######################## initConnectionUrl ########################");
        initConnectionUrl();

        System.out.println("######################## initTable ########################");
        initTable();

        System.out.println("######################## validationTable ########################");
        validationTable();

        System.out.println("######################## selectWhenExistUncommittedTransactionTest ########################");
        selectWhenExistUncommittedTransactionTest();
    }

    private static void initConnectionUrl() {
        String home = System.getProperty("user.dir");
        // 默认事务模型为 locks ,即 hsqldb.tx=locks , 在事务执行时，其会阻塞另外一个连接的 sql 调用
        url = "jdbc:hsqldb:file:" + home + "/tmp/hsqldb/transaction-database";
        // 修改事务模型为 mvcc， 在事务执行时，不会阻塞另外一个连接的 sql 调用
        // 另外我们测试时发现，如果 hsqldb db 已经启动过创建好 db file 了， 那么单纯的修改 url 中的 hsqldb.tx=mvcc 是不生效的，
        // 也就是对事务模型的修改必须是在初始化数据库时，就确认好，否则就需要我们将 db file 删除掉后，再启动 hsqldb ，重新生成该事务模型的 db file 了
        url = "jdbc:hsqldb:file:" + home + "/tmp/hsqldb/transaction-database;hsqldb.tx=mvcc";

        System.out.println("######################## connection info ########################");
        String format = String.format("home=[%s], url=[%s], username=[%s], password=[%s]", home, url, password, username);
        System.out.println(format);
    }

    private static void initTable() throws SQLException {
        try (Connection connection = getConnectionTest(url, username, password); Statement statement = connection.createStatement()) {
            connection.setAutoCommit(false);

            // 使用 sql 语句的方式，修改事务模型为 mvcc， 这种方案可以在数据库重启时生效，无需重新生成该事务模型的 db file 了，但是是特定于 hsqldb 的方式
//            statement.execute("set database transaction control MVCC;");

            statement.execute("drop table if exists odd_table;");
            statement.execute("create table odd_table(column1 integer not null primary key, column2 integer);");
            statement.execute("truncate table odd_table;");
            statement.execute("insert into odd_table values (1,10);");
            statement.execute("insert into odd_table values (2,20);");
            statement.execute("commit;");
        }
    }

    public static void validationTable() throws SQLException {
        try (Connection connection = getConnectionTest(url, username, password);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("select * from odd_table;")) {

            while (resultSet.next()) {
                int row = resultSet.getRow();
                int column1 = resultSet.getInt(1);
                int column2 = resultSet.getInt(2);
                String format = String.format("row=[%s], column1=[%s], column2=[%s]", row, column1, column2);
                System.out.println(format);
            }
        }
    }

    private static void selectWhenExistUncommittedTransactionTest() throws SQLException, InterruptedException {
        // 控制事务何时提交
        CountDownLatch countDownLatch = new CountDownLatch(1);

        // 模拟执行一个事务
        Connection connection1 = getConnectionTest(url, username, password);
        TransactionConnectionTask transactionConnectionTask = new TransactionConnectionTask(connection1, countDownLatch);
        Thread threadTransactionConnectionTask = new Thread(transactionConnectionTask, "TransactionConnectionTask");
        threadTransactionConnectionTask.start();

        // 模拟执行事务时，执行 select 语句，看 validationTableTask 是否会 hang 住 hsqldb
        ValidationTableTask validationTableTask = new ValidationTableTask(countDownLatch);
        Thread threadValidationTableTask = new Thread(validationTableTask, "ValidationTableTask");
        threadValidationTableTask.start();

        // 如果事务模型在事务中会卡住另外一个连接的 sql 执行，那么我们在等待一段时间后,手动通知事务进行提交，看提交后 validationTableTask 是否会正常结束
        TimeUnit.SECONDS.sleep(5);
        countDownLatch.countDown();
        System.out.println("transactionTest: java.util.concurrent.CountDownLatch.countDown");
    }

    private static Connection getConnectionTest(String url, String user, String password) throws SQLException {
        Connection connection = DriverManager.getConnection(url, user, password);
        return connection;
    }

}
