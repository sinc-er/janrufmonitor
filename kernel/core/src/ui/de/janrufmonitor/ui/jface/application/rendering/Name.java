package de.janrufmonitor.ui.jface.application.rendering;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.formatter.Formatter;

public class Name extends AbstractTableCellRenderer implements IJournalCellRenderer, IEditorCellRenderer {

	private static String NAMESPACE = "ui.jface.application.rendering.Name";
	
	private Formatter m_f;
	
	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller();
			}
			if (this.m_o instanceof ICaller) {
				return getFormatter().parse(IJAMConst.GLOBAL_VARIABLE_CALLERNAME, (ICaller)this.m_o);
			}
		}
		return "";
	}
	
	public String getID() {
		return "Name".toLowerCase();
	}

	private Formatter getFormatter() {
		if (this.m_f==null) {
			this.m_f = Formatter.getInstance(PIMRuntime.getInstance());
		}
		return this.m_f;
	}

	public String getNamespace() {
		return NAMESPACE;
	}
}
