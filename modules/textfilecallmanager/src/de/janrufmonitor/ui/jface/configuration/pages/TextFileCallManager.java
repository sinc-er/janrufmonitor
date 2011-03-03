package de.janrufmonitor.ui.jface.configuration.pages;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractServiceFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;

public class TextFileCallManager extends AbstractServiceFieldEditorConfigPage {

	private String NAMESPACE = "ui.jface.configuration.pages.TextFileCallManager";
    private String CONFIG_NAMESPACE = "repository.TextFileCallManager";

    private IRuntime m_runtime;
    
	public String getConfigNamespace() {
		return this.CONFIG_NAMESPACE;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null) 
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	public String getParentNodeID() {
		return IConfigPage.JOURNAL_NODE;
	}

	public String getNodeID() {
		return "TextFileCallManager".toLowerCase();
	}

	public int getNodePosition() {
		return 10;
	}

	protected void createFieldEditors() {
		super.createFieldEditors();
		
//		FileDialogFieldEditor dfe = new FileDialogFieldEditor(
//			this.CONFIG_NAMESPACE+SEPARATOR+"database",
//			this.m_i18n.getString(this.getNamespace(), "database", "label", this.m_language),
//			this.getFieldEditorParent()
//		);
//		dfe.setFileExtensions(new String[]{"*.dat"});
//		addField(dfe);
	}
}
