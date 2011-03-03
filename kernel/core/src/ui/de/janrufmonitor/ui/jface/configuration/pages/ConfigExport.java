package de.janrufmonitor.ui.jface.configuration.pages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigManager;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.framework.installer.InstallerConst;
import de.janrufmonitor.repository.zip.ZipArchive;
import de.janrufmonitor.repository.zip.ZipArchiveException;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractServiceFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;

public class ConfigExport extends AbstractServiceFieldEditorConfigPage {

	private String NAMESPACE = "ui.jface.configuration.pages.ConfigExport";
    
	private IRuntime m_runtime;

	public String getConfigNamespace() {
		return "";
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
		return IConfigPage.ADVANCED_NODE;
	}

	public String getNodeID() {
		return "ConfigExport".toLowerCase();
	}

	public int getNodePosition() {
		return 52;
	}
	
	protected void createFieldEditors() {
		this.noDefaultAndApplyButton();
		
		//super.createFieldEditors();
	
		FieldEditor bfe = new FieldEditor("check-button", "c", this.getFieldEditorParent()) {

			protected void adjustForNumColumns(int arg0) {
			}

			protected void doFillIntoGrid(final Composite c, int numCols) {
				GridData gd = new GridData();
			    gd.horizontalAlignment = GridData.FILL;
			    gd.grabExcessHorizontalSpace = true;
			    gd.horizontalSpan = numCols - 1;
			    gd.widthHint = 200;

			    new Label(c, SWT.NONE);
			    
			    final II18nManager i18 = PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager();
			    final String l = PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
			    String text = i18.getString("ui.jface.configuration.pages.ConfigExport", "execute", "label", l);
			    
				Button up = new Button(c, SWT.PUSH);
				up.setText(
					text
				);
				up.pack();
				
				up.addSelectionListener(
					new SelectionAdapter() {
						public void widgetSelected(SelectionEvent e) {						
							DirectoryDialog d = new DirectoryDialog(c.getShell());
							d.setText(i18.getString("ui.jface.configuration.pages.ConfigExport", "save", "label", l));
							d.setMessage(i18.getString("ui.jface.configuration.pages.ConfigExport", "save", "description", l));
							String dir = d.open();
							if (dir!=null && dir.trim().length()>0) {
								long id = System.currentTimeMillis();
								
								SimpleDateFormat formatter
								 	= new SimpleDateFormat("yyyy-MM-dd_HHmm");
								String current_date = formatter.format(new Date(id));
								
								// descriptor data
								Properties descriptor = new Properties();
								descriptor.setProperty(InstallerConst.DESCRIPTOR_REQUIRED_MAJOR_VERSION, IJAMConst.VERSION_MAJOR);
								descriptor.setProperty(InstallerConst.DESCRIPTOR_REQUIRED_MINOR_VERSION, IJAMConst.VERSION_MINOR);
								descriptor.setProperty(InstallerConst.DESCRIPTOR_REQUIRED_PATCH_VERSION, IJAMConst.VERSION_PATCH);
								
								descriptor.setProperty(InstallerConst.DESCRIPTOR_TYPE, "configuration");
								descriptor.setProperty(InstallerConst.DESCRIPTOR_VERSION, "1.0.0");
								descriptor.setProperty(InstallerConst.DESCRIPTOR_NAME, "mod-cfg-"+id);
								descriptor.setProperty(InstallerConst.DESCRIPTOR_NAMESPACE, "cfg."+id);
								descriptor.setProperty(InstallerConst.DESCRIPTOR_RESTART, "true");
								descriptor.setProperty(InstallerConst.DESCRIPTOR_REMOVE, "false");
								
								// inf data
								Properties inf = new Properties();
								IConfigManager cfgMan = getRuntime().getConfigManagerFactory().getConfigManager();
								String[] namespaces = cfgMan.getConfigurationNamespaces();
								Properties cfgData = null;
								for (int i=0; i<namespaces.length;i++) {
									cfgData = cfgMan.getProperties(namespaces[i], true);
									if (cfgData!=null) {
										Iterator iter = cfgData.keySet().iterator();
										String key = null;
										while (iter.hasNext()) {
											key = (String) iter.next();
											inf.setProperty("~"+namespaces[i]+":"+key+":value", cfgData.getProperty(key));
										}
									}
								}
								
								// i18n data
								Properties i18n = new Properties();
								i18n.setProperty("cfg."+id+":title:label:de", "Benutzereinstellungen (Version "+IJAMConst.VERSION_MAJOR+"."+IJAMConst.VERSION_MINOR+"."+IJAMConst.VERSION_PATCH+") vom "+new SimpleDateFormat("dd.MM.yyyy").format(new Date(id)));
								i18n.setProperty("cfg."+id+":label:label:de", "Benutzereinstellungen (Version "+IJAMConst.VERSION_MAJOR+"."+IJAMConst.VERSION_MINOR+"."+IJAMConst.VERSION_PATCH+") vom "+new SimpleDateFormat("dd.MM.yyyy").format(new Date(id)));
								i18n.setProperty("cfg."+id+":title:label:en", "user settings (version "+IJAMConst.VERSION_MAJOR+"."+IJAMConst.VERSION_MINOR+"."+IJAMConst.VERSION_PATCH+") of "+new SimpleDateFormat("yyyy/dd/MM").format(new Date(id)));
								i18n.setProperty("cfg."+id+":label:label:en", "user settings (version "+IJAMConst.VERSION_MAJOR+"."+IJAMConst.VERSION_MINOR+"."+IJAMConst.VERSION_PATCH+") of "+new SimpleDateFormat("yyyy/dd/MM").format(new Date(id)));
		
								// create jam.zip archive
								File directory = new File(dir);
								if (!directory.exists()) {
									directory.mkdirs();
								}
								
								ZipArchive z = new ZipArchive(directory.getAbsolutePath() + File.separator + "cfg."+current_date+".jam.zip");
								try {
									z.open();

									// write descriptor
									ByteArrayOutputStream bos = new ByteArrayOutputStream();
									descriptor.store(bos, "");
									bos.flush();

									ByteArrayInputStream bin = new ByteArrayInputStream(bos.toByteArray());
									z.add("~meta-inf/descriptor.properties", bin);
									
									// write inf data
									 bos = new ByteArrayOutputStream();
									inf.store(bos, "");
									bos.flush();

									bin = new ByteArrayInputStream(bos.toByteArray());
									z.add("install/cfg."+id+".inf", bin);
									
									// write i18n data
									bos = new ByteArrayOutputStream();
									i18n.store(bos, "");
									bos.flush();

									bin = new ByteArrayInputStream(bos.toByteArray());
									z.add("install/cfg."+id+".i18n", bin);
									
									
									z.close();
								} catch (ZipArchiveException ex) {
									m_logger.severe(ex.toString());
								} catch (IOException ex) {
									m_logger.severe(ex.toString());
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
	