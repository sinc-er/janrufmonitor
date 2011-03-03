package de.janrufmonitor.xml.transformation;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
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
import de.janrufmonitor.framework.IMultiPhoneCaller;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.util.io.Base64Encoder;
import de.janrufmonitor.util.io.ImageHandler;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.io.Stream;


public class XMLSerializer {
	
	private static final String _XML_HEAD = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\" ?>";
	private static final String _CRLF = "\r\n";

	public static String toXML(ICallerList callerlist, boolean headless) {
		return XMLSerializer.toXML(callerlist, headless, false);
	}
	
	public synchronized static String toXML(ICallerList callerlist, boolean headless, boolean includeImage) {
		StringBuffer c = new StringBuffer();
		if (!headless)
			c.append(_XML_HEAD);
			
		c.append("<callerlist>"+_CRLF);
		
		for (int i=0;i<callerlist.size();i++) {
			c.append(toXML(callerlist.get(i), true, includeImage)+_CRLF);
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
		return XMLSerializer.toXML(caller, headless, false);
	}

	public synchronized static String toXML(ICaller caller, boolean headless, boolean includeImage) {
		StringBuffer c = new StringBuffer();
		if (caller==null)
			return c.toString();
		
		if (!headless)
			c.append(_XML_HEAD);
		
		c.append("<caller>");
		c.append("<uuid value=\""+caller.getUUID()+"\" />");
		if (includeImage) {
			try {
				c.append(XMLSerializer.includeImage(caller));
			} catch (IOException e) {
			}
		}
		c.append("<name>");
		c.append("<firstname value=\""+encode(caller.getName().getFirstname())+"\" />");
		c.append("<lastname value=\""+encode(caller.getName().getLastname())+"\" />");
		c.append("<additional value=\""+encode(caller.getName().getAdditional())+"\" />");
		c.append("</name>");
		if (caller instanceof IMultiPhoneCaller) {
			c.append(toXML(((IMultiPhoneCaller)caller).getPhonenumbers()));
		} else {
			c.append("<phonenumber>");
			c.append("<intarea value=\""+caller.getPhoneNumber().getIntAreaCode()+"\" />");
			c.append("<area value=\""+caller.getPhoneNumber().getAreaCode()+"\" />");
			c.append("<callnumber value=\""+caller.getPhoneNumber().getCallNumber()+"\" />");
			c.append("<telephonenumber value=\""+caller.getPhoneNumber().getTelephoneNumber()+"\" />");
			c.append("</phonenumber>");
		}

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
			c.append("<attribute name=\""+encode(a.getName())+"\" value=\""+encode(a.getValue())+"\" />");
		}
		c.append("</attributes>");
		return c.toString();
	}
	
	private static String includeImage(ICaller caller) throws IOException {
		if (ImageHandler.getInstance().hasImage(caller) && !caller.getAttributes().contains(IJAMConst.ATTRIBUTE_NAME_IMAGEPATH)) {
			InputStream in = ImageHandler.getInstance().getImageStream(caller);
			if (in!=null) {
				StringBuffer c = new StringBuffer();
				c.append("<cimagecontent value=\"");
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				Base64Encoder b64 = new Base64Encoder(bos);
				Stream.copy(new BufferedInputStream(in), b64, true);	
				c.append(bos.toString());
				c.append("\" />");
				return c.toString();	
			}
		} else if (caller.getAttributes().contains(IJAMConst.ATTRIBUTE_NAME_IMAGEPATH)){
			String file = caller.getAttribute(IJAMConst.ATTRIBUTE_NAME_IMAGEPATH).getValue();
			File f = new File(PathResolver.getInstance().resolve(file));
			if (!f.exists()) return "";
			
			StringBuffer c = new StringBuffer();
			c.append("<cimagecontent value=\"");

			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			Base64Encoder b64 = new Base64Encoder(bos);
			FileInputStream fin = new FileInputStream(f);
			Stream.copy(new BufferedInputStream(fin), b64, true);	
			c.append(bos.toString());
			c.append("\" />");
			return c.toString();
		}
		return "";
	}
	
	private synchronized static String toXML(List phones) {
		StringBuffer c = new StringBuffer();
		c.append("<phonenumbers>");
		Iterator i = phones.iterator();
		IPhonenumber a = null;
		while(i.hasNext()) {
			a = ((IPhonenumber)i.next());
			c.append("<phonenumber intarea=\""+encode(a.getIntAreaCode())+"\" area=\""+encode(a.getAreaCode())+"\" callnumber=\""+encode(a.getCallNumber())+"\" telephonenumber=\""+encode(a.getTelephoneNumber()) +"\" />");
		}
		c.append("</phonenumbers>");
		return c.toString();
	}
	
	private static String encode(String text) {
		try {
			return URLEncoder.encode(text, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
		}
		return "";
	}
	
	/**
	private static String decode(String text) {
		try {
			return URLDecoder.decode(text, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
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
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
		} catch (SAXException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
		} catch (FactoryConfigurationError e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
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
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
		} catch (SAXException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
		} catch (FactoryConfigurationError e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
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
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
		} catch (SAXException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, xml);
		} catch (FactoryConfigurationError e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
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
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
		} catch (SAXException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, xml);
		} catch (FactoryConfigurationError e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).log(Level.SEVERE, e.getMessage(), e);
		}
		return null;
	}
}
