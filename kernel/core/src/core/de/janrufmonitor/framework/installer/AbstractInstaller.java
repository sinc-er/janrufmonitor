package de.janrufmonitor.framework.installer;

import java.io.File;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.io.PathResolver;

public abstract class AbstractInstaller implements IInstaller, InstallerConst {

	protected Logger m_logger;
	private File m_installFile;
	private IRuntime m_runtime;	
	
	public AbstractInstaller() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
	}
	
	protected File getFile() {
		return this.m_installFile;
	}
	
    protected IRuntime getRuntime() {
    	if (this.m_runtime==null){
    		this.m_runtime = PIMRuntime.getInstance();
    	}
    	return this.m_runtime;
    }
    
	protected File getStoreDirectory() {
		File directory = new File(PathResolver.getInstance(this.getRuntime()).getInstallDirectory(), this.getStoreDirectoryName());
		directory.mkdirs();
		return directory;
	}
	
	public void setFile(File f) {
		this.m_installFile = f;
	}

	public Properties getDescriptor() {
		return null;
	}
	
	public Properties getDescriptor(String key) {
		return null;
	}

	public int getPriority() {
		return 0;
	}
	
	public boolean uninstall(String key) throws InstallerException {
		return false;
	}
	
	public abstract String install(boolean overwrite) throws InstallerException; 
	
	public abstract String getExtension();
	
	protected abstract String getStoreDirectoryName();
	
}
