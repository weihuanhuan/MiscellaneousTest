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
import com.sun.ts.tests.ejb30.common.helper.TestFailedException;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoRemoteHome;

import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import java.rmi.RemoteException;

public interface TestIF {
    //////////////////////////////////////////////////////////////////////

    void removeTwoLocal() throws TestFailedException;

    ////////////////////////////////////////////////////////////////////
    // 测试返回间接的 remote ejb

    RemoveIF getRemoveRemoteBeanReturn();

    Remove2IF getRemoveRemoteBean2Return();

    TwoRemoteHome getTwoRemoteHomeReturn();

    Handle getTwoRemoteHomeObjectHandleReturn() throws Fault;

    HomeHandle getTwoRemoteHomeHomeHandleReturn() throws RemoteException;
}
