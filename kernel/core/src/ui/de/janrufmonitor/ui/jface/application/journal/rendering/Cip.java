package de.janrufmonitor.ui.jface.application.journal.rendering;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IJournalCellRenderer;

public class Cip extends AbstractTableCellRenderer implements IJournalCellRenderer {

	private static String NAMESPACE = "ui.jface.application.journal.rendering.Cip";

	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				return ((ICall)this.m_o).getCIP().getAdditional();
			}
		}
		return "";
	}
	
	public String getID() {
		return "Cip".toLowerCase();
	}

	public String getNamespace() {
		return NAMESPACE;
	}
}
