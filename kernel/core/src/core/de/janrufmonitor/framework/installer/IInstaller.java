package de.janrufmonitor.framework.installer;

import java.io.File;
import java.util.Properties;

public interface IInstaller {
	
	public String getExtension();
	
	public int getPriority();
	
	public void setFile(File f);
	
	public Properties getDescriptor();
	
	public Properties getDescriptor(String key);
	
	public String install(boolean overwrite) throws InstallerException;
	
	public boolean uninstall(String key) throws InstallerException;
	
}
