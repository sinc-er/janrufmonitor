package de.janrufmonitor.ui.jface.application.comment;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.command.AbstractCommand;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.IService;
import de.janrufmonitor.service.comment.CommentService;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTExecuter;

public class CommentCommand extends AbstractCommand {

	private static String NAMESPACE = "ui.jface.application.comment.Comment";
	
	private IRuntime m_runtime;
	private boolean isExecuting;
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void execute() throws Exception {
		this.isExecuting = true;
		
		IService s = this.getRuntime().getServiceFactory().getService("CommentService");
		if (s!=null && s instanceof CommentService) {
			CommentService cs = (CommentService)s;
			if (!cs.isEnabled()) {
				new SWTExecuter(true, this.getID()) {
					protected void execute() {
						MessageDialog.openError(
								new Shell(DisplayManager.getDefaultDisplay()),
								m_i18n.getString(
										getNamespace(),
										"servicedisabled",
										"label",
										m_language),
								m_i18n.getString(
										getNamespace(),
										"servicedisabled",
										"description",
										m_language)
							);
					}
				}.start();
				this.isExecuting = false;
				return;
			}
			
			final ICaller c = cs.getLastCaller();
			if (c==null) {
				new SWTExecuter(true, this.getID()) {
					protected void execute() {
						MessageDialog.openError(
								new Shell(DisplayManager.getDefaultDisplay()),
								m_i18n.getString(
										getNamespace(),
										"nocaller",
										"label",
										m_language),
								m_i18n.getString(
										getNamespace(),
										"nocaller",
										"description",
										m_language)
							);
					}
				}.start();

				this.isExecuting = false;
				return;
			}
			if (c.getPhoneNumber().isClired()) {
				new SWTExecuter(true, this.getID()) {
					protected void execute() {
						MessageDialog.openError(
								new Shell(DisplayManager.getDefaultDisplay()),
								m_i18n.getString(
										getNamespace(),
										"clircaller",
										"label",
										m_language),
								m_i18n.getString(
										getNamespace(),
										"clircaller",
										"description",
										m_language)
							);
					}
				}.start();
			} else {
				new SWTExecuter(true, this.getID()) {
					protected void execute() {
						new Comment(c).open();
					} 
				}.start();				
			}
		} else {
			this.m_logger.warning("Service CommentService not available.");
		}
		
		this.isExecuting = false;
	}

	public boolean isExecutable() {
		return !this.isExecuting();
	}

	public boolean isExecuting() {
		return this.isExecuting;
	}

	public String getID() {
		return "CommentCommand";
	}

}
