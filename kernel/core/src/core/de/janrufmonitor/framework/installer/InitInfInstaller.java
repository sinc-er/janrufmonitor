package de.janrufmonitor.framework.installer;

public class InitInfInstaller extends InfInstaller {

	public int getPriority() {
		return 5;
	}
	
	public String getExtension() {
		return InitInfInstaller.EXTENSION_INITINF;
	}
}
