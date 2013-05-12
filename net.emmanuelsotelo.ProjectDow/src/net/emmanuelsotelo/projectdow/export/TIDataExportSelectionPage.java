package net.emmanuelsotelo.projectdow.export;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import net.sourceforge.eclipsetrader.core.CorePlugin;
import net.sourceforge.eclipsetrader.core.db.Security;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.List;

public class TIDataExportSelectionPage extends WizardPage {

	Composite parent;
    	

    List list;
  
	
	protected TIDataExportSelectionPage() {
		super("Export Technical Indicator Data");
		// TODO Auto-generated constructor stub
	}

	@SuppressWarnings("unchecked")
	public void createControl(Composite parent) {		
		
		//used for ChartView. See getParent()
		this.parent = parent;
		
		// Prevents the user from clicking next without first selecting a Security
		setPageComplete( false );
				
		Composite content = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(1, false);
        gridLayout.marginWidth = gridLayout.marginHeight = 0;
        content.setLayout(gridLayout);
        content.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        setControl(content);
        
        list = new List(content, SWT.MULTI|SWT.BORDER|SWT.V_SCROLL);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.heightHint = list.getItemHeight() * 10;
        list.setLayoutData(gridData);
        list.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                setPageComplete( (list.getSelectionIndex() != -1) );
            	getContainer().updateButtons();
            	
            }
        });
      
        java.util.List securities = CorePlugin.getRepository().allSecurities();
        
        Collections.sort(securities, new Comparator() {
            public int compare(Object arg0, Object arg1)
            {
                return ((Security)arg0).getDescription().compareTo(((Security)arg1).getDescription());
            }
        });

        
        for (Iterator iter = securities.iterator(); iter.hasNext(); )
        {
            Security security = (Security)iter.next();
            list.add(security.getDescription());
            list.setData(String.valueOf(list.getItemCount() - 1), security);
        } 

    }
	
    
	public IWizardPage getNextPage()
	{
		TIDataExportChartSelectionPage chartSelectionPage = ((TIDataWizard)getWizard()).chartSelectionPage;
		chartSelectionPage.setSecurity();	
		
		return chartSelectionPage;
	}
	
	public boolean canFlipToNextPage()
	{
		return (list.getSelectionIndex() != -1);
	}

	
    public Security[] getSelectedSecurity()
    {
    	int[] indices = list.getSelectionIndices();
    	Security[] selection = new Security[indices.length];
    	
    	for (int i = 0; i < selection.length; i++)
    	{
    		selection[i] = (Security)list.getData(String.valueOf(indices[i]));
    	}
            
    	return selection;
  }
    

	
	public Composite getParent()
	{
		return parent;
	}
    
}