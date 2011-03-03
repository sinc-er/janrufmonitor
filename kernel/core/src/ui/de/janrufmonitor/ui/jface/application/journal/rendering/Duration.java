package de.janrufmonitor.ui.jface.application.journal.rendering;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.journal.Journal;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IJournalCellRenderer;

public class Duration extends AbstractTableCellRenderer implements IJournalCellRenderer {

	private static String NAMESPACE = "ui.jface.application.journal.rendering.Duration";
	
	private II18nManager m_i18n;
	private String m_language;

	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				return this.getDuration((ICall)this.m_o);
			}
		}
		return "";
	}

	public String getID() {
		return "Duration".toLowerCase();
	}
	
	private String getDuration(ICall call) {
		IAttribute ring = call.getAttribute(IJAMConst.ATTRIBUTE_NAME_RINGDURATION);
		if (ring==null || ring.getValue().equalsIgnoreCase("0")) return "";

		return ring.getValue() + this.getDurationUnit();
	}
	
	private String getDurationUnit() {
		return this.getI18nManager().getString(Journal.NAMESPACE, "durationunit", "label", this.getLanguage());
	}
	
	private II18nManager getI18nManager() {
		if (this.m_i18n==null) {
			this.m_i18n = PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager();
		}
		return this.m_i18n;
	}
	
	private String getLanguage() {
		if (this.m_language==null) {
			this.m_language = 
				PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(
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
