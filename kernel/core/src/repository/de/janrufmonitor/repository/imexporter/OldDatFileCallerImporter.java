package de.janrufmonitor.repository.imexporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IName;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.CallerNotFoundException;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.imexport.ICallerImporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.repository.types.IIdentifyCallerRepository;
import de.janrufmonitor.runtime.PIMRuntime;

public class OldDatFileCallerImporter implements ICallerImporter {
	
	private String ID = "OldDatFileCallerImporter";
	private String NAMESPACE = "repository.OldDatFileCallerImporter";

	private Logger m_logger;
	private ICallerList m_callerList;
	private II18nManager m_i18n;
	private String m_language;
	private String m_filename;
	
	public OldDatFileCallerImporter() {
		m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		m_i18n = PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager();
		m_language = PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
	}

	public ICallerList doImport() {
		m_callerList = PIMRuntime.getInstance().getCallerFactory().createCallerList();
		Date startDate = new Date();
		File db = new File(m_filename);
		try {
			FileReader dbReader = new FileReader(db);
			BufferedReader bufReader = new BufferedReader(dbReader);
			String line = null;
			ICaller aCaller = null;
			while (bufReader.ready()) {
				line = bufReader.readLine();
				aCaller = this.migrateCallerFromString(line);
				if (aCaller!=null) {	              
					this.m_callerList.add(aCaller);
				}
			}
			bufReader.close();
			dbReader.close();
		} catch (FileNotFoundException ex) {
			this.m_logger.warning("Cannot find caller backup file " + m_filename);
			return this.m_callerList;
		} catch (IOException ex) {
			this.m_logger.severe("IOException on file " + m_filename);
			return this.m_callerList;
		}
		Date endDate = new Date();
		this.m_logger.info("Successfully imported caller file " + m_filename);
		this.m_logger.info("Found " + new Integer(this.m_callerList.size()).toString() + " caller items in " + new Float((endDate.getTime() - startDate.getTime()) / 1000).toString() + " secs.");

		return this.m_callerList;
	}

	public String getID() {
		return this.ID;
	}

	public int getMode() {
		return IImExporter.CALLER_MODE;
	}

	public int getType() {
		return IImExporter.IMPORT_TYPE;
	}

	public String getFilterName() {
		return this.m_i18n.getString(this.NAMESPACE, "filtername", "label", this.m_language);
	}

	public String getExtension() {
		// work-a-round
		String filename = PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty("ui.jface.application.editor.Editor", "oldeditor");
		return (filename.length()>0? filename : "cpnumber.dat");
	}

	public void setFilename(String filename) {
		this.m_filename = filename;
	}
	
       
	private ICaller migrateCallerFromString(String scaller) {
		if (scaller!=null && scaller.trim().length()==0) return null;
		StringTokenizer st = new StringTokenizer(scaller, ";");
        
		String number = st.nextToken().trim();
		String caller = st.nextToken().trim();
		String reject = "0";
		if (st.hasMoreTokens())
			reject = st.nextToken().trim();
	
		IPhonenumber pn = PIMRuntime.getInstance().getCallerFactory().createPhonenumber(number.substring(1));
		try {
			ICaller migCaller = null;
			
			ICallerManager def = PIMRuntime.getInstance().getCallerManagerFactory().getDefaultCallerManager();
			
			if (def!=null && def.isActive() && def.isSupported(IIdentifyCallerRepository.class)) {
				migCaller = ((IIdentifyCallerRepository)def).getCaller(pn);
			} else throw new CallerNotFoundException();

			StringTokenizer ctoken = new StringTokenizer(caller, " ");
			IName name = PIMRuntime.getInstance().getCallerFactory().createName("","");
			if (ctoken.hasMoreTokens()) {
				name.setFirstname(ctoken.nextToken());
			}	
			if (ctoken.hasMoreTokens()) {
				name.setLastname(ctoken.nextToken());
			}	
			if (ctoken.hasMoreTokens()) {
				name.setAdditional(ctoken.nextToken());
			}		
			while (ctoken.hasMoreTokens()) {
				name.setAdditional(name.getAdditional()+ " " +ctoken.nextToken());
			}
			migCaller.setName(name);	
			if (reject.equalsIgnoreCase("1")){
				IAttribute att = PIMRuntime.getInstance().getCallerFactory().createAttribute(
					IJAMConst.ATTRIBUTE_NAME_REJECT,
					IJAMConst.ATTRIBUTE_VALUE_YES
				);
				migCaller.setAttribute(att);
			}
			if (reject.equalsIgnoreCase("0")){
				IAttribute att = PIMRuntime.getInstance().getCallerFactory().createAttribute(
					IJAMConst.ATTRIBUTE_NAME_REJECT,
					IJAMConst.ATTRIBUTE_VALUE_NO
				);
				migCaller.setAttribute(att);
			}	

			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).info(migCaller.toString());
			return migCaller;
		} catch (CallerNotFoundException e) {
			LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER).warning(e.getMessage());
		}
		return null;
	}
}
