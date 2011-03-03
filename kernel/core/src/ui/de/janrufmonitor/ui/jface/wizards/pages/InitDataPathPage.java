package de.janrufmonitor.ui.jface.wizards.pages;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.controls.DirectoryFieldEditor;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.io.Stream;

public class InitDataPathPage extends AbstractPage {

	private String NAMESPACE = "ui.jface.wizards.pages.InitDataPathPage";

	private String m_oldDatapath;
	private String m_newDatapath;
	private IRuntime m_runtime;
	
	public InitDataPathPage(String f) {
		super(InitDataPathPage.class.getName());
		this.m_oldDatapath = f;
	}

	public boolean performFinish() {
		if (m_newDatapath==null || m_newDatapath.trim().length()==0 ||m_newDatapath.equalsIgnoreCase(m_oldDatapath)) {
			try {
				Thread.sleep(550);
			} catch (InterruptedException e) {
				m_logger.log(Level.SEVERE, e.getMessage(), e);
			}
			return true;
		}
		
		Properties paths = new Properties();
		File newDataPath = new File(m_newDatapath);
		if (!newDataPath.exists()) newDataPath.mkdirs();
		
		File pathFile = new File(PathResolver.getInstance(getRuntime()).getInstallDirectory(), ".paths");
		if (pathFile.exists() && pathFile.isFile()) {
			try {
				FileInputStream in = new FileInputStream(pathFile);
				paths.load(in);
				in.close();
				paths.put(IJAMConst.PATHKEY_USERDATAPATH, newDataPath.getAbsolutePath());
			} catch (FileNotFoundException e) {
				m_logger.log(Level.SEVERE, e.getMessage(), e);
				return false;
			} catch (IOException e) {
				m_logger.log(Level.SEVERE, e.getMessage(), e);
				return false;
			}
		} else {
			paths.put(IJAMConst.PATHKEY_USERDATAPATH, newDataPath.getAbsolutePath());
		}
		
		try {
			FileOutputStream out = new FileOutputStream(pathFile);
			paths.store(out, "");
			out.close();
		} catch (FileNotFoundException e) {
			m_logger.log(Level.SEVERE, e.getMessage(), e);
			return false;
		} catch (IOException e) {
			m_logger.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
		
		// shutting down data containing units
		getRuntime().getMonitorListener().shutdown();
		getRuntime().getCallerManagerFactory().shutdown();
		getRuntime().getCallManagerFactory().shutdown();
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			m_logger.log(Level.SEVERE, e.getMessage(), e);
			return false;
		}
		
		// do the copy from old to new location
		List filesToCopy = getFiles(new File(m_oldDatapath));
		for (int i=0;i<filesToCopy.size();i++) {
			try {
				copyFile((File) filesToCopy.get(i), newDataPath);
				((File) filesToCopy.get(i)).delete();
				((File) filesToCopy.get(i)).getParentFile().deleteOnExit();
			} catch (IOException e) {
				m_logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		
		PathResolver.getInstance(getRuntime()).initialize();
		
		// starting up data containing units
		getRuntime().getCallerManagerFactory().startup();
		getRuntime().getCallManagerFactory().startup();
		getRuntime().getMonitorListener().startup();
		
		return true;
	}
	
	private void copyFile(File sourceFile, File targetFolder) throws IOException {
		if (!sourceFile.exists()) return;
		
		File newFile = new File(targetFolder, sourceFile.getAbsolutePath().substring(this.m_oldDatapath.length()));
		newFile.getParentFile().mkdirs();
		if (newFile.exists()) {
			this.m_logger.warning("File "+newFile.getAbsolutePath()+" alrteady exists. Overwrite blocked.");
		} else {
			FileInputStream in = new FileInputStream(sourceFile);
			FileOutputStream out = new  FileOutputStream(newFile);
			Stream.copy(in, out, true);
		}
	}

	private List getFiles(File folder) {
		List l = new ArrayList();
		if (folder.isDirectory()) {
			File[] files = folder.listFiles();
			for (int i=0;i<files.length;i++) {
				l.addAll(this.getFiles(files[i]));
			}
		}
		if (folder.isFile()) {
			l.add(folder);
		}
		return l;
	}

	
	
	public void createControl(Composite parent) {
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));

		Composite c = new Composite(parent, SWT.NONE);
	    c.setLayout(new GridLayout(1, false));
	    c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	   DirectoryFieldEditor dfe = new DirectoryFieldEditor(
			   this.m_i18n.getString(getNamespace(), "name", "label", this.m_language)
			   , this.m_i18n.getString(getNamespace(), "label", "label", this.m_language)
			   , this.m_i18n.getString(getNamespace(), "message", "label", this.m_language), 
			   c);
	   dfe.setStringValue(m_oldDatapath);
	   
	   dfe.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent e) {
				m_newDatapath = (String)e.data;
			}
	   	});
	   
	   dfe.setPropertyChangeListener(new IPropertyChangeListener() {

		public void propertyChange(PropertyChangeEvent e) {
			m_newDatapath = (String)e.getNewValue();
		}
		   
	   });
        

	    setPageComplete(isComplete());
	    setControl(c);
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
