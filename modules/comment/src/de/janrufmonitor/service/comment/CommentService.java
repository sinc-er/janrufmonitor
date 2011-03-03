package de.janrufmonitor.service.comment;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.event.IEventBroker;
import de.janrufmonitor.framework.event.IEventConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.AbstractReceiverConfigurableService;
import de.janrufmonitor.ui.jface.application.comment.Comment;
import de.janrufmonitor.ui.swt.SWTExecuter;

public class CommentService extends AbstractReceiverConfigurableService {

	private String ID = "CommentService";
	private String NAMESPACE = "service.CommentService";
	
	private String CFG_AUTOCREATE = "autocreatecomment";
	
	private CommentCallerHandler cch;
	private IRuntime m_runtime;
	private ICaller m_lastCaller;
	
	public CommentService() {
		super();
		this.getRuntime().getConfigurableNotifier().register(this);
	}
	public void startup() {
		super.startup();
		IEventBroker evtBroker = this.getRuntime().getEventBroker();
		evtBroker.register(this, evtBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL));
	}

	public void shutdown() {
		super.shutdown();
		IEventBroker evtBroker = this.getRuntime().getEventBroker();
		evtBroker.unregister(this, evtBroker.createEvent(IEventConst.EVENT_TYPE_IDENTIFIED_CALL));
	}
	
	public CommentCallerHandler getHandler() {
		if (this.cch==null)
			this.cch = new CommentCallerHandler(this.m_configuration);
			
		return this.cch;
	}

	public String getID() {
		return this.ID;
	}
	
	public String getNamespace() {
		return this.NAMESPACE;
	}

	public void receivedValidRule(final ICall aCall) {
		this.m_lastCaller = aCall.getCaller();
		if (this.m_lastCaller!=null && !this.m_lastCaller.getPhoneNumber().isClired()) {
			if (this.isAutoCreateEnabled()) {
				new SWTExecuter(true, this.getID()) {
					protected void execute() {
						new Comment(aCall).open();
					} 
				}.start();
			} else {
				this.m_logger.info("Comment Dialog not opened due to disabled auto create mode.");
			}
		} else {
			this.m_logger.info("Comment Dialog not opened due to CLIR call.");
		}
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}
	
	public ICaller getLastCaller() {
		return this.m_lastCaller;
	}
	
	private boolean isAutoCreateEnabled() {
		return (this.m_configuration.getProperty(CFG_AUTOCREATE, "false").equalsIgnoreCase("true"));
	}

}
