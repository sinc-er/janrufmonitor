package de.janrufmonitor.service.blinker;

import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.event.IEvent;
import de.janrufmonitor.framework.event.IEventBroker;
import de.janrufmonitor.framework.event.IEventConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.AbstractReceiverConfigurableService;
import de.janrufmonitor.service.IService;
import de.janrufmonitor.service.trayicon.TrayIcon;
import de.janrufmonitor.util.io.PathResolver;

public class IconBlinker extends AbstractReceiverConfigurableService {

	private class Toggler implements Runnable {

		private boolean isRunning;
		private Logger m_logger; 

		public Toggler() {
			this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		}

		public void run() {
			this.isRunning = true;
			
			IService tray = PIMRuntime.getInstance().getServiceFactory().getService("TrayIcon");
			
			if (tray==null){
				this.m_logger.warning("Could not find TrayIcon service for IconBlinker service.");
				this.isRunning = false;	
				return;
			}
			
			String imagePath = PathResolver.getInstance(PIMRuntime.getInstance()).getImageDirectory();
			String callin = imagePath + "callin.ico";
			String callout = imagePath + "jam.ico";
				
			if (tray instanceof TrayIcon) {
				while(this.isRunning) {
					this.m_logger.fine("Toggle image to: "+callin);
					((TrayIcon) tray).toggleImage(callin);
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						this.m_logger.warning(e.getMessage());
					}
					this.m_logger.fine("Toggle image to: "+callout);
					((TrayIcon) tray).toggleImage(callout);
					try {
						Thread.sleep(400);
					} catch (InterruptedException e) {
						this.m_logger.warning(e.getMessage());
					}
				}
				this.m_logger.fine("Reset image to: "+ imagePath + "jam.ico");
				((TrayIcon) tray).toggleImage(imagePath + "jam.ico");
			}
			this.isRunning = false;
		}

		public void setRunning(boolean running) {
			this.isRunning = running;
		}
		
		public boolean isRunning() {
			return this.isRunning;
		}

	}

	
	private String ID = "IconBlinker";
	private String NAMESPACE = "service.IconBlinker";
	
	private String CFG_CALLBLINK = "callblink";
	
	private Toggler m_toggler = null;
	private IRuntime m_runtime;

	public IconBlinker() {
		super();
		this.getRuntime().getConfigurableNotifier().register(this);	
	}
	
	public void startup() {
		super.startup();
		
		IEventBroker eventBroker = this.getRuntime().getEventBroker();
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLACCEPTED));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLCLEARED));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLREJECTED));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_APPLICATION_READY));
		this.m_logger.info("IconBlinker is started ...");		
	}

	public void shutdown() {
		super.shutdown();
		IEventBroker eventBroker = this.getRuntime().getEventBroker();
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLACCEPTED));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLCLEARED));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLREJECTED));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_APPLICATION_READY));
		this.m_logger.info("IconBlinker is shut down ...");
	}
	
	public String getNamespace() {
		return this.NAMESPACE;
	}

	public String getID() {
		return this.ID;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	public void receivedOtherEventCall(IEvent event) {
		super.receivedOtherEventCall(event);
		
		if (this.m_configuration.getProperty(CFG_CALLBLINK, "false").equalsIgnoreCase("true")) {
			if (event.getType() == IEventConst.EVENT_TYPE_CALLCLEARED) {
				this.stopBlinker();
				return;
			}
		}
		
		if (event.getType() == IEventConst.EVENT_TYPE_APPLICATION_READY ||
			event.getType() == IEventConst.EVENT_TYPE_CALLACCEPTED||
			event.getType() == IEventConst.EVENT_TYPE_CALLREJECTED) {	
			this.stopBlinker();
		}
	}

	private void stopBlinker() {
		this.m_logger.entering(IconBlinker.class.getName(), "stopBlinker");
		if (m_toggler!=null) {
			m_toggler.setRunning(false);
		}
		this.m_logger.exiting(IconBlinker.class.getName(), "stopBlinker");
	}

	private void startBlinker() {
		this.m_logger.entering(IconBlinker.class.getName(), "startBlinker");
		if (m_toggler==null || !m_toggler.isRunning()) {
			m_toggler = new Toggler();
			Thread t = new Thread(m_toggler);
			t.setName("JAM-IconToggler-Thread-(non-deamon)");
			t.start();
			this.m_logger.info("ToggleThread started.");
		}
		this.m_logger.exiting(IconBlinker.class.getName(), "startBlinker");	
	}

	public void receivedIdentifiedCall(IEvent event) {
		super.receivedIdentifiedCall(event);
		ICall aCall = (ICall)event.getData();
		if (aCall!=null) {
			if (getRuntime().getRuleEngine().validate(this.getID(), aCall.getMSN(), aCall.getCIP(), aCall.getCaller().getPhoneNumber())) {
				this.startBlinker();
			} else {
				this.m_logger.info("No rule assigned to execute this service for call: "+aCall);
			}
		} 
	}

}
