package de.janrufmonitor.ui.jface.application.action;

import org.eclipse.swt.SWT;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.ApplicationImageDescriptor;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class RefreshAction extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.action.RefreshAction";
	
	private IRuntime m_runtime;

	public RefreshAction() {
		super();
		this.setText(
			this.getI18nManager().getString(
				this.getNamespace(),
				"title",
				"label",
				this.getLanguage()
			)
		);
		this.setAccelerator(SWT.F5);
		this.setImageDescriptor(new ApplicationImageDescriptor(
			SWTImageManager.getInstance(this.getRuntime()).getImagePath(IJAMConst.IMAGE_KEY_REFRESH_GIF)
		));				
	}
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "refresh";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		this.m_app.getApplication().initializeController();
		this.m_app.updateViews(true);
	}
}
