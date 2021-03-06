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

package net.sourceforge.eclipsetrader.preferences;

import net.sourceforge.eclipsetrader.EclipseTraderPlugin;
import net.sourceforge.eclipsetrader.core.CorePlugin;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class StartupShutdownPreferencesPage extends PreferencePage implements IWorkbenchPreferencePage
{
    private Button minimizeToTray;
    private Button confirmExit;
    private Button updateHistoryData;
    private Button updateOnce;
    private Button updateNews;

    /* (non-Javadoc)
     * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench)
    {
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    protected Control createContents(Composite parent)
    {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.numColumns = 2;
        gridLayout.marginWidth = gridLayout.marginHeight = 0;
        content.setLayout(gridLayout);
        
        updateHistoryData = new Button(content, SWT.CHECK);
        updateHistoryData.setText(Messages.getString("StartupShutdownPreferencesPage.UpdateHistoryDataText")); //$NON-NLS-1$
        updateHistoryData.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));
        updateHistoryData.setSelection(CorePlugin.getDefault().getPreferenceStore().getBoolean(CorePlugin.PREFS_UPDATE_HISTORY));
        updateHistoryData.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
                updateOnce.setEnabled(updateHistoryData.getSelection());
            }
        });
        
        Label label = new Label(content, SWT.NONE);
        label.setLayoutData(new GridData(26, SWT.NONE));
        
        updateOnce = new Button(content, SWT.CHECK);
        updateOnce.setText(Messages.getString("StartupShutdownPreferencesPage.UpdateOnceText")); //$NON-NLS-1$
        updateOnce.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));
        updateOnce.setSelection(CorePlugin.getDefault().getPreferenceStore().getBoolean(CorePlugin.PREFS_UPDATE_HISTORY_ONCE));
        updateOnce.setEnabled(CorePlugin.getDefault().getPreferenceStore().getBoolean(CorePlugin.PREFS_UPDATE_HISTORY));

        updateNews = new Button(content, SWT.CHECK);
        updateNews.setText(Messages.getString("StartupShutdownPreferencesPage.UpdateNewsText")); //$NON-NLS-1$
        updateNews.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));
        updateNews.setSelection(CorePlugin.getDefault().getPreferenceStore().getBoolean(CorePlugin.PREFS_UPDATE_NEWS));
        
        minimizeToTray = new Button(content, SWT.CHECK);
        minimizeToTray.setText(Messages.getString("StartupShutdownPreferencesPage.MinimizeToTrayText")); //$NON-NLS-1$
        minimizeToTray.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));
        minimizeToTray.setSelection(EclipseTraderPlugin.getDefault().getPreferenceStore().getBoolean(EclipseTraderPlugin.MINIMIZE_TO_TRAY));
        
        confirmExit = new Button(content, SWT.CHECK);
        confirmExit.setText(Messages.getString("StartupShutdownPreferencesPage.ConfirmExitText")); //$NON-NLS-1$
        confirmExit.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false, 2, 1));
        confirmExit.setSelection(EclipseTraderPlugin.getDefault().getPreferenceStore().getBoolean(EclipseTraderPlugin.PROMPT_ON_EXIT));

        return content;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferencePage#performOk()
     */
    public boolean performOk()
    {
        EclipseTraderPlugin.getDefault().getPreferenceStore().setValue(EclipseTraderPlugin.PROMPT_ON_EXIT, confirmExit.getSelection());
        EclipseTraderPlugin.getDefault().getPreferenceStore().setValue(EclipseTraderPlugin.MINIMIZE_TO_TRAY, minimizeToTray.getSelection());
        
        CorePlugin.getDefault().getPreferenceStore().setValue(CorePlugin.PREFS_UPDATE_HISTORY, updateHistoryData.getSelection());
        CorePlugin.getDefault().getPreferenceStore().setValue(CorePlugin.PREFS_UPDATE_HISTORY_ONCE, updateOnce.getSelection());
        CorePlugin.getDefault().getPreferenceStore().setValue(CorePlugin.PREFS_UPDATE_NEWS, updateNews.getSelection());
        
        return super.performOk();
    }
}
