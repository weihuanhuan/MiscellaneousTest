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

package com.sun.ts.tests.ejb30.bb.session.stateful.remove.common;

import com.sun.ts.lib.harness.Fault;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.common.helper.TestFailedException;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoRemoteHome;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoRemoteIF;

import javax.ejb.CreateException;
import javax.ejb.Handle;
import javax.ejb.RemoveException;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.Properties;

abstract public class ClientBase {

    protected Properties props;

    private RemoveIF removeBean;

    private Remove2IF removeBean2;

    private TestIF testBean;

    abstract protected RemoveIF getRemoveBean();

    abstract protected Remove2IF getRemoveBean2();

    abstract protected TestIF getTestBean();

    abstract protected TwoRemoteHome getTwoRemoteHome();

    public void setup() throws Fault {
        props = null;
        removeBean = getRemoveBean();
        removeBean2 = getRemoveBean2();
        testBean = getTestBean();
    }

    public void cleanup() throws Fault {
        removeBeans();
    }

    protected void removeBeans() {
        if (removeBean != null) {
            try {
                removeBean.remove();
                TLogger.log("Successfully removed removeBean.");
            } catch (Exception e) {
                TLogger.log("Exception while removing removeBean " + e);
            }
        }
        if (removeBean2 != null) {
            try {
                removeBean2.remove();
                TLogger.log("Successfully removed removeBean2.");
            } catch (Exception e) {
                TLogger.log("Exception while removing removeBean2 " + e);
            }
        }
        // no need to remove stateless bean
        // if(testBean != null) {
        // try {
        // testBean.remove();
        // TLogger.log("Successfully removed testBean.");
        // } catch (Exception e) {
        // TLogger.log("Exception while removing testBean " + e);
        // }
        // }
    }

    //////////////////////////////////////////////////////////////////////

    /*
     * testName: removeTwoRemoteHome
     *
     * @test_Strategy:
     *
     */
    public void removeTwoRemoteHome() throws Fault {
        TwoRemoteHome beanHome = getTwoRemoteHome();
        TwoRemoteIF bean = null;
        try {
            bean = beanHome.create();
            bean.remove();
        } catch (RemoveException e) {
            throw new Fault(e);
        } catch (CreateException e) {
            throw new Fault(e);
        } catch (RemoteException e) {
            throw new Fault(e);
        }
        try {
            bean.remove();
            throw new Fault(
                    "Expecting java.rmi.NoSuchObjectException, but got none.");
        } catch (NoSuchObjectException e) {
            TLogger.log("Got expected exception: " + e.toString());
        } catch (RemoveException e) {
            throw new Fault(e);
        } catch (RemoteException e) {
            throw new Fault(e);
        }
    }

    /*
     * testName: removeTwoRemoteHomeHandle
     *
     * @test_Strategy:
     *
     */
    public void removeTwoRemoteHomeHandle() throws Fault {
        TwoRemoteHome beanHome = getTwoRemoteHome();
        TwoRemoteIF bean = null;
        try {
            bean = beanHome.create();
            beanHome.remove(bean);
        } catch (RemoveException e) {
            TLogger.log("Got expected exception " + e.toString());
        } catch (CreateException e) {
            throw new Fault(e);
        } catch (RemoteException e) {
            throw new Fault(e);
        }

        try {
            Handle handle = bean.getHandle();
            beanHome.remove(handle);
            TLogger.log("Successfully removed bean handler " + handle);
        } catch (RemoveException e) {
            throw new Fault(e);
        } catch (RemoteException e) {
            throw new Fault(e);
        }

        try {
            bean.remove();
            throw new Fault(
                    "Expecting java.rmi.NoSuchObjectException, but got none.");
        } catch (NoSuchObjectException e) {
            TLogger.log("Got expected exception: " + e.toString());
        } catch (RemoveException e) {
            throw new Fault(e);
        } catch (RemoteException e) {
            throw new Fault(e);
        }
    }

    /*
     * testName: testBeanRemoveTwoLocal
     *
     * @test_Strategy:
     *
     */
    public void testBeanRemoveTwoLocal() throws Fault {
        try {
            testBean.removeTwoLocal();
        } catch (TestFailedException e) {
            throw new Fault(e);
        }
    }

    /*
     * testName: testBeanRemoveBean
     *
     * @test_Strategy:
     *
     */
    public void testBeanRemoveBean() throws Fault {
        try {
            testBean.removeBean();
        } catch (TestFailedException e) {
            throw new Fault(e);
        }
    }

    /*
     * testName: testBeanRemoveBean2
     *
     * @test_Strategy:
     *
     */
    public void testBeanRemoveBean2() throws Fault {
        try {
            testBean.removeBean2();
        } catch (TestFailedException e) {
            throw new Fault(e);
        }
    }

}
