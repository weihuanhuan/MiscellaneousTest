/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

/*
 * $Id$
 */

package com.sun.ts.tests.ejb30.bb.session.stateful.remove.descriptor;

import com.sun.ts.lib.harness.Fault;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.Remove2IF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.RemoveIF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.RemoveLocal2IF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.RemoveLocalIF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.TestBeanBase;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.TestIF;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.common.helper.TestFailedException;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoLocalHome;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoRemoteHome;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoRemoteIF;

import javax.annotation.Resource;
import javax.ejb.CreateException;
import javax.ejb.EJB;
import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import javax.ejb.NoSuchEJBException;
import javax.ejb.Remote;
import javax.ejb.RemoveException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.rmi.RemoteException;
import java.util.Properties;

//@Remote({TestIF.class})
//@Stateless(name = "StatelessTestBean")
public class StatelessTestBean extends TestBeanBase implements TestIF {

    //@Resource(name = "sessionContext")
    private SessionContext sessionContext;

    //@EJB(name="removeBean")
    private RemoveLocalIF removeBean;

    //@EJB(name="removeBean2")
    private RemoveLocal2IF removeBean2;

    @EJB(name = "removeBeanRemote")
    private RemoveIF removeBeanRemote;

    @EJB(name = "removeBean2Remote")
    private Remove2IF removeBean2Remote;

    @EJB(name = "twoLocalHome")
    private TwoLocalHome twoLocalHome;

    @EJB(name = "twoRemoteHome")
    private TwoRemoteHome twoRemoteHome;

    protected RemoveLocalIF getRemoveLocalBean() {
        // return removeBean;
        return (RemoveLocalIF) (sessionContext.lookup("removeBean"));
    }

    protected RemoveLocal2IF getRemoveLocalBean2() {
        // return removeBean2;
        return (RemoveLocal2IF) (sessionContext.lookup("removeBean2"));
    }

    protected void setRemoveLocalBean(RemoveLocalIF b) {
        this.removeBean = b;
    }

    protected void setRemoveLocalBean2(RemoveLocal2IF b) {
        this.removeBean2 = b;
    }

    protected TwoLocalHome getTwoLocalHome() {
        return (TwoLocalHome) (sessionContext.lookup("twoLocalHome"));
    }

    //远程接口用于直接返回 remote bean 到 client

    @Override
    public RemoveIF getRemoveRemoteBeanReturn() {
        return (RemoveIF) sessionContext.lookup("removeBeanRemote");
    }

    @Override
    public RemoveIF getRemoveRemoteBeanByRemoteCtxReturn() throws NamingException {
        Properties properties = new Properties();

        //这里的 url 使用了 tcp 形式调用的 remote ejb。
        properties.put(Context.PROVIDER_URL, "ejbd://127.0.0.1:4201");
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");

        //不是使用 openejb.client.moduleId 属性，则 tomee 的 server 端执行默认在 "openejb/remote/" 下面的检索
        //这个方式和将 moduleId 设置为 "openejb/global" 时对于 global 资源定位是等效的，remote 和 global 都有 jndi 子树 global。
        //参考 org.apache.openejb.server.ejbd.JndiRequestHandler#getPrefix
        //properties.setProperty("openejb.client.moduleId", "stateful_remove_annotated_client");

        //此时资源的名字就是 ejb bean 的部署模块名，bean名以及接口信息的联合。
        InitialContext initialContext = new InitialContext(properties);
        RemoveIF lookup = (RemoveIF) initialContext.
                lookup("global/stateful_remove_annotated/stateful_remove_annotated_ejb/RemoveBean!com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.RemoveIF");
        return lookup;
    }

    @Override
    public Remove2IF getRemoveRemoteBean2Return() {
        //这里是在 server 端执行的 bean 查询，所以其名字使用的是 server 端 ejb 模块通过 DD 或者 @EJB 注解定义的名字
        return (Remove2IF) sessionContext.lookup("removeBean2Remote");
    }

    @Override
    public Remove2IF getRemoveRemoteBean2ByRemoteCtxReturn() throws NamingException {
        Properties properties = new Properties();

        //这里的 url 使用了 http 形式调用的 remote ejb。
        properties.put(Context.PROVIDER_URL, "http://127.0.0.1:8080/tomee/ejb");
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");

        //这里使用的是标准的 openejb.client.moduleId 属性，其值是一个部署在 server 上的 client 模块的模块名。
        //可以通过这个 moduleId 来获取在 server 上部署的指定 client 模块对应的 jndi 子树上面的资源。
        properties.setProperty("openejb.client.moduleId", "stateful_remove_annotated_client");

        //其资源名就是 client 应用中通过 DD 或者是 @EJB 注解定义的资源。
        InitialContext initialContext = new InitialContext(properties);
        Remove2IF lookup = (Remove2IF) initialContext.lookup("comp/env/removeBean2");
        return lookup;
    }

    @Override
    public TwoRemoteHome getTwoRemoteHomeReturn() {
        //这里使用的是 ejb 容器环境中的 context，
        //一般是属于同 jvm 的调用
        return (TwoRemoteHome) sessionContext.lookup("twoRemoteHome");
    }

    @Override
    public TwoRemoteHome getTwoRemoteHomeByRemoteCtxReturn() throws NamingException {
        //直接使用 RemoteInitialContextFactory 来使用通过远程客户端的模式获取远程序列化过来的 ejb home 对象。
        //相当于跨 jvm 的 jndi 调用
        Properties properties = new Properties();
        properties.put(Context.PROVIDER_URL, "http://127.0.0.1:8080/tomee/ejb");
        properties.put(Context.INITIAL_CONTEXT_FACTORY, "org.apache.openejb.client.RemoteInitialContextFactory");

        //这里的 openejb.client.moduleId 设置的并不是本 app client 的 id ，
        //而是直接取巧的使用了 global 作为 moduleId 来使用 server 端 jndi 树上的 openejb/global 子树，其包含了所有 global 域中的资源
        properties.setProperty("openejb.client.moduleId", "openejb/global");

        //此时资源的名字就是 ejb bean 的部署模块名，bean名以及接口信息的联合，这里的信息来源于下面的类方法
        //org.apache.openejb.server.ejbd.JndiRequestHandler#getPrefix
        //org.apache.openejb.server.ejbd.JndiRequestHandler#doLookup
        InitialContext initialContext = new InitialContext(properties);
        TwoRemoteHome lookup = (TwoRemoteHome) initialContext.
                lookup("global/stateful_remove_annotated/stateful_remove_annotated_ejb/RemoveBean!com.sun.ts.tests.ejb30.common.migration.twothree.TwoRemoteHome");
        return lookup;
    }

    //远程接口用于直接返回 remote bean 的 object handle 和 home handle 到 client

    @Override
    public Handle getTwoRemoteHomeObjectHandleReturn() throws Fault {
        TwoRemoteHome twoRemoteHome = (TwoRemoteHome) sessionContext.lookup("twoRemoteHome");

        TwoRemoteIF bean = null;
        try {
            bean = twoRemoteHome.create();
            twoRemoteHome.remove(bean);
        } catch (RemoveException e) {
            TLogger.log("Got expected exception " + e.toString());
        } catch (CreateException e) {
            throw new Fault(e);
        } catch (RemoteException e) {
            throw new Fault(e);
        }

        try {
            Handle handle = bean.getHandle();
            return handle;
        } catch (RemoteException e) {
            throw new Fault(e);
        }
    }

    @Override
    public HomeHandle getTwoRemoteHomeHomeHandleReturn() throws RemoteException {
        TwoRemoteHome twoRemoteHome = (TwoRemoteHome) sessionContext.lookup("twoRemoteHome");
        return twoRemoteHome.getHomeHandle();
    }

}
