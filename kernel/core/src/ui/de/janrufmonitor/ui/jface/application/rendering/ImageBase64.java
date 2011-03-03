package de.janrufmonitor.ui.jface.application.rendering;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.util.io.Base64Encoder;
import de.janrufmonitor.util.io.ImageHandler;
import de.janrufmonitor.util.io.Stream;

public class ImageBase64 extends AbstractTableCellRenderer {

	private static String NAMESPACE = "ui.jface.application.rendering.ImageBase64";

	public String getID() {
		return "ImageBase64".toLowerCase();
	}
	
	public String getNamespace() {
		return NAMESPACE;
	}
	
	public boolean isRenderImage() {
		return false;
	}

	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller();
			}
			if (this.m_o instanceof ICaller) {
				if (ImageHandler.getInstance().hasImage((ICaller)this.m_o)) {
					try {
						InputStream fim = ImageHandler.getInstance().getImageStream((ICaller)this.m_o);
						ByteArrayOutputStream encodedOut = new ByteArrayOutputStream();
						Base64Encoder b64 = new Base64Encoder(encodedOut);
						Stream.copy(fim, b64);						
						b64.flush();
						b64.close();
						return new String(encodedOut.toByteArray());
					} catch (IOException e) {
					}
				}
			}
		}
		return "";
	}
	
}
