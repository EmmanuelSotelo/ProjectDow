package net.emmanuelsotelo.projectdow.indicators;

import net.sourceforge.eclipsetrader.charts.IndicatorPluginPreferencePage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import net.emmanuelsotelo.projectdow.styledtext.JavaLineStyler;

public class CustomIndicatorPreferencePage extends IndicatorPluginPreferencePage {

	private StyledText textBox;
	
	public CustomIndicatorPreferencePage() {

	}
	
	 public void createControl(Composite parent)
	    {
		 
		 Composite content = new Composite(parent, SWT.NONE);
	     GridLayout gridLayout = new GridLayout(2, false);
	     gridLayout.marginWidth = gridLayout.marginHeight = 0;
	     content.setLayout(gridLayout);
	     content.setLayoutData( new GridData(GridData.FILL, GridData.FILL, true, true) );
	     setControl(content);
	     
	     addColorSelector(content, "color", "Color", CustomIndicator.DEFAULT_COLOR); //$NON-NLS-1$
	     addLabelField(content, "label", "Label", CustomIndicator.DEFAULT_LABEL); //$NON-NLS-1$
	     addLineTypeSelector(content, "lineType", "Line Type", CustomIndicator.DEFAULT_LINETYPE); //$NON-NLS-1$
	     addIntegerValueSelector(content, "period", "Period", 1, 9999, CustomIndicator.DEFAULT_PERIOD); //$NON-NLS-1$		 
	     addTextBox(content, "customCode", "Custom", CustomIndicator.DEFAULT_CUSTOMCODE);
	       
	    }
	 	
	 	public StyledText addTextBox(Composite parent, String id, String text, String defaultValue)
	    {
	        Label label = new Label(parent, SWT.NONE);
	        label.setText(text);
	        label.setLayoutData(new GridData(125, SWT.DEFAULT));
	        textBox = new StyledText(parent, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
	        textBox.addLineStyleListener(new JavaLineStyler());
	        
	        textBox.setLayoutData(new GridData(GridData.FILL_BOTH)); //right here!!!
	        textBox.setText(getSettings().getString(id, defaultValue));
	        addControl(id, textBox );
	        return textBox ;	 		

	    }
	 	
	 	@Override
	 	public void performFinish() {
	 		super.performFinish();
	 		
	 		if(textBox != null){	 		
	 			getSettings().set("customCode", textBox.getText());
	 		}
	 		
	 	}
	 
}
