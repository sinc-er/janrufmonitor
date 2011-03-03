package de.janrufmonitor.ui.jface.application.rendering;

public interface ITableCellEditorRenderer extends ITableCellRenderer {
	
	public final static int TYPE_TEXT = 0;
	public final static int TYPE_COMBO = 1;
	public final static int TYPE_CHECKBOX = 2;
	
	public boolean isEditable();
	
	public int getType();
	
	public String[] getValues();
	
}
