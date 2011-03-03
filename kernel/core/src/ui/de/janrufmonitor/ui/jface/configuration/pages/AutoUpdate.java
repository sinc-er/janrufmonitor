package de.janrufmonitor.ui.jface.configuration.pages;

import java.util.logging.Level;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.command.ICommand;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractServiceFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;

public class AutoUpdate extends AbstractServiceFieldEditorConfigPage {

	private String NAMESPACE = "ui.jface.configuration.pages.AutoUpdate";
    private String CONFIG_NAMESPACE = "service.AutoUpdateService";
    
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
		return IConfigPage.ROOT_NODE;
	}

	public String getNodeID() {
		return "AutoUpdate".toLowerCase();
	}

	public int getNodePosition() {
		return 51;
	}
	
	protected void createFieldEditors() {
		this.noDefaultAndApplyButton();
		
		super.createFieldEditors();
	
		FieldEditor bfe = new FieldEditor("check-button", "c", this.getFieldEditorParent()) {

			protected void adjustForNumColumns(int arg0) {
			}

			protected void doFillIntoGrid(Composite c, int numCols) {
				GridData gd = new GridData();
			    gd.horizontalAlignment = GridData.FILL;
			    gd.grabExcessHorizontalSpace = true;
			    gd.horizontalSpan = numCols - 1;
			    gd.widthHint = 200;

			    new Label(c, SWT.NONE);
			    
			    II18nManager i18 = PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager();
			    String l = PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
			    String text = i18.getString("ui.jface.configuration.pages.AutoUpdate", "execute", "label", l);
			    
				Button up = new Button(c, SWT.PUSH);
				up.setText(
					text
				);
				up.pack();
				
				up.addSelectionListener(
					new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {						
							ICommand c = getRuntime().getCommandFactory().getCommand("UpdatesCommand");
							if (c!=null && c.isExecutable()) {
								try {
									c.execute();
								} catch (Exception ex) {
									m_logger.log(Level.SEVERE, ex.getMessage(), ex);
								}
							}
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
			
		};
		
		addField(bfe);
	}

}
	