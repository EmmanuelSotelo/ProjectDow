package net.emmanuelsotelo.projectdow.indicators.data;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.eclipsetrader.charts.ChartsPlugin;
import net.sourceforge.eclipsetrader.charts.IIndicatorPlugin;
import net.sourceforge.eclipsetrader.charts.Indicator;
import net.sourceforge.eclipsetrader.charts.PlotLine;
import net.sourceforge.eclipsetrader.charts.Settings;
import net.sourceforge.eclipsetrader.core.db.BarData;
import net.sourceforge.eclipsetrader.core.db.Chart;
import net.sourceforge.eclipsetrader.core.db.ChartIndicator;
import net.sourceforge.eclipsetrader.core.db.ChartRow;
import net.sourceforge.eclipsetrader.core.db.ChartTab;

public class ChartData {
	
	private Chart chart;
	private int totalIndicators = -1;
	private String[] indicatorLabels = null;
	private double[][] indicatorData = null;

	public ChartData(Chart chart){
		this.chart = chart;		
	}
	
	/**
	 * Returns an array that contains data from all the indicators
	 * 
	 */
	public double[][] getIndicatorData(){
		
		if(indicatorData == null){
			initData();
		}
		
		return indicatorData;
	}
	
	public String[] getIndicatorLabels(){

		if(indicatorLabels == null){
			initData();
		}
		
		return indicatorLabels;
	}
	
	public int getTotalIndicators(){
		
		if(totalIndicators == -1){
			initData();
		}
		
		return totalIndicators;		
	}
	
	
	private void initData()
	{
		totalIndicators = totalIndicators();
		calcualteIndicatorData();
	}
	
	@SuppressWarnings("unchecked")
	private void calcualteIndicatorData(){
		
		ArrayList rows = chart.getRows();
		int barCount = chart.getSecurity().getHistory().getList().size();
		
		double[][] indicatorData = new double[barCount][totalIndicators];
		String[] indicatorLabels = new String[totalIndicators];
		int currentColumn = 0;
		
		// Bug in system if security is deleted from chart		
		
		// Iterate through Rows
		for (int i = 0; i < rows.size(); i++ )
        {			
			ChartRow row = (ChartRow) rows.get(i);
			ArrayList tabs = row.getTabs();
			
			// Iterate though tabs
			for(int j = 0; j < tabs.size(); j++ )
			{   
				ChartTab chartTab = (ChartTab) tabs.get(j);
				ArrayList indicators = chartTab.getIndicators();
				
				// Iterate through ChartIndicators
				for(int k = 0; k < indicators.size(); k++)
				{
					ChartIndicator chartIndicator = (ChartIndicator) indicators.get(k);
					chartIndicator.clearChanged();
					

					IIndicatorPlugin indicatorPlugin = ChartsPlugin.createIndicatorPlugin(chartIndicator.getPluginId());
					
					List plotLines = new ArrayList();
					chartIndicator.setData(indicatorPlugin);
	                if (indicatorPlugin != null)
	                {
	                    Settings settings = new Settings();
	                    for (Iterator iter = chartIndicator.getParameters().keySet().iterator(); iter.hasNext(); )
	                    {
	                        String key = (String)iter.next();
	                        settings.set(key, (String)chartIndicator.getParameters().get(key));
	                    }
	                    indicatorPlugin.setParameters(settings);
	                    
	                    indicatorPlugin.setInput( new BarData( chart.getSecurity().getHistory().getList() ));
	                    indicatorPlugin.clearOutput();
	                    indicatorPlugin.calculate();

	                    Indicator indicator = indicatorPlugin.getOutput();
	                    plotLines = indicator.getLines();
	                }		

					
					for(int l = 0; l < plotLines.size(); l++)
					{
						PlotLine plotLine = (PlotLine) plotLines.get(l);
						
						// Skips if Indicator is null. This is necessary
						if (plotLine.getLabel() == null || plotLine.getLabel().length() == 0)
						{
							continue;
						}
						else
	                    {        	
							indicatorLabels[currentColumn] = plotLine.getLabel();	                    	  
	                    }
						
						int p =0;
						if(plotLine.getSize() != indicatorData.length)
						{
							p += indicatorData.length - plotLine.getSize() ;
						}
						
						for (Iterator iter = plotLine.iterator(); iter.hasNext(); )
						{
							Object indicatorLine = iter.next();
							if(indicatorLine instanceof Double)
							{
								indicatorData[p][currentColumn] = (Double) indicatorLine;
								p++;
							}
						}
						currentColumn++;
					}
				}
			}
        }
		
		this.indicatorData = indicatorData;
		this.indicatorLabels = indicatorLabels;
		
	}	
	
	
	@SuppressWarnings("unchecked")
	/**
	 * Returns the total number of indicators in chart
	 */	
	private int totalIndicators()
	{
		ArrayList rows = chart.getRows();
		
		int totalIndicators = 0;
		
		// Iterate through Rows
		for (int i = 0; i < rows.size(); i++ )
        {			
			ChartRow row = (ChartRow) rows.get(i);
			ArrayList tabs = row.getTabs();
			
			// Iterate though tabs
			for(int j = 0; j < tabs.size(); j++ )
			{   
				ChartTab chartTab = (ChartTab) tabs.get(j);
				ArrayList indicators = chartTab.getIndicators();
				
				totalIndicators += indicators.size();
			}
        }
		
		if(totalIndicators-1 > 0)
		{		
			return totalIndicators-1;
		}
		else
		{
			return 0;
		}		
	}
	
	public void print(double[][] arr)
	{
		for(int i=0; i < arr.length; i++){
			
			for(int j=0; j < arr[i].length; j++){
				
				System.out.print(arr[i][j] + "   ");
			}
			System.out.println();
		}
	}
	
	public void print(String[] arr){
	
		for(int i=0; i < arr.length; i++){
			System.out.println(arr[i]);
		}
	}

}
