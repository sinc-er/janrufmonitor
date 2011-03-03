package de.janrufmonitor.ui.jface.application.journal.rendering;

import org.eclipse.swt.graphics.Image;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IJournalCellRenderer;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class Status extends AbstractTableCellRenderer implements IJournalCellRenderer {

	private static String NAMESPACE = "ui.jface.application.journal.rendering.Status";
	
	public Image renderAsImage() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				ICall call = (ICall)this.m_o;
				IAttribute att = call.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS);
				if (att != null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_REJECTED)) {          		
					return SWTImageManager.getInstance(PIMRuntime.getInstance()).get(IJAMConst.IMAGE_KEY_REJECTED_GIF);
				}
				//att = call.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS);
				if (att != null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_ACCEPTED)) {
					return SWTImageManager.getInstance(PIMRuntime.getInstance()).get(IJAMConst.IMAGE_KEY_ACCEPTED_GIF);
				}
				//att = call.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS);
				if (att != null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_OUTGOING)) {
					return SWTImageManager.getInstance(PIMRuntime.getInstance()).get(IJAMConst.IMAGE_KEY_OUTGOING_GIF);
				}	
				if (att != null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_MISSED)) {
					return SWTImageManager.getInstance(PIMRuntime.getInstance()).get(IJAMConst.IMAGE_KEY_AWAY_GIF);
				}	
/**				// 2008/11/08: work-a-round for showing old status fields
				att = call.getAttribute(IJAMConst.ATTRIBUTE_NAME_REJECTED);
				if (att != null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_YES)) {          		
					return SWTImageManager.getInstance(PIMRuntime.getInstance()).get(IJAMConst.IMAGE_KEY_REJECTED_GIF);
				}
				att = call.getAttribute(IJAMConst.ATTRIBUTE_NAME_ACCEPTED);
				if (att != null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_YES)) {
					return SWTImageManager.getInstance(PIMRuntime.getInstance()).get(IJAMConst.IMAGE_KEY_ACCEPTED_GIF);
				}
				att = call.getAttribute(IJAMConst.ATTRIBUTE_NAME_OUTGOING);
				if (att != null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_YES)) {
					return SWTImageManager.getInstance(PIMRuntime.getInstance()).get(IJAMConst.IMAGE_KEY_OUTGOING_GIF);
				}
				**/
				return SWTImageManager.getInstance(PIMRuntime.getInstance()).get(IJAMConst.IMAGE_KEY_AWAY_GIF);
			}
		}
		return null;
	}
	
	public String renderAsImageID(){
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				ICall call = (ICall)this.m_o;
				
				IAttribute att = call.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS);
				if (att != null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_REJECTED)) {          		
					return IJAMConst.IMAGE_KEY_REJECTED_GIF;
				}
				//att = call.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS);
				if (att != null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_ACCEPTED)) {
					return IJAMConst.IMAGE_KEY_ACCEPTED_GIF;
				}
				//att = call.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS);
				if (att != null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_OUTGOING)) {
					return IJAMConst.IMAGE_KEY_OUTGOING_GIF;
				}				
				if (att != null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_MISSED)) {
					return IJAMConst.IMAGE_KEY_AWAY_GIF;
				}
/**
				// 2008/11/08: work-a-round for showing old status fields
				att = call.getAttribute(IJAMConst.ATTRIBUTE_NAME_REJECTED);
				if (att != null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_YES)) {          		
					return IJAMConst.IMAGE_KEY_REJECTED_GIF;
				}
				att = call.getAttribute(IJAMConst.ATTRIBUTE_NAME_ACCEPTED);
				if (att != null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_YES)) {
					return IJAMConst.IMAGE_KEY_ACCEPTED_GIF;
				}
				att = call.getAttribute(IJAMConst.ATTRIBUTE_NAME_OUTGOING);
				if (att != null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_YES)) {
					return IJAMConst.IMAGE_KEY_OUTGOING_GIF;
				}		
				**/		
				return IJAMConst.IMAGE_KEY_AWAY_GIF;

			}
		}
		return "";
	}

	public String getID() {
		return "Status".toLowerCase();
	}

	public String getNamespace() {
		return NAMESPACE;
	}
	
	public boolean isRenderImage() {
		return true;
	}
}
