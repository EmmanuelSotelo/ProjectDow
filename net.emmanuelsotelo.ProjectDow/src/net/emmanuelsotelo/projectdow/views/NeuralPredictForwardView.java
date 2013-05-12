package net.emmanuelsotelo.projectdow.views;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;

import net.emmanuelsotelo.projectdow.neuralnetwork.LoadSaveNetwork;
import net.emmanuelsotelo.projectdow.neuralnetwork.PredictionNetwork;
import net.sourceforge.eclipsetrader.core.CorePlugin;
import net.sourceforge.eclipsetrader.core.db.Security;
import net.sourceforge.eclipsetrader.core.ui.export.Messages;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.ProgressBar;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.encog.util.csv.ReadCSV;

public class NeuralPredictForwardView extends ViewPart {

	private PredictionNetwork network;
	
	private Composite top;
	private Spinner trainingSize;
	private Spinner tWindowSize;
	private Spinner hiddenNeurons1;
	private Spinner hiddenNeurons2;
	private Spinner maxRMSE;
	private Text predictFrom;
	private Text learnFrom;
	private Combo securitySelector;
	private ProgressBar progressBar;
	private Text currentRMSE;
	private Button saveNetwork;
	private Button loadNetwork;
	private Button runBenchmark;
	private Button saveResultsCSV;
	private Text averageDifference;
	private Button trainNetwork;
	private Text networkResults;
	private Text currentFile;
		
	private List<Security> securities;
	private Security selectedSecuity;


	public NeuralPredictForwardView(){
		

		securities = CorePlugin.getRepository().allSecurities();		
		network = new PredictionNetwork();
		
	}
	
	
	@Override
	public void createPartControl(Composite parent) {
		
		
						
		GridData gridData = new GridData();
        gridData.grabExcessVerticalSpace = true;
        gridData.verticalAlignment = org.eclipse.swt.layout.GridData.FILL;
        gridData.horizontalSpan = 2;
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        top = new Composite(parent, SWT.NONE);
        top.setLayout(gridLayout);        
    
        
        Label securityLabel = new Label(top, SWT.NONE);
        securityLabel.setText("Security:");        
        securitySelector = new Combo(top, SWT.READ_ONLY | SWT.BORDER);
        addSecurities(securitySelector);
        securitySelector.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
            public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
                
            	trainNetworkAllow(); // Allow the network to network to be trained once a security is selected
            	// set the user selection
            	selectedSecuity = getSelectedSecurity(securitySelector.getSelectionIndex());
            }
        });
        
        Label TrainingSizeLabel = new Label(top, SWT.NONE);
        TrainingSizeLabel.setText("Training Size:");
       
        trainingSize = new Spinner(top, SWT.BORDER);
        trainingSize.setMaximum(5000);
        trainingSize.setSelection(network.getTrainingSize());

        Label tWindowSizeLabel = new Label(top, SWT.NONE);
        tWindowSizeLabel.setText("Window Size:");
        
        tWindowSize = new Spinner(top, SWT.BORDER);
        tWindowSize.setSelection(network.getTemporalWindow());

        Label hiddenLayers = new Label(top, SWT.NONE);
        hiddenLayers.setText("Hidden Layers");
       Label filler3 = new Label(top, SWT.NONE);
        Label hiddenLayer1Label = new Label(top, SWT.NONE);
        hiddenLayer1Label.setText("Layer 1 Neurons:");
        
        hiddenNeurons1 = new Spinner(top, SWT.BORDER);
        hiddenNeurons1.setSelection(network.getHiddenLayerOneNeurons());

        Label hiddenLayer2Label = new Label(top, SWT.NONE);
        hiddenLayer2Label.setText("Layer 2 Neurons:");
        
        hiddenNeurons2 = new Spinner(top, SWT.BORDER);
        hiddenNeurons2.setSelection(network.getHiddenLayerTwoNeurons());

        Label maxRmseLabel = new Label(top, SWT.NONE);
        maxRmseLabel.setText("Max RMSE:");
        
        maxRMSE = new Spinner(top, SWT.BORDER);
        maxRMSE.setDigits(4);
        maxRMSE.setMaximum(100000);
        maxRMSE.setSelection((int) (network.getMaxRMSE()* Math.pow(10, maxRMSE.getDigits())));

        Label learnFromLabel = new Label(top, SWT.NONE);
        learnFromLabel.setText("Learn From:");
        learnFrom = new Text(top, SWT.BORDER);
        learnFrom.setText("YYYY-MM-DD");
        learnFrom.setTextLimit(10);
        learnFrom.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
            public void modifyText(org.eclipse.swt.events.ModifyEvent e) {

            	trainNetworkAllow();
            }
        });
        trainNetwork = new Button(top, SWT.NONE);
        trainNetwork.setEnabled(false);
        trainNetwork.setText("Train Network");
        trainNetwork.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                        
                    	trainNetwork();
                    }
                });
        progressBar = new ProgressBar(top, SWT.BORDER);
        Label currentRmseLabel = new Label(top, SWT.NONE);
        currentRmseLabel.setText("Current RMSE:");
        currentRMSE = new Text(top, SWT.BORDER);
        currentRMSE.setEditable(false);
        saveNetwork = new Button(top, SWT.NONE);
        saveNetwork.setEnabled(false);
        saveNetwork.setText("Save Network");
        saveNetwork.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            	
                String saveNetworkLocation = null;
                FileDialog dlg = new FileDialog(top.getShell(), SWT.SAVE|SWT.SINGLE);
                dlg.setText("Save Network");
                String result = dlg.open();
                if (result != null)
                {
                   saveNetworkLocation = result;
                   saveNetwork(saveNetworkLocation);
                }          
            }
        });
        loadNetwork = new Button(top, SWT.NONE);
        loadNetwork.setText("Load Network");
        Label predictFromLabel = new Label(top, SWT.NONE);
        predictFromLabel.setText("Predict From:");
        predictFrom = new Text(top, SWT.BORDER);
        
        // Gets and displays current date
        predictFrom.setText(""+ReadCSV.displayDate(Calendar.getInstance().getTime()));
        
        predictFrom.setTextLimit(10);
        predictFrom.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
            public void modifyText(org.eclipse.swt.events.ModifyEvent e) {
                 
            	if(validDateFormat( predictFrom.getText() )){
            		runBenchmark.setEnabled(true);
            		saveResultsCSV.setEnabled(true);
            	}
            	else{
            		runBenchmark.setEnabled(false);
            		saveResultsCSV.setEnabled(false);
            	}
            }
        });
        loadNetwork.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                              
                String loadNetworkLocation = null;
                FileDialog dlg = new FileDialog(top.getShell(), SWT.OPEN|SWT.SINGLE);
                dlg.setText("Load Network");
                String result = dlg.open();
                if (result != null)
                {
                   loadNetworkLocation = result;
                   loadNetwork(loadNetworkLocation);
                   
                   currentFile.setText( new File(loadNetworkLocation).getName() );
                }             
            }
        });
        
        runBenchmark = new Button(top, SWT.NONE);
        runBenchmark.setEnabled(false);
        runBenchmark.setText("Run Network");
        runBenchmark.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
                    public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                        
                    	runNetwork();
                    }
                });
        
        
        saveResultsCSV =  new Button(top, SWT.NONE);
        saveResultsCSV.setEnabled(false);
        saveResultsCSV.setText("Save Results");
        saveResultsCSV.addSelectionListener(new org.eclipse.swt.events.SelectionAdapter() {
            public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
                

            	 String file = null;
                 FileDialog dlg = new FileDialog(top.getShell(), SWT.SAVE|SWT.SINGLE);
                 dlg.setText("Save Results CSV");
                 String result = dlg.open();
                 if (result != null)
                 {
                    file = result;
                    saveResultsCSV(file);
                 }          
            	
            }
        });
              
        
//        Label filler = new Label(top, SWT.NONE);
        Label averageDifferenceLabel = new Label(top, SWT.NONE);
        averageDifferenceLabel.setText("Average Difference:");
        averageDifference = new Text(top, SWT.BORDER);
        averageDifference.setEditable(false);
        
        
        currentFile = new Text(top, SWT.BORDER);
        currentFile.setEditable(false);
        
                
        //
        networkResults = new Text(parent, SWT.BORDER | SWT.MULTI);

        
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	private void addSecurities(Combo combo){		
		
		for(int i = 0; i < securities.size(); i++){
			
			combo.add( securities.get(i).getDescription() );
		}
	}
	
	private Security getSelectedSecurity(int securityIndex){
		
		return securities.get(securityIndex);
	}
	
	private int getSecurityIndexFromCode(String securityCode){
		
		for(int i = 0; i < securities.size(); i++){
			
			Security s = securities.get(i);
			
			if(s.getCode().equalsIgnoreCase(securityCode)){
				return i;
			}
		}
		
		return -1;
	}	

	private boolean trainNetworkAllow(){
		
		boolean allowTrain = false;
			
		if( validDateFormat( learnFrom.getText() ) && (securitySelector.getSelectionIndex() != -1 ) ){
			allowTrain = true;
		}
		else{
			allowTrain = false;
		}
		
		trainNetwork.setEnabled(allowTrain);
		
		return allowTrain;
	}	
	
	private boolean validDateFormat(String date){
		
		if(ReadCSV.parseDate(date) == null){
			return false;
		}else{
			return true;
		}
		
	}
	
	private void loadNetwork(String loadNetworkLocation){
				
		try {
			network = LoadSaveNetwork.loadNetwork(loadNetworkLocation);
			securitySelector.select( getSecurityIndexFromCode(network.getSecurityCode())  );
			trainingSize.setSelection(network.getTrainingSize());
			tWindowSize.setSelection(network.getTemporalWindow());
			hiddenNeurons1.setSelection(network.getHiddenLayerOneNeurons());
			hiddenNeurons2.setSelection(network.getHiddenLayerTwoNeurons());
			maxRMSE.setSelection((int) (network.getMaxRMSE()* Math.pow(10, maxRMSE.getDigits())));
			currentRMSE.setText(""+network.getRMSE());
			learnFrom.setText(network.getLearnFromDate());
								
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void saveNetwork(String saveNetworkLocation){
		
		try {
			LoadSaveNetwork.saveNetwork(network, saveNetworkLocation);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void saveResultsCSV(final String file){
		
		
		Job job = new Job("Save Results CSV") {
            protected IStatus run(IProgressMonitor monitor)
            {
            	try
                {            		
                    monitor.beginTask("Save Results CSV", 0);
                    
                    predictFrom.getDisplay().asyncExec( new Runnable(){ // Need to do this in order to access GUI elements
                    	public void run() {
                    	
                    		network.setPredictFromDate(predictFrom.getText());
                    	}
                    });
                    
            		
                    network.setSecurity(selectedSecuity);     
                    
                    Writer writer = new BufferedWriter(new FileWriter(file));
                    writer.write(network.newNetworkResults());
                    writer.flush();
                    writer.close(); 
                }	            	
            	catch (IOException e)
                {
                    CorePlugin.logException(e);
                    return new Status(IStatus.ERROR, Platform.PI_RUNTIME, IStatus.OK, "Error Saving CSV", e);
                }
            	
            	monitor.done();	                    
            	
            	return Status.OK_STATUS;
            }	            
	 };
	 
	 job.setUser(true);
     job.schedule();
		
	}
	
	private void runNetwork(){		
        

		
		network.setPredictFromDate(predictFrom.getText());
		network.setSecurity(selectedSecuity);
        
       
        currentRMSE.setText(""+network.getRMSE());
        networkResults.setText("");
        networkResults.setText(network.newNetworkResults());
        averageDifference.setText( ""+network.globalDirectionalAccuracy() );

	}
	
	private void trainNetwork(){

		final int tSize = trainingSize.getSelection();
		final int tWindow = tWindowSize.getSelection();
		final int neuronsLayer1 = hiddenNeurons1.getSelection();
		final int neuronsLayer2 = hiddenNeurons2.getSelection();
		final double targetRMSE = ((double)maxRMSE.getSelection()/ (double)Math.pow(10, maxRMSE.getDigits()) );
		final String lFromDate = learnFrom.getText();
		final int max = (int) (100/network.getMaxRMSE());
        progressBar.setMaximum(max);
		
		Job job = new Job("Train Neural Network") {
            protected IStatus run(IProgressMonitor monitor)
            {
            	// Creates the Thread that updates the status bar and the currentRMSE text field
            	Thread t = new Thread(){ //Begin Thread
            		public void run(){
            			try {
            				
            				Thread.sleep(1000);
            			}catch (InterruptedException e) {
            				e.printStackTrace();
            			}
            			
            			while(!network.isTrained()){
            				
            				try {
            					
            					Thread.sleep(200);
            				} catch (InterruptedException e) {		
            					e.printStackTrace();
            				}
            				
            				// This is the part that performs the updates to the progress bar and the currentRMSE text field
            				progressBar.getDisplay().asyncExec( new Runnable(){
            					public void run() {
            						progressBar.setSelection((int) (100/network.getRMSE()));
            						currentRMSE.setText(""+network.getRMSE());
            					}
            				});
            				
            				// Once the network is trained. Set final update to the progress bar and currentRMSE text field
            				if(network.isTrained()){
            					progressBar.getDisplay().asyncExec( new Runnable(){
            						public void run() {
            							progressBar.setSelection(max);
            							currentRMSE.setText(""+network.getRMSE());
            						}
            					});
            				}
            			}
                     }
            		}; // End Thread
                    t.setPriority(Thread.MIN_PRIORITY);
                    t.start();
                    
            	
            	monitor.beginTask("Training the Network", (int) (100/targetRMSE));           	
            	network.setSecurity(selectedSecuity);
        		network.setTrainingSize(tSize);
        		network.setTemporalWindow(tWindow);
        		network.setHiddenLayerOneNeurons(neuronsLayer1);
        		network.setHiddenLayerTwoNeurons(neuronsLayer2);
        		network.setLearnFromDate(lFromDate);
        		network.setMaxRMSE(targetRMSE);
            	network.setMonitor(monitor);            	
            	network.trainNetwork();           	            	
            	
            	// Enable the button that saves the trained network
            	saveNetwork.getDisplay().asyncExec( new Runnable(){
					public void run() {
											
						saveNetwork.setEnabled(true);
					}
            		
            	});           	
            	
            	monitor.done();           	
            	return Status.OK_STATUS;
            }	            
	 };	 
	 job.setUser(true);
     job.schedule();

	}
	
}// end class