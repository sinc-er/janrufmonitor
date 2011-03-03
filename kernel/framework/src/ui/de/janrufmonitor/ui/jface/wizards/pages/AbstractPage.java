package de.janrufmonitor.ui.jface.wizards.pages;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.ui.swt.SWTImageManager;

public abstract class AbstractPage extends WizardPage {

	private class DefaultImageDescriptor extends ImageDescriptor {

		public ImageData getImageData() {
			Image img = SWTImageManager.getInstance(getRuntime()).get(IJAMConst.IMAGE_KEY_TELEFON_JPG);
			if (img!=null)
				return img.getImageData();
			return null;
		}
		
	}
	
	protected Logger m_logger;
	protected II18nManager m_i18n;
	protected String m_language;
	
	public AbstractPage(String name) {
		super(name);

		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		this.m_i18n = getRuntime().getI18nManagerFactory().getI18nManager();
		this.m_language = getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
		setPageComplete(false);
		this.setImageDescriptor(new DefaultImageDescriptor());
	}
		
	protected boolean isComplete() {
		setErrorMessage(null);
		return true;
	}
	
	protected abstract IRuntime getRuntime();
	
	public abstract String getNamespace();
}
