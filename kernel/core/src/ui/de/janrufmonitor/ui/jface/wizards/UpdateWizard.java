package de.janrufmonitor.ui.jface.wizards;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.installer.InstallerConst;
import de.janrufmonitor.framework.installer.InstallerEngine;
import de.janrufmonitor.framework.installer.InstallerException;
import de.janrufmonitor.framework.monitor.IMonitor;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.wizards.pages.AbstractPage;
import de.janrufmonitor.ui.jface.wizards.pages.UpdatesPage;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.util.io.OSUtils;

public class UpdateWizard extends AbstractWizard {

	private class StreamRequester {
		private String url;
		
		private InputStream in;
		private Logger m_logger;
		
		public StreamRequester(String url) {
			this.url = url;
			this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		}
		
		public void go() {
			StringBuffer agent = new StringBuffer();
			agent.append("jAnrufmonitor Update Manager ");
			agent.append(IJAMConst.VERSION_DISPLAY);
			
			List monitors = getRuntime().getMonitorListener().getMonitors();
			if (monitors.size()>0) {
				agent.append(" (");
				IMonitor m  = null;
				for (int i=0,j=monitors.size();i<j;i++) {
					m = (IMonitor) monitors.get(i);
					agent.append(m.getID());
					if ((i+1)<j)
						agent.append(", ");	
				}
				agent.append(";");
				if (OSUtils.isWindows()) {
					agent.append("Windows");
				} else if (OSUtils.isLinux()) {
					agent.append("Linux");
				} else if (OSUtils.isMacOSX()) {
					agent.append("MacOSX");
				} else {
					agent.append("unknown OS");
				}
				agent.append(")");
			}
			
			if (m_logger.isLoggable(Level.INFO))
				this.m_logger.info("User-Agent: "+agent.toString());
			
			
			try {
				URL url = new URL(this.url);
				URLConnection c = url.openConnection();
				c.setDoInput(true);
				c.setRequestProperty("User-Agent", agent.toString());
				c.connect();
				
				this.m_logger.info("Querying URL "+this.url);
				
				Object o = c.getContent();
				if (o instanceof InputStream) {
					this.m_logger.info("Content successfully retrieved from "+this.url);
					this.in =(InputStream) o;
				}				
			} catch (MalformedURLException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
				this.in = null;
			} catch (IOException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
				this.in = null;
				PropagationFactory.getInstance().fire(new Message(Message.ERROR, getNamespace(), "ioexception", e));				
			}
		}
		
		public InputStream getInputStream() {
			return this.in;
		}
	}
		
	private String NAMESPACE = "ui.jface.wizards.UpdateWizard"; 

	private IRuntime m_runtime;
	private AbstractPage[] m_pages;
	private boolean m_finished;

	public UpdateWizard() {
		this(false);
	}
	
	public UpdateWizard(boolean preload) {
		super();
		setWindowTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));
		this.m_pages = new AbstractPage[1];
		this.m_pages[0] = new UpdatesPage(null, preload);
		
		this.addPage(this.m_pages[0]);
	}

	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return UpdateWizard.class.getName();
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public boolean performFinish() {
		final List l = ((UpdatesPage)this.m_pages[0]).getResult();
		final boolean isMoreUpdates = ((UpdatesPage)this.m_pages[0]).isMoreUpdates();
		if (l.size()>0) {
			try {
				IRunnableWithProgress op = new IRunnableWithProgress() {
					public void run(IProgressMonitor pm) throws InvocationTargetException, InterruptedException {
						pm.setTaskName(m_i18n.getString(getNamespace(), "updates", "label", m_language));
						Properties p = null;
						String url, name = null;
						StreamRequester sr = null;
						for (int i=0,n=l.size();i<n;i++){
							p = (Properties) l.get(i);
							url = p.getProperty(InstallerConst.DESCRIPTOR_UPDATE);
							name = p.getProperty(InstallerConst.DESCRIPTOR_NAME) + "." +p.getProperty(InstallerConst.DESCRIPTOR_VERSION) + InstallerConst.EXTENSION_ARCHIVE;
							if (url!=null && url.length()>6) {
								pm.subTask(m_i18n.getString(getNamespace(), "install", "label", m_language) + name);
								sr = new StreamRequester(url);
								sr.go();
								try {
									InstallerEngine.getInstance().install(name, sr.getInputStream(), (i==(n-1)));
								} catch (InstallerException e) {
									m_logger.severe(e.getMessage());
									PropagationFactory.getInstance().fire(new Message(Message.ERROR, getNamespace(), "failed", new Exception("Installation failed for module "+url)));
								}
							}
						}
						
						DisplayManager.getDefaultDisplay().asyncExec(
							new Runnable () {
								public void run () {
									MessageDialog.openInformation(
											getShell(), 
											m_i18n.getString(getNamespace(), "success", "label", m_language),
											m_i18n.getString(getNamespace(), "success", "description", m_language));
									m_finished = true;
								}
							}
						);
						
						
						if (isMoreUpdates) {
							DisplayManager.getDefaultDisplay().asyncExec(
								new Runnable () {
									public void run () {
										MessageDialog.openInformation(
												getShell(), 
												m_i18n.getString(getNamespace(), "moreupdates", "label", m_language),
												m_i18n.getString(getNamespace(), "moreupdates", "description", m_language));
										m_finished = true;
									}
								}
							);
						}						
					}
			    };
			    ProgressMonitorDialog pmd = new ProgressMonitorDialog(getShell());
		   		pmd.run(true, true, op);
		    } catch (InvocationTargetException e) {
		    	this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		   		m_finished = true;
		    } catch (InterruptedException e) {
		    	this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		   		m_finished = true;
		    }
		}
		while (!m_finished) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
				m_finished = true;
			}
		}
		return true;
	}

}
