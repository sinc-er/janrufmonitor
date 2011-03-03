package de.janrufmonitor.service.google;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.AbstractConfigurableService;

public class Maps extends AbstractConfigurableService {

    public static String ID = "GoogleMaps";
    public static String NAMESPACE = "service.GoogleMaps";

    private IRuntime m_runtime;
    
    public Maps() {
        super();
        this.getRuntime().getConfigurableNotifier().register(this);
    }
    
	public String getNamespace() {
		return Maps.NAMESPACE;
	}

	public String getID() {
		return Maps.ID;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

}
