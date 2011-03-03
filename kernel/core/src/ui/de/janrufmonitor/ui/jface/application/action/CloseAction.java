package de.janrufmonitor.ui.jface.application.action;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;

public class CloseAction extends AbstractAction {
	
	private static String NAMESPACE = "ui.jface.application.action.CloseAction";
	
	private IRuntime m_runtime;
	
	public CloseAction() {
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
		return "close";
	}

	public String getNamespace() {
		return NAMESPACE;
	}
	
	public void run() {
		if (this.m_app!=null) {
			this.m_app.getApplication().close();
		}
	}
}
