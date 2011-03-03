package de.janrufmonitor.ui.jface.application.editor.rendering;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IEditorCellRenderer;

public class Reject extends AbstractTableCellRenderer implements IEditorCellRenderer {

	private static String NAMESPACE = "ui.jface.application.editor.rendering.Reject";

	private II18nManager m_i18n;
	private String m_language;
	
	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller();
			}			
			if (this.m_o instanceof ICaller) {
				IAttribute att = ((ICaller)this.m_o).getAttribute(IJAMConst.ATTRIBUTE_NAME_REJECT);
				if (att != null) {
					if (att != null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_YES)) {          		
						return this.getI18nManager().getString(NAMESPACE, "rejected", "label", this.getLanguage());
					}
				}
			}
		}
		return "";
	}

	public String getID() {
		return "Reject".toLowerCase();
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
