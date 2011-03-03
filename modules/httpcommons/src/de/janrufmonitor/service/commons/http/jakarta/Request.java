package de.janrufmonitor.service.commons.http.jakarta;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.service.commons.CommonsConst;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpRequest;

public class Request implements IMutableHttpRequest, IHttpRequest, CommonsConst {

	public static final String METHOD_POST = "POST";
	public static final String METHOD_GET = "GET";

	private String m_method;
	private URI m_uri;
	private Map m_parameters;
	private byte[] m_content;
	
	public Request() {
		this.m_parameters = new HashMap();
	}

	public void setMethod(String method) {
		this.m_method = method;		
	}

	public void setURI(URI uri) {
		this.m_uri = uri;
	}

	public void setParameter(String name, String value) {
		this.m_parameters.put(name, value);
	}

	public void setContent(byte[] content) throws Exception {
		this.m_content = content;
	}

	public String getMethod() {
		return (this.m_method==null ? METHOD_GET : this.m_method);
	}

	public URI getURI() throws Exception {
		return this.m_uri;
	}

	public IMutableHttpRequest getMutable() {
		return this;
	}

	public String getParameter(String name) throws Exception {
		return (String) this.m_parameters.get(name);
	}

	public byte[] getContent() throws Exception {
		return (this.m_content!=null ? this.m_content : new byte[] {});
	}

	public InetAddress getInetAddress() throws Exception {
		return InetAddress.getLocalHost();
	}

	public OutputStream getContentStreamForWrite() throws Exception {
		return null;
	}

	public InputStream getContentStreamForRead() throws Exception {
		return new ByteArrayInputStream(this.getContent());
	}

	public IHttpRequest getImmutable() {
		return this;
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append(this.getMethod());
		b.append(" ");
		try {
			b.append(this.getURI());
		} catch (Exception e) {
			b.append(e.toString());
		}
		b.append(IJAMConst.CRLF);
		String key = null;
		Iterator iter = this.m_parameters.keySet().iterator();
		while (iter.hasNext()) {
			key = (String) iter.next();
			b.append(key);
			b.append(": ");
			b.append(this.m_parameters.get(key));
			b.append(IJAMConst.CRLF);
		}
		b.append(IJAMConst.CRLF);
		try {
			b.append(new String(this.getContent()));
		} catch (Exception e) {
			b.append(e.toString());
		}
		b.append(IJAMConst.CRLF);
		return b.toString();
	}

	public String[] getParameterNames() throws Exception {
		return new String[0];
	}
}
