package de.janrufmonitor.service.client.http.simple.handler;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.event.IEventBroker;
import de.janrufmonitor.framework.event.IEventConst;
import de.janrufmonitor.framework.event.IEventSender;
import de.janrufmonitor.service.client.request.handler.ClientCallMap;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.simple.handler.AbstractHandler;
import de.janrufmonitor.xml.transformation.XMLSerializer;

public class IdentifiedCall extends AbstractHandler implements IEventSender {
	
	private class HandlerThread implements Runnable {

		ICall m_c;
		IEventSender m_s;
		
		public HandlerThread(ICall call, IEventSender s) {
			this.m_c = call;
			this.m_s = s;
		}

		public void run() {
			if (getRuntime().getMsnManager().isMsnMonitored(
					this.m_c.getMSN()
				)) {

				IEventBroker evb = getRuntime().getEventBroker();
				evb.register(this.m_s);
			
				evb.send(this.m_s, evb.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL, this.m_c));
				m_logger.info("Send identified call event.");
			
				evb.unregister(this.m_s);
			}
		}		
	}
	
	public String getSenderID() {
		return "IdentifiedCallHandler";
	}

	public int getPriority() {
		return 0;
	}

	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException {
		try {
			ICall call = XMLSerializer.toCall(this.getPostData(req));
			if (call!=null) {
				ClientCallMap.getInstance().setCall((call.getCaller().getPhoneNumber().isClired()? IJAMConst.CLIRED_CALL : call.getCaller().getPhoneNumber().getTelephoneNumber())+"/"+call.getMSN().getMSN(), call);
				
				Thread sender = new Thread(new HandlerThread(call, this));
				sender.start();
				resp.getContentStreamForWrite().close();
			} else { 
				this.m_logger.severe("Invalid call transfered from server.");
			}
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}	
	}

}
