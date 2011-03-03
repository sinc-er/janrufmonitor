package de.janrufmonitor.service.sound;

import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.service.AbstractReceiverConfigurableService;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.event.IEventConst;
import de.janrufmonitor.framework.event.IEventBroker;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;

public class Sound extends AbstractReceiverConfigurableService implements SoundConst {

    private String ID = "Sound";
    private String NAMESPACE = "service.Sound";
    
	private IRuntime m_runtime; 
    
    public Sound() {
        super();
		this.getRuntime().getConfigurableNotifier().register(this);
    }
    
    public String getNamespace() {
        return this.NAMESPACE;
    }
    
    public void shutdown() {
    	super.shutdown();
        IEventBroker eventBroker = this.getRuntime().getEventBroker();
        eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL));
        this.m_logger.info("Sound is shut down ...");
     }
    
    public void startup() {
    	super.startup();
        IEventBroker eventBroker = this.getRuntime().getEventBroker();
        eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL));
        this.m_logger.info("Sound is started ...");
    }

	public String getID() {
		return this.ID;
	}

	public void receivedValidRule(ICall aCall) {
		String filename = "";
		
		// check wether user has an own file 
		ICaller aCaller = aCall.getCaller();
		IAttribute filenameAttribute = aCaller.getAttribute(ATTRIBUTE_USER_SOUNDFILE);
		if (filenameAttribute!=null) {
			filename = filenameAttribute.getValue();
			if (filename.length()>0) {
				filename = PathResolver.getInstance(PIMRuntime.getInstance()).resolve(filename);
				this.m_logger.info("Playing user assigned sound file: " + filename);  
			}
		}
              
		// use file from MSN assignment
		if (filename.length()==0) {
			String msn = aCall.getMSN().getMSN();
                   
			filename = this.m_configuration.getProperty(msn + "_" + ATTRIBUTE_USER_SOUNDFILE, "");
                                    
			if (filename.length()>0) {
				filename = PathResolver.getInstance(PIMRuntime.getInstance()).resolve(filename);
				this.m_logger.info("Playing MSN assigned sound file: " + filename);  
			}
		}
                   
		if (filename.length()==0) {
			filename = this.m_configuration.getProperty("default_" + ATTRIBUTE_USER_SOUNDFILE, "");
			if (filename.length()>0) {
				filename = PathResolver.getInstance(PIMRuntime.getInstance()).resolve(filename);
				this.m_logger.info("Playing default sound file: " + filename);
			}
		}
             
		if (filename.length()==0) {
			this.m_logger.warning("No sound file assigned.");  
		} else {
			SoundThread st = new SoundThread(filename);
			st.setName("JAM-SoundFile-Thread-(non-deamon)");
			st.start();
		}
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

}
