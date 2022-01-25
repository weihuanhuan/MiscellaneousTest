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
package com.sun.ts.tests.ejb30.common.helper;

import com.sun.ts.lib.util.TestUtil;

import java.util.logging.Logger;

public final class Helper {
    private static Logger logger = Logger.getLogger("com.sun.ts.tests.ejb30");

    private Helper() {
    }

    public static Logger getLogger() {
        return logger;
    }

    public static void assertEquals(final String messagePrefix,
                                    final Object expected, final Object actual, final StringBuilder sb)
            throws RuntimeException {
        sb.append(TestUtil.NEW_LINE);
        if (messagePrefix != null) {
            sb.append(TestUtil.NEW_LINE).append(messagePrefix).append(" ");
        }
        if (equalsOrNot(expected, actual)) {
            sb.append("Got the expected result:").append(actual).append("\t");
        } else {
            sb.append("Expecting ").append(expected).append(", but actual ")
                    .append(actual);
            throw new RuntimeException(sb.toString());
        }
    }

    private static boolean equalsOrNot(final Object expected,
                                       final Object actual) {
        boolean sameOrNot = false;
        if (expected == null) {
            if (actual == null) {
                sameOrNot = true;
            }
        } else {
            sameOrNot = expected.equals(actual);
        }
        return sameOrNot;
    }
}
