package de.janrufmonitor.ui.jface.application.journal.rendering;

import org.eclipse.swt.graphics.Image;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IJournalCellRenderer;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class BChannel extends AbstractTableCellRenderer implements IJournalCellRenderer {

	private static String NAMESPACE = "ui.jface.application.journal.rendering.BChannel";

	public Image renderAsImage() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				ICall call = (ICall)this.m_o;
				IAttribute att = call.getAttribute(IJAMConst.ATTRIBUTE_NAME_BCHANNEL);
				if (att != null) {          
					if (att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_B1))
						return SWTImageManager.getInstance(PIMRuntime.getInstance()).get(IJAMConst.IMAGE_KEY_B1_JPG);
					if (att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_B2))
						return SWTImageManager.getInstance(PIMRuntime.getInstance()).get(IJAMConst.IMAGE_KEY_B2_JPG);
				}
			}
		}
		return null;
	}

	public String renderAsImageID(){
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				ICall call = (ICall)this.m_o;
				IAttribute att = call.getAttribute(IJAMConst.ATTRIBUTE_NAME_BCHANNEL);
				if (att != null) {          
					if (att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_B1))
						return IJAMConst.IMAGE_KEY_B1_JPG;
					if (att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_B2))
						return IJAMConst.IMAGE_KEY_B2_JPG;
				}
			}
		}
		return "";
	}
	
	public String getID() {
		return "BChannel".toLowerCase();
	}

	public String getNamespace() {
		return NAMESPACE;
	}
}
