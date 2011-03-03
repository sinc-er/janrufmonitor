package de.janrufmonitor.ui.jface.application.editor.rendering;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.types.IReadCallerRepository;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IEditorCellRenderer;

public class ReadOnly extends AbstractTableCellRenderer implements IEditorCellRenderer {

	private static String NAMESPACE = "ui.jface.application.editor.rendering.ReadOnly";

	private II18nManager m_i18n;
	private String m_language;
	private String m_i18nData;

	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller();
			}			
			if (this.m_o instanceof ICaller) {
				IAttribute att = ((ICaller)this.m_o).getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLERMANAGER);
				if (att != null) {
					if (this.isCallerManagerReadOnly(att.getValue())) {
						return this.getI18nYes();	
					}
				}
			}
		}
		return "";
	}

	public String getID() {
		return "ReadOnly".toLowerCase();
	}
	
	private String getI18nYes() {
		if (this.m_i18nData==null)
			this.m_i18nData = this.getI18nManager().getString(getNamespace(), "yes", "label", this.getLanguage());
			
		return this.m_i18nData;
	}
	
	private boolean isCallerManagerReadOnly(String man) {
		ICallerManager m = PIMRuntime.getInstance().getCallerManagerFactory().getCallerManager(man);
		if (m!=null) {
			return (m.isSupported(IReadCallerRepository.class) && !m.isSupported(IWriteCallerRepository.class));
		}
		return false;
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
