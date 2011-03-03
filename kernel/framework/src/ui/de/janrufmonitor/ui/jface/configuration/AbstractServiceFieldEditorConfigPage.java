package de.janrufmonitor.ui.jface.configuration;

import org.eclipse.jface.preference.BooleanFieldEditor;

public abstract class AbstractServiceFieldEditorConfigPage extends AbstractFieldEditorConfigPage {

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
		
		addField(bfe);
	}

}
