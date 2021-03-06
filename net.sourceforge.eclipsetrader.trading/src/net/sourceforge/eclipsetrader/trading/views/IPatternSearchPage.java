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

package net.sourceforge.eclipsetrader.trading.views;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;

public interface IPatternSearchPage
{

    public abstract String getDescription();

    public abstract String getShortDescription();

    public abstract void run(IProgressMonitor monitor);

    public abstract List getResults();
}