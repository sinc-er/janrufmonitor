package de.janrufmonitor.repository.imexporter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IMultiPhoneCaller;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.imexport.ICallerExporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.runtime.PIMRuntime;

public class EabFileCallerExporter implements ICallerExporter {

	private String ID = "EabFileCallerExporter";

	private String NAMESPACE = "repository.EabFileCallerExporter";

	Logger m_logger;

	ICallerList m_callerList;

	II18nManager m_i18n;

	String m_language;

	String m_filename;

	public EabFileCallerExporter() {
		m_logger = LogManager.getLogManager().getLogger(
				IJAMConst.DEFAULT_LOGGER);
		m_i18n = PIMRuntime.getInstance().getI18nManagerFactory()
				.getI18nManager();
		m_language = PIMRuntime.getInstance().getConfigManagerFactory()
				.getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE,
						IJAMConst.GLOBAL_LANGUAGE);
	}

	public void setCallerList(ICallerList callerList) {
		this.m_callerList = callerList;
	}

	public boolean doExport() {
		File db = new File(m_filename);
		try {

			Properties addressbook = new Properties();

			FileOutputStream fo = new FileOutputStream(db);
			ICaller c = null;
			addressbook.setProperty("total", Integer.toString(this.m_callerList.size()));
			for (int i = 0; i < this.m_callerList.size(); i++) {
				c = this.m_callerList.get(i);
				if (c instanceof IMultiPhoneCaller) {
					List phones = ((IMultiPhoneCaller)c).getPhonenumbers();
					addressbook.setProperty((i + 1) + ".phonecount", Integer.toString(phones.size()));
					for (int a =0, b=phones.size();a<b;a++) {
						addressbook.setProperty((i + 1) + ".intarea."+a, ((IPhonenumber) phones.get(a)).getIntAreaCode());
						addressbook.setProperty((i + 1) + ".area."+a, ((IPhonenumber) phones.get(a))
								.getAreaCode());
						addressbook.setProperty((i + 1) + ".phone."+a, ((IPhonenumber) phones.get(a))
								.getCallNumber());
					}
				} else {
					addressbook.setProperty((i + 1) + ".intarea", c
							.getPhoneNumber().getIntAreaCode());
					addressbook.setProperty((i + 1) + ".area", c.getPhoneNumber()
							.getAreaCode());
					addressbook.setProperty((i + 1) + ".phone", c.getPhoneNumber()
							.getCallNumber());
				}

				IAttributeMap attributes = c.getAttributes();
				Iterator iter = attributes.iterator();
				IAttribute a = null;
				while (iter.hasNext()) {
					a = (IAttribute) iter.next();
					addressbook.setProperty((i + 1) + "." + a.getName(), a
							.getValue());
				}

			}
			addressbook.store(fo, "");
			fo.close();
		} catch (FileNotFoundException ex) {
			this.m_logger.severe("File not found: " + m_filename);
			return false;
		} catch (IOException ex) {
			this.m_logger.severe("IOException on file " + m_filename);
			return false;
		}
		return true;
	}

	public String getID() {
		return this.ID;
	}

	public int getMode() {
		return IImExporter.CALLER_MODE;
	}

	public int getType() {
		return IImExporter.EXPORT_TYPE;
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

}