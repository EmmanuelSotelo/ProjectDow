package net.emmanuelsotelo.projectdow.trading;

import java.util.HashMap;
import java.util.Map;

import net.emmanuelsotelo.projectdow.styledtext.JavaLineStyler;
import net.sourceforge.eclipsetrader.core.db.Security;
import net.sourceforge.eclipsetrader.trading.TradingSystemPluginPreferencePage;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class CustomTradingSystemPreferencePage extends TradingSystemPluginPreferencePage {

    private StyledText textBox;
    private Spinner period;

    public CustomTradingSystemPreferencePage()
    {
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.trading.TradingSystemPluginPreferencePage#init(net.sourceforge.eclipsetrader.core.db.Security, java.util.Map)
     */
    public void init(Security security, Map params)
    {
        super.init(security, params);
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.trading.AlertPluginPreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    public Control createContents(Composite parent)
    {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, true);
        content.setLayout(gridLayout);
        content.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        
        // Custom Code Entry
        Label customcode = new Label(content, SWT.NONE);
        customcode.setText("Custom Trading System code");
        customcode.setLayoutData(new GridData(150, SWT.DEFAULT));
        
        period = new Spinner(content, SWT.BORDER);
        
        textBox = new StyledText(content, SWT.MULTI | SWT.WRAP | SWT.V_SCROLL | SWT.BORDER);
        textBox.addLineStyleListener(new JavaLineStyler());
        
        if(getParameters().get("customCode") != null){
        	textBox.setText( (String) getParameters().get("customCode")  );
        }
        
        textBox.setLayoutData(new GridData(GridData.FILL_BOTH));
        
        
        return content;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.trading.AlertPluginPreferencePage#performOk()
     */
    public void performOk()
    {
        Map params = new HashMap();
        
        if(textBox != null){        	        	
        	params.put("customCode", textBox.getText() );
        }
        
        setParameters(params);
    }    
}
