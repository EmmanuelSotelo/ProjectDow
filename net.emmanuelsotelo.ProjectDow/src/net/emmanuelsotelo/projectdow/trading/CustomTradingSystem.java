package net.emmanuelsotelo.projectdow.trading;

import java.util.Map;

import bsh.EvalError;
import bsh.Interpreter;

import net.sourceforge.eclipsetrader.core.Sentiment;
import net.sourceforge.eclipsetrader.core.db.Bar;
import net.sourceforge.eclipsetrader.core.db.trading.TradingSystem;
import net.sourceforge.eclipsetrader.trading.TradingSystemPlugin;

public class CustomTradingSystem extends TradingSystemPlugin{

	private String customCode;
	
	public CustomTradingSystem(){
		
		
	}
	
	@Override
	public void run() {
		
		setSignal(TradingSystem.SIGNAL_NONE);
		Bar[] bars = new Bar[getSecurity().getHistory().getList().size()];
		
		
		for (int i = 0; i < bars.length; i++)
		{
			bars[i] = (Bar) getSecurity().getHistory().getList().get(i);
			
//			if(bars.length > 0)
//				System.out.println( bars[i].getDate() + " " + i );
		}
				
		if(getSecurity().getHistory().getList().size()  == 0){

			setChanged();
			notifyObservers();

		}else{
			
			
			Sentiment sentiment = runCustomCode(bars);
			
			if ( sentiment == Sentiment.BULLISH  ){				
				fireOpenLongSignal();
			}
			else if(sentiment == Sentiment.BEARISH)
			{				
				fireCloseLongSignal();
			}else
			{
//				setChanged();
//				notifyObservers();
			}
			setChanged();
			notifyObservers();
			
		}		
	}
	
    public void setParameters(Map parameters)
    {
    	
    	if(parameters.get("customCode") != null){
    		
    		customCode = (String) parameters.get("customCode");
    	}
    }
    
    private Sentiment runCustomCode(Bar[] bars)
    {
    	Sentiment sentiment = Sentiment.NEUTRAL;
		Interpreter interpreter = new Interpreter();
		
		// 1 = Bullish, -1 = Bearish
		int signal= 0;
		
		
		try {
			interpreter.set("bars", bars);
			interpreter.set("signal", signal);
					
			
			if(customCode.length() > 0 && customCode != null)
				interpreter.eval(customCode);
			
			signal = (Integer) interpreter.get("signal");
			
			
		} catch (EvalError e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(signal == 1){
			
			sentiment = Sentiment.BULLISH;
			
		} else if(signal == -1){
			
			sentiment = Sentiment.BEARISH;
		}
		
		return sentiment;
    }    

}
