package de.janrufmonitor.ui.jface.application.editor.action;

import java.util.logging.Level;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.OutlookContactProxy;
import de.janrufmonitor.repository.OutlookContactProxyException;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.editor.EditorConfigConst;

public class OutlookOpenAction extends AbstractAction implements EditorConfigConst {
	
	private static String NAMESPACE = "ui.jface.application.editor.action.OutlookOpenAction";
	
	private IRuntime m_runtime;
	public OutlookOpenAction() {
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
	
	public boolean isEnabled() {
		ICallerManager c = getRuntime().getCallerManagerFactory().getCallerManager("OutlookCallerManager");
		return (c!=null && c.isActive());
	}

	public String getID() {
		return "editor_outlookopen";
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
				if (o instanceof ICall) {
					o = ((ICall)o).getCaller();
				}
				if (o instanceof ICaller) {
					OutlookContactProxy opx = new OutlookContactProxy();
					try {
						opx.openContact((ICaller) o);
					} catch (OutlookContactProxyException e) {
						this.m_logger.log(Level.SEVERE, e.getMessage(), e);
					}
				}
			}
		}
	}
}

