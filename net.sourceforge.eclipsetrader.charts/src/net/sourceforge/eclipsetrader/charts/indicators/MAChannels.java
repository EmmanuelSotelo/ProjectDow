/*
 * Copyright (c) 2004-2006 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Serge Adzinets - initial implementation
 */

package net.sourceforge.eclipsetrader.charts.indicators;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import net.sourceforge.eclipsetrader.charts.IndicatorPlugin;
import net.sourceforge.eclipsetrader.charts.PlotLine;
import net.sourceforge.eclipsetrader.charts.Settings;
import net.sourceforge.eclipsetrader.core.db.BarData;

/**
 * Moving Average Channels (Envelopes) plugin.
 * MA channels are defined as:
 * MAU (Upper) = EMA + EMA * channel coefficient
 * MAL (Lower) = EMA - EMA * channel coefficient
 *
 */
public class MAChannels extends IndicatorPlugin
{
    public static final boolean DEFAULT_SCALE_FLAG = false;
    public static final RGB DEFAULT_COLOR = new RGB(0, 0, 192);
    public static final int DEFAULT_LINETYPE = PlotLine.LINE;
    public static final int DEFAULT_PERIOD = 20;
    public static final int DEFAULT_MATYPE = EMA;
    public static final double DEFAULT_PERCENTAGE = 7.0;
    private Color color = new Color(null, DEFAULT_COLOR);
    private int lineType = DEFAULT_LINETYPE;
    private int period = DEFAULT_PERIOD;
    private int maType = DEFAULT_MATYPE;
    private double percentage = DEFAULT_PERCENTAGE;
    private boolean scaleFlag = DEFAULT_SCALE_FLAG;

    public MAChannels()
    {
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.charts.IndicatorPlugin#calculate()
     */
    public void calculate()
    {
        PlotLine in = new PlotLine(getBarData(), BarData.CLOSE);

        PlotLine sma = getMA(in, maType, period);

        PlotLine mau = new PlotLine();
        mau.setColor(color);
        mau.setType(lineType);
        mau.setLabel("MAU"); //$NON-NLS-1$

        PlotLine mal = new PlotLine();
        mal.setColor(color);
        mal.setType(lineType);
        mal.setLabel("MAL"); //$NON-NLS-1$

        double upperMultiplier = 1.0 + percentage / 100.0;
        double lowerMultiplier = 1.0 - percentage / 100.0;

        for (int i = 0; i < sma.getSize(); i++)
        {
            mau.append(sma.getData(i) * upperMultiplier);
            mal.append(sma.getData(i) * lowerMultiplier);
        }

        getOutput().add(mau);
        getOutput().add(mal);

        getOutput().setScaleFlag(scaleFlag);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.charts.IndicatorPlugin#setParameters(net.sourceforge.eclipsetrader.charts.Settings)
     */
    public void setParameters(Settings settings)
    {
        scaleFlag = settings.getBoolean("scaleFlag", scaleFlag); //$NON-NLS-1$
        color = settings.getColor("color", color); //$NON-NLS-1$
        lineType = settings.getInteger("lineType", lineType).intValue(); //$NON-NLS-1$
        period = settings.getInteger("period", period).intValue(); //$NON-NLS-1$
        maType = settings.getInteger("maType", maType).intValue(); //$NON-NLS-1$
        percentage = settings.getDouble("percentage", percentage).doubleValue(); //$NON-NLS-1$
    }
}
