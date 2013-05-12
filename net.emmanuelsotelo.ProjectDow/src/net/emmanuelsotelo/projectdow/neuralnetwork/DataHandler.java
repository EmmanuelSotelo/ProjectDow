package net.emmanuelsotelo.projectdow.neuralnetwork;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.eclipsetrader.core.db.BarData;
import net.sourceforge.eclipsetrader.core.db.Security;

public class DataHandler implements Serializable, Cloneable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final int inputsCount = 5;
	
	// ROC = Rate of Change	
	private final Set<Data> volumeROC = new TreeSet<Data>();
	private final Set<Data> closePriceROC = new TreeSet<Data>();
	private final Set<Data> hiOnOpen = new TreeSet<Data>();
	private final Set<Data> lowOnOpen = new TreeSet<Data>();
	private final Set<Data> openPrevCloseDelta = new TreeSet<Data>();
	
	private final int temporalWindowSize;
	private final int outputSize;

	public DataHandler(final int inputSize, final int outputSize) {
		this.temporalWindowSize = inputSize;
		this.outputSize = outputSize;
	}

	public void getInputData(final int offset, final double[] input) {
		
		final Object[] priceROCArray = this.closePriceROC.toArray();
		final Object[] volumeROCArray = this.volumeROC.toArray();
		final Object[] hiOnOpenArray = this.hiOnOpen.toArray();
		final Object[] lowOnOpenArray = this.lowOnOpen.toArray();
		final Object[] openPrevCloseDelta = this.openPrevCloseDelta.toArray();
				
		int nextDay = 0;
		for (int i = 0; i < this.temporalWindowSize*inputsCount; i += inputsCount) { // fixed a BUG
			nextDay = (i/inputsCount) - 1; // Part of bugfix (subtracting 1 because zero based index)// we should realy be subtracting the 1 from the variable offset though
			
			final Data priceROCSample = (Data) priceROCArray[offset + nextDay ]; // part of bugfix
			final Data volumeROCSample = (Data) volumeROCArray[offset + nextDay ];
			final Data hiOnOpenSample = (Data) hiOnOpenArray[offset + nextDay ];
			final Data lowOnOpenSample = (Data) lowOnOpenArray[offset + nextDay ];
			final Data openPrevCloseDeltaSample = (Data) openPrevCloseDelta[offset + nextDay ];
						
			input[i] = priceROCSample.getData();
			input[i + 1] = volumeROCSample.getData();
			input[i + 2] = hiOnOpenSample.getData();
			input[i + 3] = lowOnOpenSample.getData();
			input[i + 4] = openPrevCloseDeltaSample.getData();
			// Max = inputsCount-1
			
		}
		
	}

	public void getOutputData(final int offset, final double[] output) {
		final Object[] samplesArray = this.closePriceROC.toArray();
		for (int i = 0; i < this.outputSize; i++) {
			// Remember! Zero (0) based index. This is why we subtract 1 from the temporalWindow size
			final Data sample = (Data) samplesArray[offset + (this.temporalWindowSize-1) + i];
			output[i] = sample.getData();
		}

	}

	// Needs revision
	/**
	 * @return the samples
	 */
	public Set<Data> getSamples() {
		return this.closePriceROC;
	}
	
	public void load(final Security security){
			
		BarData stockData = new BarData( security.getHistory().getList() );
	
		loadClosePriceROC(stockData);
		loadVolumeROC(stockData);
		loadHiOnOpen(stockData);
		loadLowOnOpen(stockData);
		loadOpenPrevCloseDelta(stockData);
		
	}

	private void loadHiOnOpen(final BarData stockData){
		
		for(int i = 1; i < stockData.size(); i++) {
			final Date date = stockData.getDate(i);
			final double calc = (stockData.getHigh(i) - stockData.getOpen(i)) / stockData.getOpen(i);
			
			final Data data = new Data(date, calc);
			this.hiOnOpen.add(data);
		}		
	}
	
private void loadLowOnOpen(final BarData stockData){
		
		for(int i = 1; i < stockData.size(); i++) {
			final Date date = stockData.getDate(i);
			final double calc = (stockData.getLow(i) - stockData.getOpen(i)) / stockData.getOpen(i);
			
			final Data data = new Data(date, calc);
			this.lowOnOpen.add(data);
		}		
	}
	
	private void loadVolumeROC(final BarData stockData){

		for(int i = 1; i < stockData.size(); i++) {
			final Date date = stockData.getDate(i);
			
			final double yesterday = Math.log(stockData.getVolume(i-1));
			final double today  = Math.log(stockData.getVolume(i));			
			final double percent = (today - yesterday)/ yesterday;
			
			final Data vol = new Data(date, percent);
			this.volumeROC.add(vol);
		}
	}
		
	private void loadClosePriceROC(final BarData stockData){
		
		for(int i = 1; i < stockData.size(); i++){
			
			final Date date = stockData.getDate(i);
			
			final double yesterday = stockData.getClose(i-1);
			final double today = stockData.getClose(i);
			final double percent = (today - yesterday)/ yesterday;
		
			
			final Data sample = new Data(date, percent);
			this.closePriceROC.add(sample);
		}
	}
	
	/**
	 * Calculates the percentage difference in between Close[T-1] and the Open price
	 * @param stockData
	 */
	private void loadOpenPrevCloseDelta(final BarData stockData){
		
		for(int i = 1; i < stockData.size(); i++){
			
			final Date date = stockData.getDate(i);
			
			final double previousClose = stockData.getClose(i-1);
			final double open = stockData.getOpen(i);
			final double delta = (open - previousClose)/open;
			
			final Data sample = new Data(date, delta);			
			this.openPrevCloseDelta.add(sample);
		}		
		
	}

	public int size() {
		return this.closePriceROC.size();
	}	
}