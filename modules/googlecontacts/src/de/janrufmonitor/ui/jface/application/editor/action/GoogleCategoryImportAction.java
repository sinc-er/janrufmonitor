package de.janrufmonitor.ui.jface.application.editor.action;

import java.util.List;

import de.janrufmonitor.repository.GoogleContactsProxy;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.ActionRegistry;
import de.janrufmonitor.ui.jface.application.action.IAction;
import de.janrufmonitor.ui.jface.application.editor.EditorConfigConst;

public class GoogleCategoryImportAction extends AbstractAction implements
		EditorConfigConst {
	
	private static String NAMESPACE = "ui.jface.application.editor.action.GoogleCategoryImportAction";
	private IRuntime m_runtime;
	
	public GoogleCategoryImportAction() {
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
		ICallerManager c = getRuntime().getCallerManagerFactory().getCallerManager(de.janrufmonitor.repository.GoogleContactsCallerManager.ID);
		return (c!=null && c.isActive());
	}

	public String getID() {
		return "editor_googlecategoryimport";
	}

	public String getNamespace() {
		return NAMESPACE;
	}
	
	public void run() {
		GoogleContactsProxy ocp = new GoogleContactsProxy();
		List subfolders = ocp.getCategories();
		if (subfolders!=null && subfolders.size()>0) {
			ocp.ensureEditorConfigurationCatergories(subfolders);
			IAction cat = ActionRegistry.getInstance().getAction("editor_category", this.m_app);
			cat.setApplication(this.m_app);
			cat.run();
		}
	}

}