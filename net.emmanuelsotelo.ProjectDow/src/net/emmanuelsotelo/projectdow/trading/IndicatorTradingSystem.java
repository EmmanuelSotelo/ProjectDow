package net.emmanuelsotelo.projectdow.trading;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;

import net.emmanuelsotelo.projectdow.indicators.data.ChartData;
import net.sourceforge.eclipsetrader.core.CorePlugin;
import net.sourceforge.eclipsetrader.core.Sentiment;
import net.sourceforge.eclipsetrader.core.db.Bar;
import net.sourceforge.eclipsetrader.core.db.Chart;
import net.sourceforge.eclipsetrader.core.db.trading.TradingSystem;
import net.sourceforge.eclipsetrader.trading.TradingSystemPlugin;

public class IndicatorTradingSystem extends TradingSystemPlugin{

	private int numIndicators = 3;
	
	private int[] indicators = new int[numIndicators];
	private boolean[] enableIndicators = new boolean[numIndicators];
	private int[] buyCondition = new int[numIndicators];
	private double[] buyConditionAttribute = new double[numIndicators];
	private int[] sellCondition = new int[numIndicators];
	private double[] sellConditionAttribute = new double[numIndicators];
	private Sentiment[] indicatorSentiment = new Sentiment[numIndicators];
	
	private int chartIndex;
	private Chart chart = null;
	
	private ChartData indicatorDataExtractor = null;
	private double[][] indicatorData = null;

	public IndicatorTradingSystem()
	{		
		// Initialize Sentiment array. Set initial values to neutral
		for(int i =0 ; i < indicatorSentiment.length; i++){
			indicatorSentiment[i] = Sentiment.NEUTRAL;
		}		
	}	
	
	@Override
	public void run() {		
		
		Sentiment sentiment = Sentiment.NEUTRAL;	
		setSignal(TradingSystem.SIGNAL_NONE);
		
		Bar[] bars = new Bar[getSecurity().getHistory().getList().size()];
		for (int i = 0; i < bars.length; i++){
			bars[i] = (Bar) getSecurity().getHistory().getList().get(i);
		}	
		
		
		if(getSecurity().getHistory().getList().size()  == 0){
			setChanged();
			notifyObservers();
		}else{
			chart = getChart(chartIndex);
			if ( chart != null ){
					
				if( indicatorDataExtractor == null ){
					indicatorDataExtractor = new ChartData(chart);					
					indicatorData = indicatorDataExtractor.getIndicatorData();				
				}			
			
				if(enableIndicators[0]){
					indicatorSentiment[0] =	getSentimentFromIndicator(indicatorData[bars.length-1][indicators[0]], buyCondition[0], buyConditionAttribute[0], sellCondition[0], sellConditionAttribute[0]);
					
				}
				
				if(enableIndicators[1]){
					indicatorSentiment[1] =	getSentimentFromIndicator(indicatorData[bars.length-1][indicators[1]], buyCondition[1], buyConditionAttribute[1], sellCondition[1], sellConditionAttribute[1]);
				}
				
				if(enableIndicators[2]){
					indicatorSentiment[2] =	getSentimentFromIndicator(indicatorData[bars.length-1][indicators[2]], buyCondition[2], buyConditionAttribute[2], sellCondition[2], sellConditionAttribute[2]);
				}
			}	
			
			sentiment = getOverallSentiment(indicatorSentiment, enableIndicators);			
						
			if ( sentiment.equals(Sentiment.BULLISH)  ){				
				
				fireOpenLongSignal();
				System.out.println("BUY: " +bars[bars.length-1].getDate());
			}
			else if(sentiment.equals(Sentiment.BEARISH) )
			{				
				fireCloseLongSignal();
				System.out.println("SELL: " +bars[bars.length-1].getDate());
			}else {
				
				setChanged();
				notifyObservers();
			}

		}		
		
	}
	
	public void setParameters(Map parameters)
    {    	
		// Get chart selection
		if( parameters.get("chart") != null ){
        	chartIndex = Integer.parseInt((String)parameters.get("chart"));
        }
		
		// Indicator 1
		if( parameters.get("indicator0") != null ){
			
			indicators[0] =  Integer.parseInt( (String)parameters.get("indicator0") );
        }
		
		if( parameters.get("enable0") != null ){
        	enableIndicators[0] = Boolean.parseBoolean( (String)parameters.get("enable0") );
        }
		
		if( parameters.get("buyCondition0") != null ){
        	buyCondition[0] = Integer.parseInt((String)parameters.get("buyCondition0") );
        }
		
		if( parameters.get("buyConditionAttribute0") != null ){
        	buyConditionAttribute[0] = Double.parseDouble((String)parameters.get("buyConditionAttribute0") );
        }
		
		if( parameters.get("sellCondition0") != null ){
        	sellCondition[0] = Integer.parseInt((String)parameters.get("sellCondition0") );
        }
		
		if( parameters.get("sellConditionAttribute0") != null ){
        	sellConditionAttribute[0] = Double.parseDouble( (String) parameters.get("sellConditionAttribute0") );
        }
		
		// Indicator 2
		if( parameters.get("indicator1") != null ){
        	indicators[1] =  Integer.parseInt( (String)parameters.get("indicator1") );
        }
		
		if( parameters.get("enable1") != null ){
        	enableIndicators[1] = Boolean.parseBoolean( (String)parameters.get("enable1") );
        }
		
		if( parameters.get("buyCondition1") != null ){
        	buyCondition[1] = Integer.parseInt((String)parameters.get("buyCondition1") );
        }
		
		if( parameters.get("buyConditionAttribute1") != null ){
        	buyConditionAttribute[1] = Double.parseDouble( (String) parameters.get("buyConditionAttribute1") );
        }
		
		if( parameters.get("sellCondition1") != null ){
        	sellCondition[1] = Integer.parseInt((String)parameters.get("sellCondition1") );
        }
		
		if( parameters.get("sellConditionAttribute1") != null ){
        	sellConditionAttribute[1] = Double.parseDouble( (String) parameters.get("sellConditionAttribute1") );
        }
		
		
		// Indicator 3
		if( parameters.get("indicator2") != null ){
        	indicators[2] =  Integer.parseInt( (String)parameters.get("indicator2") );
        }
		
		if( parameters.get("enable2") != null ){
        	enableIndicators[2] = Boolean.parseBoolean( (String)parameters.get("enable2") );
        }
		
		if( parameters.get("buyCondition2") != null ){
        	buyCondition[2] = Integer.parseInt((String)parameters.get("buyCondition2") );
        }
		
		if( parameters.get("buyConditionAttribute2") != null ){
        	buyConditionAttribute[2] = Double.parseDouble( (String) parameters.get("buyConditionAttribute2") );
        }
		
		if( parameters.get("sellCondition2") != null ){
        	sellCondition[2] = Integer.parseInt((String)parameters.get("sellCondition2") );
        }
		
		if( parameters.get("sellConditionAttribute2") != null ){
        	sellConditionAttribute[2] = Double.parseDouble( (String) parameters.get("sellConditionAttribute2") );
        }
    }
	
	@SuppressWarnings("unchecked")
	private Chart getChart(int chartIndex){
		
		java.util.List<Chart> charts = CorePlugin.getRepository().allCharts(  CorePlugin.getRepository().getSecurity(getSecurity().getCode()) );

        // Sort the charts by name.
		Collections.sort(charts, new Comparator() {
            public int compare(Object arg0, Object arg1)
            {
            	 return ((Chart)arg0).getTitle().compareTo(((Chart)arg1).getTitle());
            }
        });  

		if(charts.size() != 0)
			return charts.get(chartIndex);
		else
			return null;
	}
	
	private Sentiment getSentimentFromIndicator(double indicatorValue, int buyCondition, double buyConditionAttribute, int sellCondition, double sellConditionAttribute){

//		System.out.println("iv:"+indicatorValue + " bc:" + buyCondition + " bca:" + buyConditionAttribute + " sc" + sellCondition + " sca" + sellConditionAttribute);

		
		boolean buySignal = false;
		boolean sellSignal = false;
		
		switch(buyCondition){
		
			case 0:				// =
				if(indicatorValue == buyConditionAttribute){
					buySignal = true;
				}				
				break;
			
			case 1:				// !=
				if(indicatorValue != buyConditionAttribute){
					buySignal = true;
				}
				break;
			
			case 2:			//  >
				if(indicatorValue > buyConditionAttribute){
					buySignal = true;
				}
				break;
				
			case 3: 		// >=
				if(indicatorValue >= buyConditionAttribute){
					buySignal = true;
				}
				break;
				
			case 4:	 		// <
				if(indicatorValue < buyConditionAttribute){
					buySignal = true;
				}
				break;
			
			case 5: 		// <=
				if(indicatorValue <= buyConditionAttribute){
					buySignal = true;
				}
				break;				
			
		
			default:
				break;
		}
		
		switch(sellCondition){
		
		
			case 0:			// =
				if(indicatorValue == sellConditionAttribute){
					sellSignal = true;
				}				
				break;
				
			case 1:			// !=
				if(indicatorValue != sellConditionAttribute){
					sellSignal = true;
				}	
				break;
			
			case 2:			// >
				if(indicatorValue > sellConditionAttribute){
					sellSignal = true;
				}	
				break;
				
			case 3:			// >=
				if(indicatorValue >= sellConditionAttribute){
					sellSignal = true;
				}	
				break;
				
			case 4:			// <
				if(indicatorValue < sellConditionAttribute){
					sellSignal = true;
				}	
				break;
			
			case 5:			// <
				if(indicatorValue <= sellConditionAttribute){
					sellSignal = true;
				}	
				break;
			
		
			default:
				break;
		
		}

		if(buySignal && !sellSignal){
//			System.out.println("BULL  iv: "+indicatorValue + " bc: " + buyCondition + " bca: " + buyConditionAttribute + " sc: " + sellCondition + " sca: " + sellConditionAttribute);

			return Sentiment.BULLISH;
			
		}else if(!buySignal && sellSignal){	
//			System.out.println("BEAR  iv: "+indicatorValue + " bc: " + buyCondition + " bca: " + buyConditionAttribute + " sc: " + sellCondition + " sca: " + sellConditionAttribute);

			return Sentiment.BEARISH;
		}
		else
		{
			return Sentiment.NEUTRAL;
		}
		
	}
	
	private Sentiment getOverallSentiment(Sentiment[] indicatorSentiment, boolean[] enableIndicators){

		int sentimentDirection[]; 
		
		ArrayList<Sentiment> enabledIndicators = new ArrayList<Sentiment>();
		
		for(int i = 0; i < enableIndicators.length; i++){
			
			if(enableIndicators[i] == true){
				
				enabledIndicators.add(indicatorSentiment[i]);
			}
		}
		
		sentimentDirection = new int[enabledIndicators.size()];
		for(int i = 0; i < enabledIndicators.size(); i++){
			
			if(enabledIndicators.get(i).equals(Sentiment.BULLISH)){
				
				sentimentDirection[i] = 1;
				
			}else if(enabledIndicators.get(i).equals(Sentiment.BEARISH)) {
				
				sentimentDirection[i] = -1;
				
			} else{
				
				sentimentDirection[i] = 0;
			}
		}	

		
		// Sums up the sentiments
		int sentimentSum = 0;
		for(int i = 0; i < sentimentDirection.length; i++){
			
			sentimentSum += sentimentDirection[i];
			
		}

		
		Sentiment overallSentiment;		
		if( sentimentSum == sentimentDirection.length ){
			
			overallSentiment = Sentiment.BULLISH;
			
		}else if( sentimentSum == (-1 * sentimentDirection.length) ){
			
			overallSentiment = Sentiment.BEARISH;
			
		}else {
			overallSentiment = Sentiment.NEUTRAL;			
		}
		
		return overallSentiment;
	}
	 
}
