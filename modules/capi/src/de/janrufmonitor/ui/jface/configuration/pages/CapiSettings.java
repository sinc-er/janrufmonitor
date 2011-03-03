package de.janrufmonitor.ui.jface.configuration.pages;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractFieldEditorConfigPage;

public class CapiSettings extends AbstractFieldEditorConfigPage {
	
    private String NAMESPACE = "ui.jface.configuration.pages.CapiSettings";
    private String CONFIG_NAMESPACE = "monitor.CapiMonitor";
    
	private IRuntime m_runtime;
	
	public String getParentNodeID() {
		return "BasicIsdnSettings".toLowerCase();
	}
	
	public String getNodeID() {
		return "CapiSettings".toLowerCase();
	}

	public int getNodePosition() {
		return 3;
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
		
		BooleanFieldEditor bfe = new BooleanFieldEditor(
				this.CONFIG_NAMESPACE+SEPARATOR+"available",
				this.m_i18n.getString(this.getNamespace(), "available", "label", this.m_language),
				this.getFieldEditorParent()
			);
			addField(bfe);
		
		bfe = new BooleanFieldEditor(
			this.CONFIG_NAMESPACE+SEPARATOR+"busyonbusy",
			this.m_i18n.getString(this.getNamespace(), "busyonbusy", "label", this.m_language),
			this.getFieldEditorParent()
		);
		addField(bfe);
		
		bfe = new BooleanFieldEditor(
			this.CONFIG_NAMESPACE+SEPARATOR+"spoofing",
			this.m_i18n.getString(this.getNamespace(), "spoofing", "label", this.m_language),
			this.getFieldEditorParent()
		);
		addField(bfe);
	
		StringFieldEditor sfe = new StringFieldEditor(
			this.CONFIG_NAMESPACE+SEPARATOR+"maxcon",
			this.m_i18n.getString(this.getNamespace(), "maxcon", "label", this.m_language),
			5,
			this.getFieldEditorParent());
		sfe.setEmptyStringAllowed(false);
		addField(sfe);
		
		sfe = new StringFieldEditor(
			this.CONFIG_NAMESPACE+SEPARATOR+"maxblock",
			this.m_i18n.getString(this.getNamespace(), "maxblock", "label", this.m_language),
			5,
			this.getFieldEditorParent());
		sfe.setEmptyStringAllowed(false);
		addField(sfe);
		
		sfe = new StringFieldEditor(
			this.CONFIG_NAMESPACE+SEPARATOR+"blocksize",
			this.m_i18n.getString(this.getNamespace(), "blocksize", "label", this.m_language),
			10,
			this.getFieldEditorParent());
		sfe.setEmptyStringAllowed(false);
		addField(sfe);
		
		bfe = new BooleanFieldEditor(
			IJAMConst.GLOBAL_NAMESPACE+SEPARATOR+IJAMConst.GLOBAL_TRACE,
			this.m_i18n.getString(this.getNamespace(), IJAMConst.GLOBAL_TRACE, "label", this.m_language),
			this.getFieldEditorParent()
		);
		addField(bfe);	
	}
	
}
