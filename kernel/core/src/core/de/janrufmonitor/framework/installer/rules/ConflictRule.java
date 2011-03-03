package de.janrufmonitor.framework.installer.rules;

import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

import de.janrufmonitor.framework.installer.InstallerConst;
import de.janrufmonitor.framework.installer.InstallerEngine;

public class ConflictRule extends AbstractRule {

	public void validate(Properties descriptor) throws InstallerRuleException {
		super.validate(descriptor);

    	String depend = descriptor.getProperty(InstallerConst.DESCRIPTOR_CONFLICT);
    	if (depend==null) return;

    	List installedMod = InstallerEngine.getInstance().getModuleList();
    	StringTokenizer st = new StringTokenizer(depend, ",");
    	String dep = null;
    	while (st.hasMoreTokens()) {
    		dep = st.nextToken().trim();
    		if (installedMod.contains(dep)) throw new InstallerRuleException(toString().toLowerCase(), "The new module cannot be installed, because it is in conflict with the module <"+dep+">");
    	}
	}

	public String toString() {
		return "ConflictRule";
	}
	
}
