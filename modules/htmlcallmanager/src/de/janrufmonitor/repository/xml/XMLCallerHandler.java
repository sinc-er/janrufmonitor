package de.janrufmonitor.repository.xml;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IName;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

class XMLCallerHandler extends DefaultHandler {

	protected static final String ATTRIBUTE_VALUE = "value";
	protected static final String ATTRIBUTE_NAME = "name";
	private static final String TAG_CALLERLIST = "callerlist";
	protected static final String TAG_CALLER = "caller";
	protected static final String TAG_UUID = "uuid";
	private static final String TAG_INTAREA = "intarea";
	private static final String TAG_AREA = "area";
	private static final String TAG_CALLNUMBER = "callnumber";
	private static final String TAG_TELEPHONENUMBER = "telephonenumber";
	protected static final String TAG_ATTRIBUTE = "attribute";
	private static final String TAG_FIRSTNAME = "firstname";
	private static final String TAG_LASTNAME = "lastname";
	private static final String TAG_ADDITIONAL = "additional";
	
	private ICallerList m_callerList;
	private ICaller m_caller;
	private boolean m_multi;
	
	private IRuntime m_runtime;
	
	public XMLCallerHandler(boolean processMulti) {
		this.m_multi = processMulti;
	}

	public void startElement(String uri, String name, String qname, Attributes attributes)
		throws SAXException {

		if (this.m_multi && qname.equalsIgnoreCase(TAG_CALLERLIST)) {
				this.m_callerList = this.getRuntime().getCallerFactory().createCallerList();
			}
			
			if (qname.equalsIgnoreCase(TAG_CALLER)) {
				this.m_caller = this.getRuntime().getCallerFactory().createCaller(
					this.getRuntime().getCallerFactory().createName("",""),
					this.getRuntime().getCallerFactory().createPhonenumber(false)
				);
			}
			
			if (qname.equalsIgnoreCase(TAG_UUID)) {
				this.m_caller.setUUID(attributes.getValue(ATTRIBUTE_VALUE));
			}
			
			if (qname.equalsIgnoreCase(TAG_FIRSTNAME)) {
				IName cname = this.m_caller.getName();
				cname.setFirstname(decode(attributes.getValue(ATTRIBUTE_VALUE)));
				this.m_caller.setName(cname);
			}
			
			if (qname.equalsIgnoreCase(TAG_LASTNAME)) {
				IName cname = this.m_caller.getName();
				cname.setLastname(decode(attributes.getValue(ATTRIBUTE_VALUE)));
				this.m_caller.setName(cname);
			}
			
			if (qname.equalsIgnoreCase(TAG_ADDITIONAL)) {
				IName cname = this.m_caller.getName();
				cname.setAdditional(decode(attributes.getValue(ATTRIBUTE_VALUE)));
				this.m_caller.setName(cname);
			}
			
			if (qname.equalsIgnoreCase(TAG_INTAREA)) {
				IPhonenumber pn = this.m_caller.getPhoneNumber();
				pn.setIntAreaCode(attributes.getValue(ATTRIBUTE_VALUE));
				this.m_caller.setPhoneNumber(pn);
			}
			
			if (qname.equalsIgnoreCase(TAG_AREA)) {
				IPhonenumber pn = this.m_caller.getPhoneNumber();
				pn.setAreaCode(attributes.getValue(ATTRIBUTE_VALUE));
				this.m_caller.setPhoneNumber(pn);
			}
			
			if (qname.equalsIgnoreCase(TAG_CALLNUMBER)) {
				IPhonenumber pn = this.m_caller.getPhoneNumber();
				pn.setCallNumber(attributes.getValue(ATTRIBUTE_VALUE));
				this.m_caller.setPhoneNumber(pn);
			}
			
			if (qname.equalsIgnoreCase(TAG_TELEPHONENUMBER)) {
				IPhonenumber pn = this.m_caller.getPhoneNumber();
				pn.setTelephoneNumber(attributes.getValue(ATTRIBUTE_VALUE));
				this.m_caller.setPhoneNumber(pn);
			}
			
			if (qname.equalsIgnoreCase(TAG_ATTRIBUTE)) {
				IAttribute att = this.getRuntime().getCallerFactory().createAttribute(
					attributes.getValue(ATTRIBUTE_NAME),
					decode(attributes.getValue(ATTRIBUTE_VALUE))
				);
				this.m_caller.setAttribute(att);
			}
	}
	
	public void endElement(String uri, String name, String qname)
		throws SAXException {

			if (this.m_multi && qname.equalsIgnoreCase(TAG_CALLER)) {
				this.m_callerList.add(this.m_caller);
				this.m_caller = this.getRuntime().getCallerFactory().createCaller(
					this.getRuntime().getCallerFactory().createName("",""),
					this.getRuntime().getCallerFactory().createPhonenumber(false)
				);	
			}
	}
	
	public ICaller getCaller() {
		return this.m_caller;
	}
	
	public ICallerList getCallerList() {
		return this.m_callerList;
	}

	protected IRuntime getRuntime() {
		if (this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}
	
	protected static String decode(String text) {
		try {
			return URLDecoder.decode(text, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) { }
		return "";
	}


}
