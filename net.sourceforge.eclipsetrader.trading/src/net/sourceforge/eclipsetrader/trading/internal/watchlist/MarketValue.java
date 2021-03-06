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
import java.util.Iterator;

import net.sourceforge.eclipsetrader.core.CurrencyConverter;
import net.sourceforge.eclipsetrader.core.db.Watchlist;
import net.sourceforge.eclipsetrader.core.db.WatchlistItem;
import net.sourceforge.eclipsetrader.core.db.feed.Quote;

import org.eclipse.jface.viewers.LabelProvider;

public class MarketValue extends LabelProvider implements Comparator
{
    private NumberFormat formatter = NumberFormat.getInstance();
    private NumberFormat percentFormatter = NumberFormat.getInstance();

    public MarketValue()
    {
        formatter.setGroupingUsed(true);
        formatter.setMinimumIntegerDigits(1);
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        
        percentFormatter.setGroupingUsed(false);
        percentFormatter.setMinimumIntegerDigits(1);
        percentFormatter.setMinimumFractionDigits(2);
        percentFormatter.setMaximumFractionDigits(2);
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
            if (quote != null && item.getPosition() != null && item.getPaidPrice() != null)
            {
                double current = CurrencyConverter.getInstance().convert(item.getPosition().intValue() * quote.getLast(), item.getSecurity().getCurrency(), item.getParent().getCurrency());
                return formatter.format(current); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        
        if (element instanceof Watchlist)
        {
            double current = 0;
            
            for (Iterator iter = ((Watchlist)element).getItems().iterator(); iter.hasNext(); )
            {
                WatchlistItem item = (WatchlistItem)iter.next();
                if (item.getSecurity() == null)
                    continue;

                Quote quote = item.getSecurity().getQuote();
                if (quote != null && item.getPosition() != null && item.getPaidPrice() != null)
                {
                    current += item.getPosition().intValue() * quote.getLast();
                }
            }
            
            if (current == 0)
                return ""; //$NON-NLS-1$
            
            return formatter.format(current); //$NON-NLS-1$ //$NON-NLS-2$
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
        if (quote != null && item.getPosition() != null && item.getPaidPrice() != null)
        {
            double current = CurrencyConverter.getInstance().convert(item.getPosition().intValue() * quote.getLast(), item.getSecurity().getCurrency(), item.getParent().getCurrency());
            return current;
        }
        return 0;
    }
}
