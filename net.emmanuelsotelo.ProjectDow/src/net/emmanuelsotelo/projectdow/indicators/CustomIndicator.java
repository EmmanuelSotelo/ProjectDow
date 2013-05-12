package net.emmanuelsotelo.projectdow.indicators;

import java.util.List;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;

import bsh.EvalError;
import bsh.Interpreter;

import net.sourceforge.eclipsetrader.charts.IndicatorPlugin;
import net.sourceforge.eclipsetrader.charts.PlotLine;
import net.sourceforge.eclipsetrader.charts.Settings;
import net.sourceforge.eclipsetrader.core.db.Bar;
import net.sourceforge.eclipsetrader.core.db.BarData;

public class CustomIndicator extends IndicatorPlugin {

	public static final RGB DEFAULT_COLOR = new RGB(200, 150, 0);
    public static final String DEFAULT_LABEL = "Custom Indicator"; //$NON-NLS-1$
    public static final int DEFAULT_LINETYPE = PlotLine.LINE;
    public static final int DEFAULT_PERIOD = 1;
    public static final int DEFAULT_INPUT = BarData.CLOSE;
    public static final String DEFAULT_CUSTOMCODE = "//returnValue = 0;";

    
    private Color color = new Color(null, DEFAULT_COLOR);
    private String label = DEFAULT_LABEL;
    private int lineType = DEFAULT_LINETYPE;
    private int period = DEFAULT_PERIOD;
    private String customCode = DEFAULT_CUSTOMCODE;
	
	public CustomIndicator() {
		// TODO Auto-generated constructor stub
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
        		bars[loop2] = (Bar) stockHistory.get( barIndex );          		
   			}        	
        	
        	data = compute(bars);        	        	     	
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
        period = settings.getInteger("period", period);
        customCode = settings.getString("customCode", customCode);
    }
	
	public double compute(Bar[] bars)
	{ 		
		double returnValue = 0;
		Interpreter interpreter = new Interpreter();
		
		
		try {
			interpreter.set("bars", bars);
			interpreter.set("returnValue", returnValue);
					
			
			if(customCode.length() > 0 && customCode != null)
				interpreter.eval(customCode);
			
			Object retVal = interpreter.get("returnValue");
			
			if(retVal instanceof Double){
				returnValue = (Double)retVal;
			}
			else if (retVal instanceof Integer){
				returnValue = (Integer)retVal;
			}			
			
			
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return returnValue;		
	
	}
}
