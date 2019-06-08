package com.bes.enterprise.jdbc.support;

import javax.sql.DataSource;

/**
 * Created by JasonFitch on 6/5/2019.
 */
public class DataSourceTxHolder {

    private static final ThreadLocal<Object> dataSources = new ThreadLocal<>();

    public static void setDataSource(Object dataSource) {
        dataSources.set(dataSource);
    }

    public static Object getDataSource() {
        return dataSources.get();
    }

    public static void clearDataSource() {
        dataSources.remove();
    }
}
