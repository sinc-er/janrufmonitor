package de.janrufmonitor.framework.installer;

import java.io.File;

public class LibInstaller extends AbstractInstaller {

	private static String STORED_ARCHIVES = "installed-modules"+File.separator+"~lib"+File.separator;

	public String getExtension() {
		return LibInstaller.EXTENSION_LIB;
	}

	public int getPriority() {
		return 20;
	}

	public String install(boolean overwrite) throws InstallerException {
		if (this.getFile().exists()) {
			new LibHandler().addLibData(this.getFile());
			
			File renamed = new File(this.getStoreDirectory(), this.getFile().getName());
			renamed.getParentFile().mkdirs();
			this.getFile().renameTo(renamed);
		}
		return null;
	}

	protected String getStoreDirectoryName() {
		return LibInstaller.STORED_ARCHIVES;
	}

}
