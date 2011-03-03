package de.janrufmonitor.ui.jface.application.journal.action;

import java.util.Iterator;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.repository.ICallManager;
import de.janrufmonitor.repository.types.IWriteCallRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.ApplicationImageDescriptor;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class DeleteAction extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.journal.action.DeleteAction";
	
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
		return "journal_delete";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null && v instanceof TableViewer) {
			IStructuredSelection selection = (IStructuredSelection) v.getSelection();
			if (!selection.isEmpty()) {
				Iterator i = selection.iterator();
				ICallList list = getRuntime().getCallFactory().createCallList();
				Object o = null;
				while (i.hasNext()) {
					o = i.next();
					if (o instanceof ICall) {
						list.add((ICall)o);
					}
				}
				
				if (list.size()>0) {
					if (MessageDialog.openConfirm(
							new Shell(DisplayManager.getDefaultDisplay()),
							this.getI18nManager().getString(this.getNamespace(), "delete", "label", this.getLanguage()),
							this.getI18nManager().getString(this.getNamespace(), "delete", "description", this.getLanguage())
						)) {
						this.m_app.getController().deleteElements(list);
						this.m_app.getApplication().initializeController();
						this.m_app.updateViews(true);
					}
				}
			}
		}
	}
	
	public boolean isEnabled() {
		if (this.m_app!=null && this.m_app.getController()!=null) {
			Object o = this.m_app.getController().getRepository();
			if (o instanceof ICallManager) {
				return ((ICallManager)o).isSupported(IWriteCallRepository.class);
			}
		}
		return false;
	}
}
