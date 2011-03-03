package de.janrufmonitor.ui.jface.application.editor.rendering;

import java.util.HashMap;
import java.util.Map;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IEditorCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IJournalCellRenderer;

public class CallerManager extends AbstractTableCellRenderer implements IJournalCellRenderer, IEditorCellRenderer {

	private static String NAMESPACE = "ui.jface.application.editor.rendering.CallerManager";

	private II18nManager m_i18n;
	private String m_language;
	private Map m_i18nCmData = new HashMap();

	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller();
			}
			if (this.m_o instanceof ICaller) {
				IAttribute att = ((ICaller)this.m_o).getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLERMANAGER);
				if (att != null) {
					return this.getI18nCallermanager(att.getValue());
				}
			}
		}
		return "";
	}

	public String getID() {
		return "CallerManager".toLowerCase();
	}
	
	private String getI18nCallermanager(String id) {
		if (!this.m_i18nCmData.containsKey(id)) {
			String ns = PIMRuntime.getInstance().getConfigurableNotifier().getConfigurableNamespace(id);
			String label = this.getI18nManager().getString(ns, "title", "label", this.getLanguage());
			if (!label.equalsIgnoreCase("title"))
				this.m_i18nCmData.put(id, label);
			else
				this.m_i18nCmData.put(id, id);
		}
		return (String) this.m_i18nCmData.get(id);
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
