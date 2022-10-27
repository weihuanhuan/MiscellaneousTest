package database.derbyembedded;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by JasonFitch on 1/22/2018.
 */
public class DerbyEmbedded
{
    public static void main(String[] args)
    {
        SubRunnable sr1=new SubRunnable();
        Thread st1 = new Thread(sr1);
        st1.start();

        SubRunnable sr2=new SubRunnable();
        Thread st2 = new Thread(sr2);
        st2.start();
    }

    public void executeConnection()
    {
        String driverName = "org.apache.derby.jdbc.EmbeddedDriver";
        try
        {
            Class.forName(driverName);
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        String dbName = "dbembedded";
        String connectionURL = "jdbc:derby:" + dbName + ";create=true";

        String createApplication = "CREATE TABLE \"APP\".\"APPLICATION\" (" +
                "\"APPLICATION_NAME\" VARCHAR(256) NOT NULL," +
                "\"LOCATION\" VARCHAR(256) DEFAULT NULL," +
                "\"ENABLED\" SMALLINT DEFAULT NULL," +
                "\"UNDEPLOYED\" SMALLINT DEFAULT NULL," +
                "\"APPLICATION_TYPE\" VARCHAR(10) DEFAULT NULL)";
        String createInt = "CREATE TABLE \"APP\".\"INTT\" (" +
                "\"INTTEST\" SMALLINT DEFAULT NULL)";

        String insert = "insert into application(application_name) values('appname')";

        Connection conn = null;
        Statement cs = null;
        ResultSet rs = null;
        PreparedStatement pedsI = null;
        PreparedStatement pedsQ = null;
        try
        {
            conn = DriverManager.getConnection(connectionURL);

//            cs = conn.createStatement();
//            cs.execute(createApplication);
//            cs.execute(createInt);
//
//            pedsI =  conn.prepareStatement(insert);
//            int affected = pedsI.executeUpdate();
//            System.out.println("affected="+affected);

            String query = "select * from application";
            pedsQ = conn.prepareStatement(query);
            rs = pedsQ.executeQuery();
            int i=0;
            while(rs.next())
            {
                ++i;
                System.out.println("-----------------------------");
                System.out.println( rs.getString(1));
                System.out.println( rs.getString(2));
                System.out.println( rs.getString(3));
                System.out.println( rs.getString(4));
                System.out.println( rs.getString(5));
            }

            System.out.println("*****************************");
            System.out.println("i="+i);
            while(true)
            {
                ;
            }
        } catch (Throwable e)
        {
            e.printStackTrace();
        } finally
        {
            try
            {
                if (conn != null)
                    conn.close();
                if (cs != null)
                    cs.close();
                if (pedsI != null)
                    pedsI.close();
                if (pedsQ != null)
                    pedsQ.close();
                if (rs != null)
                    rs.close();
            } catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }
}

class SubRunnable implements Runnable
{
    private static transient Integer threadId=0;

    public SubRunnable()
    {
        ++threadId;
        System.out.println(threadId.toString());
    }

    @Override
    public void run()
    {
        DerbyEmbedded de = new DerbyEmbedded();
        de.executeConnection();
    }
}
