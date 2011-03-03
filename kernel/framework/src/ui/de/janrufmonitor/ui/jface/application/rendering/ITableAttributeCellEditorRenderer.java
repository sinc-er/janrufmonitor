package de.janrufmonitor.ui.jface.application.rendering;

import de.janrufmonitor.framework.IAttribute;

public interface ITableAttributeCellEditorRenderer extends ITableCellEditorRenderer {

	public void applyAttributeChanges(Object o, IAttribute att, Object value);
	public IAttribute getAttribute();
	
}
