package de.janrufmonitor.application.console.command;

import java.util.List;

import de.janrufmonitor.framework.command.AbstractConsoleCommand;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.IService;
import de.janrufmonitor.service.server.Client;
import de.janrufmonitor.service.server.ClientRegistry;

public class ConsoleServerStatus extends AbstractConsoleCommand {

	private String ID = "serverstatus";
	private String NAMESPACE = "application.console.command.ConsoleServerStatus";
	
	private IRuntime m_runtime;

	public IRuntime getRuntime() {
		if (m_runtime==null)
			m_runtime = PIMRuntime.getInstance();
		return m_runtime;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public void execute() {
		System.out.println("Server Status:");
		System.out.println("--------------");
		System.out.println();
		IService server = getRuntime().getServiceFactory().getService("Server");
		if (server!=null && server.isRunning()) {
			System.out.println("Server started on Port "+getRuntime().getConfigManagerFactory().getConfigManager().getProperty("service.Server", "port"));
			System.out.println();
		}
		
		ClientRegistry cr = ClientRegistry.getInstance();
		
		System.out.println("Connected clients: "+cr.getClientCount());
		System.out.println();
		if (cr.getClientCount()>0) {
			System.out.println("Client details:");
			System.out.println("---------------");
			System.out.println();
			List clients = cr.getAllClients();
			Client c = null;
			for (int i=0;i<clients.size();i++) {
				c = (Client)clients.get(i);
				System.out.println("Client: "+c.getClientName()+" ("+c.getClientIP()+")");
				System.out.println("Port: "+c.getClientPort());
				System.out.println("Send [bytes]: "+c.getByteSend());
				System.out.println("Received [bytes]: "+c.getByteReceived());
				System.out.println("---");
			}
		}
	}

	public boolean isExecutable() {
		return true;
	}

	public boolean isExecuting() {
		return false;
	}

	public String getID() {
		return this.ID;
	}

	public String getLabel() {
		return "Server Status       - SERVERSTATUS + <ENTER>";
	}

}
