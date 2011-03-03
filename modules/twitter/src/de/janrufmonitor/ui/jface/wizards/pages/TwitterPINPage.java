package de.janrufmonitor.ui.jface.wizards.pages;

import java.net.MalformedURLException;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import winterwell.jtwitter.OAuthSignpostClient;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.twitter.ITwitterServiceConst;
import de.janrufmonitor.ui.jface.wizards.pages.AbstractPage;

public class TwitterPINPage extends AbstractPage {

	private String NAMESPACE = "ui.jface.wizards.pages.TwitterPINPage";
	private IRuntime m_runtime;
	
	private String[] m_result;
	private String m_PIN;
	private OAuthSignpostClient m_client;
	
	public TwitterPINPage(String name) {
		super(name);
		this.m_client = new OAuthSignpostClient(ITwitterServiceConst.CONSUMER_KEY, 
				ITwitterServiceConst.CONSUMER_SECRET, "oob");
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

	public void createControl(Composite parent) {
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));

		Composite c = new Composite(parent, SWT.NONE);
	    c.setLayout(new GridLayout(1, false));
	    c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
	    StringFieldEditor sfe = new StringFieldEditor(
	    		"pin",
				this.m_i18n.getString(getNamespace(), "pin", "label", this.m_language),
				10,
				c);
	    sfe.setStringValue("");
	    this.m_PIN = "";
	    
	    sfe.setPropertyChangeListener(new IPropertyChangeListener() {

			public void propertyChange(PropertyChangeEvent e) {
				if (e!=null && e.getNewValue()!=null && e.getNewValue() instanceof String)
					m_PIN = (String) e.getNewValue();
				setPageComplete(isComplete());
			}
			   
		});
	    
	    Button rPIN = new Button(c, SWT.PUSH);
	    rPIN.setText(this.m_i18n.getString(getNamespace(), "rpin", "label", this.m_language));
	    rPIN.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}

			public void widgetSelected(SelectionEvent arg0) {
				try {
					Program.launch(m_client.authorizeUrl().toURL().toExternalForm());
				} catch (MalformedURLException e) {
					m_logger.severe(e.toString());
				}
			}
	    });
	  
	    setPageComplete(isComplete());
	    setControl(c);
	    
	}
	
	public String[] getResult() {
		if (this.m_PIN!=null) {
			this.m_client.setAuthorizationCode(this.m_PIN.trim());
			this.m_result = this.m_client.getAccessToken();
		}
		return this.m_result;
	}

	protected boolean isComplete() {
		return super.isComplete() && (this.m_PIN!=null && this.m_PIN.trim().length()>0);
	}
}
