package de.janrufmonitor.repository.imexporter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICallFactory;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerFactory;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.ICip;
import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.framework.IName;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.identify.Identifier;
import de.janrufmonitor.repository.imexport.ICallImporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTExecuter;

public class IsdnMoniCallImporter implements ICallImporter {
	
	private String ID = "IsdnMoniCallImporter";
	private String NAMESPACE = "repository.IsdnMoniCallImporter";

	private String CFG_SHOW_ENTRY_WARNING = "showdialog";
	
	Logger m_logger;
	ICallList m_callList;
	ICallerList m_callerList;
	List m_pnList;
	II18nManager m_i18n;
	String m_language;
	String m_filename;
	boolean isDialogFinished = false;

	private IRuntime m_runtime;
	
	private class MigratorThread implements Runnable {	

		private ICallList m_cl;
		private ICallerList m_c;
		private List m_pns;
		private String line;
		private ICallerFactory m_cf;
		private ICallFactory m_clf;

		public MigratorThread(ICallList cl, ICallerList c, String line, List pns) {
			this.m_cl = cl;
			this.m_c = c;
			this.line = line;
			this.m_cf = getRuntime().getCallerFactory();
			this.m_clf = getRuntime().getCallFactory();
			this.m_pns = pns;
		}
		

		public void run() {
			int sep = line.indexOf("->");
			
			if (sep<0) {
				m_logger.warning("Invalid migration entry: "+line);
				return;
			}
			
			IPhonenumber p = this.getCallingParty(line.substring(0, sep).trim());
			line = line.substring(sep+2);
			
			StringTokenizer st = new StringTokenizer(line, " ");
			if (st.countTokens()<3) {
				m_logger.warning("Corrupted migration entry: "+line);
				return;
			}

			IMsn msn = this.m_clf.createMsn(st.nextToken().trim(), "");

			StringBuffer b = new StringBuffer();
			while (st.countTokens()>2)
				b.append(st.nextToken() + " ");

			SimpleDateFormat sdf = new SimpleDateFormat("hh:mm dd.MM.yy");
			Date date = new Date(0);
			try {
				date = sdf.parse(st.nextToken().trim() + " " + st.nextToken().trim());
			} catch (ParseException e) { 
				m_logger.severe(e.getMessage());
			}			
			
			IName name = this.getName(b.toString().trim());
			ICaller caller =  this.m_cf.createCaller(name, p);
			if (this.m_c!=null) {
				if (caller!=null && !caller.getPhoneNumber().isClired() && !this.m_pns.contains(caller.getPhoneNumber())) {
					this.m_c.add(caller);
					this.m_pns.add(caller.getPhoneNumber());
				}
					
			} 			
			
			ICip cip = this.m_clf.createCip("999", "");

			if (caller!=null && caller.getUUID()!=null) {
				ICall c = this.m_clf.createCall(caller, msn, cip, date);
				if (c!=null)
					this.m_cl.add(c);
			} else {
				m_logger.warning("Invalid caller object. Call was ignored.");
			}
				
		}
		
		private IPhonenumber getCallingParty(String number) {
			if (number.startsWith("(00")) {
				int w = number.indexOf(")");
			
				return this.m_cf.createPhonenumber(
					number.substring(3, (w>0 ? w : 3)),
					number.substring(w+1, w+4),
					number.substring(w+4)
				);
			} else if(number.startsWith("(0")){
				int w = number.indexOf(")");
				if (w>2) {
					return this.m_cf.createPhonenumber(
						getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_INTAREA),
						number.substring(2, w),
						number.substring(w+1).trim()
					);
				} else {
					return this.m_cf.createPhonenumber(
						getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_INTAREA),
						number.substring(1,3),
						number.substring(3).trim()
					);
				}
			} else if (number.startsWith("0")) {			
				IPhonenumber p = getRuntime().getCallerFactory().createPhonenumber(number.substring(1));
				ICaller c = Identifier.identify(getRuntime(), p);
				if (c!=null)
					return c.getPhoneNumber();
				
			}
			return this.m_cf.createPhonenumber(true);
		}
		
		private IName getName(String n) {
			if (n.startsWith("<")) {
				int p = n.indexOf(">");
				return this.m_cf.createName(
					"", 
					n.substring(1, (p<1 ? n.length() : p)), 
					""
				);
			}
			
			StringTokenizer st = new StringTokenizer (n, " ");
			
			return this.m_cf.createName(
				st.nextToken().trim(), 
				(st.hasMoreTokens() ? st.nextToken().trim() : ""), 
				(st.hasMoreTokens() ? st.nextToken().trim() : "")
			);			
		}
	}

	public IsdnMoniCallImporter() {
		m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		m_i18n = getRuntime().getI18nManagerFactory().getI18nManager();
		m_language = getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
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
		return "*.tel";
	}

	public void setFilename(String filename) {
		this.m_filename = filename;
	}

	public ICallList doImport() {
		this.m_callerList = null;
		boolean doNotShowmessage = this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(this.getNamespace(), CFG_SHOW_ENTRY_WARNING).equalsIgnoreCase("true");
		if (!doNotShowmessage) {			
			new SWTExecuter()  {

				protected void execute() {
					String message = getI18nManager().getString(
							getNamespace(),
							"message",
							"description",
							getLanguage()
						);
					
					MessageDialogWithToggle d = MessageDialogWithToggle.openYesNoQuestion(
							new Shell(DisplayManager.getDefaultDisplay()),
							getI18nManager().getString(
									getNamespace(),
									"message",
									"label",
									getLanguage()
								),
							message,
							getI18nManager().getString(
									getNamespace(),
									"confirmmessage",
									"label",
									getLanguage()
								),
							false,
							null,
							null
						);
						
						if (d.getReturnCode()==2) {
							m_callerList = getRuntime().getCallerFactory().createCallerList();
						}
						
						getRuntime().getConfigManagerFactory().getConfigManager().setProperty(getNamespace(), CFG_SHOW_ENTRY_WARNING, (d.getToggleState() ? "true" : "false"));
						getRuntime().getConfigManagerFactory().getConfigManager().saveConfiguration();
						isDialogFinished = true;
				}
				
			}.start();
		} else {
			isDialogFinished = true;
		}
		
		while (!isDialogFinished) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
		
		Date startDate = new Date();
	    File db = new File(m_filename);
	    
	    int size = (int)db.length() / 64;
	    size = Math.abs(size);
	    
		ThreadGroup g = new ThreadGroup("JAM- HeuerMigrator-ThreadGroup");
		m_callList = getRuntime().getCallFactory().createCallList(size);
		m_pnList = new ArrayList(size);
		
		try {
		 FileReader dbReader = new FileReader(db);
		 BufferedReader bufReader = new BufferedReader(dbReader);
		 int i=0;
		 while (bufReader.ready()) {
			 String line = bufReader.readLine();
			 if (line.indexOf("->")>0) {
			 	try {
			 		Thread t = new Thread(g, new MigratorThread(this.m_callList, (this.m_callerList!=null ? this.m_callerList : null), line, this.m_pnList));
			 		t.setName("JAM-HeuerMigrator#"+ (i++)+"-Thread-(non-deamon)");
			 		t.start();
			 		if (g.activeCount()>100) {
			 			Thread.sleep(1000);
			 		}
			 	} catch (Exception ex) {
					this.m_logger.severe("Unexpected error during migration: "+ex);
			 	}
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
		}
		
		while (g.activeCount()>0) {
			try {
				Thread.sleep(1000);
				this.m_logger.info("Waiting for "+g.activeCount()+" Migrator threads.");
			} catch (InterruptedException e) {
				this.m_logger.severe(e.getMessage());
			}
		}
		
		if (this.m_callerList!=null) {
			this.m_logger.info("Commiting detected callers to default caller manager.");
			String cm = getRuntime().getConfigManagerFactory().getConfigManager().getProperty("ui.jface.application.editor.Editor", "repository");
			ICallerManager cmg = this.getRuntime().getCallerManagerFactory().getCallerManager(cm);
			if (cmg!=null && cmg.isActive() && cmg.isSupported(IWriteCallerRepository.class)) {
				((IWriteCallerRepository)cmg).setCaller(this.m_callerList);
			}
		}
		
		Date endDate = new Date();
		this.m_logger.info("Successfully imported call file " + m_filename);
		this.m_logger.info("Found " + new Integer(this.m_callList.size()).toString() + " call items in " + new Float((endDate.getTime() - startDate.getTime()) / 1000).toString() + " secs.");
		return m_callList;
	}

	public int getType() {
		return IImExporter.IMPORT_TYPE;
	}

	private IRuntime getRuntime() {
		if(this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}
	
	protected II18nManager getI18nManager() {
		if (this.m_i18n==null) {
			this.m_i18n = this.getRuntime().getI18nManagerFactory().getI18nManager();
		}
		return this.m_i18n;
	}
	
	protected String getLanguage() {
		if (this.m_language==null) {
			this.m_language = 
				this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(
					IJAMConst.GLOBAL_NAMESPACE,
					IJAMConst.GLOBAL_LANGUAGE
				);
		}
		return this.m_language;
	}
	
	public String getNamespace() {
		return NAMESPACE;
	}
}
