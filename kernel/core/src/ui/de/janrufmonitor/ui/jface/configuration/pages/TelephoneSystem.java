package de.janrufmonitor.ui.jface.configuration.pages;

import org.eclipse.jface.preference.StringFieldEditor;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;

public class TelephoneSystem extends AbstractFieldEditorConfigPage {
	
    private String NAMESPACE = "ui.jface.configuration.pages.TelephoneSystem";
    private String CONFIG_NAMESPACE = "repository.TelephoneSystemCallerManager";
    
	private IRuntime m_runtime;
	
	public String getParentNodeID() {
		return IConfigPage.ADVANCED_NODE;
	}
	
	public String getNodeID() {
		return "TelephoneSystem".toLowerCase();
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
		this.setTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));

		StringFieldEditor sfe = new StringFieldEditor(
			IJAMConst.GLOBAL_NAMESPACE+SEPARATOR+IJAMConst.GLOBAL_INTERNAL_LENGTH,
			this.m_i18n.getString(this.getNamespace(), IJAMConst.GLOBAL_INTERNAL_LENGTH, "label", this.m_language),
			5,
			this.getFieldEditorParent()
		);
		addField(sfe);
		
		sfe = new StringFieldEditor(
			IJAMConst.GLOBAL_NAMESPACE+SEPARATOR+IJAMConst.GLOBAL_TELEPHONESYSTEM_PREFIX,
			this.m_i18n.getString(this.getNamespace(), IJAMConst.GLOBAL_TELEPHONESYSTEM_PREFIX, "label", this.m_language),
			5,
			this.getFieldEditorParent()
		);
		addField(sfe);
		
		sfe = new StringFieldEditor(
			IJAMConst.GLOBAL_NAMESPACE+SEPARATOR+IJAMConst.GLOBAL_AREACODE_ADD_LENGTH,
			this.m_i18n.getString(this.getNamespace(), IJAMConst.GLOBAL_AREACODE_ADD_LENGTH, "label", this.m_language),
			5,
			this.getFieldEditorParent()
		);
		addField(sfe);
	}

}
