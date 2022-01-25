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

package com.sun.ts.lib.util;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * TestUtil is a final utility class responsible for implementing logging across
 * multiple VMs. It also contains many convenience methods for logging property
 * object contents, stacktraces, and header lines.
 *
 * @author Kyle Grucci
 */
public final class TestUtil {

    public static String NEW_LINE = System.getProperty("line.separator", "\n");

    /**
     * prints the stacktrace of a Throwable to a string
     *
     * @param e exception to print the stacktrace of
     */
    public static String printStackTraceToString(Throwable e) {
        String sTrace = "";
        if (e == null)
            return "";
        try {
            StringWriter sw = new StringWriter();
            PrintWriter writer = new PrintWriter(sw);
            e.printStackTrace(writer);
            sTrace = sw.toString();
            writer.close();
        } catch (Exception E) {
        }
        return sTrace;
    }

}
