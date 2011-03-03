package de.janrufmonitor.ui.jface.application.editor.rendering;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IName;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IEditorCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IJournalCellRenderer;
import de.janrufmonitor.util.formatter.Formatter;

public class Name extends AbstractTableCellRenderer implements IJournalCellRenderer, IEditorCellRenderer {

	private static String NAMESPACE = "ui.jface.application.editor.rendering.Name";
	private Formatter m_f;
	
	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller();
			}
			if (this.m_o instanceof ICaller) {
				IName n = ((ICaller)this.m_o).getName();
				if (n.getAdditional().length()>0 && n.getFirstname().length()==0 && n.getLastname().length()==0)
					return getFormatter().parse(
							"%a:add%", n);
				
				return getFormatter().parse(
						"%a:ln%, %a:fn% (%a:add%)", n);
			}
		}
		return "";
	}
	
	public String getID() {
		return "Name2".toLowerCase();
	}

	public String getNamespace() {
		return NAMESPACE;
	}
	
	private Formatter getFormatter() {
		if (this.m_f == null) {
			this.m_f = Formatter.getInstance(PIMRuntime.getInstance());
		}
		return this.m_f;
	}
}

	