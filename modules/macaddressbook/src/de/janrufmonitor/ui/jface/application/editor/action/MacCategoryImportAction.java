package de.janrufmonitor.ui.jface.application.editor.action;


import java.util.List;

import de.janrufmonitor.macab.MacAddressBookProxy;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.ActionRegistry;
import de.janrufmonitor.ui.jface.application.action.IAction;
import de.janrufmonitor.ui.jface.application.editor.EditorConfigConst;

public class MacCategoryImportAction extends AbstractAction implements
		EditorConfigConst {
	
	private static String NAMESPACE = "ui.jface.application.editor.action.MacCategoryImportAction";
	private IRuntime m_runtime;
	
	public MacCategoryImportAction() {
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
		ICallerManager c = getRuntime().getCallerManagerFactory().getCallerManager("MacAddressBookManager");
		return (c!=null && c.isActive());
	}

	public String getID() {
		return "editor_maccategoryimport";
	}

	public String getNamespace() {
		return NAMESPACE;
	}
	
	public void run() {
		List subfolders = MacAddressBookProxy.getInstance().getCategories();
		if (subfolders!=null && subfolders.size()>0) {
			MacAddressBookProxy.getInstance().ensureEditorConfigurationCatergories(subfolders);
			IAction cat = ActionRegistry.getInstance().getAction("editor_category", this.m_app);
			cat.setApplication(this.m_app);
			cat.run();
		}
	}

}