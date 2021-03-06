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

package net.sourceforge.eclipsetrader.charts.events;

import net.sourceforge.eclipsetrader.core.db.ChartObject;

public class ObjectSelection extends TabSelection
{
    private ChartObject object;

    public ObjectSelection(ChartObject object)
    {
        super(object.getParent());
        this.object = object;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.ISelection#isEmpty()
     */
    public boolean isEmpty()
    {
        return object == null;
    }

    public ChartObject getObject()
    {
        return object;
    }
}
