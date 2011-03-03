package de.janrufmonitor.repository.xml;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.logging.LogManager;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;


public class XMLSerializer {
	
	private static final String _XML_HEAD = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>";
	private static final String _CRLF = "\r\n";

	public synchronized static String toXML(ICallerList callerlist, boolean headless) {
		StringBuffer c = new StringBuffer();
		if (!headless)
			c.append(_XML_HEAD);
			
		c.append("<callerlist>"+_CRLF);
		
		for (int i=0;i<callerlist.size();i++) {
			c.append(toXML(callerlist.get(i), true)+_CRLF);
		}
		
		c.append("</callerlist>");
		return c.toString();
	}
	
	public synchronized static String toXML(ICallList calllist, boolean headless) {
		StringBuffer c = new StringBuffer();
		if (!headless)
			c.append(_XML_HEAD);
			
		c.append("<calllist>"+_CRLF);
		
		for (int i=0;i<calllist.size();i++) {
			c.append(toXML(calllist.get(i), true)+_CRLF);
		}
		
		c.append("</calllist>");
		return c.toString();
	}

	public synchronized static String toXML(ICaller caller, boolean headless) {
		StringBuffer c = new StringBuffer();
		if (caller==null)
			return c.toString();
		
		if (!headless)
			c.append(_XML_HEAD);
		
		c.append("<caller>");
		c.append("<uuid value=\""+caller.getUUID()+"\" />");
		c.append("<name>");
		c.append("<firstname value=\""+encode(caller.getName().getFirstname())+"\" />");
		c.append("<lastname value=\""+encode(caller.getName().getLastname())+"\" />");
		c.append("<additional value=\""+encode(caller.getName().getAdditional())+"\" />");
		c.append("</name>");
		c.append("<phonenumber>");
		c.append("<intarea value=\""+caller.getPhoneNumber().getIntAreaCode()+"\" />");
		c.append("<area value=\""+caller.getPhoneNumber().getAreaCode()+"\" />");
		c.append("<callnumber value=\""+caller.getPhoneNumber().getCallNumber()+"\" />");
		c.append("<telephonenumber value=\""+caller.getPhoneNumber().getTelephoneNumber()+"\" />");
		c.append("</phonenumber>");
		c.append(toXML(caller.getAttributes()));
		c.append("</caller>");
		return c.toString();
	}
	
	public synchronized static String toXML(ICall call, boolean headless) {
		StringBuffer c = new StringBuffer();
		if (call==null)
			return c.toString();
					
		if (!headless)
			c.append(_XML_HEAD);

		c.append("<call>");
		c.append("<uuid value=\""+call.getUUID()+"\" />");
		c.append("<date value=\""+call.getDate().getTime()+"\" />");
		c.append("<cip value=\""+call.getCIP().getCIP()+"\" />");
		c.append("<msn value=\""+call.getMSN().getMSN()+"\" additional=\""+encode(call.getMSN().getAdditional())+"\" />");
		c.append(toXML(call.getCaller(), true));
		c.append(toXML(call.getAttributes()));
		c.append("</call>");
		return c.toString();
	}
	
	private synchronized static String toXML(IAttributeMap l) {
		StringBuffer c = new StringBuffer();
		c.append("<attributes>");
		Iterator i = l.iterator();
		IAttribute a = null;
		while(i.hasNext()) {
			a = ((IAttribute)i.next());
			c.append("<attribute name=\""+a.getName()+"\" value=\""+encode(a.getValue())+"\" />");
		}
		c.append("</attributes>");
		return c.toString();
	}
	
	private static String encode(String text) {
		try {
			return URLEncoder.encode(text, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		}
		return "";
	}
	
	/**
	private static String decode(String text) {
		try {
			return URLDecoder.decode(text, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		}
		return "";
	}
	*/

	public synchronized static ICaller toCaller(String xml) {
		try {
			XMLCallerHandler handler = new XMLCallerHandler(false);
			SAXParser p = SAXParserFactory.newInstance().newSAXParser();
			ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
			p.parse(in, handler);
			return handler.getCaller();
		} catch (IOException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		} catch (ParserConfigurationException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		} catch (SAXException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		} catch (FactoryConfigurationError e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		}
		return null;
	}
	
	public synchronized static ICall toCall(String xml) {
		try {
			XMLCallHandler handler = new XMLCallHandler(false);
			SAXParser p = SAXParserFactory.newInstance().newSAXParser();
			ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
			p.parse(in, handler);
			return handler.getCall();
		} catch (IOException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		} catch (ParserConfigurationException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		} catch (SAXException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		} catch (FactoryConfigurationError e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		}
		return null;
	}
	
	public synchronized static ICallerList toCallerList(String xml) {
		try {
			XMLCallerHandler handler = new XMLCallerHandler(true);
			SAXParser p = SAXParserFactory.newInstance().newSAXParser();
			ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
			p.parse(in, handler);
			return handler.getCallerList();
		} catch (IOException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		} catch (ParserConfigurationException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		} catch (SAXException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		} catch (FactoryConfigurationError e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		}
		return null;
	}
	
	public synchronized static ICallList toCallList(String xml) {
		try {
			XMLCallHandler handler = new XMLCallHandler(true);
			SAXParser p = SAXParserFactory.newInstance().newSAXParser();
			ByteArrayInputStream in = new ByteArrayInputStream(xml.getBytes());
			p.parse(in, handler);
			return handler.getCallList();
		} catch (IOException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		} catch (ParserConfigurationException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		} catch (SAXException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		} catch (FactoryConfigurationError e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).severe(e.getMessage());
		}
		return null;
	}
}
