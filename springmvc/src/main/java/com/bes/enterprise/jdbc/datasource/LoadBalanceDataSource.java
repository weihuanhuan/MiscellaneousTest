package com.bes.enterprise.jdbc.datasource;

import com.bes.enterprise.jdbc.strategy.FailOver;
import com.bes.enterprise.jdbc.strategy.FailOverRouter;
import com.bes.enterprise.jdbc.support.DataSourceTxHolder;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.jdbc.datasource.lookup.DataSourceLookup;
import org.springframework.lang.Nullable;

/**
 * Created by JasonFitch on 6/5/2019.
 */
public class LoadBalanceDataSource extends AbstractRoutingDataSource {


    @Nullable
    @Override
    protected Object determineCurrentLookupKey() {
        Object dataSource = DataSourceTxHolder.getDataSource();
        if (dataSource != null) {
            return dataSource;
        }
        Map<Object, Object> targetDataSources = getTargetDataSources();
        Object targetDataSource = FailOver.getTargetDataSource(targetDataSources);
        DataSourceTxHolder.setDataSource(targetDataSource);
        return targetDataSource;
    }

    @Override
    protected DataSource determineTargetDataSource() {
        //父类的这里是调用 determineCurrentLookupKey() 的入口，所以不执行父类方法就不会调用。
//        return super.determineTargetDataSource();
        Map<Object, Object> targetDataSources = getTargetDataSources();
        FailOverRouter failOverRouter = new FailOverRouter();
        failOverRouter.setStrategy("random");
        failOverRouter.init();
        failOverRouter.updateDataSources(targetDataSources);
        return failOverRouter.getDataSource();
    }

    @Override
    public void setDataSourceLookup(@Nullable DataSourceLookup dataSourceLookup) {
        super.setDataSourceLookup(dataSourceLookup);
    }

    public Map<Object, Object> getTargetDataSources() {
        try {
            Field targetDataSources = this.getClass().getSuperclass().getDeclaredField("targetDataSources");
            targetDataSources.setAccessible(true);
            Object o = targetDataSources.get(this);
            return (Map<Object, Object>) o;
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }
}
