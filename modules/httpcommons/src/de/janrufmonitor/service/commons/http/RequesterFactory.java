package de.janrufmonitor.service.commons.http;

import java.net.URI;
import java.util.Properties;

import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.commons.http.jakarta.JakartaRequester;

public class RequesterFactory implements IConfigurable {

	private static RequesterFactory m_instance = null;

	private String ID = "RequesterFactory";
	private String NAMESPACE = "service.client.RequesterFactory";
	
	private String CFG_SERVER = "server";
	private String CFG_PORT = "port";
	
	private Properties m_configuration;
	private RequesterFactory() {
		PIMRuntime.getInstance().getConfigurableNotifier().register(this);
	}

	public synchronized static RequesterFactory getInstance() {
		if (RequesterFactory.m_instance==null)
			RequesterFactory.m_instance = new RequesterFactory();
			
		return RequesterFactory.m_instance;
	}

	public IRequester getRequester() {
		return new JakartaRequester(this.getServer(), this.getPort());
	}
	
	public IRequester createRequester(IHttpRequest req) {
		IRequester r = new JakartaRequester(this.getServer(), this.getPort());
		r.setRequest(req);
		return r;
	}
	
	public IRequester getRequester(String server, int port) {
		return new JakartaRequester(server, port);
	}
	
	public synchronized IMutableHttpRequest createPostRequest(URI uri, byte[] content) throws Exception  {
		return this.getRequester().createPostRequest(uri, content);
	}
	
	public synchronized IMutableHttpRequest createGetRequest(URI uri) throws Exception  {
		return this.getRequester().createGetRequest(uri);
	}
	
	public String getNamespace() {
		return this.NAMESPACE;
	}

	public String getConfigurableID() {
		return this.ID;
	}

	public void setConfiguration(Properties configuration) {
		this.m_configuration = configuration;
	}

	private String getServer() {
		return this.m_configuration.getProperty(this.CFG_SERVER, "localhost");
	}

	private int getPort() {
		return Integer.parseInt(this.m_configuration.getProperty(this.CFG_PORT, "80"));
	}

}
