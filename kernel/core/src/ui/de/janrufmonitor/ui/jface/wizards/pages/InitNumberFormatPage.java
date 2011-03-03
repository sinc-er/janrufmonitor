package de.janrufmonitor.ui.jface.wizards.pages;

import java.util.logging.Level;

import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class InitNumberFormatPage extends AbstractPage {

	private String NAMESPACE = "ui.jface.wizards.pages.InitNumberFormatPage";

	private String m_newFormat;
	private IRuntime m_runtime;
	
	public InitNumberFormatPage(String f) {
		super(InitNumberFormatPage.class.getName());
	}

	public boolean performFinish() {
		if (m_newFormat==null || m_newFormat.trim().length()==0) {
			try {
				Thread.sleep(550);
			} catch (InterruptedException e) {
				m_logger.log(Level.SEVERE, e.getMessage(), e);
			}
			return true;
		}
		
		getRuntime().getConfigManagerFactory().getConfigManager().setProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_VARIABLE_CALLERNUMBER, m_newFormat);
		getRuntime().getConfigManagerFactory().getConfigManager().saveConfiguration();
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			m_logger.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
		
		return true;
	}

	public void createControl(Composite parent) {
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));

		Composite c = new Composite(parent, SWT.NONE);
	    c.setLayout(new GridLayout(1, false));
	    c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	   ComboFieldEditor cfe = new ComboFieldEditor(
			   this.m_i18n.getString(getNamespace(), "name", "label", this.m_language)
			   , this.m_i18n.getString(getNamespace(), "label", "label", this.m_language)
			   , new String[][] {
				   { this.m_i18n.getString(getNamespace(), "format1", "label", this.m_language), "+%intareacode% (%areacode%) %callnumber%" }, 
				   { this.m_i18n.getString(getNamespace(), "format2", "label", this.m_language), "00%intareacode% (%areacode%) %callnumber%" }, 
				   { this.m_i18n.getString(getNamespace(), "format3", "label", this.m_language), "(0%areacode%) %callnumber%" }, 
				   { this.m_i18n.getString(getNamespace(), "format4", "label", this.m_language), "0%areacode%-%callnumber%" }, 
			   }, 
			   c);

	   cfe.setPropertyChangeListener(new IPropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent e) {
			if (e!=null && e.getNewValue()!=null)
				m_newFormat = (String) e.getNewValue();
			setPageComplete(isComplete());
		}
		   
	   });
        

	    setPageComplete(isComplete());
	    setControl(c);
	}
	
	protected boolean isComplete() {
		return m_newFormat!=null;
	}
	
	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getNamespace() {
		return NAMESPACE;
	}
}
