package de.janrufmonitor.repository.imexporter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IName;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.imexport.ICallerImporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.repository.imexport.ITracker;
import de.janrufmonitor.runtime.PIMRuntime;

public class EabFileCallerImporter implements ICallerImporter, ITracker {

	private String ID = "EabFileCallerImporter";

	private String NAMESPACE = "repository.EabFileCallerImporter";

	private Logger m_logger;

	private ICallerList m_callerList;

	private II18nManager m_i18n;

	private String m_language;

	private String m_filename;
	
	int m_current;
	int m_total;

	public EabFileCallerImporter() {
		m_logger = LogManager.getLogManager().getLogger(
				IJAMConst.DEFAULT_LOGGER);
		m_i18n = PIMRuntime.getInstance().getI18nManagerFactory()
				.getI18nManager();
		m_language = PIMRuntime.getInstance().getConfigManagerFactory()
				.getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE,
						IJAMConst.GLOBAL_LANGUAGE);
	}

	public ICallerList doImport() {
		this.m_current = 0;
		m_callerList = PIMRuntime.getInstance().getCallerFactory()
				.createCallerList();
		Date startDate = new Date();
		File db = new File(m_filename);
		try {
			FileInputStream in = new FileInputStream(db);
			Properties addressbook = new Properties();
			addressbook.load(in);
			in.close();

			m_total = Integer.parseInt(addressbook.getProperty("total", "1"));
			
			boolean hasMore = true;
			int i = 1;
			String key = null;
			while (hasMore && addressbook.size()>0) {
				key = addressbook.getProperty(i + ".phonecount");
				addressbook.remove(i + ".phonecount");
				// found old format
				if (key==null) {
					key = addressbook.getProperty(i + ".phone");
					addressbook.remove(i + ".phone");
					if (key != null && key.length() > 0) {
						IPhonenumber pn = PIMRuntime.getInstance()
								.getCallerFactory().createPhonenumber(false);
						pn.setCallNumber(key);
						pn.setAreaCode(addressbook.getProperty(i + ".area"));
						addressbook.remove(i + ".area");

						key = addressbook.getProperty(i + ".intarea");
						addressbook.remove(i + ".intarea");
						if (key == null || key.length() == 0)
							key = PIMRuntime.getInstance()
									.getConfigManagerFactory().getConfigManager()
									.getProperty(IJAMConst.GLOBAL_NAMESPACE,
											IJAMConst.GLOBAL_INTAREA);
						pn.setIntAreaCode(key);

						IName name = PIMRuntime.getInstance().getCallerFactory()
								.createName("", "");
						IAttributeMap m = PIMRuntime.getInstance()
								.getCallerFactory().createAttributeMap();
						Enumeration en = addressbook.keys();
						List keys = new ArrayList();
						while (en.hasMoreElements()) {
							key = (String) en.nextElement();
							if (key.startsWith(i + ".")) {
								keys.add(key);
								m.add(PIMRuntime.getInstance().getCallerFactory()
										.createAttribute(
												key.substring((i + ".").length()),
												addressbook.getProperty(key)));
							}
						}
						if (keys.size()>0) {
							for (int j=0;j<keys.size();j++) {
								addressbook.remove(keys.get(j));
							}
							keys.clear();
							keys = null;
						}

						ICaller caller = PIMRuntime.getInstance()
								.getCallerFactory().createCaller(name, pn);
						caller.setAttributes(m);

						this.m_callerList.add(caller);
						i++;
					} else{
						this.m_logger.info("Found no more valid keys. Left data: "+addressbook);
						hasMore = false;
					}
				} else { // new format since 5.0.0
					int phonecount = Integer.parseInt(key);
					List phones = new ArrayList(1);
					ICaller caller = null;
					for (int a=0; a<phonecount; a++) {
						key = addressbook.getProperty(i + ".phone."+a);
						addressbook.remove(i + ".phone."+a);
						if (key != null && key.length() > 0) {
							IPhonenumber pn = PIMRuntime.getInstance()
									.getCallerFactory().createPhonenumber(false);
							pn.setCallNumber(key);
							pn.setAreaCode(addressbook.getProperty(i + ".area."+a));
							addressbook.remove(i + ".area."+a);

							key = addressbook.getProperty(i + ".intarea."+a);
							addressbook.remove(i + ".intarea."+a);
							if (key == null || key.length() == 0)
								key = PIMRuntime.getInstance()
										.getConfigManagerFactory().getConfigManager()
										.getProperty(IJAMConst.GLOBAL_NAMESPACE,
												IJAMConst.GLOBAL_INTAREA);
							pn.setIntAreaCode(key);
							
							phones.add(pn);
						}
					}

					IName name = PIMRuntime.getInstance().getCallerFactory()
							.createName("", "");
					IAttributeMap m = PIMRuntime.getInstance()
							.getCallerFactory().createAttributeMap();
					Enumeration en = addressbook.keys();
					List keys = new ArrayList();
					while (en.hasMoreElements()) {
						key = (String) en.nextElement();
						if (key.startsWith(i + ".")) {
							keys.add(key);
							m.add(PIMRuntime.getInstance().getCallerFactory()
									.createAttribute(
											key.substring((i + ".").length()),
											addressbook.getProperty(key)));
						}
					}
					if (keys.size()>0) {
						for (int j=0;j<keys.size();j++) {
							addressbook.remove(keys.get(j));
						}
						keys.clear();
						keys = null;
					}

					caller = PIMRuntime.getInstance()
							.getCallerFactory().createCaller(name, phones);
					caller.setAttributes(m);
					
					this.m_callerList.add(caller);
					i++;		
					this.m_current++;
				}
			}

		} catch (FileNotFoundException ex) {
			this.m_logger.warning("Cannot find caller backup file "
					+ m_filename);
			return this.m_callerList;
		} catch (IOException ex) {
			this.m_logger.severe("IOException on file " + m_filename);
			return this.m_callerList;
		}
		Date endDate = new Date();
		this.m_logger.info("Successfully imported caller file " + m_filename);
		this.m_logger.info("Found "
				+ new Integer(this.m_callerList.size()).toString()
				+ " caller items in "
				+ new Float((endDate.getTime() - startDate.getTime()) / 1000)
						.toString() + " secs.");

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
		return this.m_i18n.getString(this.NAMESPACE, "filtername", "label",
				this.m_language);
	}

	public String getExtension() {
		return "*.eab";
	}

	public void setFilename(String filename) {
		this.m_filename = filename;
	}

	public int getCurrent() {
		return this.m_current;
	}

	public int getTotal() {
		return this.m_total;
	}
}