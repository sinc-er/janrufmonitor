package de.janrufmonitor.framework.installer;

import java.io.File;

public class I18nInstaller extends AbstractInstaller {

	private static String STORED_ARCHIVES = "installed-modules"+File.separator+"~i18n"+File.separator;

	public String getExtension() {
		return I18nInstaller.EXTENSION_I18N;
	}

	public int getPriority() {
		return 15;
	}

	public String install(boolean overwrite) throws InstallerException {
		if (this.getFile().exists()) {
			new I18nHandler().addI18nData(this.getFile());
			
			File renamed = new File(this.getStoreDirectory(), this.getFile().getName());
			renamed.getParentFile().mkdirs();
			this.getFile().renameTo(renamed);
		}
		return null;
	}

	protected String getStoreDirectoryName() {
		return I18nInstaller.STORED_ARCHIVES;
	}
}
