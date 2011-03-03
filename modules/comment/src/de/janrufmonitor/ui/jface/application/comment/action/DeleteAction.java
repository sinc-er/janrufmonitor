package de.janrufmonitor.ui.jface.application.comment.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.comment.api.IComment;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.swt.DisplayManager;

public class DeleteAction extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.comment.action.DeleteAction";
	
	private IRuntime m_runtime;
	
	public DeleteAction() {
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
		return "comment_delete";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null) {
			IStructuredSelection selection = (IStructuredSelection) v.getSelection();
			if (!selection.isEmpty()) {
				Object o = selection.getFirstElement();
				if (o instanceof IComment) {
					List l = new ArrayList();
					l.add(o);
					if (MessageDialog.openConfirm(
							new Shell(DisplayManager.getDefaultDisplay()),
							this.getI18nManager().getString(this.getNamespace(), "delete", "label", this.getLanguage()),
							this.getI18nManager().getString(this.getNamespace(), "delete", "description", this.getLanguage())
						)) {
					this.m_app.getController().deleteElements(l);
					this.m_app.getApplication().initializeController();
					this.m_app.updateViews(true);
					}
				}
			}
		}
	}
	
}
