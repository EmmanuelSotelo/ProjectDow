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

package net.sourceforge.eclipsetrader.ats.core.events;

public interface IBarListener {

	/**
	 * Called on leading edge of every bar.
	 */
	public void barOpen(BarEvent e);

	/**
	 * Called on trailing edge of every bar.
	 */
	public void barClose(BarEvent e);
}
