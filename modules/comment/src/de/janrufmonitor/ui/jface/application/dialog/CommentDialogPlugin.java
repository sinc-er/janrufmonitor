package de.janrufmonitor.ui.jface.application.dialog;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.service.IService;
import de.janrufmonitor.service.comment.CommentService;
import de.janrufmonitor.ui.jface.application.comment.Comment;
import de.janrufmonitor.ui.jface.application.dialog.AbstractDialogPlugin;
import de.janrufmonitor.ui.swt.SWTExecuter;

public class CommentDialogPlugin extends AbstractDialogPlugin {

	public String getLabel() {
		return this.getI18nManager().getString("ui.jface.application.dialog.CommentDialogPlugin", "label", "label", this.getLanguage());
	}

	public void run() {
		new SWTExecuter(this.getLabel()) {
			protected void execute() {
				ICall c = m_dialog.getCall();
				if (!c.getCaller().getPhoneNumber().isClired())
					new Comment(c).open();
			}
		}.start();
	}

	public boolean isEnabled() {
		if (this.m_dialog.getCall().getCaller().getPhoneNumber().isClired()) return false;
		
		IService s = this.getRuntime().getServiceFactory().getService("CommentService");
		if (s!=null && s instanceof CommentService) {
			CommentService cs = (CommentService)s;
			return cs.isEnabled();
		}
		return false;
	}

}
