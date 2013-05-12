// A test indicator

package net.emmanuelsotelo.projectdow.indicators;

import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import net.sourceforge.eclipsetrader.charts.IndicatorPlugin;
import net.sourceforge.eclipsetrader.charts.PlotLine;
import net.sourceforge.eclipsetrader.charts.Settings;
import net.sourceforge.eclipsetrader.core.db.Bar;

public class Weight extends IndicatorPlugin
{
    public static final RGB DEFAULT_COLOR = new RGB(0, 0, 192);
    public static final String DEFAULT_LABEL = "Weight"; //$NON-NLS-1$
    public static final int DEFAULT_LINETYPE = PlotLine.LINE;
    public static final int DEFAULT_PERIOD = 2;
//    public static final int DEFAULT_INPUT = BarData.CLOSE;
    public static final double DEFAULT_MULTIPLIER = 1;
    
    private Color color = new Color(null, DEFAULT_COLOR);
    private String label = DEFAULT_LABEL;
    private int lineType = DEFAULT_LINETYPE;
    private int period = DEFAULT_PERIOD;
    private double multiplier = DEFAULT_MULTIPLIER;
       
    public Weight()
    {
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.charts.IndicatorPlugin#calculate()
     */
    public void calculate()
    {
    	List<Bar> stockHistory = getBarData().getBars();
    	
        PlotLine sd = new PlotLine();
        sd.setColor(color);
        sd.setType(lineType);
        sd.setLabel(label);

        Bar[] bars = new Bar[period];
        double weight = 0.0;
        for (int loop = period; loop < stockHistory.size(); loop++)       
        {

            for (int loop2 = 0; loop2 < period; loop2++)
            {
            	bars[loop2] = (Bar) stockHistory.get( loop - loop2);            	
			}
			
            //bars[0] = today, bars[1] = yesterday
            weight = calculateWeight(bars[1], bars[0]);
            weight *= multiplier;

            sd.append(weight);
        }

        getOutput().add(sd);
        getOutput().setScaleFlag(true);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.charts.IndicatorPlugin#setParameters(net.sourceforge.eclipsetrader.charts.Settings)
     */
    public void setParameters(Settings settings)
    {
        color = settings.getColor("color", color); //$NON-NLS-1$
        label = settings.getString("label", label); //$NON-NLS-1$
        lineType = settings.getInteger("lineType", lineType).intValue(); //$NON-NLS-1$
        multiplier = settings.getDouble("Multiplier", multiplier);
        //period = settings.getInteger("period", period).intValue(); //$NON-NLS-1$
       // input = settings.getInteger("input", input).intValue(); //$NON-NLS-1$
    }

    private double calculateWeight(Bar nMinusOne, Bar n)
    {
    	// ( today(high - low) - yesterday(high-low) )^2 / closeToday * closeYesterday
    	return Math.pow( (n.getHigh() - n.getLow()) - (nMinusOne.getHigh() - nMinusOne.getLow()), 2) / (n.getClose() * nMinusOne.getClose() );
	}    
}