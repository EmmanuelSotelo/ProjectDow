package net.emmanuelsotelo.projectdow.neuralnetwork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.encog.neural.activation.ActivationFunction;
import org.encog.neural.activation.ActivationTANH;
import org.encog.neural.data.NeuralData;
import org.encog.neural.data.NeuralDataSet;
import org.encog.neural.data.basic.BasicNeuralData;
import org.encog.neural.data.basic.BasicNeuralDataSet;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.multi.MultiPropagation;
import org.encog.util.csv.ReadCSV;
import org.encog.util.logging.Logging;

import net.sourceforge.eclipsetrader.core.db.BarData;
import net.sourceforge.eclipsetrader.core.db.Security;

public class PredictionNetwork implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int TRAINING_SIZE = 200;
	private int TEMPORALWINDOW_SIZE = 10; // Size of sliding (temporal) window
	private int INPUTS_COUNT = 5; // The number of inputs(features)
	private int OUTPUT_SIZE = 3;
	private int NEURONS_HIDDEN_1 = 15;
	private int NEURONS_HIDDEN_2 = 5;	
	private double MAX_ERROR = 0.02;
	private Date PREDICT_FROM;
	private Date LEARN_FROM;

	private double input[][];
	private double ideal[][];
	private BasicNetwork network;
	private DataHandler actual;
	private String securityCode;
	private transient Security security;
	
	private double rmse = 100;
	private double globalDirectionalAccuracy = 0;	
	private boolean isTrained = false;
	
	
	
	private transient IProgressMonitor monitor; // Used for the GUI
	
	public void trainNetwork() {
		this.actual = new DataHandler(TEMPORALWINDOW_SIZE, OUTPUT_SIZE);
		this.actual.load(security);
		
		createNetwork();
		generateTrainingSets();
		trainNetworkBackprop();
	}
	
	public void continueTrainingNetwork(){
		
		// if is trained
		trainNetworkBackprop();
	
	}
	
	public void createNetwork() {
		final ActivationFunction threshold = new ActivationTANH();
		Logging.stopConsoleLogging();
		this.network = new BasicNetwork();
		this.network.addLayer(new BasicLayer(threshold, false,TEMPORALWINDOW_SIZE * INPUTS_COUNT));
		this.network.addLayer(new BasicLayer(threshold, false,NEURONS_HIDDEN_1));
		
		if (NEURONS_HIDDEN_2 > 0) {
			this.network.addLayer(new BasicLayer(threshold, false,NEURONS_HIDDEN_2));
		}
		this.network.addLayer(new BasicLayer(threshold, false, OUTPUT_SIZE));
		this.network.getStructure().finalizeStructure();
		this.network.reset();
	}	

	private void generateTrainingSets() {
		this.input = new double[TRAINING_SIZE][TEMPORALWINDOW_SIZE * INPUTS_COUNT];
		this.ideal = new double[TRAINING_SIZE][OUTPUT_SIZE];
		
		// find where we are starting from
		int startIndex = 0;
		for (final Data sample : this.actual.getSamples()) {
			if (sample.getDate().after(LEARN_FROM)) {
				break;
			}
			startIndex++;
		}

		final int eligibleSamples = TRAINING_SIZE - startIndex;
		if (eligibleSamples == 0) {
			System.out
					.println("Need an earlier date for LEARN_FROM or a smaller number for TRAINING_SIZE.");
			System.exit(0);
		}

		// grab the actual training data from that point
		for (int i = 0; i < TRAINING_SIZE; i++) {
			this.actual.getInputData(startIndex + (i), this.input[i]);
			this.actual.getOutputData(startIndex + (i), this.ideal[i]);
		}
	}	

	private void trainNetworkBackprop() {
		
		isTrained = false;

		final NeuralDataSet trainingData = new BasicNeuralDataSet(this.input, this.ideal);
		final MultiPropagation train = new MultiPropagation (this.network, trainingData);

		int epoch = 1;
		int lastAnneal = 0;

		do {
			train.iteration();
			double error = train.getError();
			
			rmse = error;
			
			monitor.worked(  (int) (50/(error/MAX_ERROR))  );			
			if(monitor.isCanceled()){
				
				isTrained = true;
				train.finishTraining();
				break;
			}
			

			
			epoch++;
			lastAnneal++;
		} while (train.getError() > MAX_ERROR);
		
		rmse = train.getError();
		isTrained = true;
		train.finishTraining();
	}
	

		
	public boolean isTrained(){
		
		return isTrained;
	}
	

	
	public String newNetworkResults(){
		
		ArrayList<Object[]> predictionDates = getPredictionDates();
		double[][] predictions= queryNetwork(predictionDates);
		double[][] actualResults = getActualResults(predictionDates);
		globalDirectionalAccuracy = computeDirectionalAccuracy(predictions, actualResults);
		
		return getNetworkResults(predictionDates, predictions, actualResults);
	}
	// 1st Part: Collect the data
	public ArrayList<Object[]> getPredictionDates(){
		
		ArrayList<Object[]>  predictionDates = new ArrayList<Object[]>();
				
		DataHandler currentData = new DataHandler(TEMPORALWINDOW_SIZE, OUTPUT_SIZE);	
		currentData.load(security);
		final Set<Data> samples  = currentData.getSamples();
		
		// Add extra date to be predicted		
		BarData stockData = new BarData( security.getHistory().getList() );
		Date predictedDate = stockData.getEnd();
		
		Calendar cal = Calendar.getInstance();
		
		cal.setTime(predictedDate);
		cal.add(Calendar.DATE, 1);

		// Check for Saturday and Sunday. Shift date to Monday if prediction date falls on either.
		if( (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||  (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY))  ){
			
			cal.add(Calendar.DATE, 1);
			if( (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY))  ){
				cal.add(Calendar.DATE, 1);
			}
		}		
		predictedDate = cal.getTime();
				
		final Data futureDate = new Data();
		futureDate.setData(0);
		futureDate.setDate(predictedDate);
		samples.add(futureDate);
		////
	
		
		int index = 0;
		for (final Data sample : samples) {
			
			if ( sample.getDate().after(PREDICT_FROM)) {
							
				Object[] dataObject = new Object[2];
				dataObject[0] = sample.getDate();
				dataObject[1] = index;
				
				predictionDates.add(dataObject);				
			}
			
			index++;
		}		
		
		return predictionDates;
		
	}
	
	
	// 2nd part: Query the Neural Network (get predicted data)
	public double[][] queryNetwork(ArrayList<Object[]> predictionDates){
		
		double[][] predictions = new double[predictionDates.size()][OUTPUT_SIZE];
		
		
		final double[] present = new double[TEMPORALWINDOW_SIZE * INPUTS_COUNT];		
		DataHandler currentData = new DataHandler(TEMPORALWINDOW_SIZE, OUTPUT_SIZE);	
		currentData.load(security);		

		int index = 0;
		for(int i = 0; i < predictionDates.size(); i++){
			
			final Object[] sample = predictionDates.get(i);
			index = (Integer)sample[1];
			
			currentData.getInputData( index - (TEMPORALWINDOW_SIZE-1), present);
			
			final NeuralData input = new BasicNeuralData(present);
			final NeuralData predict = this.network.compute(input); // Query the Neural Network
			
			double[] prediction = predict.getData();			
			predictions[i] = prediction;			
		}		
		
		return predictions;		
	}
	
	// 4th part:  Get actual day results
	public double[][] getActualResults(ArrayList<Object[]> predictionDates){
		
		
		int adjustedArraySize = predictionDates.size() - OUTPUT_SIZE;
		double[][] actualResults;
		if(adjustedArraySize >= 0){
			actualResults = new double[adjustedArraySize][OUTPUT_SIZE];
		}
		else{
			actualResults = new double[0][OUTPUT_SIZE];
		}
		
		DataHandler currentData = new DataHandler(TEMPORALWINDOW_SIZE, OUTPUT_SIZE);	
		currentData.load(security);

		int index = 0;
		for(int i = 0; i < adjustedArraySize; i++){
			
			final Object[] sample = predictionDates.get(i);
			index = (Integer)sample[1];
			
			double[] actualOutput = new double[OUTPUT_SIZE];
			currentData.getOutputData(index - (TEMPORALWINDOW_SIZE-1), actualOutput);
			
			actualResults[i] = actualOutput;			
		}				
		
		return actualResults;
	}
	
	// 5th part: Prepare output. Combine predicted and actual data
	public String getNetworkResults(ArrayList<Object[]> predictionDates, double[][] predictions, double[][] actualResults){
		
		StringBuilder results = new StringBuilder();
		
		for(int i = 0; i < actualResults.length; i++){
			
			final Object[] dateData = predictionDates.get(i);
			Date sample  = (Date)dateData[0];
			results.append( ReadCSV.displayDate( sample ) ); //Date
			
			//Predictions
			for(int j = 0; j < predictions[i].length; j++){
				
				results.append(", " +predictions[i][j]);
			}
			
			// Actual Results
			for(int j = 0; j < predictions[i].length; j++){
				
				results.append(", " +actualResults[i][j]);
			}			
			
			results.append("\r\n" );
		}
		
		//Future Predictions
		for(int i =  actualResults.length; i < predictions.length; i++){
			
			final Object[] dateData = predictionDates.get(i);
			Date sample  = (Date)dateData[0];
			results.append( ReadCSV.displayDate( sample ) ); //Date
			
			for(int j = 0; j < predictions[i].length; j++){
				
				results.append(", " +predictions[i][j]);
			}
			
			results.append("\r\n" );
		}
		
		return results.toString();
	}
	
	// 6th part: compute directional Accuracy
	public double computeDirectionalAccuracy(double[][] predictions, double[][] actualResults){
		
		
		double[] correct = new double[OUTPUT_SIZE];
		double total = actualResults.length * OUTPUT_SIZE;
		
		for(int i = 0; i < actualResults.length; i++){
			
			for(int j = 0; j < actualResults[i].length; j++){
				
				if(predictions[i][j] > 0){
					if(actualResults[i][j] > 0){
						correct[j]++;
					}
				} else if(predictions[i][j] < 0){
					if(actualResults[i][j] < 0){
						correct[j]++;
					}
				}
			}
		}
		

		
		
		
		System.out.println((correct[0]/actualResults.length)+ "  "+(correct[1]/actualResults.length) + "   " +(correct[2]/actualResults.length));
		
		double totalCorrect = 0;
		for(int i = 0; i < correct.length; i++){
			totalCorrect += correct[i];
		}
		
		return (totalCorrect/total);		
	}
	
	public double globalDirectionalAccuracy(){		
		
		return  globalDirectionalAccuracy;
	}
	
	
	public int getHiddenLayerOneNeurons(){
		
		return NEURONS_HIDDEN_1;
	}
	
	public void setHiddenLayerOneNeurons(int neurons){
		
		this.NEURONS_HIDDEN_1 = neurons;
	}
	
	public int getHiddenLayerTwoNeurons(){
		
		return NEURONS_HIDDEN_2;
	}
	
	public void setHiddenLayerTwoNeurons(int neurons){
		
		this.NEURONS_HIDDEN_2 = neurons;
	}
	
	public String getLearnFromDate(){
		return ReadCSV.displayDate(LEARN_FROM);
	}
	
	public void setLearnFromDate(String date){
		
		this.LEARN_FROM = ReadCSV.parseDate(date);
	}
	
	public double getMaxRMSE(){
		
		return MAX_ERROR;
	}
	
	public void setMaxRMSE(double rmse){
		
		this.MAX_ERROR = rmse;
	}
	
	public String getPredictFromDate(){
		return ReadCSV.displayDate(PREDICT_FROM);
	}
	
	public void setPredictFromDate(String date){
		
		this.PREDICT_FROM = ReadCSV.parseDate(date);
	}
	
	public double getRMSE(){
		
		return rmse;
	}
	
	public String getSecurityCode(){
		
		return securityCode;
	}
	
	public void setSecurity(Security security){
		
		this.security = security;
		this.securityCode = security.getCode();
	}
	
	public int getTemporalWindow(){
		
		return TEMPORALWINDOW_SIZE;
	}
	
	public void setTemporalWindow(int temporalWindow){
		
		this.TEMPORALWINDOW_SIZE = temporalWindow;
	}
	
	public int getTrainingSize(){
		
		return TRAINING_SIZE;
	}
	
	public void setTrainingSize(int trainingSize){
		this.TRAINING_SIZE = trainingSize;
	}
	
	public void setMonitor(IProgressMonitor monitor){
		this.monitor = monitor;
	}
}
