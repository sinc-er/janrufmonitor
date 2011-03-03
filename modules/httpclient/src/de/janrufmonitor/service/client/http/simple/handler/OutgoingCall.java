package de.janrufmonitor.service.client.http.simple.handler;

import java.util.Date;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICip;
import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.framework.IName;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.framework.event.IEvent;
import de.janrufmonitor.framework.event.IEventBroker;
import de.janrufmonitor.framework.event.IEventCondition;
import de.janrufmonitor.framework.event.IEventConst;
import de.janrufmonitor.framework.event.IEventSender;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.client.request.handler.ClientCallMap;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.simple.handler.AbstractHandler;

public class OutgoingCall extends AbstractHandler implements IEventSender {

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

				IEventBroker eventBroker = PIMRuntime.getInstance().getEventBroker();
				eventBroker.register(this.m_s);
			
				IEventCondition eventCond = eventBroker.createEventCondition();
				eventCond.addCondition("MSN", this.m_c.getMSN().getMSN());
				eventCond.addCondition("CIP", this.m_c.getCIP().getCIP());
	
				IEvent ev = eventBroker.createEvent(IEventConst.EVENT_TYPE_OUTGOINGCALL, this.m_c, eventCond);
				eventBroker.send(this.m_s, ev);  
				m_logger.info("Send outgoing call event.");
	     
				eventBroker.unregister(this.m_s);
			}
		}
		
	}
	
	public String getSenderID() {
		return "OutgoingCall";
	}

	public int getPriority() {
		return 0;
	}

	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException {
		IPhonenumber pn = null;
		try {
			String phone = req.getParameter(OutgoingCall.PARAMETER_NUMBER);
			if (phone==null || phone.length()==0)
				pn = PIMRuntime.getInstance().getCallerFactory().createPhonenumber(true);
			else
				pn = PIMRuntime.getInstance().getCallerFactory().createPhonenumber(phone);
			
			IName name = PIMRuntime.getInstance().getCallerFactory().createName("", "");
			ICaller c = PIMRuntime.getInstance().getCallerFactory().createCaller(
				name,
				pn
			);
			
			ICip cip = PIMRuntime.getInstance().getCallFactory().createCip(
				req.getParameter(OutgoingCall.PARAMETER_CIP),
				""
			);
			
			IMsn msn = PIMRuntime.getInstance().getCallFactory().createMsn(
				req.getParameter(OutgoingCall.PARAMETER_MSN),
				""
			);
			
			Date date = new Date(Long.parseLong(req.getParameter(OutgoingCall.PARAMETER_DATE)));

			ICall call = PIMRuntime.getInstance().getCallFactory().createCall(
				c,
				msn,
				cip,
				date
			);
			
			call.setAttribute(this.getRuntime().getCallFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_STARTRING, Long.toString(new Date().getTime())));
			call.setAttribute(this.getRuntime().getCallFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS, IJAMConst.ATTRIBUTE_VALUE_OUTGOING));	
			ClientCallMap.getInstance().setCall((pn.isClired() ? IJAMConst.CLIRED_CALL : pn.getTelephoneNumber())+"/"+msn.getMSN(), call);
			
			Thread sender = new Thread(new HandlerThread(call, this));
			sender.start();
			resp.getContentStreamForWrite().close();
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
	}

}
