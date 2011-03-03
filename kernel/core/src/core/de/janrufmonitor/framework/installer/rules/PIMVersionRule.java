package de.janrufmonitor.framework.installer.rules;

import java.util.Properties;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.installer.InstallerConst;

public class PIMVersionRule extends AbstractRule {

	public void validate(Properties descriptor) throws InstallerRuleException {
    	super.validate(descriptor);
    	
    	String reqiredMajorVersion = "0";
    	String reqiredMinorVersion = "0";
    	String reqiredPatchVersion = "0";
    	
    	reqiredMajorVersion = descriptor.getProperty(InstallerConst.DESCRIPTOR_REQUIRED_MAJOR_VERSION);
    	if(reqiredMajorVersion!=null && reqiredMajorVersion.compareTo(IJAMConst.VERSION_MAJOR)<0) return;
    	
    	if(reqiredMajorVersion!=null && reqiredMajorVersion.compareTo(IJAMConst.VERSION_MAJOR)==0) {
        	reqiredMinorVersion = descriptor.getProperty(InstallerConst.DESCRIPTOR_REQUIRED_MINOR_VERSION);
        	if (reqiredMinorVersion!=null && reqiredMinorVersion.compareTo(IJAMConst.VERSION_MINOR)<0) return;
        	if (reqiredMinorVersion!=null && reqiredMinorVersion.compareTo(IJAMConst.VERSION_MINOR)==0) {
            	reqiredPatchVersion = descriptor.getProperty(InstallerConst.DESCRIPTOR_REQUIRED_PATCH_VERSION);
            	
            	// ignore patch version if not set in descriptor
            	if (reqiredPatchVersion==null) return;
            	if (reqiredPatchVersion.trim().length()==0) reqiredPatchVersion = "0";
            	// 2009/02/28: fix for missing compare check
            	if (reqiredPatchVersion.toLowerCase().trim().equalsIgnoreCase("a")) reqiredPatchVersion = "10";
            	if (reqiredPatchVersion!=null && Integer.parseInt(reqiredPatchVersion)<=Integer.parseInt(IJAMConst.VERSION_PATCH)) return;
        	}
    	}
    	throw new InstallerRuleException(toString().toLowerCase(), "The new module cannot be installed, because it requires program version "+reqiredMajorVersion+"."+reqiredMinorVersion+"."+reqiredPatchVersion+", but the current version is "+IJAMConst.VERSION_DISPLAY+".");
	}

	public String toString() {
		return "PIMVersionRule";
	}

}
