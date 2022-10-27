package database.datasource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.PoolableConnection;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.geronimo.transaction.manager.GeronimoTransactionManager;
import org.apache.geronimo.transaction.manager.TransactionManagerImpl;
import org.apache.xbean.recipe.ObjectRecipe;
import org.apache.xbean.recipe.Option;

import javax.transaction.xa.XAException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

/**
 * Created by JasonFitch on 3/8/2018.
 */
public class JDBCDataSource {

    public static void main(String[] args) {

//      org.apache.commons.pool2.impl.GenericObjectPool.borrowObject(long)
//            org.apache.commons.pool2.impl.BaseGenericObjectPool.destroyedByBorrowValidationCount 增加
//      org.apache.commons.pool2.impl.GenericObjectPool.evict()
//            org.apache.commons.pool2.impl.BaseGenericObjectPool.destroyedByEvictorCount  增加
//      org.apache.commons.pool2.impl.GenericObjectPool.destroy()
//            org.apache.commons.pool2.impl.BaseGenericObjectPool.destroyedCount 增加
//            org.apache.commons.pool2.impl.GenericObjectPool.createCount        减少

        BasicDataSource bds = new BasicDataSource();
//      bds.getConnectionPool();
//      提示：getConnectionPool() has protected access in org.apache.commons.dbcp2.BasicDataSource
        PoolableBasicDataSource pbds = new PoolableBasicDataSource();
        GenericObjectPool<PoolableConnection> genericObjectPool = pbds.getConnectionPool();
//        protected 方法需要子类继承后，并重写，才可以访问。


        try {

            Properties properties = new Properties();

            Class<?> clazz = Class.forName("com.mysql.jdbc.Driver");
            ObjectRecipe objectRecipe = new ObjectRecipe(clazz);
            objectRecipe.allow(Option.PRIVATE_PROPERTIES);
            objectRecipe.allow(Option.IGNORE_MISSING_PROPERTIES);
            objectRecipe.allow(Option.CASE_INSENSITIVE_PROPERTIES);
//            objectRecipe.allow(Option.NAMED_PARAMETERS);
//            该选项使得调用 org.apache.xbean.recipe.AbstractRecipe.create() 方法时要使用一个有参数的构造器
//            会导致只有无参构造器的类实例化失败

            objectRecipe.setAllProperties(properties);
            //Properties 是实现了 Map 接口的
            objectRecipe.create();

            TransactionManagerImpl tm = new TransactionManagerImpl();
            GeronimoTransactionManager gtm = new GeronimoTransactionManager();

        } catch (XAException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }


        HashMap<String, String> hashMap = new HashMap<>();

        hashMap.put("key1", "value1");
        List<String> emptyList = Collections.emptyList();
        //构造一个size = 0 的集合，而不是 引用为 null 的集合
        System.out.println(emptyList);
        boolean isContainsAll = hashMap.keySet().containsAll(emptyList);
        //true，对于一个空的集合，一定是包含的。
        System.out.println(isContainsAll);
        hashMap.keySet().containsAll(null);   //NullPointerException



    }
}

class PoolableBasicDataSource extends BasicDataSource {
    @Override
    protected GenericObjectPool<PoolableConnection> getConnectionPool() {
        return super.getConnectionPool();
    }
}

interface IFa {

    void func();
}

//abstract class A extends IFa {}   //Interface not allowed to extends
