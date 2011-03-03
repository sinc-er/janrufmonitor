package de.janrufmonitor.ui.jface.application.comment.rendering;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.service.comment.api.IComment;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;

public class CommentStatus extends AbstractTableCellRenderer {

	private static String NAMESPACE = "ui.jface.application.comment.rendering.CommentStatus";

	public String renderAsText() {
		if (this.m_o!=null && this.m_o instanceof IComment) {
			IAttribute a = ((IComment)m_o).getAttributes().get(IComment.COMMENT_ATTRIBUTE_STATUS);			
			if (a!=null) {
				return a.getValue();
			}
		}
		return "";
	}

	public String getID() {
		return "CommentStatus".toLowerCase();
	}
	
	public String getNamespace() {
		return NAMESPACE;
	}
	
}
