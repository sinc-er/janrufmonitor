package de.janrufmonitor.service.client.http.simple.handler;

import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.IService;
import de.janrufmonitor.service.client.Client;
import de.janrufmonitor.service.client.state.ClientStateManager;
import de.janrufmonitor.service.client.state.IClientStateMonitor;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.simple.handler.AbstractHandler;

public class Shutdown extends AbstractHandler{

	private class DisconnectThread implements Runnable {
		public void run() {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				m_logger.severe(e.getMessage());
			}
			
			Client c = this.getClientService();
			if (c!=null  && c.isConnected()) {
				boolean dc = c.disconnect();
				m_logger.info("Client was "+(dc ? "" : "not ")+"disconnected successfully.");
			}
		}
		
		private Client getClientService() {
			IService client = PIMRuntime.getInstance().getServiceFactory().getService("Client");
			if (client!=null && client instanceof Client) {
				return (Client)client;
			}
			return null;
		}
	}

	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException {
		ClientStateManager.getInstance().fireState(IClientStateMonitor.SERVER_SHUTDOWN, "");

		Thread t = new Thread(new DisconnectThread());
		t.start();
		
		try {
			resp.getContentStreamForWrite().close();
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
		
	}

}
