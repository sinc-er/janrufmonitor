package de.janrufmonitor.ui.jface.application.comment.rendering;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.comment.api.IComment;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.util.formatter.Formatter;

public class CommentDate extends AbstractTableCellRenderer {

	private static String NAMESPACE = "ui.jface.application.comment.rendering.CommentDate";

	private Formatter m_f;
	
	public String renderAsText() {
		if (this.m_o!=null) {

			if (this.m_o instanceof IComment) {
				return getFormatter().parse(IJAMConst.GLOBAL_VARIABLE_CALLTIME, ((IComment)m_o).getDate());
			}
		}
		return "";
	}

	public String getID() {
		return "CommentDate".toLowerCase();
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
