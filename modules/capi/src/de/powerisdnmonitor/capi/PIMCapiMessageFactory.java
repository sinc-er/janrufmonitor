package de.powerisdnmonitor.capi;

import org.capi.capi20.Capi;
import org.capi.capi20.CapiException;
import org.capi.capi20.CapiMessage;

public class PIMCapiMessageFactory {

	public static PIMCapiMessage createListenReq(Capi capi, int appID) throws CapiException {
		PIMCapiMessage msg = (PIMCapiMessage) capi.createMessage(appID, CapiMessage.LISTEN_REQ, 1);
		msg.setDwordValue("controller", 1);
        
		// added: 2003/12/26 due to fax acceptance problems
		msg.setDwordValue("Info mask", 0x007F);

		// added: 2003/12/23 due to CIP detection
		msg.setDwordValue("CIP mask", 0x1FFF03FE);
		return msg;
	}
	
	public static PIMCapiMessage createConnectResp(Capi capi, int appID, Object plci, short cause) throws CapiException {
		return PIMCapiMessageFactory.createConnectResp(capi, appID, 1, plci, cause);
	}
	
	public static PIMCapiMessage createConnectResp(Capi capi, int appID, int msgID, Object plci, short cause) throws CapiException {
		PIMCapiMessage msg = (PIMCapiMessage) capi.createMessage(appID, PIMCapiMessage.CONNECT_RESP, msgID);
		msg.setValue("PLCI", plci);
		msg.setWordValue("Reject", cause);
		return msg;
	}	
	
	public static PIMCapiMessage createConnectRespForRejec(Capi capi, int appID, Object plci, short cause) throws CapiException {
		PIMCapiMessage msg = (PIMCapiMessage) capi.createMessage(appID, PIMCapiMessage.CONNECT_RESP, 1);
		msg.setValue("PLCI", plci);
		msg.setWordValue("Reject", cause);
		msg.setStructValue("B protocol", new byte[] { 1, 0, 1, 0, 0, 0 , 0, 0, 0});
		return msg;
	}	
	
	public static PIMCapiMessage createDisconnectReq(Capi capi, int appID, Object plci) throws CapiException {
		return PIMCapiMessageFactory.createDisconnectReq(capi, appID, 1, plci);
	}
	
	public static PIMCapiMessage createDisconnectReq(Capi capi, int appID, int msgID, Object plci) throws CapiException {
		PIMCapiMessage msg = new PIMCapiMessage(appID, PIMCapiMessage.DISCONNECT_REQ, msgID);
		msg.setValue("PLCI", plci);
		return msg;
	}
	
	public static PIMCapiMessage createDisconnectResp(Capi capi, int appID, Object plci) throws CapiException {
		return PIMCapiMessageFactory.createDisconnectResp(capi, appID, 1, plci);
	}
	
	public static PIMCapiMessage createDisconnectResp(Capi capi, int appID, int msgID, Object plci) throws CapiException {
		PIMCapiMessage msg = new PIMCapiMessage(appID, PIMCapiMessage.DISCONNECT_RESP, msgID);
		msg.setValue("PLCI", plci);
		return msg;
	}
	
	public static PIMCapiMessage createAlertReq(Capi capi, int appID, Object plci) throws CapiException {
		return PIMCapiMessageFactory.createAlertReq(capi, appID, 1, plci);
	}
	
	public static PIMCapiMessage createAlertReq(Capi capi, int appID, int msgID, Object plci) throws CapiException {
		PIMCapiMessage msg = new PIMCapiMessage(appID, PIMCapiMessage.ALERT_REQ, msgID);
		msg.setValue("PLCI", plci);
		return msg;
	}
	
	public static int getCipFromBc(PIMCapiMessage m) throws CapiException {
		byte[] bc = m.getStructValue("BC");
		int ct = 999;
	
		// added 2003/11/23: fallback, if no BC is set
		if (bc.length==0) {
			ct = 4;
		}
	
		if (bc.length > 0) {
			switch (bc[0] & 0x1f) {
				case 0x00:
					ct = 1;
					break;
				// speech
				case 0x08:
					ct = 2;
					break;
				// digital (unrestricted)
				case 0x09:
					ct = 3;
					break;
				// digital (restricted)
				case 0x10:
					ct = 4;
					break;
				// 3.1 kHz audio
				case 0x11:
					ct = 5;
					break;
				// 7 kHz audio
				case 0x18:
					ct = 6;
					break;
				// video
				default:
					//this.m_logger.info("BC structure recognized: "+(bc[0] & 0x1f));
			}
		}
		return ct;
	}
}

