package de.janrufmonitor.ui.jface.wizards.pages;

import java.util.logging.Level;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.repository.identify.Identifier;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class InitAreaCodePage extends AbstractPage {

	private String NAMESPACE = "ui.jface.wizards.pages.InitAreaCodePage";

	private String m_newIntArea;
	private String m_newArea;
	private IRuntime m_runtime;
	private Label m_city;
	
	public InitAreaCodePage(String f) {
		super(InitAreaCodePage.class.getName());
	}

	public boolean performFinish() {
		if (m_newIntArea==null || m_newIntArea.trim().length()==0) {
			try {
				Thread.sleep(550);
			} catch (InterruptedException e) {
				m_logger.log(Level.SEVERE, e.getMessage(), e);
			}
			return true;
		}
		
		getRuntime().getConfigManagerFactory().getConfigManager().setProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_AREACODE, m_newArea);
		getRuntime().getConfigManagerFactory().getConfigManager().setProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_INTAREA, m_newIntArea);
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

	    
	    StringFieldEditor sfe = new StringFieldEditor(
	    		"intarea",
				this.m_i18n.getString(getNamespace(), "intarea", "label", this.m_language),
				5,
				c);
	    sfe.setStringValue("49");
	    this.m_newIntArea = "49";
	    
	    sfe.setPropertyChangeListener(new IPropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent e) {
				if (e!=null && e.getNewValue()!=null)
					m_newIntArea = (String) e.getNewValue();
				setPageComplete(isComplete());
			}
			   
		   });
	    
	    sfe = new StringFieldEditor(
	    		"area",
				this.m_i18n.getString(getNamespace(), "area", "label", this.m_language),
				10,
				c);
	    
	    sfe.setStringValue("030");
	    this.m_newArea = "030";
	    
	    sfe.setPropertyChangeListener(new IPropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent e) {
				if (e!=null && e.getNewValue()!=null)
					m_newArea = (String) e.getNewValue();
				identify();
				setPageComplete(isComplete());
			}
			   
		   });
	    
	    new Label(c, SWT.LEFT);
	    m_city = new Label(c, SWT.LEFT);
	    GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 300;
	    
	    m_city.setText("Berlin");
	    m_city.setLayoutData(gd);

	    setPageComplete(isComplete());
	    setControl(c);
	}
	
	private void identify() {
		IPhonenumber pn = getRuntime().getCallerFactory().createPhonenumber(m_newIntArea, m_newArea.substring(1), "0000000000");
		ICaller c = Identifier.identifyDefault(getRuntime(), pn);
		if (c!=null) {
			IAttribute city = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_CITY);
			if (city!=null && city.getValue().trim().length()>0) {
				m_city.setText(city.getValue());
			} else {
				city = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_COUNTRY);
				if (city!=null && city.getValue().trim().length()>0) {
					m_city.setText(city.getValue());
				}
				else
					m_city.setText(this.m_i18n.getString(this.getNamespace(),
						"unknownareacode", "label", this.m_language));
			}
		}
		
	}
	
	protected boolean isComplete() {
		if (m_newIntArea.trim().length()>0 && this.m_newArea.trim().length()>0) {
			
			if (m_newIntArea.trim().startsWith("0")) {
				setErrorMessage(this.m_i18n.getString(this.getNamespace(),
					"leadingzero", "label", this.m_language));
				return false;
			}
			
			if (!m_newArea.trim().startsWith("0")) {
				setErrorMessage(this.m_i18n.getString(this.getNamespace(),
					"noleadingzero", "label", this.m_language));
				return false;
			}
			
			setErrorMessage(null);
			
			return true;
		}
		return false;
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
