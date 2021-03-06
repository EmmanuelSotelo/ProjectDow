/*
 * Copyright (c) 2004-2006 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 */

package net.sourceforge.eclipsetrader.charts.wizards;

import net.sourceforge.eclipsetrader.charts.internal.Messages;
import net.sourceforge.eclipsetrader.core.db.Chart;
import net.sourceforge.eclipsetrader.core.db.ChartRow;
import net.sourceforge.eclipsetrader.core.db.ChartTab;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class IndicatorLocationPage extends WizardPage
{
    private Button stacked;
    private Button newTab;
    private Button newRow;
    private Combo stackedTab;
    private Spinner rowNumber;
    private Text tabLabel;
    private Chart chart;

    public IndicatorLocationPage(Chart chart)
    {
        super(""); //$NON-NLS-1$
        this.chart = chart;
        setTitle(Messages.IndicatorLocationPage_Title);
        setDescription(Messages.IndicatorLocationPage_Description);
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
     */
    public void createControl(Composite parent)
    {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(3, false);
        gridLayout.marginWidth = gridLayout.marginHeight = 0;
        content.setLayout(gridLayout);
        content.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));
        setControl(content);

        Label label = new Label(content, SWT.NONE);
        label.setLayoutData(new GridData(125, SWT.DEFAULT));
        stacked = new Button(content, SWT.RADIO);
        stacked.setText(Messages.IndicatorLocationPage_Stacked);
        stacked.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                stackedTab.setEnabled(true);
                rowNumber.setEnabled(false);
                tabLabel.setEnabled(false);
                tabLabel.setText(""); //$NON-NLS-1$
                validatePage();
            }
        });
        stackedTab = new Combo(content, SWT.READ_ONLY);
        for (int r = 0; r < chart.getRows().size(); r++)
        {
            ChartRow row = (ChartRow)chart.getRows().get(r);
            for (int t = 0; t < row.getTabs().size(); t++)
            {
                ChartTab tab = (ChartTab)row.getTabs().get(t);
                stackedTab.add(tab.getLabel());
                stackedTab.setData(String.valueOf(stackedTab.getItemCount() - 1), tab);
            }
        }
        if (((NewIndicatorWizard)getWizard()).getDefaultTab() != null)
            stackedTab.setText(((NewIndicatorWizard)getWizard()).getDefaultTab());
        else
            stackedTab.select(0);

        label = new Label(content, SWT.NONE);
        label.setLayoutData(new GridData(125, SWT.DEFAULT));
        newTab = new Button(content, SWT.RADIO);
        newTab.setText(Messages.IndicatorLocationPage_NewTab);
        newTab.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                stackedTab.setEnabled(false);
                rowNumber.setEnabled(true);
                tabLabel.setEnabled(true);
                if (tabLabel.getText().length() == 0)
                {
                    String name = ((NewIndicatorWizard)getWizard()).getIndicatorPage().getIndicatorName();
                    if (name.indexOf(" - ") != -1) //$NON-NLS-1$
                    {
                        name = name.substring(name.indexOf(" - ") + 3); //$NON-NLS-1$
                        tabLabel.setText(name);
                    }
                }
                validatePage();
            }
        });
        rowNumber = new Spinner(content, SWT.BORDER);
        rowNumber.setMinimum(1);
        rowNumber.setMaximum(chart.getRows().size());
        rowNumber.setSelection(((NewIndicatorWizard)getWizard()).getDefaultRow());
        
        label = new Label(content, SWT.NONE);
        label.setLayoutData(new GridData(125, SWT.DEFAULT));
        newRow = new Button(content, SWT.RADIO);
        newRow.setText(Messages.IndicatorLocationPage_NewRow);
        newRow.setLayoutData(new GridData(GridData.FILL, GridData.FILL, false, false, 2, 1));
        newRow.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                stackedTab.setEnabled(false);
                rowNumber.setEnabled(false);
                tabLabel.setEnabled(true);
                if (tabLabel.getText().length() == 0)
                {
                    String name = ((NewIndicatorWizard)getWizard()).getIndicatorPage().getIndicatorName();
                    if (name.indexOf(" - ") != -1) //$NON-NLS-1$
                    {
                        name = name.substring(name.indexOf(" - ") + 3); //$NON-NLS-1$
                        tabLabel.setText(name);
                    }
                }
                validatePage();
            }
        });
        
        label = new Label(content, SWT.NONE);
        label.setText(Messages.IndicatorLocationPage_TabLabel);
        label.setLayoutData(new GridData(125, SWT.DEFAULT));
        tabLabel = new Text(content, SWT.BORDER);
        tabLabel.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, false, 2, 1));
        tabLabel.addModifyListener(new ModifyListener() {
            public void modifyText(ModifyEvent e)
            {
                validatePage();
            }
        });
        
        stacked.setSelection(true);
        stackedTab.setEnabled(true);
        rowNumber.setEnabled(false);
        tabLabel.setEnabled(false);
        setPageComplete(true);
    }
    
    private void validatePage()
    {
        if (newTab.getSelection() || newRow.getSelection())
        {
            if (tabLabel.getText().length() == 0)
                setPageComplete(false);
        }

        setPageComplete(true);
    }

    public boolean getNewRow()
    {
        return newRow.getSelection();
    }

    public void setNewRow()
    {
        this.newRow.setSelection(true);
    }

    public boolean getNewTab()
    {
        return newTab.getSelection();
    }

    public void setNewTab()
    {
        this.newTab.setSelection(true);
    }

    public int getRowNumber()
    {
        return rowNumber.getSelection();
    }

    public void setRowNumber(int rowNumber)
    {
        this.rowNumber.setSelection(rowNumber);
    }

    public boolean getStacked()
    {
        return stacked.getSelection();
    }

    public void setStacked()
    {
        this.stacked.setSelection(true);
    }

    public ChartTab getStackedTab()
    {
        return (ChartTab)stackedTab.getData(String.valueOf(stackedTab.getSelectionIndex()));
    }

    public void setStackedTab(String stackedTab)
    {
        this.stackedTab.setText(stackedTab);
    }

    public String getTabLabel()
    {
        return tabLabel.getText();
    }

    public void setTabLabel(String tabLabel)
    {
        this.tabLabel.setText(tabLabel);
    }
}
