package de.janrufmonitor.repository.imexporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
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
import de.janrufmonitor.util.io.Serializer;
import de.janrufmonitor.util.io.SerializerException;

public class DatFileCallImporter implements ICallImporter {
	
	public class Transformator {
		
		public String CALL_SEPARATOR = "***";
		
		String ATT_SEPARATOR = "%%%";
	    String SEPARATOR = ";";
	    
	    public ICall transform(String s){
	    	StringTokenizer st = new StringTokenizer(s, CALL_SEPARATOR);
			 ICaller aCaller = getCallerFromPhoneString(st.nextToken());
			 if (aCaller!=null) {
			 	ICall aCall = getCallFromString(st.nextToken());
			 	aCall.setCaller(aCaller);
			 	return aCall;
			 }
			 return null;
	    }
	    
	    private ICaller getCallerFromPhoneString(String scaller) {
			IPhonenumber pn = null;
			try {
				StringTokenizer st = new StringTokenizer(scaller, SEPARATOR);
				String intAreaCode = st.nextToken().trim();
				String areaCode = st.nextToken().trim();
				String callNumber = st.nextToken().trim();
				pn = PIMRuntime.getInstance().getCallerFactory().createPhonenumber(intAreaCode, areaCode, callNumber);
				if (pn.isClired()) {
					IName name = PIMRuntime.getInstance().getCallerFactory().createName(st.nextToken().trim(), st.nextToken().trim(), st.nextToken().trim());
					return PIMRuntime.getInstance().getCallerFactory().createCaller(name, pn);
				}
				
				ICallerManager def = PIMRuntime.getInstance().getCallerManagerFactory().getDefaultCallerManager();
					
				if (def!=null && def.isActive() && def.isSupported(IIdentifyCallerRepository.class)) {
					return ((IIdentifyCallerRepository)def).getCaller(pn);
				}
				
				return null;
			} catch (NoSuchElementException ex) { 
				m_logger.warning(ex.getMessage());
			} catch (CallerNotFoundException e) {
				m_logger.warning(e.getMessage());
			}
			return null;
		}

	    private ICall getCallFromString(String scall) {
	        StringTokenizer st = new StringTokenizer(scall, SEPARATOR);
	        String msn = st.nextToken().trim();
	        // check if * is set as MSN
	        if (msn.equalsIgnoreCase("*"))
	        	msn = "0";
	        String msnadd = st.nextToken().trim();
	        String cip = st.nextToken().trim();
	        String cipadd = st.nextToken().trim();
	        String uuid = st.nextToken().trim();
	        long sdate = new Long(st.nextToken().trim()).longValue();
	        IMsn nmsn = PIMRuntime.getInstance().getCallFactory().createMsn(msn, msnadd);
	        ICip ncip = PIMRuntime.getInstance().getCallFactory().createCip(cip, cipadd);
	        
	        ICall aCall = PIMRuntime.getInstance().getCallFactory().createCall(uuid, null, nmsn, ncip, new Date(sdate));

	        while (st.hasMoreTokens()) {
	            String attrib = st.nextToken().trim();
	            String attName = attrib.substring(0, attrib.indexOf(ATT_SEPARATOR));
	            String attValue = attrib.substring(attrib.indexOf(ATT_SEPARATOR) + ATT_SEPARATOR.length(), attrib.length());	            
	            IAttribute att = PIMRuntime.getInstance().getCallFactory().createAttribute(attName, attValue);
	            aCall.setAttribute(att);
	        }
	        return aCall;
	    }

		
	}

	private String ID = "DatFileCallImporter";
	private String NAMESPACE = "repository.DatFileCallImporter";

	private Logger m_logger;
	private ICallList m_callList;
	private II18nManager m_i18n;
	private String m_language;
	private String m_filename;

	public DatFileCallImporter() {
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
		return "*.dat";
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
		 ICall aCall = null;
		 
		 boolean isNewDatFormat = false;
		 String line = null;
		 Transformator tf = null;
		 while (bufReader.ready()) {
		 	line = bufReader.readLine();
		 	if (!isNewDatFormat && line !=null && line.indexOf("***")>-1) {
		 		if (tf==null) tf = new Transformator();
		 	} else {
		 		isNewDatFormat = true;
		 	}
		 		
		 	
			try {
				aCall = (isNewDatFormat ? Serializer.toCall(
						line.getBytes(),
						PIMRuntime.getInstance()) : tf.transform(line));
				// 2008/11/07: new call status introduced in 5.0.8
				migrateStatus(aCall);
			 	this.m_callList.add(aCall);
			} catch (SerializerException e) {
				this.m_logger.warning(e.getMessage());
			}
		 }
		 bufReader.close();
		 dbReader.close();
		} catch (FileNotFoundException ex) {
		 this.m_logger.warning("Cannot find call backup file " + m_filename);
		 return this.m_callList;
		} catch (IOException ex) {
		 this.m_logger.severe("IOException on file " + m_filename);
		 return this.m_callList;
		} catch (NoSuchElementException ex) {
			this.m_logger.severe("Invalid format in file " + m_filename);
			return this.m_callList;
		}
		Date endDate = new Date();
		this.m_logger.info("Successfully imported call file " + m_filename);
		this.m_logger.info("Found " + new Integer(this.m_callList.size()).toString() + " call items in " + new Float((endDate.getTime() - startDate.getTime()) / 1000).toString() + " secs.");

		return m_callList;
	}
	
	private void migrateStatus(ICall c) {
		if (c.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS)!=null) return; 
		IAttribute a = c.getAttribute(IJAMConst.ATTRIBUTE_VALUE_ACCEPTED);
		if (a!=null) {
			c.getAttributes().remove(a);
			a.setName(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS);
			a.setValue((a.getValue().equals(IJAMConst.ATTRIBUTE_VALUE_YES) ? IJAMConst.ATTRIBUTE_VALUE_ACCEPTED : IJAMConst.ATTRIBUTE_VALUE_MISSED));
			c.setAttribute(a);
			return;
		}
		a = c.getAttribute(IJAMConst.ATTRIBUTE_VALUE_REJECTED);
		if (a!=null) {
			c.getAttributes().remove(a);
			a.setName(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS);
			a.setValue((a.getValue().equals(IJAMConst.ATTRIBUTE_VALUE_YES) ? IJAMConst.ATTRIBUTE_VALUE_REJECTED : IJAMConst.ATTRIBUTE_VALUE_MISSED));
			c.setAttribute(a);
			return;
		}
		a = c.getAttribute(IJAMConst.ATTRIBUTE_VALUE_OUTGOING);
		if (a!=null) {
			c.getAttributes().remove(a);
			a.setName(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS);
			a.setValue((a.getValue().equals(IJAMConst.ATTRIBUTE_VALUE_YES) ? IJAMConst.ATTRIBUTE_VALUE_OUTGOING : IJAMConst.ATTRIBUTE_VALUE_MISSED));
			c.setAttribute(a);
			return;
		}		
		c.setAttribute(PIMRuntime.getInstance().getCallFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS, IJAMConst.ATTRIBUTE_VALUE_MISSED));
	}

	public int getType() {
		return IImExporter.IMPORT_TYPE;
	}
}
