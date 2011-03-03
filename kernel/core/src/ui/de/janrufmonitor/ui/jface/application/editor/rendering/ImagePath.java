package de.janrufmonitor.ui.jface.application.editor.rendering;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IEditorCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IJournalCellRenderer;
import de.janrufmonitor.util.io.ImageHandler;
import de.janrufmonitor.util.string.StringUtils;

public class ImagePath extends AbstractTableCellRenderer implements IJournalCellRenderer, IEditorCellRenderer {

	private static String NAMESPACE = "ui.jface.application.editor.rendering.ImagePath";
	
	private II18nManager m_i18n;
	private String m_language;

	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller();
			}
			
			if (this.m_o instanceof ICaller) {
				String value = ImageHandler.getInstance().getImagePath((ICaller)this.m_o);
				// check for variable
				if (value.indexOf(IJAMConst.PATHKEY_PHOTOPATH)>-1) {
					value = StringUtils.replaceString(value, IJAMConst.PATHKEY_PHOTOPATH, 
							this.getI18nManager().getString(NAMESPACE, IJAMConst.PATHKEY_PHOTOPATH, "label", getLanguage())
							);
				}
				if (value.indexOf(IJAMConst.PATHKEY_DATAPATH)>-1) {
					value = StringUtils.replaceString(value, IJAMConst.PATHKEY_DATAPATH, 
							this.getI18nManager().getString(NAMESPACE, IJAMConst.PATHKEY_DATAPATH, "label", getLanguage())
							);
				}
				if (value.indexOf(IJAMConst.PATHKEY_IMAGEPATH)>-1) {
					value = StringUtils.replaceString(value, IJAMConst.PATHKEY_IMAGEPATH, 
							this.getI18nManager().getString(NAMESPACE, IJAMConst.PATHKEY_IMAGEPATH, "label", getLanguage())
							);
				}
				if (value.indexOf(IJAMConst.PATHKEY_USERHOME)>-1) {
					value = StringUtils.replaceString(value, IJAMConst.PATHKEY_USERHOME, 
							this.getI18nManager().getString(NAMESPACE, IJAMConst.PATHKEY_USERHOME, "label", getLanguage())
							);
				}
				return value;
			}
		}
		return "";
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

	
	
	public String getID() {
		return "ImagePath".toLowerCase();
	}

	public String getNamespace() {
		return NAMESPACE;
	}

}
