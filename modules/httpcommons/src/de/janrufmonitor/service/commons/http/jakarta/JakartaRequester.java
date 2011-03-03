package de.janrufmonitor.service.commons.http.jakarta;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;

import org.apache.commons.httpclient.DefaultHttpMethodRetryHandler;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.params.HttpClientParams;

import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IHttpResponse;
import de.janrufmonitor.service.commons.http.IMutableHttpRequest;
import de.janrufmonitor.service.commons.http.IRequester;
import de.janrufmonitor.util.io.PathResolver;

public class JakartaRequester implements IRequester {

	private String m_server;
	private int m_port;
	private int m_responseCode;
	private IHttpRequest m_r;

	public JakartaRequester(String server, int port) {
		this.m_server = server;
		this.m_port = port;
	}

	public IMutableHttpRequest createGetRequest(URI uri) {
		IMutableHttpRequest r = new Request();
		r.setURI(uri);
		r.setMethod(Request.METHOD_GET);
		return r;
	}

	public IMutableHttpRequest createPostRequest(URI uri, byte[] content) throws Exception {
		IMutableHttpRequest r = new Request();
		r.setURI(uri);
		r.setMethod(Request.METHOD_POST);
		r.setContent(content);
		return r;
	}

	public String getServer() {
		return this.m_server;
	}
	
	public int getPort() {
		return this.m_port;
	}
	
	public void setRequest(IMutableHttpRequest r) {
		this.m_r = r.getImmutable();
	}
	
	public void setRequest(IHttpRequest r) {
		this.m_r = r;
	}
	
	public IHttpResponse request() {
		HttpClientParams params = new HttpClientParams();
		params.setParameter(HttpClientParams.RETRY_HANDLER, new DefaultHttpMethodRetryHandler(1, false));
		
		HttpClient client = new HttpClient(params);
		try {
			if (this.m_r instanceof Request) {
				HttpMethod m = null;
				byte[] result = null;
				if (this.m_r!=null && this.m_r.getMethod().equalsIgnoreCase(Request.METHOD_POST)) {
					String url = "http://"+this.getServer()+":"+this.getPort()+this.m_r.getURI();
					m = new PostMethod(url);
					if (this.m_r.getContent()!=null) {
						((PostMethod)m).setRequestEntity(
							new ByteArrayRequestEntity(this.m_r.getContent())
						);
						// removed: 27.05.2005: changed to httpclient 3.0
//						((PostMethod)m).setRequestBody(new ByteArrayInputStream(this.m_r.getContent()));						
//						((PostMethod)m).setRequestContentLength(this.m_r.getContent().length);
					}
					this.m_responseCode = client.executeMethod(m);
					if (this.m_responseCode==HttpStatus.SC_OK)
						result = m.getResponseBody();
				}
				if (this.m_r!=null && this.m_r.getMethod().equalsIgnoreCase(Request.METHOD_GET)) {
					String url = "http://"+this.getServer()+":"+this.getPort()+this.m_r.getURI();
					m = new GetMethod(url);
					this.m_responseCode = client.executeMethod(m);
					if (this.m_responseCode==HttpStatus.SC_OK)
						result = m.getResponseBody();
				}
				
				Response resp = new Response();
				Header[] headers = m.getResponseHeaders();
				for (int i=0;i<headers.length;i++) {
					resp.setParameter(headers[i].getName(), headers[i].getValue());
				}
				
				resp.setCode(this.m_responseCode);
				resp.setContent(result);
				
				String debugEnabled = System.getProperty("jam.http.debug");
				
				if (debugEnabled!=null && debugEnabled.equalsIgnoreCase("true")) {
					this.dump(this.m_r.toString());
					this.dump(resp.toString());
				}

				if (m!=null) m.releaseConnection();
				return resp;
			}
		} catch (IOException ex) {
			this.m_responseCode = 0;
		} catch (Exception e) {
			this.m_responseCode = 0;
		}
		Response resp = new Response();
		resp.setCode(0);
		return resp;
	}
	
    private void dump(String s){
    	try {
	    	String dumpPath = PathResolver.getInstance().getInstallDirectory()+"_http_trace.dmp";
	    	FileOutputStream os = new FileOutputStream(dumpPath, true);
	    	PrintStream ps = new PrintStream(os);
	    	ps.println(s);
	    	ps.println("<<< >>>");
	    	ps.flush();
	    	ps.close();
    	} catch (FileNotFoundException e) {
    		e.printStackTrace();
		}
    }
}
