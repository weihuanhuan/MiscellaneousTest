/*
 * Copyright (c) 2009, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.ts.tests.common.vehicle.ejbliteshare;

import java.util.Map;

public interface EJBLiteClientIF {
    public static final String TEST_PASSED = "[TEST PASSED] ";

    public static final String TEST_FAILED = "[TEST FAILED] ";

    public static final String JAVA_GLOBAL_PREFIX = "java:global/";

    public static final String JAVA_COMP_ENV_PREFIX = "java:comp/env/";

    public static final String EJBEMBED_JAR_NAME_BASE = "ejbembed_vehicle_ejb";

    public void setInjectionSupported(Boolean injectionSupported);

    public void runTestInVehicle();

    public void setTestName(String testName);

    public String getStatus();

    public String getReason();

    public void setModuleName(String mn);

    public Map<String, String> getJndiMapping();

    public javax.naming.Context getContext();

}
