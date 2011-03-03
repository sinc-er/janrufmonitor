package de.janrufmonitor.ui.jface.configuration.pages;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.ComboFieldEditor;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.GoogleContactsLoginException;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractServiceFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;
import de.janrufmonitor.ui.jface.configuration.controls.BooleanFieldEditor;
import de.janrufmonitor.ui.swt.DisplayManager;

public class GoogleContactsCallerManager extends AbstractServiceFieldEditorConfigPage {

	private String NAMESPACE = "ui.jface.configuration.pages.GoogleContactsCallerManager";
    private String CONFIG_NAMESPACE = "repository.GoogleContactsCallerManager";

    private IRuntime m_runtime;
    
    public String user, password;
    
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
		return "GoogleContactsCallerManager".toLowerCase();
	}

	public int getNodePosition() {
		return 11;
	}

	protected void createFieldEditors() {
		super.createFieldEditors();
		
		BooleanFieldEditor bfe = new BooleanFieldEditor(
					getConfigNamespace()+SEPARATOR+"keepextension",
				this.m_i18n.getString(this.getNamespace(), "keepextension", "label", this.m_language),
				this.getFieldEditorParent()
			);
			addField(bfe);	
		
		final StringFieldEditor u = new StringFieldEditor(
				getConfigNamespace()+SEPARATOR+"user",
			this.m_i18n.getString(this.getNamespace(), "user", "label", this.m_language),
			this.getFieldEditorParent()
		);
		u.setEmptyStringAllowed(false);
		addField(u);

	
		final StringFieldEditor p = new StringFieldEditor(
				getConfigNamespace()+SEPARATOR+"password",
			this.m_i18n.getString(this.getNamespace(), "password", "label", this.m_language),
			this.getFieldEditorParent()
		);
		p.getTextControl(this.getFieldEditorParent()).setEchoChar('*');
		
		addField(p);
				
		final FieldEditor ffe = new FieldEditor("check-button", "", this.getFieldEditorParent()) {

			public Button up; 
			
			protected void adjustForNumColumns(int arg0) {
			}

			protected void doFillIntoGrid(Composite c, int numCols) {
				GridData gd = new GridData();
			    gd.horizontalAlignment = GridData.FILL;
			    gd.grabExcessHorizontalSpace = true;
			    gd.horizontalSpan = numCols-1;
			    gd.widthHint = 200;
 
			    final II18nManager i18 = PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager();
			    final String l = PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
			    String text = i18.getString("ui.jface.configuration.pages.GoogleContactsCallerManager", "check", "label", l);
			    
				up = new Button(c, SWT.PUSH);
				//new Label(c, SWT.NONE);
				up.setText(
					text
				);
				up.pack();
				
				up.addSelectionListener(
					new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {						
							ICallerManager cm = getRuntime().getCallerManagerFactory().getCallerManager(de.janrufmonitor.repository.GoogleContactsCallerManager.ID);
							if (cm!=null && cm instanceof de.janrufmonitor.repository.GoogleContactsCallerManager) {
								try {
									((de.janrufmonitor.repository.GoogleContactsCallerManager)cm).checkAuthentication(user, password);
									MessageDialog.openInformation(
											new Shell(DisplayManager.getDefaultDisplay()),
											i18.getString("ui.jface.configuration.pages.GoogleContactsCallerManager",
													"success", "label",
													l),
													i18.getString("ui.jface.configuration.pages.GoogleContactsCallerManager",
															"success", "description",
															l)
										);
									return;
								} catch (GoogleContactsLoginException ex) {
									
								}
								
							}
							MessageDialog.openError(
									new Shell(DisplayManager.getDefaultDisplay()),
									i18.getString("ui.jface.configuration.pages.GoogleContactsCallerManager",
											"error", "label",
											l),
											i18.getString("ui.jface.configuration.pages.GoogleContactsCallerManager",
													"error", "description",
													l)
								);
						}
					}	
				);
			}

			protected void doLoad() {
			}

			protected void doLoadDefault() {
			}

			protected void doStore() {
			}

			public int getNumberOfControls() {
				return 1;
			}

			public void setEnabled(boolean enabled, Composite parent) {
				super.setEnabled(enabled, parent);
				if (up!=null) up.setEnabled(enabled);
			}
			
			
			
		};
		ffe.setEnabled(false, getFieldEditorParent());
		addField(ffe);
		
		p.getTextControl(this.getFieldEditorParent()).addKeyListener(new KeyAdapter() { 
			 public void keyReleased(org.eclipse.swt.events.KeyEvent e) {
				 password = p.getStringValue();
				 user = u.getStringValue();
				 ffe.setEnabled((user !=null && password!=null && user.length()>0 && password.length()>0), getFieldEditorParent());
			 }
		});
		
		u.getTextControl(this.getFieldEditorParent()).addKeyListener(new KeyAdapter() { 
			 public void keyReleased(org.eclipse.swt.events.KeyEvent e) {
				 user = u.getStringValue();
				 password = p.getStringValue();
				 ffe.setEnabled((user !=null && password!=null && user.length()>0 && password.length()>0), getFieldEditorParent());
			 }
		});
		
		ComboFieldEditor cfe = new ComboFieldEditor(
			getConfigNamespace()+SEPARATOR+"mode",	
			this.m_i18n.getString(this.getNamespace(), "mode", "label", this.m_language),
			new String[][] { 
				{this.m_i18n.getString(this.getNamespace(), "mode1", "label", this.m_language), "1"}, 
				{this.m_i18n.getString(this.getNamespace(), "mode2", "label", this.m_language), "2"}
			},	
			this.getFieldEditorParent()
		);
		addField(cfe);
		
		bfe = new BooleanFieldEditor(
				getConfigNamespace()+SEPARATOR+"syncstart",
			this.m_i18n.getString(this.getNamespace(), "syncstart", "label", this.m_language),
			this.getFieldEditorParent()
		);
		addField(bfe);	
	}
}
