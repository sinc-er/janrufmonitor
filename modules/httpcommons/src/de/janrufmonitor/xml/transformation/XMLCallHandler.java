package de.janrufmonitor.xml.transformation;

import java.util.Date;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.framework.ICip;
import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.framework.IJAMConst;

public class XMLCallHandler extends XMLCallerHandler {

	private static final String ATTRIBUTE_ADDITIONAL = "additional";
	private static final String TAG_CALLLIST = "calllist";
	private static final String TAG_CALL= "call";
	private static final String TAG_CIP = "cip";
	private static final String TAG_MSN = "msn";
	private static final String TAG_DATE = "date";

	private ICallList m_calllist;
	private ICall m_call;

	private boolean m_multi;
	private boolean m_iscaller;
	private String m_language;

	public XMLCallHandler(boolean processMulti) {
		super(false);
		this.m_multi = processMulti;
	}

	public ICall getCall() {
		return this.m_call;
	}
	
	public ICallList getCallList() {
		return this.m_calllist;
	}

	public void startElement(String uri, String name, String qname, Attributes attributes)
		throws SAXException {
			
			if (this.m_multi && qname.equalsIgnoreCase(TAG_CALLLIST)) {
				this.m_calllist = this.getRuntime().getCallFactory().createCallList();
			}
			
			if (qname.equalsIgnoreCase(TAG_CALL)) {
				this.m_call = this.getRuntime().getCallFactory().createCall(
					null,
					this.getRuntime().getCallFactory().createMsn("",""),
					this.getRuntime().getCallFactory().createCip("","")
				);
			}
			
			if (qname.equalsIgnoreCase(TAG_CALLER)) {
				this.m_iscaller = true;
			}
			
			if (this.m_iscaller){
				super.startElement(uri, name, qname, attributes);
				return;
			}
			
			if (qname.equalsIgnoreCase(TAG_UUID)) {
				this.m_call.setUUID(attributes.getValue(ATTRIBUTE_VALUE));
			}
			
			if (qname.equalsIgnoreCase(TAG_DATE)) {
				this.m_call.setDate(new Date(Long.parseLong(attributes.getValue(ATTRIBUTE_VALUE))));
			}
			
			if (qname.equalsIgnoreCase(TAG_CIP)) {
				ICip cip = this.m_call.getCIP();
				cip.setCIP(attributes.getValue(ATTRIBUTE_VALUE));
				cip.setAdditional(this.getRuntime().getCipManager().getCipLabel(cip, this.getLanguage()));
				this.m_call.setCIP(cip);
			}
			
			if (qname.equalsIgnoreCase(TAG_MSN)) {
				IMsn msn = this.m_call.getMSN();
				msn.setMSN(attributes.getValue(ATTRIBUTE_VALUE));
				msn.setAdditional(decode(attributes.getValue(ATTRIBUTE_ADDITIONAL)));
				this.m_call.setMSN(msn);
			}
			
			if (qname.equalsIgnoreCase(TAG_ATTRIBUTE)) {
				IAttribute att = this.getRuntime().getCallerFactory().createAttribute(
					attributes.getValue(ATTRIBUTE_NAME),
					decode(attributes.getValue(ATTRIBUTE_VALUE))
				);
				this.m_call.setAttribute(att);
			}
	}


	public void endElement(String uri, String name, String qname)
		throws SAXException {
			
			if (qname.equalsIgnoreCase(TAG_CALLER)) {
				this.m_iscaller = false;
				this.m_call.setCaller(this.getCaller());
			}
			
			if (this.m_multi && qname.equalsIgnoreCase(TAG_CALL)) {
				this.m_calllist.add(this.m_call);
				this.m_call = this.getRuntime().getCallFactory().createCall(
					null,
					this.getRuntime().getCallFactory().createMsn("",""),
					this.getRuntime().getCallFactory().createCip("","")
				);
			}
	}
	
	private String getLanguage() {
		if (this.m_language==null || this.m_language.length()==0)
			this.m_language = this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(
				IJAMConst.GLOBAL_NAMESPACE,
				IJAMConst.GLOBAL_LANGUAGE
			);
		return this.m_language;
	}
}
