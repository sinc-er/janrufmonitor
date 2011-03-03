package de.janrufmonitor.framework.installer.rules;

import java.util.Properties;

public interface IInstallerRule {

	public void validate(Properties descriptor) throws InstallerRuleException;

}
