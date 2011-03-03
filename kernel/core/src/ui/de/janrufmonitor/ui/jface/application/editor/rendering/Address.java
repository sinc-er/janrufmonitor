package de.janrufmonitor.ui.jface.application.editor.rendering;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IEditorCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IJournalCellRenderer;
import de.janrufmonitor.util.formatter.Formatter;

public class Address extends AbstractTableCellRenderer implements IJournalCellRenderer, IEditorCellRenderer {

	private static String NAMESPACE = "ui.jface.application.editor.rendering.Address";
	
	private Formatter m_f;
	
	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller();
			}
			if (this.m_o instanceof ICaller) {
				return getFormatter().parse("%a:str% %a:no%%CRLF%%a:pcode% %a:city%%CRLF%%a:cntry%", ((ICaller)this.m_o).getAttributes());
			}
		}
		return "";
	}
	
	public String getID() {
		return "Address".toLowerCase();
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

	