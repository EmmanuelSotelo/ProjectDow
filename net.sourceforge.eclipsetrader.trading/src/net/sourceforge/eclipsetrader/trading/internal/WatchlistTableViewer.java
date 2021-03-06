/*
 * Copyright (c) 2004-2007 Marco Maccaferri and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Marco Maccaferri - initial API and implementation
 *     Danilo Tuler     - column selection
 */

package net.sourceforge.eclipsetrader.trading.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import net.sourceforge.eclipsetrader.core.IEditableLabelProvider;
import net.sourceforge.eclipsetrader.core.db.Security;
import net.sourceforge.eclipsetrader.core.db.WatchlistColumn;
import net.sourceforge.eclipsetrader.core.db.WatchlistItem;
import net.sourceforge.eclipsetrader.core.transfers.SecurityTransfer;
import net.sourceforge.eclipsetrader.core.transfers.WatchlistItemTransfer;
import net.sourceforge.eclipsetrader.core.ui.NullSelection;
import net.sourceforge.eclipsetrader.core.ui.widgets.EditableTable;
import net.sourceforge.eclipsetrader.core.ui.widgets.EditableTableColumn;
import net.sourceforge.eclipsetrader.core.ui.widgets.IEditableItem;
import net.sourceforge.eclipsetrader.trading.WatchlistColumnSelection;
import net.sourceforge.eclipsetrader.trading.WatchlistItemSelection;
import net.sourceforge.eclipsetrader.trading.actions.ToggleShowTotalsAction;
import net.sourceforge.eclipsetrader.trading.internal.watchlist.ColumnRegistry;
import net.sourceforge.eclipsetrader.trading.views.WatchlistView;
import net.sourceforge.eclipsetrader.trading.wizards.WatchlistItemPropertiesDialog;

import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.PreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.themes.ITheme;
import org.eclipse.ui.themes.IThemeManager;

/**
 */
public class WatchlistTableViewer extends AbstractLayout
{
    public static final String EVEN_ROWS_BACKGROUND = "TABLE_EVEN_ROWS_BACKGROUND";
    public static final String EVEN_ROWS_FOREGROUND = "TABLE_EVEN_ROWS_FOREGROUND";
    public static final String ODD_ROWS_BACKGROUND = "TABLE_ODD_ROWS_BACKGROUND";
    public static final String ODD_ROWS_FOREGROUND = "TABLE_ODD_ROWS_FOREGROUND";
    public static final String TOTALS_ROWS_BACKGROUND = "TABLE_TOTALS_ROWS_BACKGROUND";
    public static final String TOTALS_ROWS_FOREGROUND = "TABLE_TOTALS_ROWS_FOREGROUND";
    public static final String TICK_BACKGROUND = "TABLE_TICK_BACKGROUND";
    public static final String POSITIVE_FOREGROUND = "TABLE_POSITIVE_FOREGROUND";
    public static final String NEGATIVE_FOREGROUND = "TABLE_NEGATIVE_FOREGROUND";
    public static final String ALERT_BACKGROUND = "TABLE_ALERT_BACKGROUND";
    List list = new ArrayList();
    Composite content;
    EditableTable table;
    Color evenForeground;
    Color evenBackground;
    Color oddForeground;
    Color oddBackground;
    Color totalsForeground;
    Color totalsBackground;
    Color tickBackground;
    Color negativeForeground;
    Color positiveForeground;
    Color alertHilightBackground;
    boolean showTotals = false;
    boolean singleClick = true;
    int sortColumn = -1;
    int sortDirection = 0;
    Action toggleShowTotals;
    Action autoResizeAction;
    Action propertiesAction;
    WatchlistColumn selectedColumn;
    IPropertyChangeListener themeChangeListener = new IPropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent event)
        {
            if (event.getProperty().equals(IThemeManager.CHANGE_CURRENT_THEME))
            {
                ((ITheme) event.getOldValue()).removePropertyChangeListener(this);
                setTheme((ITheme) event.getNewValue());
            }
            else
            {
                IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
                ITheme theme = themeManager.getCurrentTheme();

                if (event.getProperty().equals(EVEN_ROWS_BACKGROUND))
                    evenBackground = theme.getColorRegistry().get(event.getProperty());
                else if (event.getProperty().equals(EVEN_ROWS_FOREGROUND))
                    evenForeground = theme.getColorRegistry().get(event.getProperty());
                else if (event.getProperty().equals(ODD_ROWS_BACKGROUND))
                    oddBackground = theme.getColorRegistry().get(event.getProperty());
                else if (event.getProperty().equals(ODD_ROWS_FOREGROUND))
                    oddForeground = theme.getColorRegistry().get(event.getProperty());
                else if (event.getProperty().equals(TOTALS_ROWS_FOREGROUND))
                    totalsForeground = theme.getColorRegistry().get(event.getProperty());
                else if (event.getProperty().equals(TOTALS_ROWS_FOREGROUND))
                    totalsForeground = theme.getColorRegistry().get(event.getProperty());
                else if (event.getProperty().equals(POSITIVE_FOREGROUND))
                    positiveForeground = theme.getColorRegistry().get(event.getProperty());
                else if (event.getProperty().equals(NEGATIVE_FOREGROUND))
                    negativeForeground = theme.getColorRegistry().get(event.getProperty());
                else if (event.getProperty().equals(TICK_BACKGROUND))
                    tickBackground = theme.getColorRegistry().get(event.getProperty());
                else if (event.getProperty().equals(ALERT_BACKGROUND))
                    alertHilightBackground = theme.getColorRegistry().get(event.getProperty());
            }
            updateView();
        }
    };
    Comparator comparator = new Comparator() {
        public int compare(Object arg0, Object arg1)
        {
            Object provider = table.getColumn(sortColumn).getData("labelProvider");
            if (provider instanceof Comparator)
            {
                if (sortDirection == 0)
                    return ((Comparator)provider).compare(arg0, arg1);
                else
                    return ((Comparator)provider).compare(arg1, arg0);
            }
            return 0;
        }
    };
    SelectionListener columnSelectionListener = new SelectionAdapter() {
        public void widgetSelected(SelectionEvent e)
        {
            TableColumn tableColumn = (TableColumn) e.widget;
            WatchlistColumn column = (WatchlistColumn)tableColumn.getData();
            
            int index = table.indexOf(tableColumn);
            if (index == sortColumn)
            {
                if (sortDirection == 1)
                    sortColumn = -1;
                else
                    sortDirection = 1;
            }
            else
            {
                sortColumn = index;
                sortDirection = 0;
            }

            if (sortColumn != -1)
            {
                String s = column.getId() + ";" + String.valueOf(sortDirection);
                getView().getPreferenceStore().setValue(WatchlistView.PREFS_SORTING, s);
            }
            else
                getView().getPreferenceStore().setValue(WatchlistView.PREFS_SORTING, "");
            updateView();
        }
    };
    ControlAdapter columnControlListener = new ControlAdapter() {
        public void controlResized(ControlEvent e)
        {
            TableColumn tableColumn = (TableColumn)e.widget;
            WatchlistColumn column = (WatchlistColumn)tableColumn.getData();
            getView().getPreferenceStore().setValue(column.getId() + ".width", tableColumn.getWidth());
        }
    };
   
    public WatchlistTableViewer(WatchlistView view)
    {
        super(view);
        
        showTotals = getView().getPreferenceStore().getBoolean(WatchlistView.PREFS_SHOW_TOTALS);
        toggleShowTotals = new ToggleShowTotalsAction(this);
        toggleShowTotals.setChecked(showTotals);

        IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
        menuManager.appendToGroup("toggles", toggleShowTotals);
        
        autoResizeAction = new Action() {
            public void run()
            {
                TableColumn[] column = table.getColumns();
                for (int i = 0; i < column.length; i++)
                {
                    if (column[i].getResizable())
                        column[i].pack();
                }
            }
        };
        autoResizeAction.setId("autoResizeColumns");
        autoResizeAction.setText("Resize columns");
        menuManager.appendToGroup("layout", autoResizeAction);
        
        propertiesAction = new Action() {
            public void run()
            {
                TableItem[] selection = table.getSelection();
                if (selection.length != 0 && selection[0] instanceof WatchlistTableItem)
                {
                    WatchlistItem item = ((WatchlistTableItem) selection[0]).getWatchlistItem();
                    WatchlistItemPropertiesDialog dlg = new WatchlistItemPropertiesDialog(item, getViewSite().getShell());
                    dlg.open();
                }
                
            }
        };
        propertiesAction.setText("Properties");
        propertiesAction.setEnabled(false);

        IActionBars actionBars = getViewSite().getActionBars();
        actionBars.setGlobalActionHandler("properties", propertiesAction);
        actionBars.updateActionBars();
    }
    
    public Composite createPartControl(Composite parent)
    {
        content = new Composite(parent, SWT.NONE);
        GridLayout gridLayout = new GridLayout();
        gridLayout.marginWidth = gridLayout.marginHeight = 0;
        content.setLayout(gridLayout);
        
        IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
        themeManager.addPropertyChangeListener(themeChangeListener);
        ITheme theme = themeManager.getCurrentTheme();
        setTheme(theme);

        table = new EditableTable(content, SWT.MULTI|SWT.FULL_SELECTION);
        table.setHeaderVisible(true);
        table.setLinesVisible(false);
        table.setBackground(parent.getBackground());
        table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
        table.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e)
            {
            	if (singleClick)
                    updateSelection();
            }
        });
        table.addMouseListener(new MouseAdapter() {

			public void mouseDown(MouseEvent e)
            {
				if (singleClick)
				{
	            	selectedColumn = getColumn(e);
	                if (table.getItem(new Point(e.x, e.y)) == null)
	                {
	                    table.deselectAll();
                        updateSelection();
	                }
				}
            }
			
			public void mouseDoubleClick(MouseEvent e)
			{
				if (!singleClick)
				{
	            	selectedColumn = getColumn(e);
	                if (table.getItem(new Point(e.x, e.y)) == null)
	                {
	                    table.deselectAll();
	                }
                    updateSelection();
				}
			}
        });
        // TODO This is a workaround for the sort column background color
        table.addListener(SWT.EraseItem, new Listener() {
            public void handleEvent(Event event)
            {
                event.gc.setBackground(((TableItem)event.item).getBackground());
                event.gc.fillRectangle(event.getBounds());
            }
        });
        TableColumn column = new TableColumn(table, SWT.NONE);
        column.setWidth(0);
        column.setResizable(false);
        
        // Drag and drop support
        DragSource dragSource = new DragSource(table, DND.DROP_COPY|DND.DROP_MOVE);
        dragSource.setTransfer(new Transfer[] { SecurityTransfer.getInstance(), WatchlistItemTransfer.getInstance(), TextTransfer.getInstance() });
        dragSource.addDragListener(new DragSourceListener() {
            WatchlistItem[] watchlistSelection;
            
            public void dragStart(DragSourceEvent event)
            {
                if (table.getSelectionCount() == 0)
                    event.doit = false;
            }

            public void dragSetData(DragSourceEvent event)
            {
                TableItem[] selection = table.getSelection();
                watchlistSelection = new WatchlistItem[selection.length];

                if (SecurityTransfer.getInstance().isSupportedType(event.dataType))
                {
                    Security[] securities = new Security[selection.length];
                    for (int i = 0; i < selection.length; i++)
                    {
                        WatchlistTableItem item = (WatchlistTableItem)selection[i];
                        securities[i] = item.getWatchlistItem().getSecurity();
                        watchlistSelection[i] = item.getWatchlistItem();
                    }
                    event.data = securities;
                }
                else if (WatchlistItemTransfer.getInstance().isSupportedType(event.dataType))
                {
                    for (int i = 0; i < selection.length; i++)
                    {
                        WatchlistTableItem item = (WatchlistTableItem)selection[i];
                        watchlistSelection[i] = item.getWatchlistItem();
                    }
                    event.data = watchlistSelection;
                }
                else if (TextTransfer.getInstance().isSupportedType(event.dataType))
                {
                    StringBuffer data = new StringBuffer();
                    for (int i = 0; i < selection.length; i++)
                    {
                        WatchlistTableItem item = (WatchlistTableItem)selection[i];
                        for (int c = 1; c < table.getColumnCount(); c++)
                        {
                            if (c > 1)
                                data.append(";");
                            data.append(item.getText(c));
                        }
                        data.append("\r\n");
                        watchlistSelection[i] = item.getWatchlistItem();
                    }
                    event.data = data.toString();
                }
            }

            public void dragFinished(DragSourceEvent event)
            {
                if (event.doit && event.detail == DND.DROP_MOVE && watchlistSelection != null)
                {
                    for (int i = 0; i < watchlistSelection.length; i++)
                        getView().getWatchlist().getItems().remove(watchlistSelection[i]);
                }
                watchlistSelection = null;
            }
        });

        MenuManager menuMgr = new MenuManager("#popupMenu", "popupMenu"); //$NON-NLS-1$ //$NON-NLS-2$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new IMenuListener() {
            public void menuAboutToShow(IMenuManager menuManager)
            {
                menuManager.add(new Separator("top")); //$NON-NLS-1$
                menuManager.add(new Separator("search")); //$NON-NLS-1$
                getView().fillMenuBars(menuManager);
                menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
                menuManager.add(new Separator("bottom")); //$NON-NLS-1$
                menuManager.add(propertiesAction);
            }
        });
        table.setMenu(menuMgr.createContextMenu(table));
        getView().getSite().registerContextMenu(menuMgr, getView().getSite().getSelectionProvider());

        return content;
    }

	/* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
     */
    void setFocus()
    {
        table.getParent().setFocus();
    }
    
    /* (non-Javadoc)
     * @see org.eclipse.ui.part.WorkbenchPart#dispose()
     */
    public void dispose()
    {
        IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
        themeManager.removePropertyChangeListener(themeChangeListener);
        ITheme theme = themeManager.getCurrentTheme();
        theme.removePropertyChangeListener(themeChangeListener);

        IActionBars actionBars = getViewSite().getActionBars();
        actionBars.setGlobalActionHandler("properties", null);

        IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
        menuManager.remove(toggleShowTotals.getId());
        menuManager.remove(autoResizeAction.getId());
        
        if (content != null)
            content.dispose();
    }

    public void updateView()
    {
        int index;
        TableColumn tableColumn;

        String[] items = getView().getPreferenceStore().getString(WatchlistView.PREFS_SORTING).split(";");
        if (items.length != 2)
            items = new String[] { "", "0" };
        sortDirection = new Integer(items[1]).intValue();

        index = 1;
        for (Iterator iter = getView().getWatchlist().getColumns().iterator(); iter.hasNext(); )
        {
            WatchlistColumn column = (WatchlistColumn)iter.next();
            String name = "";
            int style = SWT.LEFT;
            Image image = null;

            ILabelProvider provider = ColumnRegistry.createLabelProvider(column.getId());
            if (provider != null)
            {
                LogFactory.getLog(getClass()).debug("Adding column [" + column.getId() + "]");
                name = ColumnRegistry.getName(column.getId());
                style = ColumnRegistry.getOrientation(column.getId());
                
                if (provider instanceof ITableLabelProvider)
                {
                    name = ((ITableLabelProvider)provider).getColumnText(getView().getWatchlist(), index);
                    image = ((ITableLabelProvider)provider).getColumnImage(getView().getWatchlist(), index);
                }
            }
            else
                LogFactory.getLog(getClass()).warn("Missing column [" + column.getId() + "]");
            
            if (index < table.getColumnCount())
            {
                tableColumn = table.getColumn(index);
                tableColumn.setAlignment(style);
                if (tableColumn.getData("labelProvider") != null)
                    ((ILabelProvider)tableColumn.getData("labelProvider")).dispose();
            }
            else
            {
                tableColumn = new EditableTableColumn(table, style);
                tableColumn.addControlListener(columnControlListener);
                tableColumn.addDisposeListener(new DisposeListener() {
                    public void widgetDisposed(DisposeEvent e)
                    {
                        if (e.widget.getData("labelProvider") != null)
                            ((ILabelProvider)e.widget.getData("labelProvider")).dispose();
                    }
                });
                tableColumn.addSelectionListener(columnSelectionListener);
            }
            tableColumn.setText(name);
            tableColumn.setImage(image);
            tableColumn.setData(column);
            tableColumn.setData("labelProvider", provider);

            if (column.getId().equals(items[0]))
                sortColumn = index;
            
            index++;
        }
        
        while(index < table.getColumnCount())
            table.getColumn(index).dispose();

        list = new ArrayList(getView().getWatchlist().getItems());
        if (sortColumn >= 1 && sortColumn < table.getColumnCount())
        {
            table.setSortColumn(table.getColumn(sortColumn));
            table.setSortDirection(sortDirection == 0 ? SWT.UP : SWT.DOWN);
            Collections.sort(list, comparator);
        }
        else
        {
            table.setSortColumn(null);
            sortColumn = -1;
            sortDirection = 0;
        }

        updateTableContents();

        PreferenceStore preferences = getView().getPreferenceStore(); 
        for (int i = 1; i < table.getColumnCount(); i++)
        {
            WatchlistColumn column = (WatchlistColumn)table.getColumn(i).getData();
            preferences.setDefault(column.getId() + ".width", -1);
            int size = preferences.getInt(column.getId() + ".width");
            if (size != -1)
                table.getColumn(i).setWidth(size);
            else
                table.getColumn(i).pack();
        }
        if ("gtk".equals(SWT.getPlatform()))
            table.getColumn(table.getColumnCount() - 1).pack();
    }
    
    public void updateTableContents()
    {
        WatchlistTableItem tableItem;

        int index = 0;
        for (Iterator iter = list.iterator(); iter.hasNext(); )
        {
            WatchlistItem watchlistItem = (WatchlistItem)iter.next();
            if (index < table.getItemCount())
            {
                if (table.getItem(index) instanceof WatchlistTableItem)
                {
                    tableItem = (WatchlistTableItem)table.getItem(index);
                    tableItem.setWatchlistItem(watchlistItem);
                }
                else
                    tableItem = new WatchlistTableItem(table, SWT.NONE, index, watchlistItem);
            }
            else
                tableItem = new WatchlistTableItem(table, SWT.NONE, index, watchlistItem);
            tableItem.setBackground(((index & 1) == 1) ? oddBackground : evenBackground);
            tableItem.setForeground(((index & 1) == 1) ? oddForeground : evenForeground);
            
            tableItem.ticker.setBackground(tickBackground);

            index++;
        }
        
        if (showTotals)
        {
            WatchlistTotalsTableItem item = null;
            if (index < table.getItemCount())
            {
                if (table.getItem(index) instanceof WatchlistTotalsTableItem)
                {
                    item = (WatchlistTotalsTableItem) table.getItem(index);
                    item.setWatchlistItem(getView().getWatchlist().getTotals());
                }
            }
            if (item == null)
                item = new WatchlistTotalsTableItem(table, SWT.NONE, getView().getWatchlist().getTotals());
            item.setBackground(totalsBackground);
            item.setForeground(totalsForeground);
            index++;
        }

        table.setItemCount(index);
    }

    public void update(Observable o, Object arg)
    {
        table.getDisplay().asyncExec(new Runnable() {
            public void run()
            {
                if (!table.isDisposed())
                    updateView();
            }
        });
    }
    
    private void updateSelection()
    {
        boolean enable = false;
        if (table.getSelectionCount() != 0 && table.getSelection()[0] instanceof WatchlistTableItem)
        {
            WatchlistTableItem tableItem = (WatchlistTableItem)table.getSelection()[0];
            if (selectedColumn != null)
                getView().getSite().getSelectionProvider().setSelection(new WatchlistColumnSelection(tableItem.getWatchlistItem(), selectedColumn));
            else
                getView().getSite().getSelectionProvider().setSelection(new WatchlistItemSelection(tableItem.getWatchlistItem()));
            enable = true;
        }
        else
            getView().getSite().getSelectionProvider().setSelection(new NullSelection());

        IActionBars actionBars = getViewSite().getActionBars();
        actionBars.getGlobalActionHandler("cut").setEnabled(enable);
        actionBars.getGlobalActionHandler("copy").setEnabled(enable);
        actionBars.getGlobalActionHandler("delete").setEnabled(enable);
        actionBars.getGlobalActionHandler("properties").setEnabled(enable);
    }

    public void itemAdded(Object o)
    {
        if (o instanceof WatchlistItem)
        {
            int index = getView().getWatchlist().getItems().indexOf(o);
            list.add(index >= 0 ? index : list.size(), o);
            if (sortColumn >= 0)
                Collections.sort(list, comparator);
            updateView();
        }
/*        {
            WatchlistItem watchlistItem = (WatchlistItem)o;
            int index = table.getItemCount();
            if (showTotals)
                index--;
            WatchlistTableItem tableItem = new WatchlistTableItem(table, SWT.NONE, index, watchlistItem);
            tableItem.setBackground(((index & 1) == 1) ? oddBackground : evenBackground);
        }*/
    }

    public void itemRemoved(Object o)
    {
        TableItem[] items = table.getItems();
        for (int i = 0; i < items.length; i++)
        {
            if (!(items[i] instanceof WatchlistTableItem))
                continue;
            WatchlistTableItem tableItem = (WatchlistTableItem)items[i];
            if (tableItem.getWatchlistItem().equals(o))
            {
                tableItem.dispose();
                for (; i < table.getItemCount() - 1; i++)
                    table.getItem(i).setBackground(((i & 1) == 1) ? oddBackground : evenBackground);
                break;
            }
        }
        
        list.remove(o);
        
        if (table.getSelectionCount() != 0)
        {
            WatchlistTableItem tableItem = (WatchlistTableItem)table.getSelection()[0];
            getView().getSite().getSelectionProvider().setSelection(new WatchlistItemSelection(tableItem.getWatchlistItem()));
        }
        else
            getView().getSite().getSelectionProvider().setSelection(new NullSelection());
    }
    
    public boolean isShowTotals()
    {
        return showTotals;
    }

    public void setShowTotals(boolean showTotals)
    {
        this.showTotals = showTotals;
        getView().getPreferenceStore().setValue(WatchlistView.PREFS_SHOW_TOTALS, showTotals);
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.trading.internal.AbstractLayout#getSelection()
     */
    public WatchlistItem[] getSelection()
    {
        TableItem selection[] = table.getSelection();
        WatchlistItem[] items = new WatchlistItem[selection.length];
        for (int i = 0; i < selection.length; i++)
        {
            WatchlistTableItem item = (WatchlistTableItem)selection[i];
            items[i] = item.getWatchlistItem();
        }
        return items;
    }

    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.trading.internal.AbstractLayout#tickAlert(net.sourceforge.eclipsetrader.core.db.WatchlistItem, org.eclipse.swt.graphics.RGB, org.eclipse.swt.graphics.RGB)
     */
    public void tickAlert(WatchlistItem watchlistItem, RGB foreground, RGB background)
    {
        TableItem[] items = table.getItems();
        for (int i = 0; i < items.length; i++)
        {
            WatchlistTableItem tableItem = (WatchlistTableItem)items[i];
            if (tableItem.getWatchlistItem().equals(watchlistItem))
            {
                Color fg = foreground != null ? new Color(null, foreground) : null;
                Color bg = background != null ? new Color(null, background) : new Color(null, alertHilightBackground.getRGB());
                tableItem.ticker.tick(bg, fg);
                if (bg != null)
                    bg.dispose();
                if (fg != null)
                    fg.dispose();
                break;
            }
        }
    }
    
    /* (non-Javadoc)
     * @see net.sourceforge.eclipsetrader.trading.internal.AbstractLayout#getItemIndex(org.eclipse.swt.graphics.Point)
     */
    public int getItemIndex(Point point)
    {
        TableItem tableItem = table.getItem(table.toControl(point)); 
        return tableItem != null ? table.indexOf(tableItem) : -1;
    }

    protected void setTheme(ITheme theme)
    {
        positiveForeground = theme.getColorRegistry().get(POSITIVE_FOREGROUND);
        negativeForeground = theme.getColorRegistry().get(NEGATIVE_FOREGROUND);
        evenForeground = theme.getColorRegistry().get(EVEN_ROWS_FOREGROUND);
        evenBackground = theme.getColorRegistry().get(EVEN_ROWS_BACKGROUND);
        oddForeground = theme.getColorRegistry().get(ODD_ROWS_FOREGROUND);
        oddBackground = theme.getColorRegistry().get(ODD_ROWS_BACKGROUND);
        totalsForeground = theme.getColorRegistry().get(TOTALS_ROWS_FOREGROUND);
        totalsBackground = theme.getColorRegistry().get(TOTALS_ROWS_BACKGROUND);
        tickBackground = theme.getColorRegistry().get(TICK_BACKGROUND);
        alertHilightBackground = theme.getColorRegistry().get(ALERT_BACKGROUND);
        theme.addPropertyChangeListener(themeChangeListener);
    }

    public Table getTable()
    {
        return table;
    }

    protected WatchlistColumn getColumn(MouseEvent e)
    {
        Point p = new Point(e.x, e.y);
        TableItem item = table.getItem(p);
        if (item != null)
        {
            for (int i = 0; i < table.getColumnCount(); i++)
            {
                Rectangle rect = item.getBounds(i);
                if (rect.contains(p))
                {
                    TableColumn column = table.getColumn(i);
                    if (column.getData() instanceof WatchlistColumn)
                        return (WatchlistColumn) column.getData();
                }
            }
        }

        return null;
    }
    
    private class WatchlistTableItem extends TableItem implements DisposeListener, Observer, IEditableItem
    {
        private WatchlistItem watchlistItem;
        private CellTicker ticker;

        WatchlistTableItem(Table parent, int style, int index, WatchlistItem watchlistItem)
        {
            super(parent, style, index);
            addDisposeListener(this);
            ticker = new CellTicker(this, CellTicker.BACKGROUND|CellTicker.FOREGROUND);
            setWatchlistItem(watchlistItem);
        }

        WatchlistTableItem(Table parent, int style, WatchlistItem watchlistItem)
        {
            super(parent, style);
            addDisposeListener(this);
            ticker = new CellTicker(this, CellTicker.BACKGROUND|CellTicker.FOREGROUND);
            setWatchlistItem(watchlistItem);
        }

        /* (non-Javadoc)
         * @see org.eclipse.swt.widgets.TableItem#checkSubclass()
         */
        protected void checkSubclass()
        {
        }
        
        void setWatchlistItem(WatchlistItem watchlistItem)
        {
            if (this.watchlistItem != null)
                this.watchlistItem.deleteObserver(this);
            this.watchlistItem = watchlistItem;
            
            update(false);
            
            this.watchlistItem.addObserver(this);
        }
        
        WatchlistItem getWatchlistItem()
        {
            return watchlistItem;
        }
        
        void update(boolean tick)
        {
            TableColumn[] columns = getParent().getColumns();
            for (int i = 1; i < columns.length; i++)
            {
                ILabelProvider provider = (ILabelProvider)columns[i].getData("labelProvider"); 
                if (provider == null)
                    continue;

                Image image = provider.getImage(watchlistItem);
                if (!tick || image != getImage(i))
                    setImage(i, image);

                String value = provider.getText(watchlistItem);
                if (!tick || !value.equals(getText(i)))
                {
                    setText(i, value);
                    if (value.startsWith("+"))
                        setForeground(i, positiveForeground);
                    else if (value.startsWith("-"))
                        setForeground(i, negativeForeground);
                    else
                        setForeground(i, null);
                    
                    if (tick)
                    {
                        if (provider instanceof IEditableLabelProvider)
                        {
                            if (!((IEditableLabelProvider)provider).isEditable(watchlistItem))
                                ticker.tick(i);
                        }
                        else
                            ticker.tick(i);
                    }
                }
            }
        }
        
        /* (non-Javadoc)
         * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
         */
        public void update(Observable o, Object arg)
        {
            getDisplay().asyncExec(new Runnable() {
                public void run()
                {
                    if (!isDisposed())
                        update(true);
                }
            });
        }

        /* (non-Javadoc)
         * @see net.sourceforge.eclipsetrader.core.ui.widgets.IEditableItem#canEdit(int)
         */
        public boolean canEdit(int index)
        {
            ILabelProvider provider = (ILabelProvider)getParent().getColumn(index).getData("labelProvider");
            if (provider != null && (provider instanceof IEditableLabelProvider))
                return ((IEditableLabelProvider)provider).isEditable(watchlistItem);
            return false;
        }

        /* (non-Javadoc)
         * @see net.sourceforge.eclipsetrader.core.ui.widgets.IEditableItem#isEditable()
         */
        public boolean isEditable()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see net.sourceforge.eclipsetrader.core.ui.widgets.IEditableItem#itemEdited(int, java.lang.String)
         */
        public void itemEdited(int index, String text)
        {
            ILabelProvider provider = (ILabelProvider)getParent().getColumn(index).getData("labelProvider");
            if (provider != null && (provider instanceof IEditableLabelProvider))
                ((IEditableLabelProvider)provider).setEditableText(watchlistItem, text);
        }

        /* (non-Javadoc)
         * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
         */
        public void widgetDisposed(DisposeEvent e)
        {
            if (watchlistItem != null)
                watchlistItem.deleteObserver(this);
            ticker.dispose();
        }
    }
    
    private class WatchlistTotalsTableItem extends TableItem implements DisposeListener, Observer, IEditableItem
    {
        private WatchlistItem watchlistItem;

        WatchlistTotalsTableItem(Table parent, int style, int index, WatchlistItem watchlistItem)
        {
            super(parent, style, index);
            addDisposeListener(this);
            setWatchlistItem(watchlistItem);
        }

        WatchlistTotalsTableItem(Table parent, int style, WatchlistItem watchlistItem)
        {
            super(parent, style);
            addDisposeListener(this);
            setWatchlistItem(watchlistItem);
        }

        /* (non-Javadoc)
         * @see org.eclipse.swt.widgets.TableItem#checkSubclass()
         */
        protected void checkSubclass()
        {
        }
        
        void setWatchlistItem(WatchlistItem watchlistItem)
        {
            if (this.watchlistItem != null)
                this.watchlistItem.deleteObserver(this);
            this.watchlistItem = watchlistItem;

            update();
            
            this.watchlistItem.addObserver(this);
        }
        
        WatchlistItem getWatchlistItem()
        {
            return watchlistItem;
        }
        
        void update()
        {
            TableColumn[] columns = getParent().getColumns();
            for (int i = 1; i < columns.length; i++)
            {
                ILabelProvider provider = (ILabelProvider)columns[i].getData("labelProvider"); 

                String value = provider.getText(watchlistItem.getParent());
                if (!value.equals(getText(i)))
                    setText(i, value);

                Image image = provider.getImage(watchlistItem.getParent());
                if (image != getImage(i))
                    setImage(i, image);
            }
        }
        
        /* (non-Javadoc)
         * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
         */
        public void update(Observable o, Object arg)
        {
            getDisplay().asyncExec(new Runnable() {
                public void run()
                {
                    if (!isDisposed())
                        update();
                }
            });
        }

        /* (non-Javadoc)
         * @see net.sourceforge.eclipsetrader.core.ui.widgets.IEditableItem#canEdit(int)
         */
        public boolean canEdit(int index)
        {
            return false;
        }

        /* (non-Javadoc)
         * @see net.sourceforge.eclipsetrader.core.ui.widgets.IEditableItem#isEditable()
         */
        public boolean isEditable()
        {
            return true;
        }

        /* (non-Javadoc)
         * @see net.sourceforge.eclipsetrader.core.ui.widgets.IEditableItem#itemEdited(int, java.lang.String)
         */
        public void itemEdited(int index, String text)
        {
        }

        /* (non-Javadoc)
         * @see org.eclipse.swt.events.DisposeListener#widgetDisposed(org.eclipse.swt.events.DisposeEvent)
         */
        public void widgetDisposed(DisposeEvent e)
        {
            if (watchlistItem != null)
                watchlistItem.deleteObserver(this);
        }
    }
}
