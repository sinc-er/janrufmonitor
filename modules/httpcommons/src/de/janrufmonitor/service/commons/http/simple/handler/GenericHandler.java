package de.janrufmonitor.service.commons.http.simple.handler;

import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.handler.HandlerException;

public class GenericHandler extends AbstractHandler {

	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException {
		if (!resp.isHandled())
			throw new HandlerException("Unhandled request.", 404);
	}
}
