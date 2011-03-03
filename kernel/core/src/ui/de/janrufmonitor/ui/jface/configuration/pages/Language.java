package de.janrufmonitor.ui.jface.configuration.pages;

import java.util.Locale;
import java.util.StringTokenizer;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;

public class Language extends AbstractConfigPage {
	
    private String NAMESPACE = "ui.jface.configuration.pages.Language";
    
	private IRuntime m_runtime;
	private Combo module; 
	
	public String getParentNodeID() {
		return IConfigPage.ROOT_NODE;
	}
	
	public String getNodeID() {
		return "Language".toLowerCase();
	}

	public int getNodePosition() {
		return 997;
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
		return "";
	}
	
	protected Control createContents(Composite parent) {
		this.setTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));
		
		this.noDefaultAndApplyButton();
		
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, false));
		
		Label l = new Label(c, SWT.LEFT);
		l.setText(this.m_i18n.getString(this.getNamespace(), "language", "label", this.m_language));
		l.pack();
		
		module = new Combo (c, SWT.READ_ONLY);
		GridData gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        gd.widthHint = 200;
        module.setLayoutData(gd);
        
		this.buildCombobox(module);
		
		return c;
	}
	
	private String getLanguageLabel(String ns) {
		return new Locale(ns).getDisplayLanguage();
	}
	
	private void buildCombobox(Combo combo) {

		String languages = this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE, "values");
		
		StringTokenizer st = new StringTokenizer(languages, ",");
		
		String[] languageList = new String[st.countTokens()];
		int j=0;
		while (st.hasMoreTokens()) {
			languageList[j] = st.nextToken().trim();
			j++;
		}
		
		String currentLanguage = this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
		
		String[] languageLabelList = new String[languageList.length];
		int select = 0;
		for(int i=0;i<languageList.length;i++) {
			languageLabelList[i] = this.getLanguageLabel(languageList[i]);
			this.m_logger.info("Language key <"+languageList[i]+">, Label <"+languageLabelList[i]+">");
			combo.setData(languageLabelList[i], languageList[i]);
			if (currentLanguage.equalsIgnoreCase(languageList[i]))
				select = i;
		}
		combo.setItems(languageLabelList);
		combo.select(select);
	}

	public boolean performOk() {
		this.getPreferenceStore().setValue(
			IJAMConst.GLOBAL_NAMESPACE + SEPARATOR + IJAMConst.GLOBAL_LANGUAGE,
			(String)module.getData(module.getText())
		);
		return super.performOk();
	}

}
