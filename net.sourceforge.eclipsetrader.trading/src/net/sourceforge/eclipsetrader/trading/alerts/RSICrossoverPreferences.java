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

package net.sourceforge.eclipsetrader.trading.alerts;

import java.util.HashMap;
import java.util.Map;

import net.sourceforge.eclipsetrader.trading.AlertPluginPreferencePage;

import org.eclipse.jface.preference.ColorSelector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

public class RSICrossoverPreferences extends AlertPluginPreferencePage
{
    private Combo interval;
    private Spinner period;
    private Spinner level;
    private Combo direction;
    private Label description;
    private ColorSelector color;
    private SelectionAdapter selectionAdapter = new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e)
        {
            updateDescription();
        }
    };

    public RSICrossoverPreferences()
    {
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.trading.AlertPluginPreferencePage#createContents(org.eclipse.swt.widgets.Composite)
     */
    public Control createContents(Composite parent)
    {
        Composite content = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout(2, false);
        content.setLayout(gridLayout);
        content.setLayoutData(new GridData(GridData.FILL, GridData.BEGINNING, true, false));

        Label label = new Label(content, SWT.NONE);
        label.setText("Interval");
        label.setLayoutData(new GridData(125, SWT.DEFAULT));
        interval = new Combo(content, SWT.READ_ONLY);
        interval.add("Daily");
        interval.add("Weekly");
        interval.add("Monthly");
        interval.setVisibleItemCount(15);
        interval.addSelectionListener(selectionAdapter);
        
        label = new Label(content, SWT.NONE);
        label.setText("Period");
        label.setLayoutData(new GridData(125, SWT.DEFAULT));
        period = new Spinner(content, SWT.BORDER);
        period.setMinimum(0);
        period.setMaximum(99999);
        period.addSelectionListener(selectionAdapter);
        
        label = new Label(content, SWT.NONE);
        label.setText("Direction");
        label.setLayoutData(new GridData(125, SWT.DEFAULT));
        direction = new Combo(content, SWT.READ_ONLY);
        direction.add("Upward");
        direction.add("Downward");
        direction.setVisibleItemCount(15);
        direction.addSelectionListener(selectionAdapter);
        
        label = new Label(content, SWT.NONE);
        label.setText("Level");
        level = new Spinner(content, SWT.BORDER);
        level.setMinimum(0);
        level.setMaximum(99999);
        level.addSelectionListener(selectionAdapter);
        
        label = new Label(content, SWT.NONE);
        label.setText("Hilight Color");
        label.setLayoutData(new GridData(125, SWT.DEFAULT));
        color = new ColorSelector(content);

        description = new Label(content, SWT.NONE);
        description.setLayoutData(new GridData(SWT.FILL, SWT.END, true, true, 2, 1));

        int field = MovingAverageCrossover.DAILY;
        String value = (String)getParameters().get("interval");
        if (value != null)
            field = Integer.parseInt(value);
        interval.select(field - MovingAverageCrossover.DAILY);
        
        value = (String)getParameters().get("level");
        level.setSelection(value != null ? Integer.parseInt(value) : 80);
        
        value = (String)getParameters().get("period");
        period.setSelection(value != null ? Integer.parseInt(value) : 7);
        
        field = MovingAverageCrossover.UPWARD;
        value = (String)getParameters().get("direction");
        if (value != null)
            field = Integer.parseInt(value);
        direction.select(field);

        value = (String)getParameters().get("hilightBackground");
        if (value != null)
        {
            String[] ar = value.split(",");
            color.setColorValue(new RGB(Integer.parseInt(ar[0]), Integer.parseInt(ar[1]), Integer.parseInt(ar[2])));
        }
        else
            color.setColorValue(new RGB(0, 224, 0));
        
        updateDescription();
        
        return content;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.trading.AlertPluginPreferencePage#performOk()
     */
    public void performOk()
    {
        Map parameters = new HashMap();

        parameters.put("interval", String.valueOf(interval.getSelectionIndex() + MovingAverageCrossover.DAILY));
        parameters.put("period", String.valueOf(period.getSelection()));
        parameters.put("level", String.valueOf(level.getSelection()));
        parameters.put("direction", String.valueOf(direction.getSelectionIndex()));
        RGB rgb = color.getColorValue();
        parameters.put("hilightBackground", String.valueOf(rgb.red) + "," + String.valueOf(rgb.green) + "," + String.valueOf(rgb.blue));
        
        setParameters(parameters);
    }

    private void updateDescription()
    {
        String s = "When the " + String.valueOf(period.getSelection());
        switch(interval.getSelectionIndex() + MovingAverageCrossover.DAILY)
        {
            case MovingAverageCrossover.DAILY:
                s += " days";
                break; 
            case MovingAverageCrossover.WEEKLY:
                s += " weeks";
                break; 
            case MovingAverageCrossover.MONTHLY:
                s += " months";
                break; 
        }
        s += " RSI goes";
        if (direction.getSelectionIndex() == MovingAverageCrossover.UPWARD)
            s += " upward";
        else if (direction.getSelectionIndex() == MovingAverageCrossover.DOWNWARD)
            s += " downward";
        s += " " + String.valueOf(level.getSelection());
        
        description.setText(s);
        description.getParent().layout();
    }
}
