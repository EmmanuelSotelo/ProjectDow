/*
 * Copyright (c) 2004-2007 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package net.sourceforge.eclipsetrader.trading.portfolio;

import java.util.Comparator;

import net.sourceforge.eclipsetrader.core.db.Account;

public class AccountComparator implements Comparator
{
    private static final AccountComparator instance = new AccountComparator();

    private AccountComparator()
    {
    }

    public static AccountComparator getInstance()
    {
        return instance;
    }

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object o1, Object o2)
    {
        return ((Account)o1).getDescription().compareTo(((Account)o2).getDescription());
    }
}
