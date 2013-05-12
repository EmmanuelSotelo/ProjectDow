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

public class ThreeDayUpTrend extends IndicatorPlugin {

	public static final RGB DEFAULT_COLOR = new RGB(0, 200, 0);
    public static final String DEFAULT_LABEL = "3 Day Up Trend"; //$NON-NLS-1$
    public static final int DEFAULT_LINETYPE = PlotLine.HISTOGRAM_BAR;
    public static final int DEFAULT_PERIOD = 3;
    public static final int DEFAULT_INPUT = BarData.CLOSE;

    
    private Color color = new Color(null, DEFAULT_COLOR);
    private String label = DEFAULT_LABEL;
    private int lineType = DEFAULT_LINETYPE;
    private int period = DEFAULT_PERIOD;
	
	
	public ThreeDayUpTrend(){
		
	}
	
	@Override
	public void calculate() {
		
		List<Bar> stockHistory = getBarData().getBars();
    	
        PlotLine sd = new PlotLine();
        sd.setColor(color);
        sd.setType(lineType);
        sd.setLabel(label);

        Bar[] bars = new Bar[period];
        double data = 0.0; 

        int barIndex = 0;
        for (int loop = period; loop < stockHistory.size()+1; loop++)
   		{
        	for (int loop2 = 0; loop2 < period; loop2++)
   			{
   			 	barIndex = ( (loop2 - loop) +  2*loop ) - period;
//   			 	System.out.println(barIndex + " " + loop2);
        		bars[loop2] = (Bar) stockHistory.get( barIndex );          		
   			}

        	data = compute(bars);
        	
//        	System.out.println(bars[0].getDate());
//        	System.out.println(bars[1].getDate());
//        	System.out.println(bars[2].getDate());

           
        	sd.append(data);
        }

        getOutput().add(sd);
        getOutput().setScaleFlag(true);
		
	}
	
	public void setParameters(Settings settings)
    {
        color = settings.getColor("color", color); //$NON-NLS-1$
        label = settings.getString("label", label); //$NON-NLS-1$
        lineType = settings.getInteger("lineType", lineType).intValue(); //$NON-NLS-1$
    }
	
	private double compute(Bar[] bars)
	{
		
		if(  (bars[2].getHigh() > bars[1].getHigh()) && (bars[2].getLow() > bars[1].getLow()) ){
			
			if(  (bars[1].getHigh() > bars[0].getHigh()) && (bars[1].getLow() > bars[0].getLow()) )
			{
				return 1;
			}
		}
		
		return 0;
	}

}
