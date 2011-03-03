package de.janrufmonitor.ui.jface.application.comment.action;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.IService;
import de.janrufmonitor.service.comment.CommentService;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.comment.Comment;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTExecuter;

public class ManageAction extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.comment.action.ManageAction";
	
	private IRuntime m_runtime;

	public ManageAction() {
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
		return "comment_manage";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		IService s = this.getRuntime().getServiceFactory().getService("CommentService");
		if (s!=null && s instanceof CommentService) {
			CommentService cs = (CommentService)s;
			if (!cs.isEnabled()) {
				new SWTExecuter(true, this.getID()) {
					protected void execute() {
						MessageDialog.openError(
								new Shell(DisplayManager.getDefaultDisplay()),
								getI18nManager().getString(
										getNamespace(),
										"servicedisabled",
										"label",
										getLanguage()),
								getI18nManager().getString(
										getNamespace(),
										"servicedisabled",
										"description",
										getLanguage())
							);
					}
				}.start();
				return;
			}
		}
		
		
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null) {
			IStructuredSelection selection = (IStructuredSelection) v.getSelection();
			if (!selection.isEmpty()) {
				Object o = selection.getFirstElement();
				if (o instanceof ICall) {
					Object o2 = ((ICall)o).getCaller();
					if (((ICaller)o2).getPhoneNumber().isClired()) return;
					
					new Comment(((ICall)o)).open();
				}
				if (o instanceof ICaller) {
					if (((ICaller)o).getPhoneNumber().isClired()) return;
					
					new Comment(((ICaller)o)).open();
				}
			}
		}
	}

}
