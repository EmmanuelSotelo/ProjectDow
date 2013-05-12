package net.emmanuelsotelo.projectdow.views;
import com.cloudgarden.resource.SWTResourceManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.part.ViewPart;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class PredictionManagerView extends ViewPart {

	{
		//Register as a resource user - SWTResourceManager will
		//handle the obtaining and disposing of resources
//		SWTResourceManager.registerResourceUser(this);
	}
	
	private CTabFolder cTabFolder1;
	private CTabItem cTabItem1;
	private TableColumn windowSize;
	private TableColumn trainingSize;
	private TableColumn trainedSecurity;
	private TableColumn networkName;
	private Table networkData;
	private Button loadNetworks;
	private Composite composite1;

	@Override
	public void createPartControl(Composite parent) {
		{
			cTabFolder1 = new CTabFolder(parent, SWT.NONE);
			{
				cTabItem1 = new CTabItem(cTabFolder1, SWT.NONE);
				cTabItem1.setText("cTabItem1");
				{
					composite1 = new Composite(cTabFolder1, SWT.NONE);
					FormLayout composite1Layout = new FormLayout();
					composite1.setLayout(composite1Layout);
					cTabItem1.setControl(composite1);
					{
						FormData networkDataLData = new FormData();
						networkDataLData.left =  new FormAttachment(0, 1000, 12);
						networkDataLData.top =  new FormAttachment(0, 1000, 41);
						networkDataLData.width = 409;
						networkDataLData.height = 47;
						networkData = new Table(composite1, SWT.NONE);
						networkData.setLayoutData(networkDataLData);
						networkData.setHeaderVisible(true);
						{
							networkName = new TableColumn(networkData, SWT.NONE);
							networkName.setText("File Name");
							networkName.setWidth(70);
							networkName.setMoveable(true);
						}
						{
							trainedSecurity = new TableColumn(networkData, SWT.NONE);
							trainedSecurity.setText("Trained Security");
							trainedSecurity.setWidth(100);
							trainedSecurity.setMoveable(true);
						}
						{
							trainingSize = new TableColumn(networkData, SWT.NONE);
							trainingSize.setText("Training Size");
							trainingSize.setWidth(78);
							trainingSize.setMoveable(true);
						}
						{
							windowSize = new TableColumn(networkData, SWT.NONE);
							windowSize.setText("Window Size");
							windowSize.setWidth(80);
							windowSize.setMoveable(true);
						}

					}
					{
						loadNetworks = new Button(composite1, SWT.PUSH | SWT.CENTER);
						FormData loadNetworksLData = new FormData();
						loadNetworksLData.left =  new FormAttachment(0, 1000, 12);
						loadNetworksLData.top =  new FormAttachment(81, 1000, 0);
						loadNetworksLData.width = 83;
						loadNetworksLData.height = 23;
						loadNetworks.setLayoutData(loadNetworksLData);
						loadNetworks.setText("Load Networks");
					}
				}
			}
			cTabFolder1.setSelection(0);
		}
	}
			
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
