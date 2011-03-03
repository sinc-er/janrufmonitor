package de.janrufmonitor.service.client.state;

public interface IClientStateMonitor {
	
	public final static int NETWORK_ERROR = 500;
	
	public final static int CONNECTION_OK = 200;
	public final static int CONNECTION_CLOSED = 201;

	public final static int SERVER_NOT_AUTHORIZED = 301;
	public final static int SERVER_NOT_FOUND = 302;
	public final static int SERVER_UNKNOWN_ERROR = 303;
	public final static int SERVER_SHUTDOWN = 399;
	
	public String getID();
	
	public void acceptState(int state, String message);

}
