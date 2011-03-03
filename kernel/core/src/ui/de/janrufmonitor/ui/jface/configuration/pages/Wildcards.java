package de.janrufmonitor.ui.jface.configuration.pages;

import org.eclipse.jface.preference.StringFieldEditor;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;

public class Wildcards extends AbstractFieldEditorConfigPage {
	
    private String NAMESPACE = "ui.jface.configuration.pages.Wildcards";
    private String CONFIG_NAMESPACE = IJAMConst.GLOBAL_NAMESPACE;
    
	private IRuntime m_runtime;
	
	public String getParentNodeID() {
		return IConfigPage.ADVANCED_NODE;
	}
	
	public String getNodeID() {
		return "Wildcards".toLowerCase();
	}

	public int getNodePosition() {
		return 2;
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
			IJAMConst.GLOBAL_NAMESPACE+SEPARATOR+IJAMConst.GLOBAL_VARIABLE_CALLERNAME,
			this.m_i18n.getString(this.getNamespace(), IJAMConst.GLOBAL_VARIABLE_CALLERNAME, "label", this.m_language),
			this.getFieldEditorParent()
		);
		addField(sfe);
		
		sfe = new StringFieldEditor(
			IJAMConst.GLOBAL_NAMESPACE+SEPARATOR+IJAMConst.GLOBAL_VARIABLE_CALLERNUMBER,
			this.m_i18n.getString(this.getNamespace(), IJAMConst.GLOBAL_VARIABLE_CALLERNUMBER, "label", this.m_language),
			this.getFieldEditorParent()
		);
		addField(sfe);
	
		sfe = new StringFieldEditor(
			IJAMConst.GLOBAL_NAMESPACE+SEPARATOR+IJAMConst.GLOBAL_VARIABLE_CALLTIME,
			this.m_i18n.getString(this.getNamespace(), IJAMConst.GLOBAL_VARIABLE_CALLTIME, "label", this.m_language),
			this.getFieldEditorParent()
		);
		addField(sfe);
		
		sfe = new StringFieldEditor(
			IJAMConst.GLOBAL_NAMESPACE+SEPARATOR+IJAMConst.GLOBAL_VARIABLE_MSNFORMAT,
			this.m_i18n.getString(this.getNamespace(), IJAMConst.GLOBAL_VARIABLE_MSNFORMAT, "label", this.m_language),
			this.getFieldEditorParent()
		);
		addField(sfe);
		
		sfe = new StringFieldEditor(
			IJAMConst.GLOBAL_NAMESPACE+SEPARATOR+IJAMConst.GLOBAL_VARIABLE_DATE,
			this.m_i18n.getString(this.getNamespace(), IJAMConst.GLOBAL_VARIABLE_DATE, "label", this.m_language),
			this.getFieldEditorParent()
		);
		addField(sfe);	
		
		sfe = new StringFieldEditor(
			IJAMConst.GLOBAL_NAMESPACE+SEPARATOR+IJAMConst.GLOBAL_VARIABLE_TIME,
			this.m_i18n.getString(this.getNamespace(), IJAMConst.GLOBAL_VARIABLE_TIME, "label", this.m_language),
			this.getFieldEditorParent()
		);
		addField(sfe);			
	}
	
}
