package net.emmanuelsotelo.projectdow.indicators;

import net.sourceforge.eclipsetrader.charts.IndicatorPluginPreferencePage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

public class WeightPreferencePage extends IndicatorPluginPreferencePage {

	public WeightPreferencePage() {
		// TODO Auto-generated constructor stub
	}
	
	 public void createControl(Composite parent)
	    {
	        Composite content = new Composite(parent, SWT.NONE);
	        GridLayout gridLayout = new GridLayout(2, false);
	        gridLayout.marginWidth = gridLayout.marginHeight = 0;
	        content.setLayout(gridLayout);
	        content.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
	        setControl(content);

	        addColorSelector(content, "color", Messages.WeightPreferencePage_Color, Weight.DEFAULT_COLOR); //$NON-NLS-1$
	        addLabelField(content, "label", Messages.WeightPreferencePage_Label, Weight.DEFAULT_LABEL); //$NON-NLS-1$
	        addLineTypeSelector(content, "lineType", Messages.WeightPreferencePage_LineType, Weight.DEFAULT_LINETYPE); //$NON-NLS-1$
	        addDoubleValueSelector(content, "Multiplier", Messages.WeightPreferencePage_Multiplier, 2, 1, 10000, Weight.DEFAULT_MULTIPLIER);
	       
	    }

}
