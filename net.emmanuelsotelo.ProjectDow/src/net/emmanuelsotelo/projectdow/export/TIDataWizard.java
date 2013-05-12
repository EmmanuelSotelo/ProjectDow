package net.emmanuelsotelo.projectdow.export;

import net.sourceforge.eclipsetrader.core.db.Chart;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IExportWizard;
import org.eclipse.ui.IWorkbench;

public class TIDataWizard extends Wizard implements IExportWizard {

    //Don't set these to private
	TIDataExportSelectionPage securitySelectionPage = new TIDataExportSelectionPage();
    TIDataExportChartSelectionPage chartSelectionPage = new TIDataExportChartSelectionPage();

	public TIDataWizard() {
		
		String title = "Export Technical Indicator Data";
		
		setWindowTitle(title);
        securitySelectionPage.setTitle(title);
        securitySelectionPage.setDescription("Description");
        addPage(securitySelectionPage); // Add page that selects Security
              
        // Set Description   
        chartSelectionPage.setTitle("Select Chart");
        addPage(chartSelectionPage);
	}

	
	// Allow Wizard to Finish if securitySectionPage and chartSelectionPage are complete
	@Override
	public boolean canFinish()
	{
		return  ( securitySelectionPage.isPageComplete() && chartSelectionPage.isPageComplete() );
	}
	
	@Override
	public boolean performFinish() {

		Chart chart = chartSelectionPage.getSelectedChart();
		String file = chartSelectionPage.getFile();		
		
		ChartDataWritter dataWritter = new ChartDataWritter(chart, file);	
		dataWritter.writeData();
		
		return true;
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub	

	}
}