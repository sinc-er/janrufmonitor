package de.janrufmonitor.ui.jface.application.comment.action;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.comment.api.IComment;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.comment.Comment;
import de.janrufmonitor.ui.jface.dialogs.CommentDialog;
import de.janrufmonitor.ui.swt.DisplayManager;

public class NewAction extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.comment.action.NewAction";
	
	private IRuntime m_runtime;

	public NewAction() {
		super();
		this.setText(
			this.getI18nManager().getString(
				this.getNamespace(),
				"title",
				"label",
				this.getLanguage()
			)
		);
	}
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "comment_new";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		Object callableObject = null;
		if (this.m_app instanceof Comment) {
			callableObject = ((Comment)this.m_app).getCallableObject();
		}
		CommentDialog cd = new CommentDialog(DisplayManager.getDefaultDisplay().getActiveShell(), null, callableObject);
		int result = cd.open();
		if (result == CommentDialog.OK) {
			IComment c = cd.getResult();
			this.m_app.getController().updateElement(c);
			this.m_app.updateViews(true);
		}
	}

}
