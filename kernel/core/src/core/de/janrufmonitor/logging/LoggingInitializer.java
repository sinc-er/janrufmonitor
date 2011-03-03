package de.janrufmonitor.logging;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.io.Stream;
import de.janrufmonitor.util.string.StringUtils;

public class LoggingInitializer {
	
	public static void run() {
		File dir = new File(PathResolver.getInstance().getLogDirectory());
		if (!dir.exists())
			dir.mkdirs();
		
		LogManager lm = LogManager.getLogManager();
		
		// create PIM logger
		Logger log = Logger.getLogger(IJAMConst.DEFAULT_LOGGER);
		lm.addLogger(log);
		
		// set configuration
		try {
			File f = new File(PathResolver.getInstance().getConfigDirectory()+"logging.properties");
			InputStream in = new FileInputStream(f);
			String config = new String(LoggingInitializer.toByteArray(in));
			config = StringUtils.replaceString(config, IJAMConst.PATHKEY_LOGPATH, PathResolver.getInstance().getLogDirectory());
			config = StringUtils.replaceString(config, IJAMConst.PATHKEY_INSTALLPATH, PathResolver.getInstance().getInstallDirectory());
			config = StringUtils.replaceString(config, "\\", "/");
			ByteArrayInputStream is = new ByteArrayInputStream(config.getBytes());
			lm.readConfiguration(is);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		Stream.copy(in, bout, 1024);
		return bout.toByteArray();
	}
}
