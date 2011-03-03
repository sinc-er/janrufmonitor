package de.janrufmonitor.repository.imexporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.linuxense.javadbf.DBFReader;

import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICallFactory;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICip;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.identify.Identifier;
import de.janrufmonitor.repository.imexport.ICallImporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.util.io.PathResolver;

public class FritzXCallImporter implements ICallImporter {

	private class CallCollection {
		String m_id;
		List m_list;
		public CallCollection(String id) {
			this.m_id = id;
			this.m_list = new ArrayList(5);
		}
		
		public String getId() {
			return this.m_id;
		}
		
		public void add(Object[] o) {
			this.m_list.add(o);
		}
		
		public List getList() {
			return this.m_list;
		}
		
		public boolean isAccepted() {
			boolean isAccepted = false;
			Object[] o = null;
			for (int i=0;i<this.m_list.size();i++) {
				o = (Object[]) this.m_list.get(i);
				isAccepted |= ((Double)o[3]).toString().startsWith("1");
			}
			return isAccepted;
		}
		
		public String getAcceptedMSN() {
			if (!this.isAccepted() && this.m_list.size()>0) return getMSNMapping(((Double)((Object[]) this.m_list.get(0))[1]).toString().substring(0,1));
			if (this.isAccepted()) {
				Object[] o = null;
				for (int i=0;i<this.m_list.size();i++) {
					o = (Object[]) this.m_list.get(i);
					if (((Double)o[3]).toString().startsWith("1")) {
						return getMSNMapping(((Double)o[1]).toString().substring(0,1));
					}
				}
			}
			return getMSNMapping("0");
		}
		
		private String getMSNMapping(String msn) {
			File mapping = new File(PathResolver.getInstance(getRuntime()).getConfigDirectory(), "fritzx-mapping.properties");
			if (!mapping.exists() && msn.trim().length()==1) {
				String[] msns = getRuntime().getMsnManager().getMsnList();
				int m = Integer.parseInt(msn);
				if (msns.length>=(m+1)) return msns[m];
			} else if(mapping.exists()) {
				Properties map = new Properties();
				
				try {
					FileInputStream fis = new FileInputStream(mapping);
					map.load(fis);
					fis.close();
					String value = map.getProperty(msn);
					if (value!=null) return value;
				} catch (FileNotFoundException e) {
					m_logger.log(Level.SEVERE, e.toString(), e);
				} catch (IOException e) {
					m_logger.log(Level.SEVERE, e.toString(), e);
				}				
			} 				
			String[] msns = getRuntime().getMsnManager().getMsnList();
			if (msns.length>0) return msns[0];
			return msn;
		}
	}
	
	private class MigratorThread implements Runnable {

		private ICallList m_cl;

		private CallCollection m_call;

		private ICallFactory m_clf;

		public MigratorThread(ICallList cl, CallCollection line) {
			this.m_cl = cl;
			this.m_call = line;
			this.m_clf = getRuntime().getCallFactory();
		}

		public void run() {
			if (this.m_call == null || m_call.getList().size()==0) {
				m_logger.warning("Invalid migration entry.");
				return;
			}
			
			Object[] call = ((Object[])m_call.getList().get(0));
			
			// date format: 28/11/2009 14/14/23
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH/mm/ss"); 
			Date date = new Date();
			try {
				date = sdf.parse((String) call[0]);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			IMsn msn = getRuntime().getMsnManager().createMsn(this.m_call.getAcceptedMSN());
			ICip cip = getRuntime().getCipManager().createCip("1");
			
			IPhonenumber pn = getRuntime().getCallerFactory().createPhonenumber(((String) call[2]).substring(1));
			ICaller c = null;
			if (((String) call[2]).startsWith("Ohn")) {
				pn = getRuntime().getCallerFactory().createPhonenumber(true);
				c = getRuntime().getCallerFactory().createCaller(pn);
			} else {
				c = Identifier.identify(getRuntime(), pn);
			}
			
			
			IAttributeMap mc = this.m_clf.createAttributeMap();
			
			mc.add(
				getRuntime().getCallerFactory().createAttribute(
				IJAMConst.ATTRIBUTE_NAME_CALLSTATUS, 
				(this.m_call.isAccepted() ? IJAMConst.ATTRIBUTE_VALUE_ACCEPTED : IJAMConst.ATTRIBUTE_VALUE_MISSED)
				)
			);					
			
			String uuid = date.getTime() + pn.getTelephoneNumber();
			
			this.m_cl.add(
				this.m_clf.createCall(uuid, c, msn, cip, date, mc)
			);
		}

	}

	private String ID = "FritzXCallImporter";

	private String NAMESPACE = "repository.FritzXCallImporter";

	Logger m_logger;

	ICallList m_callList;

	II18nManager m_i18n;

	String m_language;

	String m_filename;

	private IRuntime m_runtime;

	public FritzXCallImporter() {
		m_logger = LogManager.getLogManager().getLogger(
				IJAMConst.DEFAULT_LOGGER);
		m_i18n = getRuntime().getI18nManagerFactory().getI18nManager();
		m_language = getRuntime().getConfigManagerFactory().getConfigManager()
				.getProperty(IJAMConst.GLOBAL_NAMESPACE,
						IJAMConst.GLOBAL_LANGUAGE);
	}

	public String getExtension() {
		return "inccall.dbf";
	}

	public String getFilterName() {
		return this.m_i18n.getString(this.NAMESPACE, "filtername", "label",
				this.m_language);
	}

	public String getID() {
		return ID;
	}

	public int getMode() {
		return IImExporter.CALL_MODE;
	}

	public int getType() {
		return IImExporter.IMPORT_TYPE;
	}

	public void setFilename(String filename) {
		this.m_filename = filename;
	}

	private IRuntime getRuntime() {
		if (this.m_runtime == null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public ICallList doImport() {
		long startDate = System.currentTimeMillis();
		try {
			File fritzxfile = new File(m_filename);
			FileInputStream fis = new FileInputStream(fritzxfile);
			DBFReader dbfReader = new DBFReader(fis);
	
			int size = dbfReader.getRecordCount();
			size = Math.abs(size);

			m_callList = getRuntime().getCallFactory().createCallList(size);
		
			Object[]rowObjects = null;
			CallCollection cc = null;
			List cc_list = new ArrayList();
		    while((rowObjects = dbfReader.nextRecord()) != null) {
				if (cc!=null && cc.getId().equalsIgnoreCase((String) rowObjects[0])) {
					cc.add(rowObjects);
				} else {
					cc = new CallCollection((String) rowObjects[0]);
					cc_list.add(cc);
					cc.add(rowObjects);
				}	
			}
		    for (int i=0,j=cc_list.size();i<j;i++) {
		    	new MigratorThread(
						this.m_callList, (CallCollection)cc_list.get(i)).run();		
		    }
		    
		    
			fis.close();
		} catch (FileNotFoundException ex) {
			this.m_logger.warning("Cannot find call file " + m_filename);
			return this.m_callList;
		} catch (IOException ex) {
			this.m_logger.severe("IOException on file " + m_filename);
			return this.m_callList;
		}

		long endDate = System.currentTimeMillis();
		this.m_logger.info("Successfully imported call file " + m_filename);
		this.m_logger
				.info("Found " + new Integer(this.m_callList.size()).toString()
						+ " call items in "
						+ new Float((endDate - startDate) / 1000).toString()
						+ " secs.");
		return m_callList;
	}

}
