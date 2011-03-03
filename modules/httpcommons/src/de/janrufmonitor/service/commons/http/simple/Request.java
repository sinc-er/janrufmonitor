package de.janrufmonitor.service.commons.http.simple;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpRequest;

public class Request implements IHttpRequest {

	private simple.http.Request m_req;

	public Request(simple.http.Request req) {
		this.m_req = req;
	}

	public String getMethod() {
		return this.m_req.getMethod();
	}

	public URI getURI() throws Exception {
		return new URI(this.m_req.getURI());
	}

	public IMutableHttpRequest getMutable() {
		return null;
	}

	public String getParameter(String name) throws IOException {
		return this.m_req.getParameter(name);
	}

	public byte[] getContent() throws IOException {
		InputStream in = this.m_req.getInputStream();
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buffer = new byte[Short.MAX_VALUE];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}  
		in.close(); 
		out.flush();
		return out.toByteArray();
	}

	public InetAddress getInetAddress() {
		return this.m_req.getInetAddress();
	}

	public InputStream getContentStreamForRead() throws Exception {
		return this.m_req.getInputStream();
	}

	public String[] getParameterNames() throws Exception {
		Enumeration e = 
			this.m_req.getParameters().getParameterNames();
		
		List names = new ArrayList();
		while (e.hasMoreElements()) {
			names.add(e.nextElement());
		}
		
		String[] name = new String[names.size()];
		for (int i=0;i<name.length;i++)
			name[i] = (String) names.get(i);
		
		return name;
	}

}
