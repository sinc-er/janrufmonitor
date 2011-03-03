package de.janrufmonitor.service.commons.http.jakarta;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.service.commons.http.IHttpResponse;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;

public class Response implements IMutableHttpResponse, IHttpResponse {

	private int m_code;
	private Map m_parameters;
	private byte[] m_content;

	public Response() {
		this.m_parameters = new HashMap();
	}

	public void setCode(int code) {
		this.m_code = code;
	}

	public void setParameter(String name, String value) {
		this.m_parameters.put(name, value);
	}

	public void setContent(byte[] content) throws Exception {
		this.m_content = content;
	}

	public OutputStream getContentStreamForWrite() throws Exception {
		return null;
	}

	public int getCode() {
		return this.m_code;
	}

	public IMutableHttpResponse getMutable() {
		return this;
	}

	public String getParameter(String name) throws Exception {
		return (String) this.m_parameters.get(name);
	}

	public byte[] getContent() throws Exception {
		return this.m_content;
	}
	
	public InputStream getContentStreamForRead() throws Exception {
		return new ByteArrayInputStream(this.getContent());
	}

	public InetAddress getInetAddress() throws Exception {
		return InetAddress.getLocalHost();
	}

	public IHttpResponse getImmutable() {
		return this;
	}

	public boolean isHandled() {
		return true;
	}
	
	public String toString() {
		StringBuffer b = new StringBuffer();
		b.append("HTTP Response code: ");
		b.append(this.getCode());

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

}
