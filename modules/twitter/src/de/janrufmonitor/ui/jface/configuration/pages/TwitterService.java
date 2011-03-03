package de.janrufmonitor.ui.jface.configuration.pages;

import java.util.logging.Level;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.framework.command.ICommand;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractServiceFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;
import de.janrufmonitor.ui.jface.configuration.controls.BooleanFieldEditor;

public class TwitterService extends AbstractServiceFieldEditorConfigPage {
	
    private String NAMESPACE = "ui.jface.configuration.pages.TwitterService";
    private String CONFIG_NAMESPACE = "service.TwitterService";
    
	private IRuntime m_runtime;
	
	public String getParentNodeID() {
		return IConfigPage.SERVICE_NODE;
	}
	
	public String getNodeID() {
		return "TwitterService".toLowerCase();
	}

	public int getNodePosition() {
		return 100;
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
		super.createFieldEditors();
	
		BooleanFieldEditor bfe = new BooleanFieldEditor(
			this.getConfigNamespace()+SEPARATOR+"incoming",
			this.m_i18n.getString(this.getNamespace(), "incoming", "label", this.m_language),
			this.getFieldEditorParent()
		);
		addField(bfe);
		
		StringFieldEditor sfe = new StringFieldEditor(
				getConfigNamespace()+SEPARATOR+"inmessage",
			this.m_i18n.getString(this.getNamespace(), "inmessage", "label", this.m_language),
			this.getFieldEditorParent()
		);
		sfe.setEmptyStringAllowed(false);
		sfe.setTextLimit(160);
		addField(sfe);
		
		bfe = new BooleanFieldEditor(
			this.getConfigNamespace()+SEPARATOR+"outgoing",
			this.m_i18n.getString(this.getNamespace(), "outgoing", "label", this.m_language),
			this.getFieldEditorParent()
		);
		addField(bfe);
			
		sfe = new StringFieldEditor(
				getConfigNamespace()+SEPARATOR+"outmessage",
			this.m_i18n.getString(this.getNamespace(), "outmessage", "label", this.m_language),
			this.getFieldEditorParent()
		);
		sfe.setEmptyStringAllowed(false);
		sfe.setTextLimit(160);
		addField(sfe);
		
		if (getRuntime().getConfigManagerFactory().getConfigManager().getProperty("service.TwitterService", "auth1").length()==0 || getRuntime().getConfigManagerFactory().getConfigManager().getProperty("service.TwitterService", "auth2").length()==0) {
			Button authenticate = new Button(this.getFieldEditorParent(), SWT.PUSH);
			authenticate.setText(this.m_i18n.getString(this.getNamespace(), "auth", "label", this.m_language));
			authenticate.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent arg0) {

				}

				public void widgetSelected(SelectionEvent arg0) {
					ICommand c = getRuntime().getCommandFactory().getCommand("TwitterPINCommand");
					if (c!=null && c.isExecutable()) {
						try {
							c.execute();
						} catch (Exception ex) {
							m_logger.log(Level.SEVERE, ex.getMessage(), ex);
						}
					}
				}
				
			});
		} else {
			final Label l = new Label(this.getFieldEditorParent(), SWT.NORMAL);
			l.setText(this.m_i18n.getString(this.getNamespace(), "isauth", "label", this.m_language));
			GridData gd = new GridData();
			gd.horizontalSpan = 2;
			gd.widthHint = 500;
			l.setLayoutData(gd);
			
			Button unauthenticate = new Button(this.getFieldEditorParent(), SWT.PUSH);
			unauthenticate.setLayoutData(gd);
			unauthenticate.setText(this.m_i18n.getString(this.getNamespace(), "unauth", "label", this.m_language));
			unauthenticate.addSelectionListener(new SelectionListener() {

				public void widgetDefaultSelected(SelectionEvent arg0) {

				}

				public void widgetSelected(SelectionEvent arg0) {
					getRuntime().getConfigManagerFactory().getConfigManager().setProperty("service.TwitterService", "auth1", "");
					getRuntime().getConfigManagerFactory().getConfigManager().setProperty("service.TwitterService", "auth2", "");
					getRuntime().getConfigManagerFactory().getConfigManager().saveConfiguration();
					l.setText(m_i18n.getString(getNamespace(), "isnotauth", "label", m_language));
				}
				
			});
			
		}

		
		
	}

}
