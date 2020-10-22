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

import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.ClientBase;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.Remove2IF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.RemoveIF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.RemoveNotRetainIF;
import com.sun.ts.tests.ejb30.bb.session.stateful.remove.common.TestIF;
import com.sun.ts.tests.ejb30.common.migration.twothree.TwoRemoteHome;
import java.lang.reflect.Method;
import java.util.LinkedHashSet;
import java.util.Set;
import javax.ejb.EJB;

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
            methodList.add("removeTwoRemoteHome");
            methodList.add("removeTwoRemoteHomeHandle");
            methodList.add("testBeanRemoveTwoLocal");
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
            theTests.execMethod(theTests, methodName);
            System.out.println(String.format(messageFormat, startTag, "end", methodName, endTag));
            System.out.println();
        }

    }

    protected void execMethod(Object object, String methodName) {
        try {
            Method testMethod = getTestMethod(methodName);
            testMethod.invoke(object, null);
            System.out.println(String.format(messageFormat, startTag, "success", methodName, endTag));
        } catch (Exception e) {
            System.out.println(String.format(messageFormat, startTag, "failed", methodName, endTag));
            e.printStackTrace();
        }
    }

    protected Method getTestMethod(String methodName) throws NoSuchMethodException {
        Method method = this.getClass().getMethod(methodName);
        return method;
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
