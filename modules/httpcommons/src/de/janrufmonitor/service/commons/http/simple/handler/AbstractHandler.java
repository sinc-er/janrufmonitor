package de.janrufmonitor.service.commons.http.simple.handler;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import simple.http.Request;
import simple.http.Response;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.commons.CommonsConst;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.handler.Handler;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.simple.HttpLogger;

public abstract class AbstractHandler implements Handler, SimpleHandler, CommonsConst {
	
	protected Logger m_logger;
	private IRuntime m_runtime;
	
	public AbstractHandler() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
	}
	
	public void handle(Request req, Response resp) {
		try {
			this.handleWithException(
				new de.janrufmonitor.service.commons.http.simple.Request(req),
				new de.janrufmonitor.service.commons.http.simple.Response(resp));
		} catch (HandlerException e) {
			if (e.getCode()!=404)
				this.m_logger.warning(e.getMessage());
			handle(req, resp, e.getCode());
		}
		HttpLogger.getInstance().write(req, resp);
		if (HttpLogger.getInstance().isDumpEnabled()) {
			HttpLogger.getInstance().dump(req);
			HttpLogger.getInstance().dump(resp);
		}
	}
	
	public synchronized void handle(Request req, Response resp, int errorcode) {
		resp.setCode(errorcode);
		switch (errorcode) {
			case 200: resp.setText("OK");break;
			case 404: resp.setText("Not Found");break;
			case 401: resp.setText("Unauthorized");break;
			case 403: resp.setText("Forbidden");break;
			case 501: resp.setText("Not Implemented");break;
			case 503: resp.setText("Service Unavailable");break;
			default: {
				resp.setCode(500);
				resp.setText("Internal Server Error");
			}
		}
		try {
			if (!resp.isCommitted())
				resp.getOutputStream().close();
		} catch (IOException e) {
			this.m_logger.warning(e.getMessage());
		}
	}
	
	protected IRuntime getRuntime() {
		if (this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
			
		return this.m_runtime;
	}
	
	protected String getPostData(IHttpRequest req) throws Exception {
		InputStream in = new BufferedInputStream(req.getContentStreamForRead());
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[Short.MAX_VALUE];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1) {
			bos.write(buffer, 0, bytesRead);
		}  
		in.close(); 
		return bos.toString();
	}

}
