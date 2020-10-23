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
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.ClientBase;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.Remove2IF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.RemoveIF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.RemoveNotRetainIF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.TestIF;
import com.sun.ts.tests.ejb30.common.helper.TLogger;
import com.sun.ts.tests.ejb30.common.helper.TestFailedException;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoRemoteHome;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoRemoteIF;
import java.lang.reflect.Method;
import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.ejb.CreateException;
import javax.ejb.EJB;
import javax.ejb.EJBHome;
import javax.ejb.EJBMetaData;
import javax.ejb.Handle;
import javax.ejb.HomeHandle;
import javax.ejb.NoSuchEJBException;
import javax.ejb.RemoveException;

public class Client extends ClientBase {
    @EJB(name = "removeBean")
    private static RemoveIF removeBean;

    @EJB(name = "removeBean2")
    private static Remove2IF removeBean2;

    @EJB(name = "testBean")
    private static TestIF testBean;

    @EJB(name = "removeNotRetainBean")
    private static RemoveNotRetainIF removeNotRetainBean;

    @EJB(name = "twoRemoteHome")
    private static TwoRemoteHome twoRemoteHome;

    private static final String startTag = "################";
    private static final String endTag = "################";
    private static final String messageFormat = "%s %s to test :%s %s";

    public static void main(String[] args) {
        Set<String> methodList = new LinkedHashSet<>();
        methodList.add("setup");
        if (args == null || args.length == 0) {
            //原始的测试
            methodList.add("removeTwoRemoteHome");
            methodList.add("removeTwoRemoteHomeHandle");
            methodList.add("testBeanRemoveTwoLocal");

            //测试间接的 bean
            methodList.add("testGetRemoveRemoteBeanReturn");
            methodList.add("testGetRemoveRemoteBean2Return");
            methodList.add("testGetTwoRemoteHomeReturn");
            methodList.add("testGetTwoRemoteHomeReturnHandle");
            methodList.add("testGetTwoRemoteHomeReturnHomeHandle");
        }
        for (String arg : args) {
            if (arg.equals("setup") || arg.equals("cleanup")) {
                continue;
            }
            methodList.add(arg);
        }
        methodList.add("cleanup");

        Client theTests = new Client();
        for (String methodName : methodList) {
            System.out.println(String.format(messageFormat, startTag, "start", methodName, endTag));
            execMethod(theTests, methodName);
            System.out.println(String.format(messageFormat, startTag, "end", methodName, endTag));
            System.out.println();
        }

    }

    protected static void execMethod(Object object, String methodName) {
        try {
            Method testMethod = getTestMethod(object, methodName);
            testMethod.invoke(object, null);
            System.out.println(String.format(messageFormat, startTag, "success", methodName, endTag));
        } catch (Exception e) {
            System.out.println(String.format(messageFormat, startTag, "failed", methodName, endTag));
            e.printStackTrace();
        }
    }

    protected static Method getTestMethod(Object object, String methodName) throws NoSuchMethodException {
        for (Method method : object.getClass().getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new NoSuchMethodException(String.format("cannot find method:%s from class %s.", methodName, object.getClass().getName()));
    }

    protected TestIF getTestBean() {
        return testBean;
    }

    protected RemoveIF getRemoveBean() {
        return removeBean;
    }

    protected Remove2IF getRemoveBean2() {
        return removeBean2;
    }

    protected RemoveNotRetainIF getRemoveNotRetainBean() {
        return removeNotRetainBean;
    }

    protected TwoRemoteHome getTwoRemoteHome() {
        return twoRemoteHome;
    }

    /**
     * 相似的测试和 removeBeanRemote 使用 bean removeBeanRemote, 但是他使用 server lookup 后的结果.
     *
     * @see com.sun.ts.tests.ejb30.bb.session.stateful.remove.annotated.StatelessTestBean#getRemoveRemoteBean()
     * @see com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.TestBeanBase#removeBeanRemote()
     */
    public void testGetRemoveRemoteBeanReturn() throws TestFailedException {
        RemoveIF removeRemoteBeanReturn = testBean.getRemoveRemoteBeanReturn();

        removeRemoteBeanReturn.hi();
        removeRemoteBeanReturn.remove();
        try {
            removeRemoteBeanReturn.remove2();
            throw new TestFailedException(
                    "Expecting javax.ejb.NoSuchEJBException, " + "but got none");
        } catch (NoSuchEJBException e) {
            // good.
        }
    }

    /**
     * 相似的测试和 removeBean2Remote 使用 bean removeBean2Remote, 但是他使用 server lookup 后的结果.
     *
     * @see com.sun.ts.tests.ejb30.bb.session.stateful.remove.annotated.StatelessTestBean#getRemoveRemoteBean2()
     * @see com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.TestBeanBase#removeBean2Remote()
     */
    public void testGetRemoveRemoteBean2Return() throws TestFailedException {
        Remove2IF removeRemoteBean2Return = testBean.getRemoveRemoteBean2Return();

        try {
            removeRemoteBean2Return.hi();
            removeRemoteBean2Return.remove();
        } catch (RemoteException e) {
            throw new TestFailedException(e);
        }
        try {
            removeRemoteBean2Return.remove2();
            throw new TestFailedException(
                    "Expecting java.rmi.NoSuchObjectException, " + "but got none");
        } catch (java.rmi.NoSuchObjectException e) {
            // good.
            // note that Remove2IF extends java.rmi.Remote and so we are expecting
            // java.rmi.NoSuchObjectException, rather than NoSuchEJBException.
        } catch (RemoteException e) {
            throw new TestFailedException(e);
        }
    }

    /**
     * 相似的测试和 removeTwoRemoteHome 使用 bean twoRemoteHome, 但是他使用 server lookup 后的结果.
     *
     * @see ClientBase#getTwoRemoteHome()
     * @see ClientBase#removeTwoRemoteHome()
     */
    public void testGetTwoRemoteHomeReturn() throws RemoteException, Fault {
        TwoRemoteHome twoRemoteHomeReturn = testBean.getTwoRemoteHomeReturn();

        TwoRemoteIF bean = null;
        try {
            bean = twoRemoteHomeReturn.create();
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

    /**
     * 相似的测试和 removeTwoRemoteHomeHandle 使用 bean twoRemoteHome, 但是他使用 server lookup 后的结果.
     *
     * @see ClientBase#getTwoRemoteHome()
     * @see ClientBase#removeTwoRemoteHomeHandle()
     */
    public void testGetTwoRemoteHomeReturnHandle() throws RemoteException, Fault {
        TwoRemoteHome twoRemoteHomeReturn = testBean.getTwoRemoteHomeReturn();

        TwoRemoteIF bean = null;
        try {
            bean = twoRemoteHomeReturn.create();
            twoRemoteHomeReturn.remove(bean);
        } catch (RemoveException e) {
            TLogger.log("Got expected exception " + e.toString());
        } catch (CreateException e) {
            throw new Fault(e);
        } catch (RemoteException e) {
            throw new Fault(e);
        }

        try {
            Handle handle = bean.getHandle();
            twoRemoteHomeReturn.remove(handle);
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

    /**
     * 相似的测试和 removeTwoRemoteHomeHandle 使用 bean twoRemoteHome, 但是他使用 server lookup 后的结果,
     * 并且这里使用的是 HomeHandle， 之前的测试使用的是 ObjectHandle
     *
     * @see ClientBase#getTwoRemoteHome()
     * @see ClientBase#removeTwoRemoteHomeHandle()
     */
    public void testGetTwoRemoteHomeReturnHomeHandle() throws RemoteException, Fault {
        TwoRemoteHome twoRemoteHomeReturn = testBean.getTwoRemoteHomeReturn();
        HomeHandle homeHandle = twoRemoteHomeReturn.getHomeHandle();
        TwoRemoteHome ejbHomeByHomeHandle = (TwoRemoteHome) homeHandle.getEJBHome();

        TwoRemoteIF bean = null;
        try {
            bean = ejbHomeByHomeHandle.create();
            ejbHomeByHomeHandle.remove(bean);
        } catch (RemoveException e) {
            TLogger.log("Got expected exception " + e.toString());
        } catch (CreateException e) {
            throw new Fault(e);
        } catch (RemoteException e) {
            throw new Fault(e);
        }

        try {
            Handle handle = bean.getHandle();
            ejbHomeByHomeHandle.remove(handle);
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

        EJBMetaData twoRemoteHomeReturnEJBMetaData = twoRemoteHomeReturn.getEJBMetaData();
        EJBMetaData ejbMetaDataByHomeHandle = ejbHomeByHomeHandle.getEJBMetaData();
        System.out.println("twoRemoteHomeReturnEJBMetaData" + twoRemoteHomeReturnEJBMetaData);
        System.out.println("ejbMetaData" + ejbMetaDataByHomeHandle);
    }

  /*
   * @testName: removeBean
   * 
   * @assertion_ids: EJB:JAVADOC:148; EJB:JAVADOC:126; EJB:JAVADOC:125;
   * EJB:JAVADOC:147
   * 
   * @test_Strategy:
   * 
   */

  /*
   * @testName: removeBean2
   * 
   * @assertion_ids: EJB:JAVADOC:148; EJB:JAVADOC:126; EJB:JAVADOC:125;
   * EJB:JAVADOC:147
   * 
   * @test_Strategy:
   *
   */

  /*
   * @testName: testBeanremoveBean
   * 
   * @assertion_ids: EJB:JAVADOC:148; EJB:JAVADOC:126; EJB:JAVADOC:125;
   * EJB:JAVADOC:147
   * 
   * @test_Strategy:
   *
   */

  /*
   * @testName: testBeanremoveBeanRemote
   * 
   * @assertion_ids: EJB:JAVADOC:148; EJB:JAVADOC:126; EJB:JAVADOC:125;
   * EJB:JAVADOC:147
   * 
   * @test_Strategy: client remotely invokes testBean, which remotely invokes
   * RemoveBean via RemoteIF.
   *
   */

  /*
   * @testName: testBeanremoveBean2
   * 
   * @assertion_ids: EJB:JAVADOC:148; EJB:JAVADOC:126; EJB:JAVADOC:125;
   * EJB:JAVADOC:147
   * 
   * @test_Strategy:
   *
   */
  /*
   * @testName: testBeanremoveBean2Remote
   * 
   * @assertion_ids: EJB:JAVADOC:148; EJB:JAVADOC:126; EJB:JAVADOC:125;
   * EJB:JAVADOC:147
   * 
   * @test_Strategy: client remotely invokes testBean, which remotely invokes
   * RemoveBean via RemoteIF2.
   *
   */
  /*
   * @testName: retainBean
   * 
   * @assertion_ids:
   * 
   * @test_Strategy:
   *
   */
  /*
   * @testName: retainBean2
   * 
   * @assertion_ids:
   * 
   * @test_Strategy:
   *
   */
  /*
   * @testName: testBeanretainBean
   * 
   * @assertion_ids:
   * 
   * @test_Strategy:
   *
   */
  /*
   * @testName: testBeanretainBeanRemote
   * 
   * @assertion_ids:
   * 
   * @test_Strategy: client remotely invokes testBean, which remotely invokes
   * RemoveBean via RemoteIF.
   *
   */

  /*
   * @testName: testBeanretainBean2
   * 
   * @assertion_ids:
   * 
   * @test_Strategy:
   *
   */
  /*
   * @testName: testBeanretainBean2Remote
   * 
   * @assertion_ids:
   * 
   * @test_Strategy: client remotely invokes testBean, which remotely invokes
   * RemoveBean via RemoteIF2.
   *
   */

  /*
   * @testName: removeNotRetainBean
   * 
   * @assertion_ids:
   * 
   * @test_Strategy:
   *
   */
  /*
   * @testName: removeNotRetainBean2
   * 
   * @assertion_ids:
   * 
   * @test_Strategy:
   *
   */
  /*
   * @testName: alwaysRemoveAfterSystemException
   * 
   * @assertion_ids:
   * 
   * @test_Strategy: a bean must always be removed after a system exception,
   * even though the remove method is retainIfException true.
   *
   */
  /*
   * @testName: removeTwoRemoteHome
   * 
   * @assertion_ids:
   * 
   * @test_Strategy:
   *
   */
  /*
   * @testName: removeTwoRemoteHomeHandle
   * 
   * @assertion_ids:
   * 
   * @test_Strategy:
   *
   */
  /*
   * @testName: testBeanRemoveTwoLocal
   * 
   * @assertion_ids:
   * 
   * @test_Strategy:
   *
   */

  /*
   * @testName: testBeanretainBeanOverloaded
   * 
   * @test_Strategy:
   *
   */
  /*
   * @testName: retainBeanOverloaded
   * 
   * @test_Strategy:
   *
   */

}
