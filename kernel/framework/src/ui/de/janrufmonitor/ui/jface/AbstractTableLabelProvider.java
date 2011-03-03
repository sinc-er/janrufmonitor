package de.janrufmonitor.ui.jface;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public abstract class AbstractTableLabelProvider implements ITableLabelProvider {

	public Image getColumnImage(Object arg0, int arg1) {
		return null;
	}

	public String getColumnText(Object o, int column) {
		return null;
	}

	public void addListener(ILabelProviderListener arg0) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	public void removeListener(ILabelProviderListener arg0) {
	}
	
}
