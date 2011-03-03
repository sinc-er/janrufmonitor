package de.janrufmonitor.service.client.state;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClientStateManager {

	private static ClientStateManager m_instance;
	private Map m_clients;

	private ClientStateManager() {
		this.initialize();
	}
	
	public static synchronized ClientStateManager getInstance() {
		if (m_instance == null) {
			m_instance = new ClientStateManager();
		}
		return m_instance;
	}
	
	public void initialize() {
		this.m_clients = Collections.synchronizedMap(new HashMap());
	}
	
	public void register(IClientStateMonitor c){
		if (!this.m_clients.containsKey(c.getID())) {
			this.m_clients.put(c.getID(), c);
		}
	}
	
	public void unregister(IClientStateMonitor c) {
		if (this.m_clients.containsKey(c.getID())) {
			this.m_clients.remove(c.getID());
		}
	}
	
	public synchronized void fireState(int state, String message) {
		synchronized(this.m_clients) {
			Iterator iter = this.m_clients.keySet().iterator();
			while (iter.hasNext()) {
				String key = (String)iter.next();
				IClientStateMonitor c = (IClientStateMonitor)this.m_clients.get(key);
				if (c!=null)
					c.acceptState(state, message);
			}
		}	
	}

}
