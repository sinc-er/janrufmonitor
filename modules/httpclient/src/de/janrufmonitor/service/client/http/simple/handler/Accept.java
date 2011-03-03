package de.janrufmonitor.service.client.http.simple.handler;

import java.util.Date;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.event.IEventBroker;
import de.janrufmonitor.framework.event.IEventConst;
import de.janrufmonitor.framework.event.IEventSender;
import de.janrufmonitor.service.client.request.handler.ClientCallMap;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.simple.handler.AbstractHandler;

public class Accept extends AbstractHandler implements IEventSender {

	private class HandlerThread implements Runnable {

		IEventSender m_s;
		ICall m_c;

		public HandlerThread(ICall c, IEventSender s) {
			this.m_s = s;
			this.m_c = c;
		}

		public void run() {
			IEventBroker evb = getRuntime().getEventBroker();
			evb.register(this.m_s);
		
			evb.send(this.m_s, evb.createEvent(IEventConst.EVENT_TYPE_CALLACCEPTED, this.m_c));
			m_logger.info("Send accept event.");
		
			evb.unregister(this.m_s);
		}
	}

	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException {
		try {
			String phone = req.getParameter(Accept.PARAMETER_NUMBER);
			String msn = req.getParameter(Accept.PARAMETER_MSN);
		
			if (phone==null || phone.length()==0) phone = IJAMConst.CLIRED_CALL;

			ICall call = ClientCallMap.getInstance().getCall(phone+"/"+msn);
			this.m_logger.info("Key for ClientCallMap: "+phone+"/"+msn);
			if (call!=null){
				long end = new Date().getTime();
				long start = this.getStartTime(call);
				call.setAttribute(this.getRuntime().getCallFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_ENDRING, Long.toString(end)));
				call.setAttribute(this.getRuntime().getCallFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_RINGDURATION, Long.toString((end-start)/1000)));
				call.setAttribute(this.getRuntime().getCallFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS, IJAMConst.ATTRIBUTE_VALUE_ACCEPTED));
			
				if (ClientCallMap.getInstance().removeCall(phone+"/"+msn))
					this.m_logger.info("Successfully found call: "+phone+"/"+msn);
			
				Thread sender = new Thread(new HandlerThread(call, this));
				sender.start();
				resp.getContentStreamForWrite().close();
			}
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
	}

	public String getSenderID() {
		return "AcceptHandler";
	}

	public int getPriority() {
		return 0;
	}

	private long getStartTime(ICall c) {
		IAttribute startring = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_STARTRING);
		if (startring==null) return 0;
		return Long.parseLong(startring.getValue());
	}
}
