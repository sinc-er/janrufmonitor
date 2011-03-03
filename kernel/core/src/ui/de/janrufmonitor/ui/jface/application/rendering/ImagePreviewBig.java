package de.janrufmonitor.ui.jface.application.rendering;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.swt.SWTException;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.util.io.ImageHandler;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.io.Stream;

public class ImagePreviewBig extends AbstractTableCellRenderer {

	private static String NAMESPACE = "ui.jface.application.rendering.ImagePreviewBig";

	public String getID() {
		return "ImagePreviewBig".toLowerCase();
	}

	public Image renderAsImage() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller();
			}
			if (this.m_o instanceof ICaller) {
				if (ImageHandler.getInstance().hasImage((ICaller)this.m_o)) {
					return this.getCallerImage(ImageHandler.getInstance().getImageStream((ICaller)this.m_o));
				}
			}
		}
		return null;
	}
	
	public String renderAsImageID(){
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller();
			}
			if (this.m_o instanceof ICaller) {
				if (ImageHandler.getInstance().hasImage((ICaller)this.m_o)) {
					String path = ImageHandler.getInstance().getImagePath((ICaller)this.m_o);
					if (new File(path).exists()) return path;
					
					String newPath = PathResolver.getInstance().getTempDirectory() + File.separator + ((ICaller)this.m_o).getUUID()+".jpg";
					try {
						FileOutputStream fos = new FileOutputStream(newPath);
						InputStream imgin =  ImageHandler.getInstance().getImageStream((ICaller)this.m_o);
						Stream.copy(imgin, fos, true);
						return newPath;
					} catch (FileNotFoundException e) {
					} catch (IOException e) {
					}	
				}
			}
		}
		return "";
	}
	
	private Image getCallerImage(InputStream in) {
		if (in!=null) {
			try {
				ImageData id = new ImageData(in);

				// calculate proportions
				if (id.height>id.width) {
					float height = ((float)id.height / (float)id.width) * 90;
					id = id.scaledTo(90, Math.max((int) height, 90));
				} else {
					float width = ((float)id.width / (float)id.height) * 90;
					id = id.scaledTo(Math.max((int) width, 90), 90);
				}
				
				
				in.close();
				return new Image(DisplayManager.getDefaultDisplay(), id);
			} catch (SWTException e) {
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
		return null;
	}
	
	public String getNamespace() {
		return NAMESPACE;
	}
	
	public boolean isRenderImage() {
		return true;
	}

	public String renderAsText() {
		return this.renderAsImageID();
	}
	
}
