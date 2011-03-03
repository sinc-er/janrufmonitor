package de.janrufmonitor.service.client.http.simple.handler;

import de.janrufmonitor.framework.event.IEventBroker;
import de.janrufmonitor.framework.event.IEventConst;
import de.janrufmonitor.framework.event.IEventSender;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.simple.handler.AbstractHandler;

public class ApplicationReady extends AbstractHandler implements IEventSender {
	
	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException {
		try {
			IEventBroker evb = this.getRuntime().getEventBroker();
			evb.register(this);
			
			evb.send(this, evb.createEvent(IEventConst.EVENT_TYPE_APPLICATION_READY));
			this.m_logger.info("Send application ready event.");
			
			evb.unregister(this);
			resp.getContentStreamForWrite().close();
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
	}

	public String getSenderID() {
		return "ApplicationReadyHandler";
	}

	public int getPriority() {
		return 0;
	}

}
