package de.janrufmonitor.ui.jface.application.rendering;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.ITreeItemCallerData;
import de.janrufmonitor.util.formatter.Formatter;

public class Number extends AbstractTableCellRenderer implements IJournalCellRenderer, IEditorCellRenderer {

	private static String NAMESPACE = "ui.jface.application.rendering.Number";
	
	private Formatter m_f;
	
	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller();
			}
			if (this.m_o instanceof ICaller) {
				return getFormatter().parse(IJAMConst.GLOBAL_VARIABLE_CALLERNUMBER, (ICaller)this.m_o);
			}	
			if (this.m_o instanceof IPhonenumber) {
				return getFormatter().parse(IJAMConst.GLOBAL_VARIABLE_CALLERNUMBER, (IPhonenumber)this.m_o);
			}
			if (this.m_o instanceof ITreeItemCallerData) {
				return getFormatter().parse(IJAMConst.GLOBAL_VARIABLE_CALLERNUMBER, ((ITreeItemCallerData)this.m_o).getPhone());
			}				
		}
		return "";
	}
	
	public String getID() {
		return "Number".toLowerCase();
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
