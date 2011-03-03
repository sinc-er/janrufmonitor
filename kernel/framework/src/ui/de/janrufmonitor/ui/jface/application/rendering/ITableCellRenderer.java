package de.janrufmonitor.ui.jface.application.rendering;

import org.eclipse.swt.graphics.Image;

public interface ITableCellRenderer extends IRenderer {
	
	public boolean isRenderImage();

	public Image renderAsImage();
	
	public String renderAsImageID();
	
	public String renderAsText();
	
	public void updateData(Object o);
	
	public String getID();
	
	public String getHeader();
	
	public String getLabel();
	
	public String getNamespace();
	
	public void setID(String id);
	
}
