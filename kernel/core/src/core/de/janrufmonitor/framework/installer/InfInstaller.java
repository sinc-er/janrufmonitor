package de.janrufmonitor.framework.installer;

import java.io.File;

public class InfInstaller extends AbstractInstaller {

	private static String STORED_ARCHIVES = "installed-modules"+File.separator+"~inf"+File.separator;

	public String getExtension() {
		return InfInstaller.EXTENSION_INF;
	}

	public int getPriority() {
		return 10;
	}

	public String install(boolean overwrite) throws InstallerException {
		if (this.getFile().exists()) {
			InfHandler ih = new InfHandler();
			ih.addInfData(this.getFile());
			ih.setOverwriteConfiguration(overwrite);
			
			File renamed = new File(this.getStoreDirectory(), this.getFile().getName());
			renamed.getParentFile().mkdirs();
			this.getFile().renameTo(renamed);
		}
		return null;
	}

	protected String getStoreDirectoryName() {
		return InfInstaller.STORED_ARCHIVES;
	}
}
