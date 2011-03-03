package de.janrufmonitor.ui.jface.configuration.pages;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.installer.InstallerConst;
import de.janrufmonitor.framework.installer.InstallerEngine;
import de.janrufmonitor.repository.zip.ZipArchive;
import de.janrufmonitor.repository.zip.ZipArchiveException;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.AbstractTableLabelProvider;
import de.janrufmonitor.ui.jface.configuration.AbstractConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;
import de.janrufmonitor.util.io.PathResolver;

public class Info extends AbstractConfigPage {
	
	private class ContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object o) {
			List l = (List)o;
			return l.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {	
		}
		
	}
	
	private class LabelProvider extends AbstractTableLabelProvider {

		public String getColumnText(Object o, int column) {
			String[] module = (String[])o;

		    switch (column) {
		    case 0:
		    	return module[0];
		    case 1:
		    	return module[1];
		    }
			return null;
		}
	}

    private String NAMESPACE = "ui.jface.configuration.pages.Info";
    
	private IRuntime m_runtime;
	private TableViewer m_moduleViewer;
	private TableViewer m_appViewer;

	
	public String getParentNodeID() {
		return IConfigPage.ROOT_NODE;
	}
	
	public String getNodeID() {
		return "Info".toLowerCase();
	}

	public int getNodePosition() {
		return 999;
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
		
		final Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, true));
		
		Label l = new Label(c, SWT.NULL);
		l.setText(this.m_i18n.getString(this.getNamespace(), "program", "label", this.m_language));
		
		m_appViewer = new TableViewer(c);
		m_appViewer.setContentProvider(new ContentProvider());
		m_appViewer.setLabelProvider(new LabelProvider());
			
		Table tb = m_appViewer.getTable();
		tb.setLayoutData(new GridData(GridData.FILL_BOTH));
		TableColumn tc = new TableColumn(tb, SWT.LEFT);
		tc = new TableColumn(tb, SWT.LEFT);
		
		m_appViewer.setInput(this.getAppList());
		
	    for (int i = 0, n = tb.getColumnCount(); i < n; i++) {
	      tb.getColumn(i).pack();
	    }

	    tb.setHeaderVisible(true);
	    tb.setLinesVisible(true);	
	    
	    l = new Label(c, SWT.NULL);
		l.setText(this.m_i18n.getString(this.getNamespace(), "modules", "label", this.m_language));
		
		
		m_moduleViewer = new TableViewer(c);
		m_moduleViewer.setContentProvider(new ContentProvider());
		m_moduleViewer.setLabelProvider(new LabelProvider());
			
		tb = m_moduleViewer.getTable();
		tb.setLayoutData(new GridData(GridData.FILL_BOTH));
		tc = new TableColumn(tb, SWT.LEFT);
		tc.setText(this.m_i18n.getString(this.getNamespace(), "module", "label", this.m_language));
		
		
		tc = new TableColumn(tb, SWT.LEFT);
		tc.setText(this.m_i18n.getString(this.getNamespace(), "version", "label", this.m_language));
		
		
		m_moduleViewer.setInput(this.getModuleList());
		
	    for (int i = 0, n = tb.getColumnCount(); i < n; i++) {
	      tb.getColumn(i).pack();
	    }

	    tb.setHeaderVisible(true);
	    tb.setLinesVisible(true);	
		
	    if (this.isExpertMode()) {
	    
	    Button b = new Button(c, SWT.PUSH);
	    b.setText(this.m_i18n.getString(this.getNamespace(), "export", "label", this.m_language));
	    b.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						FileDialog dlg = new FileDialog(c.getShell(), SWT.SAVE);
						dlg.setFilterExtensions(new String[]{"*.zip"});
						dlg.setFileName("jam-info.zip");
						String path = getRuntime().getConfigManagerFactory().getConfigManager().getProperty(getNamespace(), "last");
						if (path==null || path.length()==0) {
							path = PathResolver.getInstance(getRuntime()).getUserhomeDirectory();
						}
						path = PathResolver.getInstance(getRuntime()).resolve(path);

						dlg.setFilterPath(path);
						if (dlg.open()!=null) {
							getRuntime().getConfigManagerFactory().getConfigManager().setProperty(getNamespace(), "last", dlg.getFilterPath());
							
							StringBuffer sb = new StringBuffer();
							
							Properties env = System.getProperties();
							Iterator iter = env.keySet().iterator();
							String key = null;
							
							sb.append("Program information jAnrufmonitor:");
							sb.append(IJAMConst.CRLF);
							sb.append(IJAMConst.CRLF);
		
							sb.append("version = "+IJAMConst.VERSION_DISPLAY + " (Build: "+IJAMConst.VERSION_BUILD+")");
							sb.append(IJAMConst.CRLF);
							
							sb.append("install path = "+PathResolver.getInstance(getRuntime()).getInstallDirectory());
							sb.append(IJAMConst.CRLF);
							
							sb.append("user home path = "+PathResolver.getInstance(getRuntime()).getUserhomeDirectory());

							sb.append(IJAMConst.CRLF);
							sb.append("temp path = "+PathResolver.getInstance(getRuntime()).getTempDirectory());

							sb.append(IJAMConst.CRLF);
							
							sb.append("language = "+getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE));
							sb.append(IJAMConst.CRLF);
							while (iter.hasNext()) {
								key = (String) iter.next();
								sb.append(key + " = "+env.getProperty(key));
								sb.append(IJAMConst.CRLF);
							}
							
							sb.append(IJAMConst.CRLF);
							sb.append(IJAMConst.CRLF);
							
							String rkey = getRuntime().getConfigManagerFactory().getConfigManager().getProperty("service.update.UpdateManager", "regkey");
							if (rkey!=null && rkey.trim().length()>0) {
								sb.append("registry key = "+rkey);
								sb.append(IJAMConst.CRLF);
								sb.append(IJAMConst.CRLF);
							}
							
							sb.append("Installed modules:");
							sb.append(IJAMConst.CRLF);
							sb.append(IJAMConst.CRLF);
							
							List modules = InstallerEngine.getInstance().getModuleList();
							
							Properties mod = null;
							for (int i=0, j=modules.size();i<j;i++) {
								mod = InstallerEngine.getInstance().getDescriptor((String) modules.get(i));
								if (mod!=null) {
									sb.append(getNamespaceLabel((String) modules.get(i))+" = "+ mod.getProperty(InstallerConst.DESCRIPTOR_VERSION, "-"));
									sb.append(IJAMConst.CRLF);
								}
							}
							
							File f = new File(dlg.getFilterPath() + File.separator + dlg.getFileName());
							f.getParentFile().mkdirs();
							
							try {
								ZipArchive zip = new ZipArchive(f, true);
								zip.open();
								Map entries = new HashMap();
								
								// add program information
								entries.put("info/jam-info.log", new ByteArrayInputStream(sb.toString().getBytes()));
								
								// add configuration
								File configDir = new File(PathResolver.getInstance(getRuntime()).getConfigDirectory());
								if (configDir.exists() && configDir.isDirectory()) {
									File[] files = configDir.listFiles();
									for (int i=0;i<files.length;i++) {
										if (files[i].isFile()) {
											entries.put("config/"+files[i].getName(), new FileInputStream(files[i]));
										}
									}
								}
																
								// add logs
								File logDir = new File(PathResolver.getInstance(getRuntime()).getLogDirectory());
								if (logDir.exists() && logDir.isDirectory()) {
									File[] files = logDir.listFiles();
									for (int i=0;i<files.length;i++) {
										if (files[i].isFile()) {
											entries.put("logs/"+files[i].getName(), new FileInputStream(files[i]));
										}
									}
								}
								
								zip.add(entries);
								zip.close();
							} catch (ZipArchiveException ex) {
								m_logger.log(Level.SEVERE, ex.getMessage(), ex);
							} catch (FileNotFoundException ex) {
								m_logger.log(Level.SEVERE, ex.getMessage(), ex);
							}
						}
					}
				}
			);
	    }
		
	    
		return c;
	}

	private List getAppList() {
		List l = new ArrayList();
		l.add(new String[]{
				this.m_i18n.getString(this.getNamespace(), "version", "label", this.m_language), 
				IJAMConst.VERSION_DISPLAY + " (Build: "+IJAMConst.VERSION_BUILD+")"
		});	
		
		l.add(new String[]{
				this.m_i18n.getString(this.getNamespace(), "installpath", "label", this.m_language), 
				PathResolver.getInstance(this.getRuntime()).getInstallDirectory()
		});	

		l.add(new String[]{
				this.m_i18n.getString(this.getNamespace(), "userhome", "label", this.m_language), 
				PathResolver.getInstance(this.getRuntime()).getUserhomeDirectory()
		});	
		
		l.add(new String[]{
				this.m_i18n.getString(this.getNamespace(), "userdatadir", "label", this.m_language), 
				PathResolver.getInstance(this.getRuntime()).getUserDataDirectory()
		});	
		
		l.add(new String[]{
				this.m_i18n.getString(this.getNamespace(), "configdir", "label", this.m_language), 
				PathResolver.getInstance(this.getRuntime()).getConfigDirectory()
		});	
		
		l.add(new String[]{
				this.m_i18n.getString(this.getNamespace(), "photodir", "label", this.m_language), 
				PathResolver.getInstance(this.getRuntime()).getPhotoDirectory()
		});	
		
		l.add(new String[]{
				this.m_i18n.getString(this.getNamespace(), "temp", "label", this.m_language), 
				PathResolver.getInstance(this.getRuntime()).getTempDirectory()
		});	
		
		l.add(new String[]{
				this.m_i18n.getString(this.getNamespace(), "language", "label", this.m_language), 
				this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE)
		});	
		
		l.add(new String[]{
				this.m_i18n.getString(this.getNamespace(), "os", "label", this.m_language), 
				System.getProperty("os.name")
		});	

		l.add(new String[]{
				this.m_i18n.getString(this.getNamespace(), "arch", "label", this.m_language), 
				System.getProperty("os.arch")
		});	

		l.add(new String[]{
				this.m_i18n.getString(this.getNamespace(), "vm", "label", this.m_language), 
				System.getProperty("java.vm.name")
		});	

		l.add(new String[]{
				this.m_i18n.getString(this.getNamespace(), "vmversion", "label", this.m_language), 
				System.getProperty("java.vm.version")
		});	

		l.add(new String[]{
				this.m_i18n.getString(this.getNamespace(), "vmvendor", "label", this.m_language), 
				System.getProperty("java.vendor")
		});	

		l.add(new String[]{
				this.m_i18n.getString(this.getNamespace(), "vmhome", "label", this.m_language), 
				System.getProperty("java.home")
		});	
		
		String rkey = getRuntime().getConfigManagerFactory().getConfigManager().getProperty("service.update.UpdateManager", "regkey");
		if (rkey!=null && rkey.trim().length()>0) {
			l.add(new String[]{
					this.m_i18n.getString(this.getNamespace(), "rkey", "label", this.m_language), 
					rkey
			});	
		}
		
		return l;
	}

	private List getModuleList() {
		List l = new ArrayList();
		
		List modules = InstallerEngine.getInstance().getModuleList();
		
		Properties mod = null;
		for (int i=0, j=modules.size();i<j;i++) {
			mod = InstallerEngine.getInstance().getDescriptor((String) modules.get(i));
			if (mod!=null) {
				l.add(new String[] {getNamespaceLabel((String) modules.get(i)), mod.getProperty(InstallerConst.DESCRIPTOR_VERSION, "-")});
			}
		}
		
		return l;
	}
	
	private String getNamespaceLabel(String ns) {
		return this.m_i18n.getString(ns,"title","label",this.m_language);
	}


}
