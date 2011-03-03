package de.janrufmonitor.ui.jface.configuration.pages;

import org.eclipse.jface.preference.BooleanFieldEditor;

import de.janrufmonitor.fritzbox.firmware.FirmwareManager;
import de.janrufmonitor.fritzbox.firmware.SessionIDFritzBoxFirmware;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractServiceFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;


public class FritzBoxPhonebookManager extends AbstractServiceFieldEditorConfigPage {

	private String NAMESPACE = "ui.jface.configuration.pages.FritzBoxPhonebookManager";
    private String CONFIG_NAMESPACE = "repository.FritzBoxPhonebookManager";

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
		return IConfigPage.CALLER_NODE;
	}

	public String getNodeID() {
		return "FritzBoxPhonebookManager".toLowerCase();
	}

	public int getNodePosition() {
		return 11;
	}
	
	protected void createFieldEditors() {
		String label = this.m_i18n.getString(this.getNamespace(), "enabled", "label", this.m_language);
		if (label.length()<150)
			for (int i=150;i>label.length();i--){
				label += " ";
			}
		
		BooleanFieldEditor bfe = new BooleanFieldEditor(
			this.getConfigNamespace()+SEPARATOR+"enabled",
			label,
			this.getFieldEditorParent()
		);		
		bfe.setEnabled(FirmwareManager.getInstance().isInstance(SessionIDFritzBoxFirmware.class), this.getFieldEditorParent());
		addField(bfe);
	}

}
