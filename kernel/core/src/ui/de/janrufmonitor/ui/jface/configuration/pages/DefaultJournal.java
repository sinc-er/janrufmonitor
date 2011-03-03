package de.janrufmonitor.ui.jface.configuration.pages;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractServiceFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;

public class DefaultJournal extends AbstractServiceFieldEditorConfigPage {
	
    private String NAMESPACE = "ui.jface.configuration.pages.DefaultJournal";
    private String CONFIG_NAMESPACE = "repository.DefaultJournal";
    
	private IRuntime m_runtime;
	
	public String getParentNodeID() {
		return IConfigPage.JOURNAL_NODE;
	}
	
	public String getNodeID() {
		return "DefaultJournal".toLowerCase();
	}

	public int getNodePosition() {
		return 1;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null) 
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	public String getConfigNamespace() {
		return this.CONFIG_NAMESPACE;
	}
	
	protected void createFieldEditors() {
		super.createFieldEditors();
		//this.setTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));
		
//		if (isExpertMode()) {
//			FileDialogFieldEditor dfe = new FileDialogFieldEditor(
//				this.CONFIG_NAMESPACE+SEPARATOR+"db",
//				this.m_i18n.getString(this.getNamespace(), "db", "label", this.m_language),
//				this.getFieldEditorParent()
//			);
//			dfe.setFileExtensions(new String[]{"*.db"});
//			addField(dfe);
//		}
	}

}
