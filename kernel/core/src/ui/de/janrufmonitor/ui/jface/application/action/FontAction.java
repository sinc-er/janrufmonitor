package de.janrufmonitor.ui.jface.application.action;

import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.IConfigConst;
import de.janrufmonitor.ui.swt.DisplayManager;

public abstract class FontAction extends AbstractAction implements IConfigConst {

	protected void saveFontData(Font font) {
		if (font==null) return;
		
		int size = font.getFontData()[0].getHeight();
		this.m_app.getApplication().getConfiguration().setProperty(
			CFG_FONT_SIZE, Integer.toString(size)
		);
		this.m_app.getApplication().storeConfiguration();
	}
	
	protected Font getSizedFont(FontData baseFontData, int value, boolean incremental){
		// set absolut value
		int fontHeight = value;
		
		// set incremental value
		if (incremental)
			fontHeight = baseFontData.getHeight() + value;
			
		if (fontHeight<=0) fontHeight = 5;
		if (fontHeight>=72) fontHeight = 72;
		baseFontData.setHeight(fontHeight);
		return new Font(DisplayManager.getDefaultDisplay(), baseFontData);
	}
}
