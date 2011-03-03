package de.janrufmonitor.ui.jface.application.editor.rendering;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IEditorCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IJournalCellRenderer;

public class Machine extends AbstractTableCellRenderer implements IJournalCellRenderer, IEditorCellRenderer {

	private static String NAMESPACE = "ui.jface.application.editor.rendering.Machine";
	
	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller();
			}
			
			if (this.m_o instanceof ICaller) {
				ICaller c = (ICaller)this.m_o;
				if (c.getAttributes().contains(IJAMConst.ATTRIBUTE_NAME_MACHINE_NAME)) {
					String machine = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_MACHINE_NAME).getValue();
					if (c.getAttributes().contains(IJAMConst.ATTRIBUTE_NAME_MACHINE_IP)) {
						machine += " ("+
							c.getAttribute(IJAMConst.ATTRIBUTE_NAME_MACHINE_IP).getValue()
						+")";
					}
					return machine;
				}
			}
		}
		return "";
	}
	
	
	public String getID() {
		return "Machine".toLowerCase();
	}

	public String getNamespace() {
		return NAMESPACE;
	}

}
