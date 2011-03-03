package de.janrufmonitor.ui.jface.application;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Display;

public class ApplicationImageDescriptor extends ImageDescriptor {

	private String path;
	
	public ApplicationImageDescriptor(String path) {
		this.path = path;
	}
	
	public ImageData getImageData() {
		Image i = new Image(Display.getCurrent(), this.path);
		return i.getImageData();
	}

}
