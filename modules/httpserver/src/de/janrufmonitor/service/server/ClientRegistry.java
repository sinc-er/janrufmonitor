package de.janrufmonitor.service.server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.framework.IJAMConst;


public class ClientRegistry {
	
	private Map m_clients;
	private Map m_clientIps;
	private long m_timeout = (1000*60*60*24); // 24h
	private Logger m_logger;

	private static ClientRegistry m_instance = null;

	private ClientRegistry() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		this.m_clients = Collections.synchronizedMap(new HashMap());
		this.m_clientIps = Collections.synchronizedMap(new HashMap());
		this.m_logger.info("Created new ClientRegistry.");
	}
	
	public static synchronized ClientRegistry getInstance() {
		if (ClientRegistry.m_instance==null)
			ClientRegistry.m_instance = new ClientRegistry();
		return ClientRegistry.m_instance;
	}

	public void register(Client c) {
		if (!this.m_clients.containsKey(c.getClientName().toLowerCase()) && !this.m_clientIps.containsKey(c.getClientIP().toLowerCase())) {
			this.m_clients.put(c.getClientName().toLowerCase(), c);
			this.m_clientIps.put(c.getClientIP().toLowerCase(), c);
			
			PropagationFactory.getInstance().fire(
					new Message(Message.INFO,
					"service.Server",
					"connected",
					new String[] {c.getClientName(), c.getClientIP()},
					new Throwable(c.getClientName()+ " ("+c.getClientIP()+")"))
				);
			
		} else 
			this.m_logger.info("Client "+c+" already registered.");
		
		this.m_logger.info(this.m_clients.size()+" Clients registered in total.");	
	}
	
	public void unregister(Client c) {
		this.m_clients.remove(c.getClientName().toLowerCase());
		this.m_clientIps.remove(c.getClientIP().toLowerCase());
		//added 2008/04/07: to be sure no redundant ip is still registered
		this.m_clients.remove(c.getClientIP().toLowerCase());
		
		this.m_logger.info(this.m_clients.size()+" Clients registered in total.");	
	}
	
	public boolean contains(String name) {
		return (this.m_clients.containsKey(name.toLowerCase()) || this.m_clientIps.containsKey(name.toLowerCase()));
	}
	
	public Client getClient(String name) {
		Client c = (Client) this.m_clients.get(name.toLowerCase());
		if (c==null)
			c = (Client)this.m_clientIps.get(name.toLowerCase());
		return c;
	}
	
	public List getAllClients() {
		List l = new ArrayList();
		synchronized (this.m_clients) {
			Iterator iter = this.m_clients.keySet().iterator();
			while (iter.hasNext()) {
				Client c = this.getClient((String)iter.next());
				if (c!=null)
					l.add(c);
			}
		}
		this.m_logger.info("List of all registered clients: "+l.toString());
		return l;
	}
	
	public int getClientCount() {
		return this.m_clients.size();
	}
	
	public void invalidate() {
		List l = new ArrayList();
		long current = new Date().getTime();
		synchronized (this.m_clients) {
			Iterator iter = this.m_clients.keySet().iterator();
			while (iter.hasNext()) {
				Client c = this.getClient((String)iter.next());
				if (c!=null && (c.getTimestamp() + this.m_timeout) < current) {
					this.m_logger.info("Client "+c+"  has timeout reached. Will be removed from ClientRegistry.");	
					l.add(c);
				}
			}
		}
		for (int i=0;i<l.size();i++) {
			this.unregister((Client)l.get(i));
		}
	}
	
	public void shutdown() {
		this.m_clientIps = null;
		this.m_clients = null;
		ClientRegistry.m_instance = null;
	}

}
