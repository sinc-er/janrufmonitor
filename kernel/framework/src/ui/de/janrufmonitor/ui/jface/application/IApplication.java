package de.janrufmonitor.ui.jface.application;

public interface IApplication {

	public AbstractApplication getApplication();
	
	public IApplicationController getController();
	
	public void updateViews(boolean reload);
	
	public void updateViews(Object[] controllerData, boolean reload);
	
	public String getID();
	
	public String getNamespace();
	
}
