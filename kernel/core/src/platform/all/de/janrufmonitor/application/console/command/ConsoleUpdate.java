package de.janrufmonitor.application.console.command;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.update.UpdateManager;
import de.janrufmonitor.util.io.OSUtils;
import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.command.AbstractConsoleCommand;
import de.janrufmonitor.framework.installer.InstallerConst;
import de.janrufmonitor.framework.installer.InstallerEngine;
import de.janrufmonitor.framework.installer.InstallerException;
import de.janrufmonitor.framework.monitor.IMonitor;


public class ConsoleUpdate extends AbstractConsoleCommand {
	
	private class StreamRequester {
		private String url;
		
		private InputStream in;
		private Logger m_logger;
		
		public StreamRequester(String url) {
			this.url = url;
			this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		}
		
		public void go() {
			StringBuffer agent = new StringBuffer();
			agent.append("jAnrufmonitor Console Update Manager ");
			agent.append(IJAMConst.VERSION_DISPLAY);
			
			List monitors = getRuntime().getMonitorListener().getMonitors();
			if (monitors.size()>0) {
				agent.append(" (");
				IMonitor m  = null;
				for (int i=0,j=monitors.size();i<j;i++) {
					m = (IMonitor) monitors.get(i);
					agent.append(m.getID());
					if ((i+1)<j)
						agent.append(", ");	
				}
				agent.append(";");
				if (OSUtils.isWindows()) {
					agent.append("Windows");
				} else if (OSUtils.isLinux()) {
					agent.append("Linux");
				} else {
					agent.append("unknown OS");
				}
				agent.append(")");
			}
			
			if (m_logger.isLoggable(Level.INFO))
				this.m_logger.info("User-Agent: "+agent.toString());
			
			
			try {
				URL url = new URL(this.url);
				URLConnection c = url.openConnection();
				c.setDoInput(true);
				c.setRequestProperty("User-Agent", agent.toString());
				c.connect();
				
				this.m_logger.info("Querying URL "+this.url);
				
				Object o = c.getContent();
				if (o instanceof InputStream) {
					this.m_logger.info("Content successfully retrieved from "+this.url);
					this.in =(InputStream) o;
				}				
			} catch (MalformedURLException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
				this.in = null;
			} catch (IOException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
				this.in = null;
				PropagationFactory.getInstance().fire(new Message(Message.ERROR, getNamespace(), "ioexception", e));				
			}
		}
		
		public InputStream getInputStream() {
			return this.in;
		}
	}
	
	private String ID = "update";
	private String NAMESPACE = "application.console.command.ConsoleUpdate";
	
	private IRuntime m_runtime;
	private boolean isExecuting; 

	public String getLabel() {
		return "Module update       - UPDATE <module-name> + <ENTER>";
	}
	
	public IRuntime getRuntime() {
		if (m_runtime==null)
			m_runtime = PIMRuntime.getInstance();
		return m_runtime;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public void execute() {
		this.isExecuting = true;
		
		if (this.getExecuteParams().length!=1) {
			System.out.println("ERROR: Parameters are invalid. Please specify a valid module for update.");
			this.isExecuting = false;
			return;
		}
		
		String modname = this.getExecuteParams()[0];
		List updates = new UpdateManager().getUpdates();
		
		if (updates.size()>0) {
			Properties p, f =null;
			for (int i=0; i<updates.size();i++) {
				p = (Properties) updates.get(i);
				if (p.getProperty(InstallerConst.DESCRIPTOR_NAME).equalsIgnoreCase(modname)) {
					f = p;
				}
			}
			if (f==null) {
				System.out.println("No updates found for module [ "+modname+" ].");
			} else {
				System.out.println("Installing update for module [ "+modname+" ]...");
				String url = f.getProperty(InstallerConst.DESCRIPTOR_UPDATE);
				String name = f.getProperty(InstallerConst.DESCRIPTOR_NAME) + "." +f.getProperty(InstallerConst.DESCRIPTOR_VERSION) + InstallerConst.EXTENSION_ARCHIVE;
				if (url!=null && url.length()>6) {
					StreamRequester sr = new StreamRequester(url);
					System.out.println("Downloading update for module [ "+modname+" ] from "+url);
					sr.go();
					try {
						InstallerEngine.getInstance().install(name, sr.getInputStream(), false);
						System.out.println("Installation of update for module [ "+modname+" ] successfully finished.");
						boolean b = Boolean.parseBoolean(f.getProperty(InstallerConst.DESCRIPTOR_RESTART, "false"));
						if (b) {
							System.out.println("Module can be used after restarting jAnrufmonitor.");
						}
					} catch (InstallerException e) {
						System.out.println("ERROR during installing update for module [ "+modname+" ]: "+e.getMessage());						
					}
				} else {
					System.out.println("ERROR: Downloading update for module [ "+modname+" ] from "+url+" failed.");				
				}
			}			
		} else {
			System.out.println("No update required all modules are up to date...");
		}
		System.out.println();
		this.isExecuting = false;
	}

	public boolean isExecutable() {
		return true;
	}

	public boolean isExecuting() {
		return this.isExecuting;
	}

	public String getID() {
		return this.ID;
	}

}
