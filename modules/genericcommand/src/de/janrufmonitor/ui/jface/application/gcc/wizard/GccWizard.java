package de.janrufmonitor.ui.jface.application.gcc.wizard;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.installer.InstallerConst;
import de.janrufmonitor.repository.zip.ZipArchive;
import de.janrufmonitor.repository.zip.ZipArchiveException;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.gcc.wizard.pages.GccPage;
import de.janrufmonitor.ui.jface.wizards.AbstractWizard;
import de.janrufmonitor.ui.jface.wizards.pages.AbstractPage;

public class GccWizard extends AbstractWizard {

	private String NAMESPACE = "ui.jface.application.gcc.wizard.GccWizard"; 

	private IRuntime m_runtime;
	private AbstractPage[] m_pages;
	
	public GccWizard() {
		super();
		setWindowTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));
		this.m_pages = new AbstractPage[1];
		this.m_pages[0] = new GccPage(null);
		
		this.addPage(this.m_pages[0]);
	}


	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return GccWizard.class.getName();
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public boolean performFinish() {
		Map data = ((GccPage)this.m_pages[0]).getResult();

		long id = System.currentTimeMillis();
		
		// descriptor data
		Properties descriptor = new Properties();
		descriptor.setProperty(InstallerConst.DESCRIPTOR_REQUIRED_MAJOR_VERSION, IJAMConst.VERSION_MAJOR);
		descriptor.setProperty(InstallerConst.DESCRIPTOR_REQUIRED_MINOR_VERSION, IJAMConst.VERSION_MINOR);
		descriptor.setProperty(InstallerConst.DESCRIPTOR_REQUIRED_PATCH_VERSION, IJAMConst.VERSION_PATCH);
		
		descriptor.setProperty(InstallerConst.DESCRIPTOR_TYPE, "application");
		descriptor.setProperty(InstallerConst.DESCRIPTOR_VERSION, "1.0.0");
		descriptor.setProperty(InstallerConst.DESCRIPTOR_NAME, "mod-app-"+id);
		descriptor.setProperty(InstallerConst.DESCRIPTOR_NAMESPACE, "gcc.action."+id);
		descriptor.setProperty(InstallerConst.DESCRIPTOR_RESTART, "false");
		descriptor.setProperty(InstallerConst.DESCRIPTOR_REMOVE, "false");
		
		// inf data
		Properties inf = new Properties();
		inf.setProperty("ui.jface.application.ActionRegistry:action_"+id+":value", "de.janrufmonitor.ui.jface.application.action.GenericWebAction");
		inf.setProperty("ui.jface.application.ActionRegistry:action_"+id+":access", "system");
		inf.setProperty("gcc.action."+id+":url:value", (String) data.get("url"));
		
		Boolean check = (Boolean) data.get("editor");
		if (check!=null && check.booleanValue())
			inf.setProperty("+ui.jface.application.editor.Editor:popup_actions:value", id +",");
		check = (Boolean) data.get("journal");
		if (check!=null && check.booleanValue())
			inf.setProperty("+ui.jface.application.journal.Journal:popup_actions:value", id +",");
		
		check = (Boolean) data.get("dialog");
		if (check!=null && check.booleanValue()) {
			inf.setProperty("+ui.jface.application.dialog.Dialog:pluginlist:value", id +",");
			inf.setProperty("ui.jface.application.dialog.Dialog:"+id+":value", "de.janrufmonitor.ui.jface.application.dialog.GenericWebDialogPlugin");
		}

		// i18n data
		Properties i18n = new Properties();
		i18n.setProperty("gcc.action."+id+":title:label:de", (String) data.get("name"));
		i18n.setProperty("gcc.action."+id+":label:label:de", (String) data.get("name"));
		
		// create jam.zip archive
		File dir = new File((String) data.get("directory"));
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		ZipArchive z = new ZipArchive(dir.getAbsolutePath() + File.separator + "webCommand."+id+".jam.zip");
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
			z.add("install/gcc.action."+id+".inf", bin);
			
			// write i18n data
			bos = new ByteArrayOutputStream();
			i18n.store(bos, "");
			bos.flush();

			bin = new ByteArrayInputStream(bos.toByteArray());
			z.add("install/gcc.action."+id+".i18n", bin);
			
			
			z.close();
		} catch (ZipArchiveException e) {
			this.m_logger.severe(e.toString());
		} catch (IOException e) {
			this.m_logger.severe(e.toString());
		}
		
		return true;
	}

}
