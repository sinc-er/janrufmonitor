package de.janrufmonitor.repository.imexporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICip;
import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.framework.IName;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.CallerNotFoundException;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.imexport.ICallImporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.repository.types.IIdentifyCallerRepository;
import de.janrufmonitor.runtime.PIMRuntime;

public class OldDatFileCallImporter implements ICallImporter {
	
	private String ID = "OldDatFileCallImporter";
	private String NAMESPACE = "repository.OldDatFileCallImporter";

	private Logger m_logger;
	private ICallList m_callList;
	private II18nManager m_i18n;
	private String m_language;
	private String m_filename;
	private String m_migrationDateFormat;

	public OldDatFileCallImporter() {
		m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		m_i18n = PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager();
		m_language = PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
	}

	public String getID() {
		return this.ID;
	}

	public int getMode() {
		return IImExporter.CALL_MODE;
	}

	public String getFilterName() {
		return this.m_i18n.getString(this.NAMESPACE, "filtername", "label", this.m_language);
	}

	public String getExtension() {
		// work-a-round
		String filename = PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty("ui.jface.application.journal.Journal", "oldjournal");
		return (filename.length()>0? filename : "ajournal.dat");

	}

	public void setFilename(String filename) {
		this.m_filename = filename;
	}

	public ICallList doImport() {
		m_callList = PIMRuntime.getInstance().getCallFactory().createCallList();
		Date startDate = new Date();
		File db = new File(m_filename);
		try {
			FileReader dbReader = new FileReader(db);
			BufferedReader bufReader = new BufferedReader(dbReader);
			String line = null;
			ICall aCall = null;
			while (bufReader.ready()) {
				line = bufReader.readLine();
				aCall = this.migrateCallFromString(line, this.m_migrationDateFormat);
				if (aCall!=null) {	              
					this.m_callList.add(aCall);
				}
			}
			bufReader.close();
			dbReader.close();
		} catch (FileNotFoundException ex) {
			this.m_logger.warning( "Cannot find call backup file " + m_filename);
			return this.m_callList;
		} catch (IOException ex) {
			this.m_logger.severe("IOException on file " + m_filename);
			return this.m_callList;
		}
		Date endDate = new Date();
		this.m_logger.info("Successfully imported call file " + m_filename);
		this.m_logger.info("Found " + new Integer(this.m_callList.size()).toString() + " call items in " + new Float((endDate.getTime() - startDate.getTime()) / 1000).toString() + " secs.");

		return this.m_callList;	
	}
	
	private IMsn parseMsn(String msn) {
		IMsn parsedMSN = PIMRuntime.getInstance().getCallFactory().createMsn("","");
		if (msn.indexOf("(")>0) {
			String _msn = msn.substring(0, msn.indexOf("(")-1);
			_msn = _msn.trim();
			String _msnAdd = msn.substring(msn.indexOf("(")+1, msn.lastIndexOf(")"));
			parsedMSN.setMSN(_msn);
			parsedMSN.setAdditional(_msnAdd);
		} else {
			msn = msn.trim();
			parsedMSN.setMSN(msn);
			parsedMSN.setAdditional(PIMRuntime.getInstance().getMsnManager().getMsnLabel(msn));
		}
		return parsedMSN;
	}
	
	private ICip parseCip(String cip) {
		ICip parsedCIP = PIMRuntime.getInstance().getCallFactory().createCip("","");
		parsedCIP.setCIP("999");
		parsedCIP.setAdditional(PIMRuntime.getInstance().getCipManager().getCipLabel("999", ""));        
		if (cip.startsWith("Digitaler Telefondienst (ISDN)")) {
			parsedCIP.setCIP("1");
			parsedCIP.setAdditional(PIMRuntime.getInstance().getCipManager().getCipLabel("1", ""));
		}
		if (cip.startsWith("Analoger Telefondienst / Fax")) {
			parsedCIP.setCIP("4");
			parsedCIP.setAdditional(PIMRuntime.getInstance().getCipManager().getCipLabel("4", ""));
		}
		if (cip.startsWith("Fax Gruppe 2/3")) {
			parsedCIP.setCIP("4");
			parsedCIP.setAdditional(PIMRuntime.getInstance().getCipManager().getCipLabel("4", ""));
		}   
		return parsedCIP;
	}
	
	private IAttribute parseState(String state) {
		IAttribute att = PIMRuntime.getInstance().getCallFactory().createAttribute("","");
		if (state.equalsIgnoreCase("1")){
			att.setName(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS);
			att.setValue(IJAMConst.ATTRIBUTE_VALUE_REJECTED);
		} else if (state.equalsIgnoreCase("2")){
			att.setName(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS);
			att.setValue(IJAMConst.ATTRIBUTE_VALUE_ACCEPTED);
		} else {
			att.setName(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS);
			att.setValue(IJAMConst.ATTRIBUTE_VALUE_MISSED);
		}
		return att;
	}
	
	private IPhonenumber parsePhone(String caller) {
		if (caller.indexOf("(0")>-1) {
			IPhonenumber phone = null;
			String pn = caller.substring(caller.indexOf("(0") + 2, caller.lastIndexOf(")"));
			if (pn.length()>0) {
				phone = PIMRuntime.getInstance().getCallerFactory().createPhonenumber(pn);
				ICallerManager cm = PIMRuntime.getInstance().getCallerManagerFactory().getDefaultCallerManager();
				if (cm!=null && cm.isActive() && cm.isSupported(IIdentifyCallerRepository.class)) {
					try {
						ICaller parsedCaller = ((IIdentifyCallerRepository)cm).getCaller(phone);
						return parsedCaller.getPhoneNumber();
					} catch (CallerNotFoundException e) {
						this.m_logger.warning(e.getMessage());
					}
				}
			}
		}
		return PIMRuntime.getInstance().getCallerFactory().createPhonenumber(true);
	}
	
	private IName parseName(String caller) {
		String name = "";
		
		if (caller.indexOf("( -")>-1) {
			name = caller.substring(0, caller.indexOf("( -"));
		}
		
		if (caller.indexOf("(0")>-1) {
			name = caller.substring(0, caller.indexOf("(0"));
		}

		StringTokenizer st = new StringTokenizer(name, " ");
		if (st.countTokens()==1) {
			return PIMRuntime.getInstance().getCallerFactory().createName(
				"","",st.nextToken()
			);
		}
		if (st.countTokens()==2) {
			return PIMRuntime.getInstance().getCallerFactory().createName(
				st.nextToken(),st.nextToken()
			);
		}
		if (st.countTokens()==3) {
			return PIMRuntime.getInstance().getCallerFactory().createName(
				st.nextToken(),st.nextToken(),st.nextToken()
			);
		}
		if (st.countTokens()>3) {
			IName n = PIMRuntime.getInstance().getCallerFactory().createName("","");
			n.setFirstname(st.nextToken());
			n.setLastname(st.nextToken());
			n.setAdditional(st.nextToken());
			while (st.hasMoreTokens()) {
				n.setAdditional(n.getAdditional()+ " "+st.nextToken());
			}
			return n;
		}
		
		return PIMRuntime.getInstance().getCallerFactory().createName("","");
	}
	
	private Date parseDate(String date, String datePattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(datePattern);
		try {
			return sdf.parse(date);
		} catch (ParseException e) {
			this.m_logger.warning("Wrong date format detected.");
		}
		return new Date(0);
	}
	
	private ICall migrateCallFromString(String scall, String datePattern) {
		if (scall!=null && scall.trim().length()==0) return null;
		try { 
			StringTokenizer st = new StringTokenizer(scall, "%$");
        
        	if (st.countTokens()<5){
				this.m_logger.warning("Invalid entry found: entry was dropped for migration.");
				return null;
        	}        		
        
			String state = st.nextToken().trim();
			String msn = st.nextToken().trim();
			String caller = st.nextToken().trim();
			String cip = st.nextToken().trim();
			String date = st.nextToken().trim();
        
			// parse MSN
			IMsn parsedMSN = this.parseMsn(msn);
        
			ICip parsedCIP = this.parseCip(cip);   
        
			IAttribute att = this.parseState(state);	
			
			IPhonenumber phone = this.parsePhone(caller);
			
			IName name = this.parseName(caller);
			
			ICaller parsedCaller = PIMRuntime.getInstance().getCallerFactory().createCaller(name, phone);
			
			Date parsedDate = this.parseDate(date, datePattern);
		
			ICall aCall = PIMRuntime.getInstance().getCallFactory().createCall(
				parsedCaller,
				parsedMSN,
				parsedCIP,
				parsedDate
			);
		
			if (!att.getName().equalsIgnoreCase("")) {
				aCall.setAttribute(att);
			}	
			
			this.m_logger.info(aCall.toString());
			return aCall;
		} catch (NoSuchElementException ex) {
			this.m_logger.warning("FormatException: migration failed due to incompatible format information.");
		} catch (NullPointerException ex) {
			this.m_logger.warning("Invalid entry found: entry was dropped for migration.");
		}
		return null;
	}


	public void setDatePattern(String pattern) {
		this.m_migrationDateFormat = pattern;
	}

	public int getType() {
		return IImExporter.IMPORT_TYPE;
	}
}
