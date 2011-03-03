package de.janrufmonitor.repository;

import java.util.Locale;

import de.janrufmonitor.repository.web.AbstractURLRequester;
import de.janrufmonitor.repository.web.RegExpURLRequester;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class DynamicWebCallerManager extends AbstractWebCallerManager {

	private static final String CFG_INTAREACODE = "intareacode";
	private static final String CFG_LOCALE = "locale";
	
	
	private IRuntime m_runtime;
	
	public DynamicWebCallerManager(String id) {
		super();
		this.m_externalID = id;
		this.getRuntime().getConfigurableNotifier().register(this);
	}
	
	public DynamicWebCallerManager() {
		this(null);		
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	public String getID() {
		return this.m_externalID;
	}

	public String getNamespace() {
		return "repository."+this.getID();
	}

	protected AbstractURLRequester createURLRequester(String url, long skip) {
		return createURLRequester(url, skip, null);
	}
	
	protected AbstractURLRequester createURLRequester(String url, long skip, String pn) {
		return new RegExpURLRequester(url, skip, pn, getNamespace(), this.m_configuration, this.getRuntime(), getLocale(), this.getSupportedIntAreaCode(), this.getUserAgent());
	}

	protected Locale getLocale() {
		String loc = this.m_configuration.getProperty(CFG_LOCALE, "de_DE");
		if (loc!=null) {
			if (loc.indexOf("_")>0) {
				return new Locale(loc.split("_")[0], loc.split("_")[1]);
			}
			return new Locale(loc);
		}
		return Locale.GERMANY;
	}
	
	protected String getSupportedIntAreaCode() {
		return this.m_configuration.getProperty(CFG_INTAREACODE, "49");
	}
}
