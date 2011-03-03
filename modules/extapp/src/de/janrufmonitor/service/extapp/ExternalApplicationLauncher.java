package de.janrufmonitor.service.extapp;

import java.io.IOException;
import java.util.StringTokenizer;
import java.util.logging.Level;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.event.IEventBroker;
import de.janrufmonitor.framework.event.IEventConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.AbstractReceiverConfigurableService;
import de.janrufmonitor.util.formatter.Formatter;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.string.StringUtils;

public class ExternalApplicationLauncher extends AbstractReceiverConfigurableService {

	private String ID = "ExternalApplicationLauncher";
    private String NAMESPACE = "service.ExternalApplicationLauncher";
    
    private String CONFIG_APPLICATION = "_extapp";
    private String CONFIG_OUTGOING = "outgoing";
    
	private IRuntime m_runtime;
       
    public ExternalApplicationLauncher() {
    	super();
    	this.getRuntime().getConfigurableNotifier().register(this);	
    }

	public void startup() {
		super.startup();
        IEventBroker eventBroker = this.getRuntime().getEventBroker();
        eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL));
        if (this.m_configuration.getProperty(CONFIG_OUTGOING, "false").equalsIgnoreCase("true"))
        	eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_OUTGOING_CALL));
        this.m_logger.info("ExternalApplicationLauncher is started ...");		
	}

	public void shutdown() {
		super.shutdown();
		IEventBroker eventBroker = this.getRuntime().getEventBroker();
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_OUTGOING_CALL));
        this.m_logger.info("ExternalApplicationLauncher is shut down ...");
	}

	public String getID() {
		return this.ID;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}
	
	public void receivedValidRule(ICall aCall) {
		String msn = aCall.getMSN().getMSN();
		
		String command = this.m_configuration.getProperty(msn + this.CONFIG_APPLICATION, "");
		
		if (command.length()==0) {
			this.m_logger.info("No command configured for MSN "+msn+". Taking default command.");
			command = this.m_configuration.getProperty("default" + this.CONFIG_APPLICATION, "");
		} else {
			this.m_logger.info("Taking command configured for MSN "+msn+".");
		}
		
		if (command.length()>0) {
			try {
				command = PathResolver.getInstance(this.getRuntime()).resolve(command);
				Formatter f = Formatter.getInstance(this.getRuntime());
				
				StringTokenizer st = new StringTokenizer(command);
				String[] env = new String[st.countTokens()];
				int i=0;
				while (st.hasMoreTokens()) {
					env[i] = st.nextToken();
					env[i] = f.parse(env[i], aCall);
					i++;
				}
				
				command = f.parse(command, aCall);
				
				// 2009/04/17: added path resolution to command if %imagepath% or else is used
				if (command.indexOf("%")>-1)
					command = PathResolver.getInstance(getRuntime()).resolve(command);

				if (command.toLowerCase().indexOf(".bat")>0 || command.toLowerCase().indexOf(".cmd")>0)
					command = "cmd /c start "+command;	
				if (this.m_logger.isLoggable(Level.INFO)) {
					this.m_logger.info("Launching command: \""+command+"\" for call "+aCall.toString());
					String text = this.getRuntime().getI18nManagerFactory().getI18nManager().getString(getNamespace(),
							"executed", "label",
							this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(
									IJAMConst.GLOBAL_NAMESPACE,
									IJAMConst.GLOBAL_LANGUAGE
								));
					PropagationFactory.getInstance().fire(
							new Message(Message.INFO, 
									this.getRuntime().getI18nManagerFactory().getI18nManager().getString(getNamespace(),
									"title", "label",
									this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(
											IJAMConst.GLOBAL_NAMESPACE,
											IJAMConst.GLOBAL_LANGUAGE
										)), 
									new Exception(StringUtils.replaceString(text, "{%1}", command))),
							"Tray");	
				}
				
				Runtime.getRuntime().exec(command);
			} catch (IOException e) {
				this.m_logger.severe(e.getMessage());
			}
		} else {
			this.m_logger.info("No command configured for MSN "+msn+" and no default command available.");
		}
	}
}
