package de.janrufmonitor.ui.jface.application.editor.rendering;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IEditorCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IJournalCellRenderer;

public class CreationDate extends AbstractTableCellRenderer implements IJournalCellRenderer, IEditorCellRenderer {

	private static String NAMESPACE = "ui.jface.application.editor.rendering.CreationDate";
	
	private SimpleDateFormat formatter;
	
	public CreationDate() {
		formatter = new SimpleDateFormat(
				PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_VARIABLE_DATE)+ " " +
				PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_VARIABLE_TIME)
		);
	}
	
	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller();
			}
			
			if (this.m_o instanceof ICaller) {
				ICaller c = (ICaller)this.m_o;
				if (c.getAttributes().contains(IJAMConst.ATTRIBUTE_NAME_CREATION)) {
					String value = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_CREATION).getValue();
					if (value!=null && value.length()>0) {
						return formatter.format(new Date(Long.parseLong(value)));
					}
				}
			}
		}
		return "";
	}
	
	
	public String getID() {
		return "CreationDate".toLowerCase();
	}

	public String getNamespace() {
		return NAMESPACE;
	}

}
