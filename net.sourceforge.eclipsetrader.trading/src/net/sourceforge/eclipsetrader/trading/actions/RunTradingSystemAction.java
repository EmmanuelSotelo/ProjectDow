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

package net.sourceforge.eclipsetrader.trading.actions;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.eclipsetrader.core.CorePlugin;
import net.sourceforge.eclipsetrader.core.IHistoryFeed;
import net.sourceforge.eclipsetrader.core.db.trading.TradingSystem;
import net.sourceforge.eclipsetrader.trading.TradingPlugin;
import net.sourceforge.eclipsetrader.trading.TradingSystemPlugin;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;

public class RunTradingSystemAction implements IViewActionDelegate
{

    public RunTradingSystemAction()
    {
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IViewActionDelegate#init(org.eclipse.ui.IViewPart)
     */
    public void init(IViewPart view)
    {
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#run(org.eclipse.jface.action.IAction)
     */
    public void run(IAction action)
    {
        Job job = new Job("Updating Trading System Signals") {
            protected IStatus run(IProgressMonitor monitor)
            {
                List list = CorePlugin.getRepository().getTradingSystems();
                monitor.beginTask("Updating Trading System Signals", list.size());
                
                for (Iterator iter = list.iterator(); iter.hasNext(); )
                {
                    TradingSystem system = (TradingSystem) iter.next();
                    
                    if (system.getSecurity().getHistoryFeed() != null)
                    {
                        String id = system.getSecurity().getHistoryFeed().getId();
                        IHistoryFeed feed = CorePlugin.createHistoryFeedPlugin(id);
                        monitor.subTask(system.getSecurity().getDescription() + " - Updating history");
                        feed.updateHistory(system.getSecurity(), IHistoryFeed.INTERVAL_DAILY);
                    }
                    
                    monitor.subTask(system.getSecurity().getDescription() + " - Running trading system");
                    TradingSystemPlugin plugin = TradingPlugin.createTradingSystemPlugin(system);
                    plugin.run();

                    monitor.worked(1);
                    if (monitor.isCanceled())
                        break;
                }
                monitor.done();
                return Status.OK_STATUS;
            }
        };
        job.setUser(true);
        job.schedule();
    }

    /* (non-Javadoc)
     * @see org.eclipse.ui.IActionDelegate#selectionChanged(org.eclipse.jface.action.IAction, org.eclipse.jface.viewers.ISelection)
     */
    public void selectionChanged(IAction action, ISelection selection)
    {
    }
}
