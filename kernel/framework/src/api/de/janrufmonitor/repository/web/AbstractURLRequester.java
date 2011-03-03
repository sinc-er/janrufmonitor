package de.janrufmonitor.repository.web;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;

/**
 * This abstract call must be used to implement the HTTP call to the web caller manager.
 * 
 *@author     Thilo Brandt
 *@created    2006/08/24
 */
public abstract class AbstractURLRequester {

	protected String url;
	
	protected long m_skip;
	protected IAttributeMap m;
	protected IPhonenumber pn;
	protected Logger m_logger;
	
	public AbstractURLRequester(String url, long skip) {
		this.url = url;
		this.m_skip = skip;
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
	}
	
	/**
	 * Requests the HTTP server and parses the result. The result is stored 
	 * in an IAttributeMap and in an IPhonenumber object.
	 * 
	 * @throws Exception
	 */
	public abstract void go() throws Exception;

	public IAttributeMap getAttributes() {
		return this.m;
	}
	
	public IPhonenumber getPhonenumber() {
		return this.pn;
	}
	
	public long getTimeout() {
		return 10000;
	}
	
}
