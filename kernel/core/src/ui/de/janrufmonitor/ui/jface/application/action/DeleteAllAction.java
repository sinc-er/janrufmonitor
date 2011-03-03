package de.janrufmonitor.ui.jface.application.action;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.ModalContext;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.manager.IRepositoryManager;
import de.janrufmonitor.repository.types.IWriteCallRepository;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.ApplicationImageDescriptor;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class DeleteAllAction extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.action.DeleteAllAction";
	
	private IRuntime m_runtime;

	public DeleteAllAction() {
		super();
		this.setText(
			this.getI18nManager().getString(
				this.getNamespace(),
				"title",
				"label",
				this.getLanguage()
			)
		);
		this.setImageDescriptor(new ApplicationImageDescriptor(
			SWTImageManager.getInstance(this.getRuntime()).getImagePath(IJAMConst.IMAGE_KEY_DELETE_GIF)
		));	
	}
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "delete_all";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		if (MessageDialog.openConfirm(
				new Shell(DisplayManager.getDefaultDisplay()),
				this.getI18nManager().getString(this.getNamespace(), "delete", "label", this.getLanguage()),
				this.getI18nManager().getString(this.getNamespace(), "delete", "description", this.getLanguage())
			)) {
			
			try {
		        // Launch the Save runnable
		        ModalContext.run(new IRunnableWithProgress() {
		          public void run(IProgressMonitor progressMonitor) {
		            progressMonitor.beginTask(
		            	getI18nManager().getString(getNamespace(), "deleteprogress", "label", getLanguage()), 
						IProgressMonitor.UNKNOWN);
		           
					m_app.getController().deleteAllElements();
					m_app.getApplication().initializeController();
					
		            progressMonitor.done();
		          }
		        }, true, this.m_app.getApplication().getStatusLineManager().getProgressMonitor(), this.m_app.getApplication().getShell()
		            .getDisplay());
		      } catch (InterruptedException e) {} 
		        catch (InvocationTargetException e) {} 
		        finally {
		        	m_app.updateViews(true);
		      }

		}
	}

	public boolean isEnabled() {
		if (this.m_app!=null && this.m_app.getController()!=null) {
			Object o = this.m_app.getController().getRepository();
			if (o instanceof IRepositoryManager) {
				return (((IRepositoryManager)o).isSupported(IWriteCallerRepository.class) || ((IRepositoryManager)o).isSupported(IWriteCallRepository.class));
			}
		}
		return false;
	}
}
