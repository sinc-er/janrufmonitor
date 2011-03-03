package de.janrufmonitor.service.server.http.simple.handler;

import de.janrufmonitor.framework.event.IEventBroker;
import de.janrufmonitor.framework.event.IEventConst;
import de.janrufmonitor.framework.event.IEventSender;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.simple.handler.AbstractHandler;

public class Reject extends AbstractHandler implements IEventSender {

	private class HandlerThread implements Runnable {

		IEventSender m_s;

		public HandlerThread(IEventSender s) {
			this.m_s = s;
		}

		public void run() {
			IEventBroker evb = getRuntime().getEventBroker();
			evb.register(this.m_s);
		
			evb.send(this.m_s, evb.createEvent(IEventConst.EVENT_TYPE_CALLREJECTED));
			m_logger.info("Send reject event.");
		
			evb.unregister(this.m_s);
		}
	}
	
	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException {
		Thread sender = new Thread(new HandlerThread(this));
		sender.start();
		
		try {
			resp.getContentStreamForWrite().close();
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
	}

	public String getSenderID() {
		return "RejectHandler";
	}

	public int getPriority() {
		return 0;
	}

}
