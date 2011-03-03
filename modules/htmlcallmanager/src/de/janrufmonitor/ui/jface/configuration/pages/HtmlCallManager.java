package de.janrufmonitor.ui.jface.configuration.pages;

import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.jface.preference.ComboFieldEditor;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractServiceFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;
import de.janrufmonitor.ui.jface.configuration.controls.FileDialogFieldEditor;
import de.janrufmonitor.util.io.PathResolver;

public class HtmlCallManager extends AbstractServiceFieldEditorConfigPage {

	private String NAMESPACE = "ui.jface.configuration.pages.HtmlCallManager";
    private String CONFIG_NAMESPACE = "repository.HtmlCallManager";

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
		return "HtmlCallManager".toLowerCase();
	}

	public int getNodePosition() {
		return 10;
	}

	protected void createFieldEditors() {
		super.createFieldEditors();
		
		FileDialogFieldEditor dfe = new FileDialogFieldEditor(
			this.CONFIG_NAMESPACE+SEPARATOR+"database",
			this.m_i18n.getString(this.getNamespace(), "database", "label", this.m_language),
			this.getFieldEditorParent()
		);
		dfe.setFileExtensions(new String[]{"*.html", "*.xml"});
		addField(dfe);
		
		if (isExpertMode()) {
			// get templates
			File templateDir = new File(PathResolver.getInstance(getRuntime()).getConfigDirectory() + File.separator + "templates" + File.separator + "journals");
			if (!templateDir.exists()) templateDir.mkdirs();
			
			File[] templates = templateDir.listFiles(new FilenameFilter() {
				public boolean accept(File folder, String name) {
					return name.endsWith(".template");
				}});
			
			if (templates.length>0) {
				String[][] list = new String[templates.length][2];
				String name = null;
				for (int i=0, j=templates.length; i<j;i++) {
					name = templates[i].getName();
					list[i][0] = name.substring(0, name.lastIndexOf("."));
					list[i][1] = list[i][0];
				}
				ComboFieldEditor cfe = new ComboFieldEditor(
				 this.CONFIG_NAMESPACE+SEPARATOR+"template"
				   , this.m_i18n.getString(getNamespace(), "template", "label", this.m_language)
				   , list
				   , this.getFieldEditorParent());
				 addField(cfe);
			}
		}
	}
}
