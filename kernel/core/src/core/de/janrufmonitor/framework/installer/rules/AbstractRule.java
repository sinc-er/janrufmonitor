package de.janrufmonitor.framework.installer.rules;

import java.util.Properties;

public abstract class AbstractRule implements IInstallerRule {
	
	public void validate(Properties descriptor) throws InstallerRuleException {
    	if (descriptor==null || descriptor.size()==0) throw new InstallerRuleException(toString().toLowerCase(), "Deployment descriptor was not set or is empty. This module is not intend to be istalled in this program version.");
	}

	public abstract String toString();
	
}
