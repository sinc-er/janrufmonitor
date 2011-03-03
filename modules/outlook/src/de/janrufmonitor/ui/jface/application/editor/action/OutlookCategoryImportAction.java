package de.janrufmonitor.ui.jface.application.editor.action;

import java.util.List;

import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.OutlookContactProxy;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.ActionRegistry;
import de.janrufmonitor.ui.jface.application.action.IAction;
import de.janrufmonitor.ui.jface.application.editor.EditorConfigConst;

public class OutlookCategoryImportAction extends AbstractAction implements
		EditorConfigConst {
	
	private static String NAMESPACE = "ui.jface.application.editor.action.OutlookCategoryImportAction";
	private IRuntime m_runtime;
	
	public OutlookCategoryImportAction() {
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
		return "editor_outlookcategoryimport";
	}

	public String getNamespace() {
		return NAMESPACE;
	}
	
	public void run() {
		OutlookContactProxy ocp = new OutlookContactProxy();
		List subfolders = ocp.getAllContactFolders();
		if (subfolders!=null && subfolders.size()>0) {
			ocp.ensureEditorConfigurationCatergories(subfolders);
			IAction cat = ActionRegistry.getInstance().getAction("editor_category", this.m_app);
			cat.setApplication(this.m_app);
			cat.run();
		}
	}

}