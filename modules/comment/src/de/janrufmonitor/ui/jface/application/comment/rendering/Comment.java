package de.janrufmonitor.ui.jface.application.comment.rendering;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.IService;
import de.janrufmonitor.service.comment.CommentCallerHandler;
import de.janrufmonitor.service.comment.CommentService;
import de.janrufmonitor.service.comment.api.IComment;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IEditorCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IJournalCellRenderer;

public class Comment extends AbstractTableCellRenderer implements IJournalCellRenderer, IEditorCellRenderer {

	private static String NAMESPACE = "ui.jface.application.comment.rendering.Comment";

	private II18nManager m_i18n;
	private String m_language;
	private String m_i18nData;
	private CommentService m_commentService;

	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller();
			}
			
			if (this.m_o instanceof ICaller) {
				if (!((ICaller) this.m_o).getPhoneNumber().isClired() && this.hasCallerComment((ICaller) this.m_o)) {
					return this.getI18nYes();
				}
			}
			if (this.m_o instanceof IComment) {
				int maxtext = 0;
				try {
					maxtext = Integer.parseInt(PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty("service.CommentService", "maxtext"));
				} catch (Exception e) {
				}
				
				String t = ((IComment)m_o).getText();
				return (maxtext>0 ? t.substring(0, Math.min(maxtext, (t.length() > maxtext ? maxtext : t.length())))+"..." : t);
			}
		}
		return "";
	}

	public String getID() {
		return "Comment".toLowerCase();
	}
	
	private String getI18nYes() {
		if (this.m_i18nData==null)
			this.m_i18nData = this.getI18nManager().getString(getNamespace(), "yes", "label", this.getLanguage());
			
		return this.m_i18nData;
	}
	
	private boolean hasCallerComment(ICaller c) {
		if (this.m_commentService==null) {
			IService s = PIMRuntime.getInstance().getServiceFactory().getService("CommentService");
			if (s!=null && s instanceof CommentService)
			this.m_commentService = (CommentService)s;
		}

		if (this.m_commentService!=null) {
			CommentCallerHandler cch = this.m_commentService.getHandler();
			if (cch!=null) {
				return cch.hasCommentCaller(c);
			}
		}

		return false;
	}
	
	private II18nManager getI18nManager() {
		if (this.m_i18n==null) {
			this.m_i18n = PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager();
		}
		return this.m_i18n;
	}
	
	private String getLanguage() {
		if (this.m_language==null) {
			this.m_language = 
				PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(
					IJAMConst.GLOBAL_NAMESPACE,
					IJAMConst.GLOBAL_LANGUAGE
				);
		}
		return this.m_language;
	}

	public String getNamespace() {
		return NAMESPACE;
	}
	
}
