package de.janrufmonitor.ui.jface.configuration;

public interface IConfigPage {
	
	public static final String SEPARATOR =":";
	
	public static final String ROOT_NODE = "root";
	public static final String SERVICE_NODE = "service";
	public static final String CALLER_NODE = "caller";
	public static final String JOURNAL_NODE = "journal";
	public static final String ADVANCED_NODE = "advanced";
		
	public String getParentNodeID();
	
	public String getNodeID();
	
	public void setNodeID(String id);
	
	public int getNodePosition();
	
	public String getNamespace();
	
	public boolean isExpertMode();
}
