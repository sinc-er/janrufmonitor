package de.janrufmonitor.service.config;

import java.util.List;
import java.util.logging.Level;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.AbstractConfigurableService;
import de.janrufmonitor.ui.jface.wizards.InitializerWizard;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTExecuter;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class ConfigService extends AbstractConfigurableService {

    private String ID = "ConfigService";
    private String NAMESPACE = "service.ConfigService";

    private IRuntime m_runtime;
    
    public ConfigService() {
        super();
        this.getRuntime().getConfigurableNotifier().register(this);
    }
    
	public String getNamespace() {
		return this.NAMESPACE;
	}

	public String getID() {
		return this.ID;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}
	
	public List getDependencyServices() {
		List dependency = super.getDependencyServices();
		dependency.add("TrayIcon");
		return dependency;
	}

	public void startup() {
		super.startup();
		
		
		if (this.isEnabled()) {
			new SWTExecuter() {
				protected void execute() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						m_logger.log(Level.SEVERE, e.getMessage(), e);
					}
					
					boolean filesInstalled = (System.getProperty("jam.installer.restart", "false").equalsIgnoreCase("true") ? true : false);
					
					if (filesInstalled) return;
					
					InitializerWizard id = new InitializerWizard();
					WizardDialog.setDefaultImage(SWTImageManager.getInstance(getRuntime()).get(IJAMConst.IMAGE_KEY_PIM_ICON));
					WizardDialog dlg = new WizardDialog(new Shell(DisplayManager.getDefaultDisplay()), id);
				    dlg.open();
				    if (dlg.getReturnCode() == WizardDialog.OK) {
				    	getRuntime().getConfigManagerFactory().getConfigManager().saveConfiguration();
				    }
				}
			}.start();
		}
		
	}

}
