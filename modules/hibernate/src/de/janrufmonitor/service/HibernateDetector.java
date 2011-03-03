package de.janrufmonitor.service;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.framework.command.ICommand;
import de.janrufmonitor.framework.monitor.IMonitor;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.swt.SWTExecuter;
import de.janrufmonitor.util.io.PathResolver;

public class HibernateDetector extends AbstractConfigurableService {

	private String ID = "HibernateDetector";
	private String NAMESPACE = "service.HibernateDetector";
	private IRuntime m_runtime;

	private String CFG_DELAYTIME = "delay";
	private boolean isHibernateChecking;

	public HibernateDetector() {
		super();
		this.getRuntime().getConfigurableNotifier().register(this);
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public String getID() {
		return ID;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime == null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	public void startup() {
		super.startup();
		if (isEnabled()) {
			this.isHibernateChecking = true;

			Thread t = new Thread(new Runnable() {

				public void run() {

						// check file system
	File trackFolder =
		new File(
			PathResolver.getInstance(getRuntime()).getLogDirectory(),
			"~" + ID.toLowerCase());
					if (!trackFolder.exists())
						trackFolder.mkdirs();

					m_logger.info(
						"Hibernate tracking directory: "
							+ trackFolder.getAbsolutePath());

					// clear folder if exists
					File[] files = trackFolder.listFiles();
					for (int i = 0; i < files.length; i++) {
						files[i].delete();
					}

					File pid = null;
					while (isHibernateChecking) {
						pid =
							new File(
								trackFolder,
								Long.toString(System.currentTimeMillis()));
						try {
							pid.createNewFile();
						} catch (IOException e) {
							m_logger.log(Level.SEVERE, e.toString(), e);
						}

						try {
							Thread.sleep(getDelayTime());
						} catch (InterruptedException e) {
							m_logger.log(Level.SEVERE, e.toString(), e);
						}

						// check if time stamps are there
						files = trackFolder.listFiles();
						if (files.length == 0) {
							isHibernateChecking = false;
							continue;
						}

						if (files.length == 1) {
							try {
								long l = Long.parseLong(files[0].getName());
								long result = System.currentTimeMillis() - l;
								m_logger.info(
									"Delta hibernate detection time (ms): "
										+ result);
								if (result > (2 * getDelayTime()) && PIMRuntime.getInstance().getMonitorListener().getMonitors().size()>0) {								
									if (((IMonitor)PIMRuntime.getInstance().getMonitorListener().getMonitors().get(0)).getID().equalsIgnoreCase("FritzBoxMonitor")) {
										m_logger.info("Detected FritzBoxMonitor for re-connect...");
										reconnectFritzBox();
									} else {
										m_logger.info("Detected CAPI/TAPI for re-connect...");
										reconnectCapiTapi();
									}
								}
							} catch (Exception e) {
								m_logger.log(Level.SEVERE, e.toString(), e);
							}
						}
						for (int i = 0; i < files.length; i++) {
							files[i].delete();
						}
					}
				}
			});
			t.setDaemon(true);
			t.setName("JAM-"+ID+"-Thread-(deamon)");
			t.start();
		}
	}

	public void shutdown() {
		this.isHibernateChecking = false;

		super.shutdown();
	}

	private long getDelayTime() {
		String value = this.m_configuration.getProperty(CFG_DELAYTIME, "60");
		try {
			return (Long.parseLong(value) * 1000);
		} catch (Exception ex) {
			this.m_logger.warning(ex.getMessage());
		}

		return 60000;
	}

	public List getDependencyServices() {
		List dependency = super.getDependencyServices();
		dependency.add("TrayIcon");
		return dependency;
	}
	
	private void reconnectFritzBox() {
		new SWTExecuter() {
			protected void execute() {

				PropagationFactory
					.getInstance()
					.fire(
					new Message(
						Message.WARNING,
						getNamespace(),
						"hibernate",
						new Exception("Programm out of sync: OS was probably in hibernate mode.")));
				
				IMonitor mon =
					(IMonitor) PIMRuntime
					.getInstance()
					.getMonitorListener().getMonitors().get(0);													
				if (mon != null
					&& mon.isAvailable()) {
					ICommand activator =
						PIMRuntime
							.getInstance()
							.getCommandFactory()
							.getCommand(
							"Activator");
					if (activator == null) {
						m_logger.severe(
							"Command Activator not found.");
						return;
					}

					try {
						activator.execute();
						m_logger.info(
							"Toggling monitor (hibernate action)");
					} catch (Exception ex) {
						m_logger.severe(
							ex.getMessage());
					}

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						m_logger.severe(
							e.getMessage());
					}
					
					try {
						activator.execute();
						m_logger.info(
							"Toggling monitor (hibernate action)");
					} catch (Exception ex) {
						m_logger.severe(
							ex.getMessage());
					}
					
					// check for syncronizer service
					if (getRuntime().getServiceFactory().isServiceAvailable("SynchronizerService") && getRuntime().getServiceFactory().isServiceEnabled("SynchronizerService")) {
						m_logger.info(
						"Restarting Fritz!Box SynchronizerService...");
						getRuntime().getServiceFactory().restartService("SynchronizerService");
					}
				}
			}
		}
		.start();
	}
	
	private void reconnectCapiTapi() {
		new SWTExecuter() {
			protected void execute() {

				PropagationFactory
					.getInstance()
					.fire(
					new Message(
						Message.WARNING,
						getNamespace(),
						"hibernate",
						new Exception("Programm out of sync: OS was probably in hibernate mode.")));
				
				IMonitor mon =
					(IMonitor) PIMRuntime
					.getInstance()
					.getMonitorListener().getMonitors().get(0);													
				if (mon != null
					&& mon.isAvailable()
					&& mon.isStarted()) {
					ICommand activator =
						PIMRuntime
							.getInstance()
							.getCommandFactory()
							.getCommand(
							"Activator");
					if (activator == null) {
						m_logger.severe(
							"Command Activator not found.");
						return;
					}

					try {
						activator.execute();
//						PIMRuntime
//							.getInstance()
//							.enableMonitorListener(
//							false);
						m_logger.info(
							"Disable monitor (hibernate action)");
					} catch (Exception ex) {
						m_logger.severe(
							ex.getMessage());
					}

					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						m_logger.severe(
							e.getMessage());
					}

					try {
						activator.execute();
//						PIMRuntime
//							.getInstance()
//							.enableMonitorListener(
//							true);
						m_logger.info(
							"Enable monitor (hibernate action)");
					} catch (Exception ex) {
						m_logger.severe(
							ex.getMessage());
					}
				}
			}
		}
		.start();
	}

}
