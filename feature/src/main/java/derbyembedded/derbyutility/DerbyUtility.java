package derbyembedded.derbyutility;

import java.sql.Connection;

/**
 * Created by JasonFitch on 1/22/2018.
 */
public class DerbyUtility
{
    public static Connection getConnection()
    {
        String driverName = "org.apache.derby.jdbc.EmbeddedDriver";

        Connection conn=null;

        try
        {
            Class.forName(driverName);
        } catch (ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return conn;
    }
}
