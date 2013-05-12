package net.emmanuelsotelo.projectdow.trading;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.emmanuelsotelo.projectdow.indicators.data.ChartData;
import net.sourceforge.eclipsetrader.core.CorePlugin;
import net.sourceforge.eclipsetrader.core.db.Chart;
import net.sourceforge.eclipsetrader.core.db.Security;
import net.sourceforge.eclipsetrader.trading.TradingSystemPluginPreferencePage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


public class IndicatorTradingSystemPreferencePage extends TradingSystemPluginPreferencePage {

	private int numIndicators =3;
	
	private Combo[] indicators = new Combo[numIndicators];
	private Button[] enableIndicators = new Button[3];
    private Combo[] buyCondition = new Combo[numIndicators];
    private Text[] buyConditionAttribute = new Text[numIndicators];
    private Combo[] sellCondition = new Combo[numIndicators];
    private Text[] sellConditionAttribute = new Text[numIndicators];
    
    private List<Chart> charts;
    private Combo chartSelector;
    private ChartData chartData;
    private Chart chart;

	public void init(Security security, Map params)
    {
        super.init(security, params);        
    }
	
	public Control createContents(Composite parent) {
		
		Composite content = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(3, false);
        content.setLayout(gridLayout);
        content.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
		      
        // Select Chart
        Label selectChart = new Label(content, SWT.NONE);
        selectChart.setText("Select Chart");
        selectChart.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        chartSelector = new Combo(content, SWT.READ_ONLY);
        chartSelector.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

        chartSelector.addModifyListener(new org.eclipse.swt.events.ModifyListener() {
            public void modifyText(org.eclipse.swt.events.ModifyEvent e) {

            	updateIndicatorNames();
            	
            }            
        });
        
       
        addCharts();
       
        if( getParameters().get("chart") != null ){
        	chartSelector.select( Integer.parseInt((String)getParameters().get("chart")) );
        }
        
        Label rowPlaceHolder0 = new Label(content, SWT.NONE);
        rowPlaceHolder0.setText("");
        rowPlaceHolder0.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        
        //Blank Space       
        Label[] rowPlaceHolder00 = new Label[3];
        for(int i = 0; i < rowPlaceHolder00.length; i++){
        	
        	rowPlaceHolder00[i] = new Label(content, SWT.NONE);
        	rowPlaceHolder00[i].setText("");
        	rowPlaceHolder00[i].setLayoutData(new GridData(125, SWT.DEFAULT));
        }
        
        // Indicator 1
        Label labelIndicator1 = new Label(content, SWT.NONE);
        labelIndicator1.setText("Indicator 1");
        labelIndicator1.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        indicators[0] = new Combo(content, SWT.READ_ONLY);
        addChartIndicators(indicators[0]);
        indicators[0].setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
        if( getParameters().get("indicator0") != null ){
        	indicators[0].select( Integer.parseInt((String)getParameters().get("indicator0")) );
        }
        
        Label rowPlaceHolder1 = new Label(content, SWT.NONE);
        rowPlaceHolder1.setText("");
        rowPlaceHolder1.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        Label enableLabelInd1 = new Label(content, SWT.NONE);
        enableLabelInd1.setText("Enable");
        enableLabelInd1.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        enableIndicators[0] = new Button(content ,SWT.CHECK);
        if( getParameters().get("enable0") != null ){
        	enableIndicators[0].setSelection( Boolean.parseBoolean( (String)getParameters().get("enable0") ) );
        }
        
        Label rowPlaceHolder2 = new Label(content, SWT.NONE);
        rowPlaceHolder2.setText("");
        rowPlaceHolder2.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        Label buyIf1 = new Label(content, SWT.NONE);
        buyIf1.setText("Buy if:");
        buyIf1.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        buyCondition[0] = new Combo(content, SWT.READ_ONLY);
        addOperators(buyCondition[0]);
        buyCondition[0].setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
        if( getParameters().get("buyCondition0") != null ){
        	buyCondition[0].select( Integer.parseInt((String)getParameters().get("buyCondition0")) );
        }
        
        buyConditionAttribute[0] = new Text(content, SWT.BORDER);
        buyConditionAttribute[0].setLayoutData(new GridData(55, SWT.DEFAULT));
        if( getParameters().get("buyConditionAttribute0") != null ){
        	buyConditionAttribute[0].setText( (String) getParameters().get("buyConditionAttribute0") );
        }
        else{
        	buyConditionAttribute[0].setText("0.00");
        }
                
        Label sellIf1 = new Label(content, SWT.NONE);
        sellIf1.setText("Sell if:");
        sellIf1.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        sellCondition[0] = new Combo(content, SWT.READ_ONLY);
        addOperators(sellCondition[0]);
        sellCondition[0].setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
        if( getParameters().get("sellCondition0") != null ){
        	sellCondition[0].select( Integer.parseInt((String)getParameters().get("sellCondition0")) );
        }
       
        sellConditionAttribute[0] = new Text(content, SWT.BORDER);
        sellConditionAttribute[0].setLayoutData(new GridData(55, SWT.DEFAULT));
        if( getParameters().get("sellConditionAttribute0") != null ){
        	sellConditionAttribute[0].setText( (String) getParameters().get("sellConditionAttribute0") );
        }
        else{
        	sellConditionAttribute[0].setText("0.00");
        }
        
        //Blank Space       
        Label[] rowPlaceHolder01 = new Label[3];
        for(int i = 0; i < rowPlaceHolder01.length; i++){
        	
        	rowPlaceHolder01[i] = new Label(content, SWT.NONE);
        	rowPlaceHolder01[i].setText("");
        	rowPlaceHolder01[i].setLayoutData(new GridData(125, SWT.DEFAULT));
        }
        
        // Indicator 2
        Label labelIndicator2 = new Label(content, SWT.NONE);
        labelIndicator2.setText("Indicator 2");
        labelIndicator2.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        indicators[1] = new Combo(content, SWT.READ_ONLY);
        addChartIndicators(indicators[1]);
        indicators[1].setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
        if( getParameters().get("indicator1") != null ){
        	indicators[1].select( Integer.parseInt((String)getParameters().get("indicator1")) );
        }
        
        Label rowPlaceHolder3 = new Label(content, SWT.NONE);
        rowPlaceHolder3.setText("");
        rowPlaceHolder3.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        Label enableLabelInd2 = new Label(content, SWT.NONE);
        enableLabelInd2.setText("Enable");
        enableLabelInd2.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        enableIndicators[1] = new Button(content ,SWT.CHECK);
        if( getParameters().get("enable1") != null ){
        	enableIndicators[1].setSelection( Boolean.parseBoolean( (String)getParameters().get("enable1") ) );
        }

        
        Label rowPlaceHolder4 = new Label(content, SWT.NONE);
        rowPlaceHolder4.setText("");
        rowPlaceHolder4.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        Label buyIf2 = new Label(content, SWT.NONE);
        buyIf2.setText("Buy if:");
        buyIf2.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        buyCondition[1] = new Combo(content, SWT.READ_ONLY);
        addOperators(buyCondition[1]);
        buyCondition[1].setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
        if( getParameters().get("buyCondition1") != null ){
        	buyCondition[1].select( Integer.parseInt((String)getParameters().get("buyCondition1")) );
        }
        
        buyConditionAttribute[1] = new Text(content, SWT.BORDER);
        buyConditionAttribute[1].setLayoutData(new GridData(55, SWT.DEFAULT));
        if( getParameters().get("buyConditionAttribute1") != null ){
        	buyConditionAttribute[1].setText( (String) getParameters().get("buyConditionAttribute1") );
        }
        else{
        	buyConditionAttribute[1].setText("0.00");
        }
        
        Label sellIf2 = new Label(content, SWT.NONE);
        sellIf2.setText("Sell if:");
        sellIf2.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        sellCondition[1] = new Combo(content, SWT.READ_ONLY);
        addOperators(sellCondition[1]);
        sellCondition[1].setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
        if( getParameters().get("sellCondition1") != null ){
        	sellCondition[1].select( Integer.parseInt((String)getParameters().get("sellCondition1")) );
        }
       
        sellConditionAttribute[1] = new Text(content, SWT.BORDER);
        sellConditionAttribute[1].setLayoutData(new GridData(55, SWT.DEFAULT));
        if( getParameters().get("sellConditionAttribute1") != null ){
        	sellConditionAttribute[1].setText( (String) getParameters().get("sellConditionAttribute1") );
        }
        else{
        	sellConditionAttribute[1].setText("0.00");
        }
        
      //Blank Space       
        Label[] rowPlaceHolder02 = new Label[3];
        for(int i = 0; i < rowPlaceHolder02.length; i++){
        	
        	rowPlaceHolder02[i] = new Label(content, SWT.NONE);
        	rowPlaceHolder02[i].setText("");
        	rowPlaceHolder02[i].setLayoutData(new GridData(125, SWT.DEFAULT));
        }
        
        // Indicator 3
        Label labelIndicator3 = new Label(content, SWT.NONE);
        labelIndicator3.setText("Indicator 3");
        labelIndicator3.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        indicators[2] = new Combo(content, SWT.READ_ONLY);
        addChartIndicators(indicators[2]);
        indicators[2].setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
        if( getParameters().get("indicator2") != null ){
        	indicators[2].select( Integer.parseInt((String)getParameters().get("indicator2")) );
        }
        
        Label rowPlaceHolder5 = new Label(content, SWT.NONE);
        rowPlaceHolder5.setText("");
        rowPlaceHolder5.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        Label enableLabelInd3 = new Label(content, SWT.NONE);
        enableLabelInd3.setText("Enable");
        enableLabelInd3.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        enableIndicators[2] = new Button(content ,SWT.CHECK);
        if( getParameters().get("enable2") != null ){
        	enableIndicators[2].setSelection( Boolean.parseBoolean( (String)getParameters().get("enable2") ) );
        }
        
        Label rowPlaceHolder6 = new Label(content, SWT.NONE);
        rowPlaceHolder6.setText("");
        rowPlaceHolder6.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        Label buyIf3 = new Label(content, SWT.NONE);
        buyIf3.setText("Buy if:");
        buyIf3.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        buyCondition[2] = new Combo(content, SWT.READ_ONLY);
        addOperators(buyCondition[2]);
        buyCondition[2].setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
        if( getParameters().get("buyCondition2") != null ){
        	buyCondition[2].select( Integer.parseInt((String)getParameters().get("buyCondition2")) );
        }
        
        buyConditionAttribute[2] = new Text(content, SWT.BORDER);
        buyConditionAttribute[2].setLayoutData(new GridData(55, SWT.DEFAULT));
        if( getParameters().get("buyConditionAttribute2") != null ){
        	buyConditionAttribute[2].setText( (String) getParameters().get("buyConditionAttribute2") );
        }
        else{
        	buyConditionAttribute[2].setText("0.00");
        }
                
        Label sellIf3 = new Label(content, SWT.NONE);
        sellIf3.setText("Sell if:");
        sellIf3.setLayoutData(new GridData(125, SWT.DEFAULT));
        
        sellCondition[2] = new Combo(content, SWT.READ_ONLY);
        addOperators(sellCondition[2]);
        sellCondition[2].setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));
        if( getParameters().get("sellCondition2") != null ){
        	sellCondition[2].select( Integer.parseInt((String)getParameters().get("sellCondition2")) );
        }
       
        sellConditionAttribute[2] = new Text(content, SWT.BORDER);
        sellConditionAttribute[2].setLayoutData(new GridData(55, SWT.DEFAULT));
        if( getParameters().get("sellConditionAttribute2") != null ){
        	sellConditionAttribute[2].setText( (String) getParameters().get("sellConditionAttribute2") );
        }
        else{
        	sellConditionAttribute[2].setText("0.00");
        }
        
        return content;
	}
	
    public void performOk()
    {
        Map<String, String> params = new HashMap<String, String>();
        
        // Select Chart
        if(chartSelector != null){
        	
        	params.put("chart", String.valueOf(chartSelector.getSelectionIndex()));
        }
        
        
        // Indicator1
        if(indicators[0] != null){        	
        	
        	params.put("indicator0", String.valueOf(indicators[0].getSelectionIndex()));
        }
        
        if(enableIndicators[0] != null){
        	
        	params.put("enable0", String.valueOf(enableIndicators[0].getSelection()));
        }
        
        if(buyCondition[0] != null){
        	
        	params.put("buyCondition0", String.valueOf(buyCondition[0].getSelectionIndex()));
        }
        
        if(buyConditionAttribute[0] != null){
        	
        	params.put("buyConditionAttribute0", buyConditionAttribute[0].getText() );
        }
        
        if( sellCondition[0] != null){
        	
        	params.put("sellCondition0", String.valueOf(sellCondition[0].getSelectionIndex()));
        }
        
        if(sellConditionAttribute[0] != null){
        	
        	params.put("sellConditionAttribute0", sellConditionAttribute[0].getText() );
        }
        
        
     // Indicator2
        if(indicators[1] != null){        	
        	
        	params.put("indicator1", String.valueOf(indicators[1].getSelectionIndex()));
        }
        
        if(enableIndicators[1] != null){
        	
        	params.put("enable1", String.valueOf(enableIndicators[1].getSelection()));
        }
        
        if(buyCondition[1] != null){
        	
        	params.put("buyCondition1", String.valueOf(buyCondition[1].getSelectionIndex()));
        }
        
        if(buyConditionAttribute[1] != null){
        	
        	params.put("buyConditionAttribute1", buyConditionAttribute[1].getText() );
        }
        
        if( sellCondition[1] != null){
        	
        	params.put("sellCondition1", String.valueOf(sellCondition[1].getSelectionIndex()));
        }
        
        if(sellConditionAttribute[1] != null){
        	
        	params.put("sellConditionAttribute1", sellConditionAttribute[1].getText() );
        }
        
        
        
        // Indicator3
        if(indicators[2] != null){        	
        	
        	params.put("indicator2", String.valueOf(indicators[2].getSelectionIndex()));
        }
        
        if(enableIndicators[2] != null){
        	
        	params.put("enable2", String.valueOf(enableIndicators[2].getSelection()));
        }
        
        if(buyCondition[2] != null){
        	
        	params.put("buyCondition2", String.valueOf(buyCondition[2].getSelectionIndex()));
        }
        
        if(buyConditionAttribute[2] != null){
        	
        	params.put("buyConditionAttribute2", buyConditionAttribute[2].getText() );
        }
        
        if( sellCondition[2] != null){
        	
        	params.put("sellCondition2", String.valueOf(sellCondition[2].getSelectionIndex()));
        }
        
        if(sellConditionAttribute[2] != null){
        	
        	params.put("sellConditionAttribute2", sellConditionAttribute[2].getText() );
        }
        
        setParameters(params);
    }
    
    @SuppressWarnings("unchecked")
	private void addCharts()
    {    	
    	List<Chart> charts = CorePlugin.getRepository().allCharts( getSecurity()  );
        
        // Sort the charts by name.
		Collections.sort(charts, new Comparator() {
            public int compare(Object arg0, Object arg1)
            {
            	 return ((Chart)arg0).getTitle().compareTo(((Chart)arg1).getTitle());
            }
        });
            

		for (Iterator iter = charts.iterator(); iter.hasNext(); )
        {
        	Chart currentChart = (Chart)iter.next();
            this.chartSelector.add( currentChart.getTitle() );            
        } 
	}
    
@SuppressWarnings("unchecked")
private Chart getChart(int chartIndex){

	charts = CorePlugin.getRepository().allCharts( getSecurity()  );
	
        
    	// Sort the charts by name.
		Collections.sort(charts, new Comparator() {
            public int compare(Object arg0, Object arg1)
            {
            	 return ((Chart)arg0).getTitle().compareTo(((Chart)arg1).getTitle());
            }
        });  
		
		Chart chart = charts.get(chartIndex);
		
		// Initialize indicator data extractor
		if(chartData == null){
			chartData = new ChartData(chart);
		}		

		return chart;
	}
    
    @SuppressWarnings("unchecked")
	private void addChartIndicators(Combo combo)
    {
        int selectedChart = 0;
        selectedChart =  chartSelector.getSelectionIndex();   
        
        if(selectedChart != - 1){             
        	chart =  getChart(selectedChart);
        }
    	     	
    	if(chart != null){ 		

    		String[] indicatorNames = chartData.getIndicatorLabels();
    		
    		for(int i = 0; i < indicatorNames.length; i++){
    			
    			combo.add(indicatorNames[i]);
    		}
    	}    	
    }
    
    /**
     * Adds Equality and Relational Operators to the Combo Selector
     */
    private void addOperators(Combo combo){
    	
    	combo.add("=");
    	combo.add("!=");
    	combo.add(">");
    	combo.add(">=");
    	combo.add("<");
    	combo.add("<=");
    	
    }
    
    private void updateIndicatorNames(){
    	
    	int selectedChart =  chartSelector.getSelectionIndex();
    	chart =  getChart(selectedChart);
    	chartData = new ChartData(chart);
    	
    	for(int i = 0; i < indicators.length; i++ ){
    		
    		if(indicators[i] != null){
    			indicators[i].removeAll();
    			addChartIndicators(indicators[i]);
    		}
    	}
    	
    }

}