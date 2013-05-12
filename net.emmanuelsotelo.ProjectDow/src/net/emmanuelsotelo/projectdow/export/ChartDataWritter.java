package net.emmanuelsotelo.projectdow.export;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import net.emmanuelsotelo.projectdow.indicators.data.ChartData;

import net.sourceforge.eclipsetrader.core.CorePlugin;
import net.sourceforge.eclipsetrader.core.db.Bar;
import net.sourceforge.eclipsetrader.core.db.BarData;
import net.sourceforge.eclipsetrader.core.db.Chart;
import net.sourceforge.eclipsetrader.core.ui.export.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class ChartDataWritter {

	private String file;
	private Chart chart;
	private ChartData indicatorDataExtractor;
    
    String separator = ","; //$NON-NLS-1$
    String eol = "\r\n"; //$NON-NLS-1$    
	
	public ChartDataWritter(Chart chart, String file){
		
		this.chart = chart;
		this.file = file;
		this.indicatorDataExtractor = new ChartData(this.chart);
		
		
	}
	
	/**
	 *  Writes the data from the Chart object to a CSV format text file
	 */
	public void writeData()
	{
		final SimpleDateFormat dateFormat = CorePlugin.getDateFormat();
         
        final NumberFormat priceFormat = NumberFormat.getInstance(Locale.US);
        priceFormat.setMinimumIntegerDigits(1);
        priceFormat.setMinimumFractionDigits(4);
        priceFormat.setMaximumFractionDigits(4);
        priceFormat.setGroupingUsed(false);
        
        final NumberFormat numberFomatIndicators = NumberFormat.getInstance();
        numberFomatIndicators.setGroupingUsed(false);
        numberFomatIndicators.setMinimumIntegerDigits(1);
        numberFomatIndicators.setMinimumFractionDigits(6);
        numberFomatIndicators.setMaximumFractionDigits(6);
                        
        List<Bar> barList = chart.getSecurity().getHistory().getList();
        final BarData barData = new BarData(barList);
        final Bar[] bars = barData.toArray();
		
		Job job = new Job("Export Chart Data") {
	            protected IStatus run(IProgressMonitor monitor)
	            {
	            	try
	                {
	            		
	                    monitor.beginTask("Export Chart Data ", bars.length);
	                    
	                    int columns = indicatorDataExtractor.getTotalIndicators();
	                    String[] columnNames = indicatorDataExtractor.getIndicatorLabels();
	            		double[][] indicatorData = new double[bars.length][columns];
	            		indicatorData = indicatorDataExtractor.getIndicatorData();
	            		
	            		Writer writer = new BufferedWriter(new FileWriter(file));
	            		
	            		writer.write("Date" + separator);
	            		writer.write("Open" + separator);
	            		writer.write("High" + separator);
	            		writer.write("Low" + separator);
	            		writer.write("Close" + separator);
	            		
	            		for(int k = 0; k < columnNames.length; k++)
	            		{
	            			// Don't write separator if its the last column
	            			if(k == (columns-1) ){
	            				writer.write( columnNames[k]);
	            			}
	            			else
	            			{
	            				writer.write( columnNames[k] + separator );
	            			}
	            		}
	            		
	            		writer.write(eol);
	            		
	            		for(int i=0; i < indicatorData.length; i++)
	            		{
	            			writer.write( dateFormat.format(bars[i].getDate()) + separator );
	            			writer.write( priceFormat.format(bars[i].getOpen()) + separator);
	            			writer.write( priceFormat.format(bars[i].getHigh()) + separator);
	            			writer.write( priceFormat.format(bars[i].getLow()) + separator );
	            			writer.write( priceFormat.format(bars[i].getClose()) + separator );
	            			
	            			for(int k = 0; k < columns; k++)
	            			{
	            							
	            				// Don't write separator if its the last column
	            				if(k == (columns-1) ){
	            					writer.write( numberFomatIndicators.format(indicatorData[i][k]) );
	            				}
	            				else
	            				{
	            					writer.write( numberFomatIndicators.format(indicatorData[i][k]) + separator );
	            				}
	            								
	            			}
	            			writer.write(eol);
	            		}
	            		
	            		  writer.flush();
	                      writer.close();                 
                        
	                }	            	
	            	catch (IOException e)
	                {
	                    CorePlugin.logException(e);
	                    return new Status(IStatus.ERROR, Platform.PI_RUNTIME, IStatus.OK, Messages.CSVExport_ErrorMessage, e);
	                }
	            	
	            	monitor.done();	                    
	            	
	            	return Status.OK_STATUS;
	            }	            
		 };
		 
		 job.setUser(true);
	     job.schedule();
	}
}
