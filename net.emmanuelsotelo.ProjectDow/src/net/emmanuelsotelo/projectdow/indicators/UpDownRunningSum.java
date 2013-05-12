// A test indicator
package net.emmanuelsotelo.projectdow.indicators;

import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import net.sourceforge.eclipsetrader.charts.IndicatorPlugin;
import net.sourceforge.eclipsetrader.charts.PlotLine;
import net.sourceforge.eclipsetrader.charts.Settings;
import net.sourceforge.eclipsetrader.core.db.Bar;
import net.sourceforge.eclipsetrader.core.db.BarData;

public class UpDownRunningSum extends IndicatorPlugin {
	
    public static final RGB DEFAULT_COLOR = new RGB(0, 150, 225);
    public static final String DEFAULT_LABEL = "Up/Dwn RunSum"; //$NON-NLS-1$
    public static final int DEFAULT_LINETYPE = PlotLine.LINE;
    public static final int DEFAULT_PERIOD = 2;
    public static final int DEFAULT_INPUT = BarData.CLOSE;
    private Color color = new Color(null, DEFAULT_COLOR);
    private String label = DEFAULT_LABEL;
    private int lineType = DEFAULT_LINETYPE;
    private int period = DEFAULT_PERIOD;
    private int input = DEFAULT_INPUT;

	public UpDownRunningSum() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void calculate() {
				
    	List stockHistory = getBarData().getBars();
    	
        PlotLine sd = new PlotLine();
        sd.setColor(color);
        sd.setType(lineType);
        sd.setLabel(label);

        Bar[] bars = new Bar[period];
        int upDownRunningSum = 0;
        for (int loop = period; loop < stockHistory.size(); loop++ )       
        {

            for (int loop2 = 0; loop2 < period; loop2++)
            {
            	bars[loop2] = (Bar) stockHistory.get( loop - loop2);
			}        	
			
            //bars[0] = today, bars[1] = yesterday
            upDownRunningSum += determineUpDown(bars[1], bars[0]);

            sd.append(upDownRunningSum);
        }

        getOutput().add(sd);
        getOutput().setScaleFlag(true);
    }		
	
		
	public void setParameters(Settings settings)
    {
        color = settings.getColor("color", color); //$NON-NLS-1$
        label = settings.getString("label", label); //$NON-NLS-1$
        lineType = settings.getInteger("lineType", lineType).intValue(); //$NON-NLS-1$
        //period = settings.getInteger("period", period).intValue(); //$NON-NLS-1$
       // input = settings.getInteger("input", input).intValue(); //$NON-NLS-1$
    }
	
	// This function uses closing prices
	// if Day1 > Day0 then up
	// if Day1 < Day0 then down
	// up returns 1
	// down return -1
	
	private int determineUpDown(Bar nMinusOne, Bar n)
	{
		if(n.getClose() > nMinusOne.getClose())
		{
			return 1;
		}
		else
		{
			return -1;
		}
	}

}
