package de.janrufmonitor.ui.jface.configuration.pages;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.ColorFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractServiceFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;
import de.janrufmonitor.ui.jface.configuration.controls.DirectoryFieldEditor;

public class CommentService extends AbstractServiceFieldEditorConfigPage {

	private String NAMESPACE = "ui.jface.configuration.pages.CommentService";
    private String CONFIG_NAMESPACE = "service.CommentService";

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
		return IConfigPage.SERVICE_NODE;
	}

	public String getNodeID() {
		return "CommentService".toLowerCase();
	}

	public int getNodePosition() {
		return 15;
	}

	protected void createFieldEditors() {
		super.createFieldEditors();
		
		if (isExpertMode()) {
			DirectoryFieldEditor dfe = new DirectoryFieldEditor(
				this.CONFIG_NAMESPACE+SEPARATOR+"path",
				this.m_i18n.getString(this.getNamespace(), "path", "label", this.m_language),
				this.m_i18n.getString(this.getNamespace(), "path", "description", this.m_language),
				this.getFieldEditorParent()
			);
			addField(dfe);				
			
			BooleanFieldEditor bfe = new BooleanFieldEditor(
				this.getConfigNamespace()+SEPARATOR+"autocreatecomment",
				this.m_i18n.getString(this.getNamespace(), "autocreatecomment", "label", this.m_language),
				this.getFieldEditorParent()
			);
			addField(bfe);

			StringFieldEditor sfe = new StringFieldEditor(
				this.getConfigNamespace()+SEPARATOR+"starttext",
				this.m_i18n.getString(this.getNamespace(), "starttext", "label", this.m_language),
				this.getFieldEditorParent()
			);
			addField(sfe);
			
			sfe = new StringFieldEditor(
				this.getConfigNamespace()+SEPARATOR+"maxtext",
				this.m_i18n.getString(this.getNamespace(), "maxtext", "label", this.m_language),
				3,
				this.getFieldEditorParent()
			);
			addField(sfe);
			sfe = new StringFieldEditor(
				this.getConfigNamespace()+SEPARATOR+"status",
				this.m_i18n.getString(this.getNamespace(), "status", "label", this.m_language),
				35,
				this.getFieldEditorParent()
			);
			sfe.setEmptyStringAllowed(false);
			addField(sfe);			
			
			ColorFieldEditor cfe = new ColorFieldEditor(
				this.getConfigNamespace()+SEPARATOR+"followupcolor",
				this.m_i18n.getString(this.getNamespace(), "followupcolor", "label", this.m_language),
				this.getFieldEditorParent()	
			);
			addField(cfe);	
		}
	}
}
