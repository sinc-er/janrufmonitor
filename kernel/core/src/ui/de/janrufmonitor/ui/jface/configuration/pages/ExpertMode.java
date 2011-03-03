package de.janrufmonitor.ui.jface.configuration.pages;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;
import de.janrufmonitor.ui.jface.configuration.controls.BooleanFieldEditor;

public class ExpertMode extends AbstractFieldEditorConfigPage {
	
    private String NAMESPACE = "ui.jface.configuration.pages.ExpertMode";
    
	private IRuntime m_runtime;
	
	public String getParentNodeID() {
		return IConfigPage.ROOT_NODE;
	}
	
	public String getNodeID() {
		return "ExpertMode".toLowerCase();
	}

	public int getNodePosition() {
		return 998;
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
		return IJAMConst.GLOBAL_NAMESPACE;
	}
	
	protected void createFieldEditors() {
		this.setTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));

		this.noDefaultAndApplyButton();
		
		BooleanFieldEditor sfe = new BooleanFieldEditor(
			this.getConfigNamespace()+SEPARATOR+IJAMConst.GLOBAL_CONFIG_EXPERT_MODE,
			this.m_i18n.getString(this.getNamespace(), IJAMConst.GLOBAL_CONFIG_EXPERT_MODE, "label", this.m_language),
			this.getFieldEditorParent()
		);
		addField(sfe);
	}

}
