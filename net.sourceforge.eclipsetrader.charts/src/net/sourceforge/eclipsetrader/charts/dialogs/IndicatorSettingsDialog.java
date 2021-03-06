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

package net.sourceforge.eclipsetrader.charts.dialogs;

import java.util.Iterator;

import net.sourceforge.eclipsetrader.charts.ChartsPlugin;
import net.sourceforge.eclipsetrader.charts.IndicatorPluginPreferencePage;
import net.sourceforge.eclipsetrader.charts.Settings;
import net.sourceforge.eclipsetrader.charts.internal.Messages;
import net.sourceforge.eclipsetrader.core.db.ChartIndicator;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.swt.widgets.Shell;

/**
 */
public class IndicatorSettingsDialog extends PreferenceDialog
{

    public IndicatorSettingsDialog(ChartIndicator indicator, Shell parentShell)
    {
        super(parentShell, new PreferenceManager());

        Settings settings = new Settings();
        for (Iterator iter = indicator.getParameters().keySet().iterator(); iter.hasNext(); )
        {
            String key = (String)iter.next();
            settings.set(key, (String)indicator.getParameters().get(key));
        }
        
        IConfigurationElement plugin = ChartsPlugin.getIndicatorPlugin(indicator.getPluginId());
        IConfigurationElement[] members = plugin.getChildren("preferencePage"); //$NON-NLS-1$
        if (members.length != 0)
        {
            IConfigurationElement item = members[0];
            try {
                Object obj = item.createExecutableExtension("class"); //$NON-NLS-1$
                if (obj instanceof IndicatorPluginPreferencePage)
                {
                    ((IndicatorPluginPreferencePage)obj).setSettings(settings);
                    IndicatorPage page = new IndicatorPage(indicator, (IndicatorPluginPreferencePage)obj);
                    if (item.getAttribute("title") != null) //$NON-NLS-1$
                        page.setTitle(item.getAttribute("title")); //$NON-NLS-1$

                    getPreferenceManager().addToRoot(new PreferenceNode("indicator", page)); //$NON-NLS-1$
                }

                for (int p = 1; p < members.length; p++)
                {
                    obj = item.createExecutableExtension("class"); //$NON-NLS-1$
                    if (obj instanceof IndicatorPluginPreferencePage)
                    {
                        ((IndicatorPluginPreferencePage)obj).setSettings(settings);
                        IndicatorPage page = new IndicatorPage(indicator, (IndicatorPluginPreferencePage)obj);
                        if (item.getAttribute("title") != null) //$NON-NLS-1$
                            page.setTitle(item.getAttribute("title")); //$NON-NLS-1$

                        getPreferenceManager().addToRoot(new PreferenceNode("prefs" + p, page)); //$NON-NLS-1$
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.preference.PreferenceDialog#configureShell(org.eclipse.swt.widgets.Shell)
     */
    protected void configureShell(Shell newShell)
    {
        super.configureShell(newShell);
        newShell.setText(Messages.IndicatorSettingsDialog_Title);
    }
}
