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

package net.sourceforge.eclipsetrader.trading.patterns;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.eclipsetrader.core.IPattern;
import net.sourceforge.eclipsetrader.core.Sentiment;
import net.sourceforge.eclipsetrader.core.db.Bar;
import net.sourceforge.eclipsetrader.core.db.Security;

public class DoubleReversalRule implements IPattern
{
    boolean _reversals[] = new boolean[3];
    List bars = new ArrayList();
    int minimumBars = 6;
    int maximumBars = 6;
    Sentiment sentiment = Sentiment.INVALID;

    public DoubleReversalRule()
    {
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.core.IPattern#init(net.sourceforge.eclipsetrader.core.db.Security)
     */
    public void init(Security security)
    {
        bars = new ArrayList();
        sentiment = Sentiment.INVALID;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.core.IPattern#add(net.sourceforge.eclipsetrader.core.db.Bar)
     */
    public void add(Bar bar)
    {
        bars.add(bar);
        if (bars.size() > maximumBars)
            bars.remove(0);

        if (bars.size() >= minimumBars)
        {
            sentiment = Sentiment.NEUTRAL;
            Bar[] recs = (Bar[])bars.toArray(new Bar[bars.size()]);

            // scan for bullish reversals
            boolean reversals[] = { false, false, false, false, false, false };
            int end = (recs.length < 6 ? recs.length : 6) - 1;
            int complete[] = { 0, 0 };

            for (int n = 0; n < end; ++n)
            {
                Bar rn = recs[n];
                Bar rn1 = recs[n + 1]; // save memory accesses
                double high[] = { rn.getHigh(), rn1.getHigh() };
                double low[] = { rn.getLow(), rn1.getLow() };
                double close[] = { rn.getClose(), rn1.getClose() };

                // high-low reversal
                if (close[1] >= high[1] - 0.05 && close[0] <= low[0] + 0.05)
                {
                    reversals[0] = true;
                    if (n + 1 > complete[0])
                        complete[0] = n + 1;
                }
                else if (close[0] >= high[0] - 0.05 && close[1] <= low[0] + 0.05)
                {
                    reversals[3] = true;
                    if (n + 1 > complete[1])
                        complete[1] = n + 1;
                }

                // closing price reversal
                if (low[1] < low[0] // lower low
                        && close[1] > close[0]) //higher close
                {
                    reversals[1] = true;
                    if (n + 1 > complete[0])
                        complete[0] = n + 1;
                }
                else if (high[1] >= high[0] // higher high
                        && close[1] <= close[0]) // lower close
                {
                    reversals[4] = true;
                    if (n + 1 > complete[1])
                        complete[1] = n + 1;
                }

                // key reversal
                if (high[0] < high[1] && low[0] > low[1])
                {
                    if (close[1] >= high[0])
                    {
                        reversals[2] = true;
                        if (n + 1 > complete[0])
                            complete[0] = n + 1;
                    }
                }
                else if (high[0] < high[1] && low[0] > low[1])
                {
                    if (close[1] <= low[0])
                    {
                        reversals[5] = true;
                        if (n + 1 > complete[1])
                            complete[1] = n + 1;
                    }
                }
            }

            if ((reversals[0] && (reversals[1] || reversals[2])) || (reversals[1] && (reversals[0] || reversals[2])) || (reversals[2] && (reversals[0] || reversals[1])))
            {
                _reversals[0] = reversals[0];
                _reversals[1] = reversals[1];
                _reversals[2] = reversals[2];
                sentiment = Sentiment.BULLISH;
                return;
            }
            else if ((reversals[3] && (reversals[4] || reversals[5])) || (reversals[4] && (reversals[3] || reversals[5])) || (reversals[5] && (reversals[3] || reversals[4])))
            {
                _reversals[0] = reversals[3];
                _reversals[1] = reversals[4];
                _reversals[2] = reversals[5];
                sentiment = Sentiment.BEARISH;
                return;
            }
            else
                _reversals[0] = _reversals[1] = _reversals[2] = false;
        }
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.core.IPattern#getSentiment()
     */
    public Sentiment getSentiment()
    {
        return sentiment;
    }
}
