package de.janrufmonitor.ui.jface.application.rendering;

import org.eclipse.swt.graphics.Image;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.PIMRuntime;

public abstract class AbstractTableCellEditorRenderer implements ITableAttributeCellEditorRenderer {
	
	private II18nManager m_i18n;
	private String m_language;
	
	protected Object m_o;
	
	public Image renderAsImage() {
		return null;
	}
	
	public String renderAsImageID(){
		return "";
	}
	
	public String renderAsText() {
		return "";
	}
	
	public void updateData(Object o) {
		this.m_o = o;
	}

	public String getHeader() {
		return this.getI18nManager().getString(this.getNamespace(), "name", "label", this.getLanguage());
	}
	
	public String getLabel() {
		String l = this.getI18nManager().getString(this.getNamespace(), "label", "label", this.getLanguage());
		if (l.equalsIgnoreCase("label")) return this.getHeader();
		return l;
	}
	
	public void setID(String id) {}
	
	public boolean isEditable() {
		return true;
	}
	
	public String[] getValues() {
		return new String[0];
	}

	public int getType() {
		return ITableCellEditorRenderer.TYPE_TEXT;
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
	
	public boolean isRenderImage() {
		return false;
	}
}
