package de.janrufmonitor.framework.installer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.launcher.ILauncher;
import de.janrufmonitor.runtime.launcher.LauncherFactory;

public class LibHandler {

	private Logger m_logger;

	public LibHandler() {
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
	}

	public void removeLibData(File f) {
        if (!f.getName().toLowerCase().endsWith(InstallerConst.EXTENSION_LIB)) {
            this.m_logger.severe("File is not a LIB file: " + f.getName());
            return;
        }
		
		try {
			Properties props = new Properties();
			FileInputStream fis = new FileInputStream(f);
			props.load(fis);
			fis.close();
			removeLibData(props);
		} catch (FileNotFoundException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		}
    }
	
	public void removeLibData(Properties libData) {
		LauncherFactory lf = LauncherFactory.getInstance();
		String libTagNew = libData.getProperty("lib", "");
		String[] lIds = lf.getAllLauncherIDs();
		for (int i=0;i<lIds.length;i++) {
			this.m_logger.info("Updating launcher "+lIds[i]);
			ILauncher l = lf.getLauncher(lIds[i]);
			StringTokenizer st = new StringTokenizer(libTagNew, ";");
			while(st.hasMoreTokens()) {
				String token = st.nextToken();
				if (token.startsWith("-")) {
					l.addLibrary(token.substring(1));
				} else{
					l.removeLibrary(token);
					l.removeLibrary(token.substring(1));
				}
			}
		}
	}
	
	public void addLibData(Properties libData) {
		LauncherFactory lf = LauncherFactory.getInstance();
		String libTagNew = libData.getProperty("lib", "");
		String[] lIds = lf.getAllLauncherIDs();
		for (int i=0;i<lIds.length;i++) {
			this.m_logger.info("Updating launcher "+lIds[i]);
			ILauncher l = lf.getLauncher(lIds[i]);
			StringTokenizer st = new StringTokenizer(libTagNew, ";");
			while(st.hasMoreTokens()) {
				String token = st.nextToken();
				if (token.startsWith("-")) {
					l.removeLibrary(token);
					l.removeLibrary(token.substring(1));
				} else{
					l.addLibrary(token);
				}
			}
		}
	}
	
	public void addLibData(File f) {
        if (!f.getName().toLowerCase().endsWith(InstallerConst.EXTENSION_LIB)) {
            this.m_logger.severe("File is not a LIB file: " + f.getName());
            return;
        }
		
		try {
			Properties props = new Properties();
			FileInputStream fis = new FileInputStream(f);
			props.load(fis);
			fis.close();
			addLibData(props);
		} catch (FileNotFoundException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		}
    }

}
