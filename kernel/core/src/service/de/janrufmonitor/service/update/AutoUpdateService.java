package de.janrufmonitor.service.update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.command.ICommand;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.AbstractConfigurableService;

public class AutoUpdateService extends AbstractConfigurableService {

	private String ID = "AutoUpdateService";
    private String NAMESPACE = "service.AutoUpdateService";

    private IRuntime m_runtime;
    
    public AutoUpdateService() {
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
	
    public void setConfiguration(Properties configuration) {
		super.setConfiguration(configuration);
		
		String lup = getRuntime().getConfigManagerFactory().getConfigManager().getProperty(NAMESPACE, "lastupdatecheck");
		lup = (lup==null || lup.trim().length()==0 ? "0" : lup);
		long lastupdatetime = Long.parseLong(lup);
		
		if (lastupdatetime>0) {
			if (System.currentTimeMillis()-lastupdatetime>=(180*24*3600*1000L)) { // 180*24*3600*1000 ^= 180 Tage
				triggerUpdate();				
			}
		} else {
			getRuntime().getConfigManagerFactory().getConfigManager().setProperty(NAMESPACE,  "lastupdatecheck", Long.toString(System.currentTimeMillis()));
			getRuntime().getConfigManagerFactory().getConfigManager().saveConfiguration();
		}
	}

	private void triggerUpdate() {
		this.m_logger.info("Starting automatic Update-Check...");
		UpdateManager m = new UpdateManager();
		List u = m.getUpdates();
		if (u!=null && u.size()>0) {
			PropagationFactory.getInstance().fire(
					new Message(Message.INFO, 
							getRuntime().getI18nManagerFactory().getI18nManager().getString(getNamespace(),
							"title", "label",
							getRuntime().getConfigManagerFactory().getConfigManager().getProperty(
									IJAMConst.GLOBAL_NAMESPACE,
									IJAMConst.GLOBAL_LANGUAGE
								)), 
							new Exception(getRuntime().getI18nManagerFactory().getI18nManager()
									.getString(getNamespace(),
											"newupdates", "label",
											getRuntime().getConfigManagerFactory().getConfigManager().getProperty(
													IJAMConst.GLOBAL_NAMESPACE,
													IJAMConst.GLOBAL_LANGUAGE
												)))),
					"Tray");	
			
			this.m_logger.info("Automatic Update-Check is executed for "+u.size()+" modules.");
			ICommand updateCommand = this.getRuntime().getCommandFactory().getCommand("UpdatesCommand");
			if (updateCommand!=null && updateCommand.isExecutable()) {
				try {
					this.m_logger.info("Executing UpdateCommand...");
					Map parameter = new HashMap(1);
					parameter.put("preload", Boolean.TRUE);
					updateCommand.setParameters(parameter);
					updateCommand.execute();
				} catch (Exception e) {
					this.m_logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
		} else {
			this.m_logger.info("No updates available.");
		}
		getRuntime().getConfigManagerFactory().getConfigManager().setProperty(NAMESPACE,  "lastupdatecheck", Long.toString(System.currentTimeMillis()));
		getRuntime().getConfigManagerFactory().getConfigManager().saveConfiguration();
	}
    
    public void startup() {
		super.startup();

		if (this.isEnabled()) {
			this.triggerUpdate();
		}
	}

}
