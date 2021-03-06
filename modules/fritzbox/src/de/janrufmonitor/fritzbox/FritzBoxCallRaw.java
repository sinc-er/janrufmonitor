package de.janrufmonitor.fritzbox;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Properties;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICip;
import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.framework.monitor.PhonenumberAnalyzer;
import de.janrufmonitor.framework.monitor.PhonenumberInfo;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class FritzBoxCallRaw extends AbstractFritzBoxCall {
	
	public FritzBoxCallRaw(String rawline, Properties config) {
		// INCOMING
		// Datum;Event;Line;Rufnummer;Eigene Rufnummer;Merkmal
		// 22.08.06 19:08:59;RING;2;072657110;911956;ISDN;
		
		// OUTGOING
		// Datum;Event;Line;Dauer(sec);Rufnummer;Eigene Rufnummer;Merkmal
		// 22.08.06 19:08:58;CALL;1;4;7110;911956;ISDN
		super(rawline, config);
	}

	public ICall toCall() {
		if (this.m_line==null || this.m_line.length()==0) return null;

		if (this.m_call==null) {
			IRuntime r = PIMRuntime.getInstance();
			String[] call = this.m_line.split(";");
			if (call.length>=4 && call[1].equalsIgnoreCase("RING")) {
				// create MSN
				IMsn msn = r.getCallFactory().createMsn(getFestnetzAlias(call[4]), "");
				msn.setAdditional(r.getMsnManager().getMsnLabel(msn));
				
				IPhonenumber pn = PhonenumberAnalyzer.getInstance().createClirPhonenumberFromRaw(call[3].trim());
				
				// if no CLIR call, check internal
				if (pn==null) pn = PhonenumberAnalyzer.getInstance().createInternalPhonenumberFromRaw(call[3].trim(), msn.getMSN());
				// if no internal call, check regular
				if (pn==null)  {
					// if incoming call does not start with 0, the Provider number seems to have the wrong format
					// assume it is an international format 4971657110
					if (!call[3].startsWith("0")&& !PhonenumberInfo.containsSpecialChars(call[3])) {
						call[3] = "00"+call[3];
					}
					pn = PhonenumberAnalyzer.getInstance().createPhonenumberFromRaw(call[3].trim(), msn.getMSN());
				}
				ICaller caller = r.getCallerFactory().createCaller(pn);
				
				// create call data
				// added 2009/05/01
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
				Date date = new Date(0);
				try {
					date = sdf.parse(call[0]);
					date = this.toFritzboxDate(date);						
				} catch (ParseException e) {
					Logger.getLogger(IJAMConst.DEFAULT_LOGGER).severe("Wrong standard date format detected.");
					sdf = new SimpleDateFormat(this.m_config.getProperty(CFG_DATEFORMAT, "dd.MM.yy HH:mm"));
					try {
						date = sdf.parse(call[0]);
						date = this.toFritzboxDate(date);							
					} catch (ParseException ex) {
						Logger.getLogger(IJAMConst.DEFAULT_LOGGER).severe("Wrong custom date format detected.");
					}
				}

				ICip cip = r.getCallFactory().createCip(getCip(call[5]), "");
				cip.setAdditional(r.getCipManager().getCipLabel(cip, ""));
				
				// create attributes
				IAttributeMap am = r.getCallFactory().createAttributeMap();

				am.add(r.getCallFactory().createAttribute("fritzbox.key", call[2]));
				am.add(r.getCallFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS
						, IJAMConst.ATTRIBUTE_VALUE_MISSED));
				
				// create UUID
				StringBuffer uuid = new StringBuffer();
				uuid.append(date.getTime());
				uuid.append("-");
				uuid.append(pn.getTelephoneNumber());
				uuid.append("-");
				uuid.append(msn.getMSN());
				
				// limit uuid to 32 chars
				if (uuid.length()>31) {
					// reduce byte length to append -1 for redundant calls max -1-1 --> 3 calls
					uuid = new StringBuffer(uuid.substring(0,31));
				}
				uuid = new StringBuffer(FritzBoxUUIDManager.getInstance().calculateUUID(uuid.toString()));
				
				this.m_call = r.getCallFactory().createCall(uuid.toString(), caller, msn, cip, date);
				this.m_call.setAttributes(am);
			}
			if (call.length>=4 && call[1].equalsIgnoreCase("CALL")) {
				// create msn
				IMsn msn = r.getCallFactory().createMsn(getFestnetzAlias(call[4]), "");
				msn.setAdditional(r.getMsnManager().getMsnLabel(msn));
				
				// create caller data
				String callByCall = null;
				
				IPhonenumber pn = PhonenumberAnalyzer.getInstance().createClirPhonenumberFromRaw(call[5].trim());
				
				// if no CLIR call, check internal
				if (pn==null) pn = PhonenumberAnalyzer.getInstance().createInternalPhonenumberFromRaw(call[5].trim(), msn.getMSN());
				// if no internal call, check regular
				if (pn==null)  {
					// added 2006/08/10: trim call-by-call information
					// only can occure on state CALL (out-going calls)
					callByCall = getCallByCall(call[5]);
					if (callByCall!=null) {
						call[5] = call[5].substring(callByCall.length());
					}
					// added 2011/01/13: added areacode additional on special FritzBox mode. Having no leading 0, 
					// requires addition of areacode
					if (!call[5].startsWith("0")) {
						Logger.getLogger(IJAMConst.DEFAULT_LOGGER).info("Assuming number "+call[5]+" has missing areacode.");
						call[5] = this.getGeneralAreaCode() + call[5];
						Logger.getLogger(IJAMConst.DEFAULT_LOGGER).info("Added areacode to number "+call[5]);
					}
					
					pn = PhonenumberAnalyzer.getInstance().createPhonenumberFromRaw(call[5].trim(), msn.getMSN());
				}


				ICaller caller = r.getCallerFactory().createCaller(pn);
				
				// create call data
				// added 2009/05/27
				SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yy HH:mm:ss");
				Date date = new Date(0);
				try {
					date = sdf.parse(call[0]);
					date = this.toFritzboxDate(date);					
				} catch (ParseException e) {
					Logger.getLogger(IJAMConst.DEFAULT_LOGGER).severe("Wrong standard date format detected.");
					sdf = new SimpleDateFormat(this.m_config.getProperty(CFG_DATEFORMAT, "dd.MM.yy HH:mm"));
					try {
						date = sdf.parse(call[0]);
						date = this.toFritzboxDate(date);			
					} catch (ParseException ex) {
						Logger.getLogger(IJAMConst.DEFAULT_LOGGER).severe("Wrong custom date format detected.");
					}
				}

				ICip cip = r.getCallFactory().createCip(getCip(call[6]), "");
				cip.setAdditional(r.getCipManager().getCipLabel(cip, ""));
				
				// create attributes
				IAttributeMap am = r.getCallFactory().createAttributeMap();

				am.add(r.getCallFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS, IJAMConst.ATTRIBUTE_VALUE_OUTGOING));				
				am.add(r.getCallFactory().createAttribute("fritzbox.key", call[2]));
				
				if (callByCall!=null)
					am.add(r.getCallFactory().createAttribute("fritzbox.callbycall", callByCall));
								
				// create UUID
				StringBuffer uuid = new StringBuffer();
				uuid.append(date.getTime());
				uuid.append("-");
				uuid.append(pn.getTelephoneNumber());
				uuid.append("-");
				uuid.append(msn.getMSN());
				
				// limit uuid to 32 chars
				if (uuid.length()>31) {
					// reduce byte length to append -1 for redundant calls max -1-1 --> 3 calls
					uuid = new StringBuffer(uuid.substring(0,31));
				}
				uuid = new StringBuffer(FritzBoxUUIDManager.getInstance().calculateUUID(uuid.toString()));
				
				this.m_call = r.getCallFactory().createCall(uuid.toString(), caller, msn, cip, date);
				this.m_call.setAttributes(am);
			}
		}
		return this.m_call;
	}
	
	private Date toFritzboxDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		int seconds = c.get(Calendar.SECOND);
		if (seconds>=0 && seconds<30) {
			Logger.getLogger(IJAMConst.DEFAULT_LOGGER).info("Transfer from Date: "+c.getTime().toString());
			c.add(Calendar.MINUTE, -1);
			c.set(Calendar.SECOND, 0);
			Logger.getLogger(IJAMConst.DEFAULT_LOGGER).info("Transfer to Date: "+c.getTime().toString());
			return c.getTime();
		}		
		c.set(Calendar.SECOND, 0);
		Logger.getLogger(IJAMConst.DEFAULT_LOGGER).info("Date: "+c.getTime().toString());
		return c.getTime();
	}
	
	public static String getAction(String call) {
		String [] data = call.split(";");
		if (data!=null && data.length>=2) return data[1];
		return null;
	}

	public static String getLine(String c) {
		String[] call = c.split(";");
		if (call.length>=3) {
			return call[2];
		}
		return null;
	}
	
	public static String getDuration(String c) {
		String[] call = c.split(";");
		if (call.length>=4) {
			int d = Integer.parseInt(call[3]);
			if (d>0) d = d + 60;
			return Integer.toString(d);
		}
		return null;
	}
	
	public static String getKey(ICall call) {
		IAttribute att = call.getAttribute("fritzbox.key");
		if (att!=null) return att.getValue();
		return null;
	}
}
