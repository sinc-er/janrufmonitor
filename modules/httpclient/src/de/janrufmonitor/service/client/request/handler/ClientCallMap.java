package de.janrufmonitor.service.client.request.handler;

import java.util.HashMap;
import java.util.Map;

import de.janrufmonitor.framework.ICall;

public class ClientCallMap {

	private static ClientCallMap m_instance;
	private Map m_activeCalls;

	private ClientCallMap() {
		this.m_activeCalls = new HashMap();
	}

	public static synchronized ClientCallMap getInstance() {
		if (m_instance == null) {
			m_instance = new ClientCallMap();
		}
		return m_instance;
	}
	
	public ICall getCall(Object key) {
		if (this.m_activeCalls.containsKey(key)) {
			return (ICall)this.m_activeCalls.get(key);
		}
		return null;
	}
	
	public boolean removeCall(Object key) {
		Object o = this.m_activeCalls.remove(key);
		if (o==null) return false;
		return true;
	}
	
	public void setCall(Object key, ICall call) {
		this.m_activeCalls.put(key, call);
	}
	
	public void clearCalls() {
		this.m_activeCalls.clear();
	}
}
