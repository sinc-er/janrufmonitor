package de.janrufmonitor.repository;

import java.io.*;
import java.util.Properties;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.repository.types.ILocalRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.io.Serializer;
import de.janrufmonitor.util.io.SerializerException;
import de.janrufmonitor.util.string.StringUtils;

public class TextFileCallManager extends AbstractPersistentCallManager implements ILocalRepository {
    
	private String ID = "TextFileCallManager";
	private String NAMESPACE = "repository.TextFileCallManager";

	private String CONFIG_KEY = "database";

	private String ATT_SEPARATOR = "%%%";
	private String CALL_SEPARATOR = "***";
    
	public TextFileCallManager() {
		super();
		this.getRuntime().getConfigurableNotifier().register(this);
	}

	public void loadDatabase() {
		loadDatabase(PathResolver.getInstance(PIMRuntime.getInstance()).resolve(this.m_configuration.getProperty(CONFIG_KEY)));
	}
    
	public void saveDatabase() {
		saveDatabase(PathResolver.getInstance(PIMRuntime.getInstance()).resolve(this.m_configuration.getProperty(CONFIG_KEY)));        
	}
    
	public String getNamespace() {
		return this.NAMESPACE;
	}
    
	public void setConfiguration(Properties configuration) {
		super.setConfiguration(configuration);
		this.m_callList = null;
		this.m_callList = PIMRuntime.getInstance().getCallFactory().createCallList();
		if (this.checkDBfile(PathResolver.getInstance(PIMRuntime.getInstance()).resolve(this.m_configuration.getProperty(CONFIG_KEY)))){
			this.loadDatabase();
		} 
	}
    
	private boolean checkDBfile(String path){
		File file = new File(path);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				this.m_logger.severe("File could not be created: " + path);
				return false;
			}
		}
		return true;
	}    
    
	 private synchronized void saveDatabase(String database) {
		File db = new File(database);
		try {
			FileWriter dbWriter = new FileWriter(db);
			BufferedWriter bufWriter = new BufferedWriter(dbWriter);
			for (int i = 0; i < this.m_callList.size(); i++) {
				try {
					bufWriter.write(new String(Serializer.toByteArray(this.m_callList.get(i))));
					bufWriter.newLine();
				} catch (SerializerException e) {
					this.m_logger.severe(e.getMessage());
				}
			}
			bufWriter.flush();
			bufWriter.close();
			dbWriter.close();
		} catch (FileNotFoundException ex) {
			this.m_logger.severe("File not found: " + database);
		} catch (IOException ex) {
			this.m_logger.severe("IOException on file " + database);
		}
	}

	private void loadDatabase(String database) {
		long startDate = System.currentTimeMillis();
		File db = new File(database);
		try {
			FileReader dbReader = new FileReader(db);
			BufferedReader bufReader = new BufferedReader(dbReader);
			String line = null;
			ICall aCall = null;
			while (bufReader.ready()) {
				line = bufReader.readLine();
				
				// check for old entries
				if (line.indexOf(CALL_SEPARATOR)>-1)
					line = this.transformOldTextfileData(line);

				try {
					aCall = Serializer.toCall(line.getBytes(), this.getRuntime());
					this.m_callList.add(aCall);
				} catch (SerializerException e) {
					this.m_logger.severe(e.getMessage());
				}
			}
			bufReader.close();
			dbReader.close();
		} catch (FileNotFoundException ex) {
			this.m_logger.warning("Cannot find call database: " + database);
			this.m_logger.info("Creating call database: " + database);

			File dbnew = new File(database);
			if (!dbnew.exists()) {
				try {
					dbnew.createNewFile();
				} catch (IOException e) {
					this.m_logger.severe("Cannot create file: " + database + ". Check read/write permissions.");
				}
			}
		} catch (IOException ex) {
			this.m_logger.severe("IOException on file " + database);
		} catch (NullPointerException ex) {
		this.m_logger.warning("Final character in file "+database+" is null and filtered out.");	
	  }

		long endDate = System.currentTimeMillis();
		this.m_logger.info("Successfully loaded call database " + database);
		this.m_logger.info("Found " + new Integer(this.m_callList.size()).toString() + " call items in " + Float.toString((endDate - startDate) / 1000) + " secs.");
	}

	public IRuntime getRuntime() {
		return PIMRuntime.getInstance();
	}
	
	private String transformOldTextfileData(String oldLine) {
		this.m_logger.info("Converting old call data ...");
		oldLine = StringUtils.replaceString(oldLine, CALL_SEPARATOR, " ; &");
		oldLine = StringUtils.replaceString(oldLine, ATT_SEPARATOR, "=");
		oldLine += " ;";
		return oldLine;
	}

	public String getID() {
		return this.ID;
	}

	public String getFile() {
		return PathResolver.getInstance(PIMRuntime.getInstance()).resolve(this.m_configuration.getProperty(CONFIG_KEY, "%installpath%/journal.dat"));
	}

	public String getFileType() {
		return "*.dat";
	}

	public void setFile(String filename) {
		this.getRuntime().getConfigManagerFactory().getConfigManager().setProperty(getNamespace(), CONFIG_KEY, filename);
		this.getRuntime().getConfigManagerFactory().getConfigManager().saveConfiguration();		
	}

}

