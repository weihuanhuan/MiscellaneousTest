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

import com.sun.ts.tests.ejb30.common.helper.TestFailedException;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoLocalHome;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoLocalIF;

import javax.ejb.CreateException;
import javax.ejb.NoSuchEJBException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.RemoveException;

abstract public class TestBeanBase implements TestIF {

    abstract protected RemoveLocalIF getRemoveLocalBean();

    abstract protected RemoveLocal2IF getRemoveLocalBean2();

    abstract protected void setRemoveLocalBean(RemoveLocalIF b);

    abstract protected void setRemoveLocalBean2(RemoveLocal2IF b);

    abstract protected TwoLocalHome getTwoLocalHome();

    //////////////////////////////////////////////////////////////////////

    @Override
    public void removeBean() throws TestFailedException {
        RemoveLocalIF removeBean = getRemoveLocalBean();
        removeBean.hi();
        removeBean.remove();
        setRemoveLocalBean(null);
        try {
            removeBean.remove2();
            throw new TestFailedException(
                    "Expecting javax.ejb.NoSuchEJBException, " + "but got none");
        } catch (NoSuchEJBException e) {
            // good.
        }
    }

    @Override
    public void removeBean2() throws TestFailedException {
        RemoveLocal2IF removeBean2 = getRemoveLocalBean2();
        removeBean2.hi();
        removeBean2.remove();
        setRemoveLocalBean2(null);
        try {
            removeBean2.remove2();
            throw new TestFailedException(
                    "Expecting javax.ejb.NoSuchEJBException, " + "but got none");
        } catch (NoSuchEJBException e) {
            // good.
        }
    }

    @Override
    public void removeTwoLocal() throws TestFailedException {
        TwoLocalHome beanHome = getTwoLocalHome();
        TwoLocalIF bean = null;
        try {
            bean = beanHome.create();
            beanHome.remove(bean);
            throw new TestFailedException(
                    "Expecting javax.ejb.RemoveException, but got none");
        } catch (RemoveException e) {
            // TLogger.log("Got expected exception " + e.toString());
        } catch (CreateException e) {
            throw new TestFailedException(
                    "Expecting javax.ejb.RemoveException, but got", e);
        }
        try {
            bean.remove();
        } catch (RemoveException e) {
            throw new TestFailedException("Expecting no exception, but got", e);
        }
        try {
            bean.remove();
            throw new TestFailedException(
                    "Expecting javax.ejb.NoSuchObjectLocalException, " + "but got none.");
        } catch (NoSuchObjectLocalException e) {
            // TLogger.log("Got expected exception: " + e.toString());
        } catch (RemoveException e) {
            throw new TestFailedException(
                    "Expecting javax.ejb.NoSuchObjectLocalException, but got", e);
        }
    }
}
