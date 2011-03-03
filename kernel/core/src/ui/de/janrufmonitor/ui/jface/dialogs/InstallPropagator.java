package de.janrufmonitor.ui.jface.dialogs;

import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.exception.IPropagator;
import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class InstallPropagator implements IPropagator {

	private String NAMESPACE = "ui.jface.dialogs.InstallPropagator";
	
	protected Logger m_logger;
	private IRuntime m_runtime;
	protected String m_language;
	protected II18nManager m_i18n;
	
	public InstallPropagator() {
		super();
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		this.m_language = 
			this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(
				IJAMConst.GLOBAL_NAMESPACE,
				IJAMConst.GLOBAL_LANGUAGE
			);
		this.m_i18n = this.getRuntime().getI18nManagerFactory().getI18nManager();
		
		PropagationFactory.getInstance().add(this);
	}
	
	public void propagate(final Message m) {
		if (m==null || !m.getNamespace().equalsIgnoreCase("installer.InstallerEngine")) return;
		
		final Throwable t = m.getThrowable();
		
		Thread td = new Thread(
			new Runnable() {
				public void run() {
					DisplayManager.getDefaultDisplay().asyncExec (new Runnable () {
						public void run () {
							openDialog(m.getLevel(), m.getNamespace(), m.getMessage(), t.toString());
						}
					});
				}
			}
		);
		td.setName("JAM-Propagation-Thread-(non-deamon)");
		td.start();
		try {
			td.join();
		} catch (InterruptedException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	private synchronized void openDialog(String level, String namespace, String id, String cause) {
		Display d = DisplayManager.getDefaultDisplay();
		Shell shell = new Shell(d);
		
		Status status = null;
		
		if (level.equalsIgnoreCase(Message.ERROR)) {
			status = new Status(IStatus.ERROR, namespace, 0, cause, null);	
		}
		
		if (level.equalsIgnoreCase(Message.WARNING)) {
			status = new Status(IStatus.WARNING, namespace, 0, cause, null);	
		}
		
		if (level.equalsIgnoreCase(Message.INFO)) {
			status = new Status(IStatus.INFO, namespace, 0, cause, null);	
		}

		String message = this.m_i18n.getString(namespace, id, "label", this.m_language);
		if (message.equalsIgnoreCase(id)) {
			message = this.m_i18n.getString(getNamespace(), "unknown", "label", this.m_language);
		}
		
		ErrorDialog.setDefaultImage(SWTImageManager.getInstance(this.getRuntime()).get(IJAMConst.IMAGE_KEY_PIM_ICON));
		
		ErrorDialog.openError(
			shell, 
			this.m_i18n.getString(getNamespace(), level, "label", this.m_language), 
			message,
			status
		);
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null)
				this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}
	
	public String getNamespace() {
		return this.NAMESPACE;
	}

	public String getID() {
		return "InstallPropagator";
	}

}
