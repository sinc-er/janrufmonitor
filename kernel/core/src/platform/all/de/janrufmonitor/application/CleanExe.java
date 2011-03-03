package de.janrufmonitor.application;

import java.io.File;

import de.janrufmonitor.util.io.PathResolver;

public class CleanExe {
	
	public void run() {
		System.out.println("Deleting all jAnrufmonitor *.lck files...");
		File startDir = new File(PathResolver.getInstance().getInstallDirectory());
		deleteLocks(startDir);		
		System.out.println("...finished!");
	}
	
	private void deleteLocks(File dir) {
		if (dir.exists() && dir.isDirectory()){
			System.out.println("Processing folder "+dir.getAbsolutePath());
			File[] files = dir.listFiles();
			for (int i=0;i<files.length;i++) {
				if (files[i].isDirectory())
					deleteLocks(files[i]);
				else {
					if (files[i].getName().endsWith("pimjava.exe") || files[i].getName().endsWith("loader.ini") || files[i].getName().endsWith("pimconsole.bat")) {
						System.out.println("Deleting file "+files[i].getAbsolutePath());
						if (!files[i].delete())
							files[i].deleteOnExit();
					}
				}
			}
		}
	}

	public static void main(String[] args) {
		new CleanExe().run();
	}
}
