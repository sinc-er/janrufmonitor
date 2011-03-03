package de.janrufmonitor.ui.jface.application.journal.rendering;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IJournalCellRenderer;
import de.janrufmonitor.util.formatter.Formatter;

public class Msn extends AbstractTableCellRenderer implements IJournalCellRenderer {

	private static String NAMESPACE = "ui.jface.application.journal.rendering.Msn";
	
	private Formatter m_f;
	
	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				return getFormatter().parse(IJAMConst.GLOBAL_VARIABLE_MSNFORMAT, ((ICall)this.m_o).getMSN());
			}
		}
		return "";
	}
	
	public String getID() {
		return "Msn".toLowerCase();
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	private Formatter getFormatter() {
		if (this.m_f==null) {
			this.m_f = Formatter.getInstance(PIMRuntime.getInstance());
		}
		return this.m_f;
	}
}
