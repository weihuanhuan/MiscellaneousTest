/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bes.enterprise.jdbc.datasource;

import com.bes.enterprise.jdbc.reflection.Reflections;
import com.bes.enterprise.jdbc.strategy.Router;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class RoutedDataSource implements DataSource {

    protected Router delegate;

    public RoutedDataSource() {
        // no-op
    }

    public RoutedDataSource(final Router router) {
        delegate = router;
    }

    public PrintWriter getLogWriter() throws SQLException {
        if (getTargetDataSource() == null) {
            return null;
        }
        return getTargetDataSource().getLogWriter();
    }

    public void setLogWriter(final PrintWriter out) throws SQLException {
        if (getTargetDataSource() != null) {
            getTargetDataSource().setLogWriter(out);
        }
    }

    public void setLoginTimeout(final int seconds) throws SQLException {
        if (getTargetDataSource() != null) {
            getTargetDataSource().setLoginTimeout(seconds);
        }
    }

    public int getLoginTimeout() throws SQLException {
        if (getTargetDataSource() == null) {
            return -1;
        }
        return getTargetDataSource().getLoginTimeout();
    }

    public <T> T unwrap(final Class<T> iface) throws SQLException {
        if (getTargetDataSource() == null) {
            return null;
        }
        return (T) Reflections.invokeByReflection(getTargetDataSource(), "unwrap",
            new Class<?>[]{Class.class}, new Object[]{iface});
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        if (getTargetDataSource() == null) {
            return null;
        }
        return (Logger) Reflections.invokeByReflection(getTargetDataSource(), "getParentLogger", new Class<?>[0], null);
    }

    public boolean isWrapperFor(final Class<?> iface) throws SQLException {
        if (getTargetDataSource() == null) {
            return false;
        }
        return (Boolean) Reflections.invokeByReflection(getTargetDataSource(), "isWrapperFor",
            new Class<?>[]{Class.class}, new Object[]{iface});
    }

    public Connection getConnection() throws SQLException {
        return getTargetDataSource().getConnection();
    }

    public Connection getConnection(final String username, final String password)
        throws SQLException {
        return getTargetDataSource().getConnection(username, password);
    }

    public Router getDelegate() {
        if (delegate == null) {
            throw new IllegalStateException("a router has to be defined");
        }
        return delegate;
    }

    private DataSource getTargetDataSource() {
        return getDelegate().getDataSource();
    }
}
