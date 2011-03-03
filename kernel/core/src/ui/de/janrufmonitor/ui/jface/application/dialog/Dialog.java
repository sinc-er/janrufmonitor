package de.janrufmonitor.ui.jface.application.dialog;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import de.janrufmonitor.framework.*;
import de.janrufmonitor.framework.event.*;
import de.janrufmonitor.repository.ICallManager;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.filter.CallerFilter;
import de.janrufmonitor.repository.types.IReadCallRepository;
import de.janrufmonitor.repository.types.IWriteCallRepository;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.IService;
import de.janrufmonitor.ui.jface.application.AbstractApplication;
import de.janrufmonitor.ui.jface.application.RendererRegistry;
import de.janrufmonitor.ui.jface.application.rendering.ITableCellRenderer;
import de.janrufmonitor.ui.jface.wizards.JournalCallerWizard;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTExecuter;
import de.janrufmonitor.ui.swt.SWTImageManager;
import de.janrufmonitor.util.formatter.Formatter;
import de.janrufmonitor.util.io.ImageHandler;
import de.janrufmonitor.util.string.StringUtils;

public class Dialog extends AbstractApplication implements IDialog, IEventReceiver, IEventSender, DialogConst {

	private class NameAssignDialog extends Thread {

		private ICaller m_caller;
		private Logger m_logger;

		public NameAssignDialog(ICaller caller) {
			this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
			this.m_caller = caller;
		}
		
		public void run() {
			ICaller newCaller = openCallerWizard(this.m_caller);
			if (newCaller!=null) {
				this.storeCaller(newCaller);
			}
		}
		
		protected ICaller openCallerWizard(ICaller caller) {
		    Display display = DisplayManager.getDefaultDisplay();
			Shell shell = new Shell(display);

		    WizardDialog.setDefaultImage(SWTImageManager.getInstance(getRuntime()).get(IJAMConst.IMAGE_KEY_PIM_ICON));
		    JournalCallerWizard callerWiz = new JournalCallerWizard(caller);
		    WizardDialog dlg = new WizardDialog(shell, callerWiz);
		    dlg.open();
		    if (dlg.getReturnCode() == WizardDialog.OK) {
		    	return callerWiz.getResult();
		    }
		    return null;
		}

		public void storeCaller(ICaller caller) {
			if (!caller.getPhoneNumber().isClired()) {
				List cpms = PIMRuntime.getInstance().getCallerManagerFactory().getTypedCallerManagers(IWriteCallerRepository.class);
				for (int i = 0; i < cpms.size(); i++) {
					if (((ICallerManager) cpms.get(i)).isActive() && ((ICallerManager) cpms.get(i)).isSupported(IWriteCallerRepository.class))
						((IWriteCallerRepository) cpms.get(i)).updateCaller(caller);
				}

				List cms = PIMRuntime.getInstance().getCallManagerFactory().getTypedCallManagers(IWriteCallRepository.class);
				if (cms!=null && cms.size()>0) {
					ICallManager cmgr = null;
					for (int i=0;i<cms.size();i++) {
						cmgr = (ICallManager) cms.get(i);
						if (cmgr!=null && cmgr.isActive() && cmgr.isSupported(IWriteCallRepository.class) && cmgr.isSupported(IReadCallRepository.class)) {
							this.m_logger.info("Updating call from repository manager <"+cmgr.getManagerID()+">.");
							ICallList oldCalls = ((IReadCallRepository)cmgr).getCalls(
								new CallerFilter(
									caller
								)
							);

							for (int j = 0; j < oldCalls.size(); j++) {
								ICall newCall = oldCalls.get(j);
								newCall.setCaller(caller);
							}
							((IWriteCallRepository)cmgr).updateCalls(oldCalls);		
						}
					}
				}
			}
		}

	}
	
	public static String NAMESPACE = "ui.jface.application.dialog.Dialog";
	
	private ICall m_call;
	private Button reject;
	private Label caller;
	private Map m_colors;
	
	public Dialog(Properties configuration, ICall incommingCall) {
		super();
		this.setShellStyle(SWT.DIALOG_TRIM | SWT.ON_TOP);
		this.m_configuration = configuration;
		this.m_call = incommingCall;
		
		IEventBroker eventBroker = this.getRuntime().getEventBroker();
		eventBroker.register(this);
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLCLEARED));   		
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLACCEPTED));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLREJECTED));  		
	}

	public IRuntime getRuntime() {
		return PIMRuntime.getInstance();
	}

	public String getNamespace() {
		return Dialog.NAMESPACE;
	}

	public String getID() {
		return "Dialog";
	}
	
	public Properties getConfiguration() {
		return this.m_configuration;
	}
	
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(this.getI18nManager().getString(this.getNamespace(), (this.isOutgoing(this.m_call) ? "outgoing" : "call"), "label", this.getLanguage()));
		shell.addControlListener(
			new ControlAdapter() {
				public void controlMoved(ControlEvent e) {
					if (e.widget instanceof Shell && isFreePositioning()) {					
						new SWTExecuter(true, getID()) {
							protected void execute() {
								moveWindow();
							}
						}.start();
					}
				} 
			}
		);
		this.setDialogPosition(shell);
	}
	
	protected Control createContents(Composite parent) {		
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Group g = new Group(composite, SWT.SHADOW_ETCHED_IN);
		g.setLayout(new GridLayout(2, false));
		
		Label date = new Label(g, SWT.NONE);
		date.setText(
			this.getI18nManager().getString(this.getNamespace(), "date_label", "label", this.getLanguage())+
			this.getParsedDate()
		);
		Font initialFont = date.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(this.getFontSize()-3);
		}
		Font newFont = new Font(DisplayManager.getDefaultDisplay(), fontData[0]);
		date.setFont(newFont);
		
		boolean hasCallerImage = this.hasCallerImage();
		
		Label image = new Label(g, SWT.BORDER | SWT.RIGHT);
		GridData gd = new GridData();
		gd.verticalSpan = 6;
		image.setVisible(false);
		if (hasCallerImage) {
			gd.widthHint = 92;
			gd.heightHint = 110;
			gd.horizontalIndent = 10;
			image.setVisible(true);
			image.setImage(this.getCallerImage());
		}
		image.setLayoutData(gd);
		
		Label msn = new Label(g, SWT.NONE);
		msn.setText(
			this.getI18nManager().getString(this.getNamespace(), "msn_label", "label", this.getLanguage())+
			this.getParsedMsn()
		);
		initialFont = msn.getFont();
		fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(this.getFontSize()-3);
			fontData[i].setStyle(SWT.BOLD);
		}
		newFont = new Font(DisplayManager.getDefaultDisplay(), fontData[0]);
		msn.setFont(newFont);
		
		Label callerLabel = new Label(g, SWT.NONE);
		callerLabel.setText(
			this.getI18nManager().getString(this.getNamespace(), "caller_label", "label", this.getLanguage())
		);
		initialFont = callerLabel.getFont();
		fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(this.getFontSize()-3);
		}
		newFont = new Font(DisplayManager.getDefaultDisplay(), fontData[0]);
		callerLabel.setFont(newFont);
		
		caller = new Label(g, SWT.WRAP);
		caller.setText(
			this.getParsedCaller()				
		);
		caller.setForeground(new Color(DisplayManager.getDefaultDisplay(), this.getColor()));
		initialFont = caller.getFont();
		fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(this.getFontSize());
			fontData[i].setStyle(SWT.BOLD);
		}
		newFont = new Font(DisplayManager.getDefaultDisplay(), fontData[0]);
		caller.setFont(newFont);
		caller.pack();
		this.checkCallerLength(caller);
		caller.pack();
		
		// added 2008/04/08: add provider image if present
		Composite numberbar = new Composite(g, SWT.NONE);
		ITableCellRenderer tr = RendererRegistry.getInstance().getRenderer("ProviderLogo".toLowerCase());
		Image img = null;
		Label l = null;
		if (tr!=null && this.m_call!=null && !isCliredCaller()) {
			tr.updateData(this.m_call);
			img = tr.renderAsImage();
		}
		numberbar.setLayout(new GridLayout((img!=null ? 2 : 1), false));
		if (img!=null) {
			l = new Label(numberbar, SWT.LEFT);
			l.setImage(new Image(DisplayManager.getDefaultDisplay(), img.getImageData().scaledTo(32, 32)));
		}

		Label number = new Label(numberbar, SWT.NONE);
		number.setText(
			this.getParsedNumber()
		);
		number.setForeground(new Color(DisplayManager.getDefaultDisplay(), this.getColor()));
		initialFont = number.getFont();
		fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(this.getFontSize()-3);
			fontData[i].setStyle(SWT.BOLD);
		}
		newFont = new Font(DisplayManager.getDefaultDisplay(), fontData[0]);
		number.setFont(newFont);

		new Label(g, SWT.NONE).setText(
			this.getParsedCip()
		);

		
	    // check for plugins
	    List plugins = this.getPlugins(this.getConfiguration().getProperty("pluginlist", ""));

		Composite buttonbar = new Composite(composite, SWT.NONE);
		buttonbar.setLayout(new GridLayout(Math.max(4, (plugins.size()+2)), false));

		
		// check for active reject service
		IService rejectService = this.getRuntime().getServiceFactory().getService("Reject");
		if (rejectService!=null && rejectService.isEnabled()) {
			reject = new Button(buttonbar, SWT.PUSH);
			reject.setText(
				this.getI18nManager().getString(this.getNamespace(), "reject", "label", this.getLanguage())	
			);
			
			reject.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						if (e.widget instanceof Button) {
							reject.setEnabled(false);
							IEventBroker eventBroker = getRuntime().getEventBroker();
							if (getCall()!=null)
								getCall().setAttribute(getRuntime().getCallFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS, IJAMConst.ATTRIBUTE_VALUE_REJECTED));
							eventBroker.send(Dialog.this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLREJECTED, getCall()));
						}	
					}
				}
			);
		}
		
		// dialog close manually
		if (this.getShowTime() == -2) {
			Button close = new Button(buttonbar, SWT.PUSH);
			close.setText(
				this.getI18nManager().getString(this.getNamespace(), "close", "label", this.getLanguage())	
			);
			close.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						if (e.widget instanceof Button) {
							close();
						}	
					}	
				}
			);
		}
		
		// name assign is active
		if (this.isAssignement() && !this.isCliredCaller()) {
			Button assign = new Button(buttonbar, SWT.PUSH);
			assign.setText(
				this.getI18nManager().getString(this.getNamespace(), "assign", "label", this.getLanguage())	
			);

			assign.addSelectionListener(
				new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						if (e.widget instanceof Button) {
							Thread thread = new Thread () {
								public void run () {
									DisplayManager.getDefaultDisplay().asyncExec(
										new NameAssignDialog(getCaller())
									);
								}
							};
							thread.setName(getID());
							thread.start();
						}	
					}
				}
			);
		}
		
		// add plugins
		String classString = null;
		for (int i=0,j=plugins.size();i<j;i++) {
			classString = this.getConfiguration().getProperty((String) plugins.get(i));
			if (classString!=null && classString.trim().length()>0) {
				try {
					Class classObject = Thread.currentThread().getContextClassLoader().loadClass(classString);
					final IDialogPlugin plugin = (IDialogPlugin) classObject.newInstance();
					plugin.setDialog(this);
					plugin.setID((String) plugins.get(i));
					if (plugin.isEnabled()) {			
						Button button = new Button(buttonbar, SWT.PUSH);
						button.setText(plugin.getLabel());
					
						button.pack();
						button.addSelectionListener(
							new SelectionAdapter() {
								public void widgetSelected(SelectionEvent e) {
									if (e.widget instanceof Button) {
										plugin.run();
									}	
								}
							}
						);
					}
				} catch (ClassNotFoundException e) {
					this.m_logger.warning("Class not found: "+classString);
				} catch (InstantiationException e) {
					this.m_logger.log(Level.SEVERE, e.getMessage(), e);
				} catch (IllegalAccessException e) {
					this.m_logger.log(Level.SEVERE, e.getMessage(), e);
				}		
			}
		}
		
		if (this.getShowTime() > 0) {
			Timer aTimer = new Timer();
			aTimer.schedule(new TimerTask() {
				public void run() {
					new SWTExecuter(getID()) {
						protected void execute() {
							close();
						}
					}.start();
				}
			}, (long) ((this.getShowDuration()+1) * 1000));
		}
		
		gd = new GridData();
		if (!hasCallerImage) {
			gd.widthHint = Math.max(350, this.caller.getBounds().width + 10);
		}
		g.setLayoutData(gd);
		
		parent.getShell().pack();
		
		return composite;
	}
	
	private List getPlugins(String config) {	
		if (config.trim().length()==0) return new ArrayList(0);
		
		StringTokenizer st = new StringTokenizer(config, ",");
		List l = new ArrayList(st.countTokens());
		while (st.hasMoreTokens()) {
			l.add(st.nextToken());
		}
		return l;
	}
	
	private void setDialogPosition(Shell shell) {
		String position = this.getConfiguration().getProperty(CFG_POSITION, "left-top");
		if (DisplayManager.getDefaultDisplay()==null) {
			this.m_logger.severe("Could not get monitor device object for dialog.");
			return;
		}
		Monitor primaryMonitor = DisplayManager.getDefaultDisplay().getPrimaryMonitor ();

		int x = 0, y = 0;

		if (position.equalsIgnoreCase("left-top")) {
			x=0;
			y=0;
		}
		if (position.equalsIgnoreCase("right-top") || position.length()==0) {
			y=0;
			x=primaryMonitor.getClientArea().width - shell.getBounds().width - 1;
		}
		if (position.equalsIgnoreCase("left-bottom")) {
			x=0;
			y=primaryMonitor.getClientArea().height - shell.getBounds().height - 1;
		}
		if (position.equalsIgnoreCase("right-bottom")) {
			x=primaryMonitor.getClientArea().width - shell.getBounds().width - 1;
			y=primaryMonitor.getClientArea().height - shell.getBounds().height - 1;
		}
		if (position.equalsIgnoreCase("center")) {
			x=(primaryMonitor.getClientArea().width/2) - (shell.getBounds().width/2) - 1;
			y=(primaryMonitor.getClientArea().height/2) - (shell.getBounds().height/2) - 1;
		}
		
		// dialog has free defined position
		if (this.isFreePositioning()) {
			x = this.getFreePosX();
			y = this.getFreePosY();
		}

		shell.setBounds(
			x,
			y,
			shell.getBounds().width,
			shell.getBounds().height
		);
	}

	private boolean isFreePositioning() {
		return this.getConfiguration().getProperty(CFG_FREE_POS, "false").equalsIgnoreCase("true");
	}
	
	private int getFreePosX() {
		String value = this.getConfiguration().getProperty(CFG_FREE_POSX, "0");
		try {
			return Integer.parseInt(value);
		} catch (Exception ex){
			this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);
		}
		return 0;
	}
	
	private int getFreePosY() {
		String value = this.getConfiguration().getProperty(CFG_FREE_POSY, "0");
		try {
			return Integer.parseInt(value);
		} catch (Exception e){
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		}
		return 0;
	}
	
	private Image getCallerImage() {
		if (this.m_call.getCaller()!=null) {
			try {
				InputStream in = ImageHandler.getInstance().getImageStream(this.m_call.getCaller());
				ImageData id = new ImageData(in);
				// calculate proportions
				float height = ((float)id.height / (float)id.width) * 90;
				id = id.scaledTo(90, (int) height);
				in.close();
				return new Image(DisplayManager.getDefaultDisplay(), id);
			} catch (FileNotFoundException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			} catch (IOException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			}
		}
		return null;
	}
	
	private boolean hasCallerImage() {
		if (this.getCaller()!=null) {
			return ImageHandler.getInstance().hasImage(this.getCaller());
		}
		return false;
	}
	
	
	private ICaller getCaller() {
		if (this.m_call!=null) return this.m_call.getCaller();
		return null;
	}
	
	public ICall getCall() {
		return this.m_call;
	}
	
	private int getFontSize() {
		int size = 0;
		String sizeString = this.getConfiguration().getProperty(CFG_FONTSIZE, "11");
		size = new Integer(sizeString).intValue();
		return size;   
	}
    
	private RGB getColor() {
		int red = 0;
		int green = 0;
		int blue = 0;
		
		String colors = null;
		if (this.isUseMsnColors())
			colors = getMsnColor(this.getCall().getMSN().getMSN());
		
		if (colors==null)
			colors = this.getConfiguration().getProperty(CFG_FONTCOLOR, "255,0,0");
		
		StringTokenizer st = new StringTokenizer(colors, ",");
		red = new Integer(st.nextToken()).intValue();
		green = new Integer(st.nextToken()).intValue();
		blue = new Integer(st.nextToken()).intValue();
		return new RGB(red, green, blue);
	}
	
	private boolean isUseMsnColors() {
		return this.getConfiguration().getProperty(CFG_USEMSNCOLOR, "false").equalsIgnoreCase("true");
	}
	
	private String getMsnColor(String msn){
		if (this.m_colors==null) {
			this.m_colors = new HashMap();
			String colors = getRuntime().getConfigManagerFactory().getConfigManager().getProperty("ui.jface.application.journal.Journal", "msnfontcolor");
			StringTokenizer st = new StringTokenizer(colors, "[");

			while (st.hasMoreTokens()) {
				String singleColor = st.nextToken();
				singleColor = singleColor.substring(0, singleColor.length()-1).trim();
				if (singleColor.length()>0) {
					StringTokenizer s = new StringTokenizer(singleColor, "%");
					while (s.hasMoreTokens()) {
						String key = s.nextToken();
						String color = s.nextToken();
						
						// only add if MSNs is existing
						if (this.getRuntime().getMsnManager().existMsn(
							this.getRuntime().getMsnManager().createMsn(key))) {
							
							this.m_colors.put(key, color);
						}
					}
				}
			}
		}
		
		if (this.m_colors.containsKey(msn)) {
			return (String)this.m_colors.get(msn);
		}
		return null;	
	}
	
	private void checkCallerLength(Label caller) {
		if (caller.getBounds().width > 400) {
			int max = 300;
			StringTokenizer st = new StringTokenizer(caller.getText(), " ");
			caller.setText("");
			caller.pack();
			while (st.hasMoreTokens()) {
				caller.setText(caller.getText() + st.nextToken() + " ");
				caller.pack();
				if (caller.getBounds().width > max) {
					max = caller.getBounds().width;
					caller.setText(caller.getText().trim() + IJAMConst.CRLF);
				}
			}
		}
	}
	
	private String getParsedDate() {
		Formatter formatter = Formatter.getInstance(this.getRuntime());
		if (this.getCall()!=null)
			return formatter.parse(IJAMConst.GLOBAL_VARIABLE_CALLTIME, this.getCall().getDate());
		
		return "";
	}
	
	private String getParsedMsn() {
		Formatter formatter = Formatter.getInstance(this.getRuntime());
		if (this.getCall()!=null)
			return formatter.parse(IJAMConst.GLOBAL_VARIABLE_MSNFORMAT, this.getCall().getMSN());
		
		return "";
	}
	
	private String getParsedCip() {
		if (this.getCall()!=null)
			return this.getRuntime().getCipManager().getCipLabel(this.getCall().getCIP(), this.getLanguage());
		return "";
	}

	private String getParsedCaller() {
		Formatter formatter = Formatter.getInstance(this.getRuntime());

		String name = "";
		if (this.getCaller()!=null && this.getCaller().getPhoneNumber().isClired()) {
			name = this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_CLIR);
		} else {
			name = formatter.parse(IJAMConst.GLOBAL_VARIABLE_CALLERNAME, this.getCall());
	          
			// hack for 4.2.1 & sign is not shown
			name = StringUtils.replaceString(name, "&", "%amp%");
			name = StringUtils.replaceString(name, "%amp%", "&&");
		}
		return name;
	}
	
	private String getParsedNumber() {
		Formatter formatter = Formatter.getInstance(this.getRuntime());
		String number = "";
		if (this.getCaller()!=null && !this.getCaller().getPhoneNumber().isClired()) {
			number = formatter.parse(IJAMConst.GLOBAL_VARIABLE_CALLERNUMBER, this.getCall());
		} 
		return number;
	}
	
	private int getShowTime(){
		String sTime = this.getConfiguration().getProperty(CFG_SHOWTIME);
		return Integer.parseInt(((sTime == null || sTime.length()==0) ? "0" : sTime));
	}
	
	private int getShowDuration(){
		String sTime = this.getConfiguration().getProperty(CFG_SHOWDURATION);
		return Integer.parseInt(((sTime == null || sTime.length()==0) ? "0" : sTime));
	}
	
	private boolean isAssignement() {
		return this.getConfiguration().getProperty(CFG_ASSIGNEMENT, "false").equalsIgnoreCase("true");
	}
	
	private boolean isCliredCaller() {
		return (this.getCaller()!=null && this.getCaller().getPhoneNumber().isClired());
	}

	public void received(IEvent event) {
		if (event.getType() == IEventConst.EVENT_TYPE_CALLCLEARED ||
			event.getType() == IEventConst.EVENT_TYPE_CALLACCEPTED ||
			event.getType() == IEventConst.EVENT_TYPE_CALLREJECTED) {
			
			if (this.getShowTime() == -1) {
				new SWTExecuter(getID()) {
					protected void execute() {
						close();
					}
				}.start();
			} else {
				new SWTExecuter(getID()) {
					protected void execute() {
						if (reject==null) return;
						reject.setEnabled(false);
					}
				}.start();
			} 
		}
	}

	public String getReceiverID() {
		return this.getID();
	}

	public int getPriority() {
		return 0;
	}

	public String getSenderID() {
		return this.getID();
	}

	public boolean close() {
		IEventBroker eventBroker = this.getRuntime().getEventBroker();
		eventBroker.unregister(this);
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLCLEARED));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLACCEPTED));  
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLREJECTED));  

		return super.close();
	}
	
	public int open() {		
		new SWTExecuter(true, getID()) {
			protected void execute() {
				setDialogPosition(getShell());
				// added 2007/10/24:
				if (getConfiguration().getProperty(CFG_FOCUS, "false").equalsIgnoreCase("true")) {
					getShell().forceFocus();
					getShell().forceActive();
				}

			}
		}.start();
		return super.open();
	}
	
	private void moveWindow() {		
		if (!this.isFreePositioning())
			return;
		
		int x = getShell().getBounds().x;
		int y = getShell().getBounds().y;
		
		this.getRuntime().getConfigManagerFactory().getConfigManager().setProperty(
			this.getNamespace(),
			CFG_FREE_POSX,
			new Integer(x).toString()
		);
		
		this.getRuntime().getConfigManagerFactory().getConfigManager().setProperty(
			this.getNamespace(),
			CFG_FREE_POSY,
			new Integer(y).toString()
		);
		
		this.getRuntime().getConfigManagerFactory().getConfigManager().saveConfiguration();
		this.getRuntime().getConfigurableNotifier().notifyByNamespace(this.getNamespace());
	}
	
	private boolean isOutgoing(ICall c) {
		if (c==null) return false;
		IAttribute outgoing = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS);
		return (outgoing!=null && outgoing.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_OUTGOING));
	}
}
