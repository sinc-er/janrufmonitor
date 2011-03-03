package de.janrufmonitor.service.commons.http.simple;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import de.janrufmonitor.service.commons.http.IHttpResponse;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;

public class Response implements IMutableHttpResponse {

	private simple.http.Response m_resp;

	public Response(simple.http.Response resp) {
		this.m_resp = resp;
	}

	public void setParameter(String name, String value) {
		this.m_resp.set(name, value);
	}

	public void setContent(byte[] content) throws IOException {
		InputStream in = new ByteArrayInputStream(content);
		OutputStream out = this.m_resp.getOutputStream();
		byte[] buffer = new byte[Short.MAX_VALUE];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}  
		in.close(); 
		out.flush();
		out.close();
	}

	public void setCode(int code) {
		this.m_resp.setCode(code);		
	}

	public OutputStream getContentStreamForWrite() throws IOException {
		return this.m_resp.getOutputStream();
	}

	public IHttpResponse getImmutable() {
		return null;
	}

	public boolean isHandled() {
		return this.m_resp.isCommitted();
	}
	
}
