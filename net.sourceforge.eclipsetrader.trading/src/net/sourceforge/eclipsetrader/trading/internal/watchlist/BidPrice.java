/*
 * Copyright (c) 2004-2006 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package net.sourceforge.eclipsetrader.trading.internal.watchlist;

import java.text.NumberFormat;
import java.util.Comparator;

import net.sourceforge.eclipsetrader.core.CurrencyConverter;
import net.sourceforge.eclipsetrader.core.db.WatchlistItem;
import net.sourceforge.eclipsetrader.core.db.feed.Quote;

import org.eclipse.jface.viewers.LabelProvider;

public class BidPrice extends LabelProvider implements Comparator
{
    private NumberFormat formatter = NumberFormat.getInstance();

    public BidPrice()
    {
        formatter.setGroupingUsed(true);
        formatter.setMinimumIntegerDigits(1);
        formatter.setMinimumFractionDigits(4);
        formatter.setMaximumFractionDigits(4);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.LabelProvider#getText(java.lang.Object)
     */
    public String getText(Object element)
    {
        if (element instanceof WatchlistItem)
        {
            WatchlistItem item = (WatchlistItem)element;
            if (item.getSecurity() == null)
                return ""; //$NON-NLS-1$
            Quote quote = item.getSecurity().getQuote();
            if (quote != null && quote.getBid() != 0)
                return formatter.format(CurrencyConverter.getInstance().convert(quote.getBid(), item.getSecurity().getCurrency(), item.getParent().getCurrency()));
        }

        return ""; //$NON-NLS-1$
    }

    /* (non-Javadoc)
     * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
     */
    public int compare(Object arg0, Object arg1)
    {
        if (getValue((WatchlistItem)arg0) > getValue((WatchlistItem)arg1))
            return 1;
        else if (getValue((WatchlistItem)arg0) < getValue((WatchlistItem)arg1))
            return -1;
        return 0;
    }

    private double getValue(WatchlistItem item)
    {
        if (item.getSecurity() == null)
            return 0;
        Quote quote = item.getSecurity().getQuote();
        if (quote != null && quote.getBid() != 0)
            return CurrencyConverter.getInstance().convert(quote.getBid(), item.getSecurity().getCurrency(), item.getParent().getCurrency());
        return 0;
    }
}
