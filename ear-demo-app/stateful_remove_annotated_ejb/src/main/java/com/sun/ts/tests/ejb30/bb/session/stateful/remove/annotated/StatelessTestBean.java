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

package com.sun.ts.tests.ejb30.bb.session.stateful.remove.annotated;

import com.sun.ts.lib.harness.Fault;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.Remove2IF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.RemoveIF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.TestBeanBase;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.TestIF;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoLocalHome;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoRemoteHome;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoRemoteIF;

import javax.annotation.Resource;
import javax.ejb.CreateException;
import javax.ejb.EJB;
import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import javax.ejb.Remote;
import javax.ejb.RemoveException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import java.rmi.RemoteException;

@Remote({TestIF.class})
@Stateless(name = "StatelessTestBean")
public class StatelessTestBean extends TestBeanBase implements TestIF {

    @Resource(name = "sessionContext")
    private SessionContext sessionContext;

    @EJB(name = "removeBeanRemote")
    private RemoveIF removeBeanRemote;

    @EJB(name = "removeBean2Remote")
    private Remove2IF removeBean2Remote;

    @EJB(name = "twoLocalHome")
    private TwoLocalHome twoLocalHome;

    @EJB(name = "twoRemoteHome")
    private TwoRemoteHome twoRemoteHome;


    protected TwoLocalHome getTwoLocalHome() {
        return (TwoLocalHome) (sessionContext.lookup("twoLocalHome"));
    }

    //远程接口用于直接返回 remote bean 到 client

    @Override
    public RemoveIF getRemoveRemoteBeanReturn() {
        return (RemoveIF) sessionContext.lookup("removeBeanRemote");
    }

    @Override
    public Remove2IF getRemoveRemoteBean2Return() {
        return (Remove2IF) sessionContext.lookup("removeBean2Remote");
    }

    @Override
    public TwoRemoteHome getTwoRemoteHomeReturn() {
        return (TwoRemoteHome) sessionContext.lookup("twoRemoteHome");
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
