package de.janrufmonitor.xml.transformation;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IMultiPhoneCaller;
import de.janrufmonitor.framework.IName;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.io.Base64Decoder;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.io.Stream;
import de.janrufmonitor.util.string.StringUtils;

public class XMLCallerHandler extends DefaultHandler {

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
	private static final String TAG_PHONENUMBER = "phonenumber";
	private static final String TAG_IMAGE_CONTENT = "cimagecontent";
	
	private ICallerList m_callerList;
	private IMultiPhoneCaller m_caller;
	private boolean m_multi;
	private String m_image_content;
		
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
				List phns = new ArrayList();
				this.m_caller = this.getRuntime().getCallerFactory().createCaller(
					this.getRuntime().getCallerFactory().createName("",""),
					phns
				);
				this.m_caller.getPhonenumbers().clear();
			}
			
			if (qname.equalsIgnoreCase(TAG_UUID)) {
				this.m_caller.setUUID(attributes.getValue(ATTRIBUTE_VALUE));
			}
			
			if (qname.equalsIgnoreCase(TAG_IMAGE_CONTENT)) {
				this.m_image_content = attributes.getValue(ATTRIBUTE_VALUE);
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
			
			if (qname.equalsIgnoreCase(TAG_PHONENUMBER)) {
				List pns = new ArrayList(this.m_caller.getPhonenumbers());
				String iarea = attributes.getValue(TAG_INTAREA);
				String area = attributes.getValue(TAG_AREA);
				String cn = attributes.getValue(TAG_CALLNUMBER);
				String tn = attributes.getValue(TAG_TELEPHONENUMBER);
				if (iarea!=null && iarea.length()>0 && area!=null && cn!=null && cn.length()>0) {
					IPhonenumber n = getRuntime().getCallerFactory().createPhonenumber(iarea, area, cn);
					n.setTelephoneNumber(tn);
					
					if (pns.size()==0) {
						this.m_caller.setPhoneNumber(n);
					} else {
						pns.add(n);
						this.m_caller.setPhonenumbers(pns);
					}					
				}

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

			if (qname.equalsIgnoreCase(TAG_CALLER) && this.m_image_content!=null && this.m_caller!=null && this.m_caller.getAttributes().contains(IJAMConst.ATTRIBUTE_NAME_IMAGEPATH)) {
				String file = this.m_caller.getAttribute(IJAMConst.ATTRIBUTE_NAME_IMAGEPATH).getValue();
				if (file!=null && file.length()>0) {
					// check for File.separator
					if (File.separator.equalsIgnoreCase("/")) {
						file = StringUtils.replaceString(file, "\\", File.separator);
					}
					File f = new File(PathResolver.getInstance().resolve(file));
					File image = new File(getCentralImageStorePath(), f.getName());				
					ByteArrayInputStream encodedIn = new ByteArrayInputStream(this.m_image_content.getBytes());
					Base64Decoder b64 = new Base64Decoder(encodedIn);
					
					try {
						FileOutputStream decodedOut = new FileOutputStream(image);
						Stream.copy(b64, decodedOut);
						b64.close();
						decodedOut.close();
					} catch (FileNotFoundException e) {
					} catch (IOException e) {
					}
					this.m_caller.setAttribute(getRuntime().getCallerFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_IMAGEPATH, image.getAbsolutePath()));
					this.m_image_content = null;
				}
			}
		
		
			if (this.m_multi && qname.equalsIgnoreCase(TAG_CALLER)) {
				this.m_callerList.add(this.m_caller);
				List phns = new ArrayList();
				this.m_caller = this.getRuntime().getCallerFactory().createCaller(
					this.getRuntime().getCallerFactory().createName("",""),
					phns
				);	
				this.m_caller.getPhonenumbers().clear();
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
	
	private String getCentralImageStorePath() {
		File cis = new File (PathResolver.getInstance(getRuntime()).getPhotoDirectory() + File.separator + "contacts");
		if (!cis.exists()) {
			cis.mkdirs();
		}
		return cis.getAbsolutePath();
	}


}
