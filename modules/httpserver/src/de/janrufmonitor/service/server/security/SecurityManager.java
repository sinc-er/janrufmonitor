package de.janrufmonitor.service.server.security;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.PIMRuntime;

public class SecurityManager implements IConfigurable {

	private String ID = "SecurityManager";
	private String NAMESPACE = "server.security.SecurityManager";

	static SecurityManager m_instance = null;
	
	String GENERIC_MASK = "*.*.*.*";
	String CFG_ALLOWED_CLIENTS = "allowed";
	String CFG_ALLOWED_BROWSER = "browser";
	
	HashSet m_allowedServers;
	Map m_msnClient;
	Properties m_configuration;
	Logger m_logger;

	protected SecurityManager () {	
		this.m_allowedServers = new HashSet();
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		PIMRuntime.getInstance().getConfigurableNotifier().register(this);
	}
	
	public static synchronized SecurityManager getInstance() {
		if (m_instance == null) {
			m_instance = new SecurityManager();
		}
		return m_instance;
	}
	
	public boolean isAllowed(String ip) {
		return this.checkIP(ip);
	}
	
	public boolean isAllowedForMSN(String client, String msn) {
		if (m_msnClient==null) return true;
		
		String[] msns = (String[]) this.m_msnClient.get(client);
		if (msns==null) return true;
		
		for (int i=0;i<msns.length;i++) {
			if (msns[i].equalsIgnoreCase(msn)) return true;
		}
		
		return false;
	}
	
	public String[] getAllowedMSNs(String ip){
		return (String[]) this.m_msnClient.get(ip);
	}
	
	
	public boolean isAllowed(InetAddress adress) {
		if (this.checkIP(adress.getHostAddress())) {
			return true;
		}
		return this.checkIP(adress.getHostName());
	}
	
	private boolean checkIP(String ip) {
		
		// reject browser checks
		if (ip.trim().length()==0 && !this.isBrowserAllowed())
			return false;
		
		Iterator iter = this.m_allowedServers.iterator();
		while (iter.hasNext()) {
			String ipc = (String) iter.next();
			if (ipc.equalsIgnoreCase(this.GENERIC_MASK))  {
				return true;
			}
			if (ipc.equalsIgnoreCase(ip)) {
				return true;
			}
			String ipcTail = ipc.substring(0, (ipc.indexOf(".*")>0 ? ipc.indexOf(".*") : 0));
			ipcTail = ipcTail.trim();
			if (ip.length()>0 && ipcTail.length()>0 && ip.startsWith(ipcTail)) {
				return true;
			}
		}
		return false;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public String getConfigurableID() {
		return this.ID;
	}

	public void setConfiguration(Properties config) {
		this.m_configuration = config;
		this.m_allowedServers.clear();
		StringTokenizer st = new StringTokenizer(this.getAllowedClients(),",");
		String c = null;
		while (st.hasMoreTokens()) {
			c = st.nextToken().trim();
			if (c.indexOf(" ")==-1)
				this.m_allowedServers.add(c);
			else
				this.m_allowedServers.add(c.substring(0, c.indexOf(" ")).trim());
		}
		this.m_logger.info("Allowed clients to connect: "+this.m_allowedServers.toString());
		
		this.m_msnClient = new HashMap();
		String msnsClients = this.getAllowedClients();
		if (msnsClients!=null && msnsClients.length()>0) {
			st = new StringTokenizer(msnsClients,",");
			c = null;
			while (st.hasMoreTokens()) {
				c = st.nextToken().trim();
				if (c.indexOf(" -")>-1) {
					String msns = c.substring(c.indexOf(" -")+2, c.length()).trim();
					String[] m = msns.split("/");
					
					String client = c.substring(0, c.indexOf(" -"));
					m_msnClient.put(client, m);
				}
			}
		}
		
	}
	
	private String getAllowedClients() {
		return this.m_configuration.getProperty(this.CFG_ALLOWED_CLIENTS,"");		
	}
	
	private boolean isBrowserAllowed() {
		return (this.m_configuration.getProperty(this.CFG_ALLOWED_BROWSER, "false").equalsIgnoreCase("true") ? true : false);
	}
}
