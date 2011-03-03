package de.janrufmonitor.service.commons.http.simple.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.handler.HandlerException;

public class Ping extends AbstractHandler {

	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws de.janrufmonitor.service.commons.http.handler.HandlerException {
		try {
			resp.setCode(200);
			resp.setParameter("Content-type", "text/html");
			OutputStream ps = resp.getContentStreamForWrite();
			ps.write(("<html><head><title>Ping check</title></head><body>PING OK - Service is up and running @ "+new Date().toString()+"</body></html>").getBytes());
			ps.flush();
			ps.close();
		} catch (IOException e) {
			throw new HandlerException(e.getMessage(), 500);
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
	}

}
