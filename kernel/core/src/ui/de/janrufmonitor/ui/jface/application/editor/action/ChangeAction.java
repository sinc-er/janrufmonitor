package de.janrufmonitor.ui.jface.application.editor.action;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IMultiPhoneCaller;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class ChangeAction extends CallerAction {

	private static String NAMESPACE = "ui.jface.application.editor.action.ChangeAction";
	
	private IRuntime m_runtime;
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "editor_change";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null && v instanceof TreeViewer) {
			IStructuredSelection selection = (IStructuredSelection) v.getSelection();
			if (!selection.isEmpty()) {
				if (selection.getFirstElement() instanceof IMultiPhoneCaller) {
					ICaller caller = (IMultiPhoneCaller) selection.getFirstElement();
					
					ICaller newCaller = openCallerWizard(caller);
					if (newCaller!=null) {
//						ICallerList list = this.getRuntime().getCallerFactory().createCallerList(1);
//						list.add(caller);
//						this.m_app.getController().deleteElements(list);
//						
//						list.clear();
//						list.add(newCaller);
//						
//						this.m_app.getController().addElements(list);
						
						ICallerList list = this.getRuntime().getCallerFactory().createCallerList(1);
						list.add(newCaller);
						
						this.m_app.getController().updateElement(list);
						this.m_app.updateViews(true);
					}
				} else if (selection.getFirstElement() instanceof ICaller) {
					ICaller caller = (ICaller) selection.getFirstElement();
					
					ICaller newCaller = openCallerWizard(caller);
					if (newCaller!=null) {
						ICallerList list = this.getRuntime().getCallerFactory().createCallerList(1);
						list.add(caller);
						this.m_app.getController().deleteElements(list);
						
						list.clear();
						list.add(newCaller);
						
						this.m_app.getController().addElements(list);
						this.m_app.updateViews(true);
					}
				}			
			}
		}
	}

	public boolean isEnabled() {
		if (this.m_app!=null && this.m_app.getController()!=null) {
			Object o = this.m_app.getController().getRepository();
			if (o instanceof ICallerManager) {
				return ((ICallerManager)o).isSupported(IWriteCallerRepository.class);
			}
		}
		return false;
	}
}
