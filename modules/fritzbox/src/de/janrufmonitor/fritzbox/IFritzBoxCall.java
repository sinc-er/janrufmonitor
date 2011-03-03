package de.janrufmonitor.fritzbox;

import de.janrufmonitor.framework.ICall;

public interface IFritzBoxCall {
	
	public ICall toCall();
	
	public boolean isValid();
	
}
