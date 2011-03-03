package de.janrufmonitor.ui.jface.application.editor.rendering;

import java.io.File;

import org.eclipse.swt.graphics.Image;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.ITreeItemCallerData;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IEditorCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IJournalCellRenderer;
import de.janrufmonitor.ui.swt.SWTImageManager;
import de.janrufmonitor.util.io.PathResolver;

public class ProviderLogo extends AbstractTableCellRenderer implements IJournalCellRenderer, IEditorCellRenderer {

	private static String NAMESPACE = "ui.jface.application.editor.rendering.ProviderLogo";
	
	public Image renderAsImage() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller().getPhoneNumber();
			}
			if (this.m_o instanceof ICaller) {
				this.m_o = ((ICaller)this.m_o).getPhoneNumber();
			}	
			if (this.m_o instanceof ITreeItemCallerData) {
				this.m_o = ((ITreeItemCallerData)this.m_o).getPhone();
			}		
			if (this.m_o instanceof IPhonenumber) {
				if (((IPhonenumber)this.m_o).isClired()) return null;
				String key = "provider/"+((IPhonenumber)this.m_o).getIntAreaCode()+"_"+((IPhonenumber)this.m_o).getAreaCode()+".jpg";
				
				File logoFile = new File(PathResolver.getInstance(PIMRuntime.getInstance()).getImageDirectory()+key);
				if (!logoFile.exists() || !logoFile.isFile()) {
					key = "provider/"+((IPhonenumber)this.m_o).getIntAreaCode()+".jpg";
					logoFile = new File(PathResolver.getInstance(PIMRuntime.getInstance()).getImageDirectory()+key);
					if (!logoFile.exists() || !logoFile.isFile()) return null;
				}
				
				Image logo = SWTImageManager.getInstance(PIMRuntime.getInstance()).get(key);
				if (logo==null) {
					SWTImageManager.getInstance(PIMRuntime.getInstance()).loadImage(key, 40, 40);
					logo = SWTImageManager.getInstance(PIMRuntime.getInstance()).get(key);
					return logo;
				} 
				return logo;
			}
		}
		return null;
	}
	
	public String getID() {
		return "ProviderLogo".toLowerCase();
	}

	public String getNamespace() {
		return NAMESPACE;
	}
	
	public boolean isRenderImage() {
		return true;
	}
}
