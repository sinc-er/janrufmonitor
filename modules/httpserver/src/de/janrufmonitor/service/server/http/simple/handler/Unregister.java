package de.janrufmonitor.service.server.http.simple.handler;

import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.simple.handler.AbstractHandler;
import de.janrufmonitor.service.server.Client;
import de.janrufmonitor.service.server.ClientRegistry;

public class Unregister extends AbstractHandler {

	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException {
		String client = "";
		String clientip = "";
		try {
			client = req.getInetAddress().getHostName();
			clientip = req.getInetAddress().getHostAddress();
			if (clientip==null) throw new HandlerException("No valid IP address.", 403);
			if (client==null) client = "";
			
		} catch (Exception ex) {
			throw new HandlerException(ex.getMessage(), 500);
		}
		if (client.length()>0 || clientip.length()>0) {
			try {
				String s_port = req.getParameter(Unregister.PARAMETER_CLIENT_PORT);
				if (s_port==null)
					throw new HandlerException("No client call back port.", 403);
				
				int port = 0;
				if (s_port.length()>0) {
					port = Integer.parseInt(s_port);
				}
				this.m_logger.info("Unregistering client "+client+":"+port);
				ClientRegistry.getInstance().unregister(new Client(client, clientip, port));
				resp.getContentStreamForWrite().close();
			} catch (Exception e) {
				throw new HandlerException(e.getMessage(), 500);
			}
		}	
	}

}
