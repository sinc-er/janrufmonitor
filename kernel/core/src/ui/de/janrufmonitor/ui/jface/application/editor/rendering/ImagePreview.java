package de.janrufmonitor.ui.jface.application.editor.rendering;

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
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IEditorCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IJournalCellRenderer;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTImageManager;
import de.janrufmonitor.util.io.ImageHandler;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.io.Stream;


public class ImagePreview extends AbstractTableCellRenderer implements IJournalCellRenderer, IEditorCellRenderer {

	private static String NAMESPACE = "ui.jface.application.editor.rendering.ImagePreview";

	private Image m_defaultImage;

	public String getID() {
		return "ImagePreview".toLowerCase();
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
				return this.getDefault();
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
					float height = ((float)id.height / (float)id.width) * 45;
					id = id.scaledTo(45, Math.max((int) height, 45));
				} else {
					float width = ((float)id.width / (float)id.height) * 45;
					id = id.scaledTo(Math.max((int) width, 45), 45);
				}
				
				
				in.close();
				return new Image(DisplayManager.getDefaultDisplay(), id);
			} catch (SWTException e) {
			} catch (FileNotFoundException e) {
			} catch (IOException e) {
			}
		}
		
		return this.getDefault();
	}

	
	private Image getDefault() {
		if (this.m_defaultImage==null) {
			Image img = SWTImageManager.getInstance(PIMRuntime.getInstance()).get(IJAMConst.IMAGE_KEY_EMPTYCALLER_JPG);
			this.m_defaultImage = new Image(DisplayManager.getDefaultDisplay(), img.getImageData().scaledTo(45,55));
		}
		return this.m_defaultImage;		
	}
	
	public String getNamespace() {
		return NAMESPACE;
	}
	
	public boolean isRenderImage() {
		return true;
	}
	
}
