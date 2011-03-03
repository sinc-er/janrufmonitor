package de.janrufmonitor.ui.jface.application;

import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.jface.action.StatusLineManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.ui.jface.application.rendering.IRenderer;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTImageManager;

public abstract class AbstractApplication extends ApplicationWindow implements IApplication {

	private static String CFG_MAXIMIZED = "maximized";
	private static String CFG_POSITION_X = "x";
	private static String CFG_POSITION_Y = "y";
	private static String CFG_WIDTH = "width";
	private static String CFG_HEIGHT = "height";
	
	private Image m_icon;
	private II18nManager m_i18n;
	private String m_language;
	protected Properties m_configuration;
	
	protected Logger m_logger;
	protected Viewer viewer;
	
	// added 2009/10/03
	private static boolean isToplevel() {
		return System.getProperty(IJAMConst.SYSTEM_UI_TOPLEVEL, "false").equalsIgnoreCase("true");
	}
	
	public AbstractApplication() {
		this((AbstractApplication.isToplevel() ? null : new Shell(DisplayManager.getDefaultDisplay())));
	}
	
	public AbstractApplication(boolean isBlocking) {
		this((AbstractApplication.isToplevel() ? null : new Shell(DisplayManager.getDefaultDisplay())), isBlocking);
	}
	
	public AbstractApplication(Shell shell) {
		this(shell, true);
	}
	
	public AbstractApplication(Shell shell, boolean isBlocking) {
		super(shell);
		this.setBlockOnOpen(isBlocking);
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);	
	}
	
	public AbstractApplication getApplication() {
		return this;
	}
	
	public Viewer getViewer() {
		return this.viewer;
	}
	
	public Properties getConfiguration() {
		if (this.m_configuration==null) {
			this.m_configuration = this.getRuntime().getConfigManagerFactory().getConfigManager().getProperties(this.getNamespace());
		}
		return this.m_configuration;
	}
	
	public void storeConfiguration() {
		if (this.m_configuration!=null) {
			this.getRuntime().getConfigManagerFactory().getConfigManager().setProperties(
				this.getNamespace(),
				this.m_configuration
			);
			this.getRuntime().getConfigManagerFactory().getConfigManager().saveConfiguration();
		}
	}
	
	public void initializeController() {
		// must be overriden by subclass
	}
	
	public IApplicationController getController() {
		// must be overriden by subclass
		return null;
	}

	public void updateViews(boolean reload) {
		this.updateViews(null, reload);
	}
	
	public void updateViews(Object[] controllerdata, boolean reload) {
		// must be overriden by subclass
	}
	
	public StatusLineManager getStatusLineManager() {
		return super.getStatusLineManager();
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.addControlListener(
			new ControlListener() {
				public void controlMoved(ControlEvent e) {
					internalSavePosition(e.display.getActiveShell());
					storeConfiguration();
				}
				public void controlResized(ControlEvent e) {
					internalSavePosition(e.display.getActiveShell());
					storeConfiguration();				
				}
			}
		);
		boolean isMaximized = (this.getConfiguration().getProperty(CFG_MAXIMIZED,"false").equalsIgnoreCase("true") ? true : false);
		if (isMaximized) {
			shell.setMaximized(true);
		} else {
			shell.setMaximized(false);
			shell.setBounds(
				Integer.parseInt(this.getConfiguration().getProperty(CFG_POSITION_X, "0")),
				Integer.parseInt(this.getConfiguration().getProperty(CFG_POSITION_Y, "0")),
				Integer.parseInt(this.getConfiguration().getProperty(CFG_WIDTH, "800")),
				Integer.parseInt(this.getConfiguration().getProperty(CFG_HEIGHT, "600"))
			);
		}
		shell.setText(this.getI18nManager().getString(this.getNamespace(), "title", "label", this.getLanguage()));
		shell.setImage(this.getIcon());
	}	

	protected void setIcon(Image icon) {
		this.m_icon = icon;
	}
	
	protected void refreshConfiguration() {
		this.m_configuration = null;
	}
	
	protected II18nManager getI18nManager() {
		if (this.m_i18n==null) {
			this.m_i18n = this.getRuntime().getI18nManagerFactory().getI18nManager();
		}
		return this.m_i18n;
	}
	
	protected String getLanguage() {
		if (this.m_language==null) {
			this.m_language = 
				this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(
					IJAMConst.GLOBAL_NAMESPACE,
					IJAMConst.GLOBAL_LANGUAGE
				);
		}
		return this.m_language;
	}

	private Image getIcon() {
		if (this.m_icon==null) {
			this.m_icon = SWTImageManager.getInstance(this.getRuntime()).get(IJAMConst.IMAGE_KEY_PIM_ICON);
		}
		return this.m_icon;
	}
	
	private void internalSavePosition(Shell shell) {
		if (shell!=null) {
			if (shell.getMaximized()) {
				this.m_configuration.setProperty(CFG_MAXIMIZED, "true");
			} else {
				this.m_configuration.setProperty(CFG_MAXIMIZED, "false");
				this.m_configuration.setProperty(CFG_POSITION_X, Integer.toString(shell.getBounds().x));
				this.m_configuration.setProperty(CFG_POSITION_Y, Integer.toString(shell.getBounds().y));
				this.m_configuration.setProperty(CFG_HEIGHT, Integer.toString(shell.getBounds().height));
				this.m_configuration.setProperty(CFG_WIDTH, Integer.toString(shell.getBounds().width));
			}
		}
	}
	
	public void updateProviders() {
		
	}
	
	public boolean isSupportingRenderer(IRenderer r) {
		return false;
	}
	
	public abstract IRuntime getRuntime();
	
	public abstract String getNamespace();

}
