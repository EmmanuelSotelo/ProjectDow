package net.emmanuelsotelo.projectdow.export;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

import net.sourceforge.eclipsetrader.core.CorePlugin;
import net.sourceforge.eclipsetrader.core.db.Chart;
import net.sourceforge.eclipsetrader.core.db.Security;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

public class TIDataExportChartSelectionPage extends WizardPage {	
	
	List list;
	Text file;

	Security security;

	protected TIDataExportChartSelectionPage() {
		super("Select Chart");
		
	}

	public void createControl(Composite parent) {

		// Prevents the user from clicking finish without first completing all steps
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
                setPageComplete( (list.getSelectionIndex() != -1) && file.getText().length() != 0  );               
                getContainer().updateButtons();
                
                
            }
        });
        
        
      Label label = new Label(content, SWT.NONE);
      label.setText("Save as CSV"); // The destination: where the file will be written to

      Composite group = new Composite(content, SWT.NONE);
      gridLayout = new GridLayout(3, false);
      gridLayout.marginWidth = gridLayout.marginHeight = 0;
      group.setLayout(gridLayout);
      group.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
      
      label = new Label(group, SWT.NONE);
      label.setText("File Name");                 // change this
      file = new Text(group, SWT.BORDER);
      file.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
      file.addModifyListener(new ModifyListener() {
          public void modifyText(ModifyEvent e)
          {
              setPageComplete((list.getSelectionIndex() != -1) && file.getText().length() != 0);
              getContainer().updateButtons();
          }
      });
      
      
    Button button = new Button(group, SWT.PUSH);
    button.setText("Browse...");
    button.addSelectionListener(new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e)
        {
            FileDialog dlg = new FileDialog(getShell(), SWT.SAVE|SWT.SINGLE);
            dlg.setText("Export to File");
            String result = dlg.open();
            if (result != null)
            {
                file.setText(result);
                setPageComplete((list.getSelectionIndex() != -1 ) && file.getText().length() != 0);
                getContainer().updateButtons();
            }
        }
    });      
      
        
        // Next set of steps.
        // Extract selected chart, use similar strategy for extracting Securities
        // Set button conditions.
        // Send data from charts to Wizard
        // Extract data and place in CSV
       // Use chart data view for strategy.
	}
	
	@SuppressWarnings("unchecked")
	public void setSecurity()
	{
		TIDataExportSelectionPage securitySelectionPage = ((TIDataWizard)getWizard()).securitySelectionPage;
		Security[] securities = securitySelectionPage.getSelectedSecurity();
		
		security = securities[0];
		
		java.util.List<Chart> charts = CorePlugin.getRepository().allCharts(security);
        
        Collections.sort(charts, new Comparator() {
            public int compare(Object arg0, Object arg1)
            {
//                return ((Security)arg0).getDescription().compareTo(((Security)arg1).getDescription());
            	 return ((Chart)arg0).getTitle().compareTo(((Chart)arg1).getTitle());
            }
        });
            
        // Clear the list to remove old data and deal with the double calling of getNextPage() bug
        list.removeAll();
        for (Iterator iter = charts.iterator(); iter.hasNext(); )
        {
        	Chart chart = (Chart)iter.next();
            list.add(chart.getTitle());
            list.setData(String.valueOf(list.getItemCount() - 1), chart);
        } 
	}
	
	public Chart getSelectedChart()
	{
		int[] charts = list.getSelectionIndices();
        Chart[] selection = new Chart[charts.length];
        
        for (int i = 0; i < selection.length; i++)
        {
            selection[i] = (Chart)list.getData(String.valueOf(charts[i]));
        }
        
        Chart chart = selection[0];
        
        return chart;
	}
	
  public String getFile()
  {
      return file.getText();
  }  

}