package de.janrufmonitor.framework.installer.rules;

import java.util.Properties;

import de.janrufmonitor.framework.installer.InstallerConst;
import de.janrufmonitor.framework.installer.InstallerEngine;

public class ModuleVersionRule extends AbstractRule {

	public void validate(Properties descriptor) throws InstallerRuleException {
		super.validate(descriptor);
		
    	String reqiredModuleVersion = descriptor.getProperty(InstallerConst.DESCRIPTOR_REQUIRED_MOD_VERSION);
    	
    	// ignore patch version if not set in descriptor
    	if (reqiredModuleVersion==null) return;
    	
    	Properties currentModuleDescriptor = InstallerEngine.getInstance().getDescriptor(
    		descriptor.getProperty(InstallerConst.DESCRIPTOR_NAMESPACE), false
    	);
    	
    	// module is not yet installed
    	if (currentModuleDescriptor==null) return;
    	
    	String currentModuleVersion = currentModuleDescriptor.getProperty(InstallerConst.DESCRIPTOR_VERSION);
    	if (reqiredModuleVersion!=null && reqiredModuleVersion.compareTo(currentModuleVersion)<=0){
    		return;
    	} else {
    	   	throw new InstallerRuleException(toString().toLowerCase(), "The new module cannot be installed, because it requires module version "+reqiredModuleVersion+", but the current version is "+currentModuleVersion+".");
    	}
	}

	public String toString() {
		return "ModuleVersionRule";
	}
}
