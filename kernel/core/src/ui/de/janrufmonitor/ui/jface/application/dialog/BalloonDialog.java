package de.janrufmonitor.ui.jface.application.dialog;

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import de.janrufmonitor.framework.*;
import de.janrufmonitor.framework.event.*;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.ICallManager;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.filter.CallerFilter;
import de.janrufmonitor.repository.types.IReadCallRepository;
import de.janrufmonitor.repository.types.IWriteCallRepository;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.IService;
import de.janrufmonitor.ui.jface.application.RendererRegistry;
import de.janrufmonitor.ui.jface.application.controls.BalloonWindow;
import de.janrufmonitor.ui.jface.application.controls.HyperLink;
import de.janrufmonitor.ui.jface.application.rendering.ITableCellRenderer;
import de.janrufmonitor.ui.jface.wizards.JournalCallerWizard;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTExecuter;
import de.janrufmonitor.ui.swt.SWTImageManager;
import de.janrufmonitor.util.formatter.Formatter;
import de.janrufmonitor.util.io.ImageHandler;
import de.janrufmonitor.util.string.StringUtils;

public class BalloonDialog extends BalloonWindow implements IDialog, IEventSender, IEventReceiver, DialogConst {

	private class AssignPlugin implements IDialogPlugin {

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

		private IDialog m_dialog;
		
		public String getLabel() {
			return getI18nManager().getString(getNamespace(), "assign", "label", getLanguage());
		}

		public void setDialog(IDialog d) {
			this.m_dialog =d;
		}

		public void run() {
			Thread thread = new Thread () {
				public void run () {
					DisplayManager.getDefaultDisplay().asyncExec(
						new NameAssignDialog(m_dialog.getCall().getCaller())
					);
				}
			};
			thread.setName(getID());
			thread.start();
		}

		public boolean isEnabled() {
			return isAssignement();
		}

		public void setID(String id) {

		}
		
	}
	
	public static String NAMESPACE = "ui.jface.application.dialog.Dialog";

	private static int m_instance_count = -1;
	
	private Properties m_configuration;
	private ICall m_call;
	
	private II18nManager m_i18n;
	private String m_language;
	private Logger m_logger;
	
	public BalloonDialog(Properties config, ICall call) {
		super(DisplayManager.getDefaultDisplay(), SWT.ON_TOP | SWT.CLOSE | SWT.TITLE);
	
		if (m_instance_count>2) m_instance_count = -1;
		m_instance_count++;
		
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);	

		IEventBroker eventBroker = this.getRuntime().getEventBroker();
		eventBroker.register(this);
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLCLEARED));   		
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLACCEPTED));
		eventBroker.register(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLREJECTED));
		
		this.m_configuration = config;
		this.m_call = call;
		
		this.setAnchor(SWT.NONE);
		
		this.setText(this.getI18nManager().getString(
			this.getNamespace(),
			(isOutgoing(call)? "outgoing" : "call"),
			"label",
			this.getLanguage()
		));
		
		this.setImage(SWTImageManager.getInstance(this.getRuntime()).get((isOutgoing(call) ? IJAMConst.IMAGE_KEY_OUTGOING_GIF : IJAMConst.IMAGE_KEY_ACCEPTED_GIF)));
	}
	
	public void createDialog() {

		final Color color = new Color(getShell().getDisplay(), 255, 255, 225);
		boolean hasCallerImage = this.hasCallerImage();
		
		Composite c = this.getContents();
		c.setLayout(new GridLayout((hasCallerImage ? 2 : 1), false));
		
		if (hasCallerImage) {
			Label image = new Label(c, SWT.BORDER | SWT.RIGHT);
			GridData gd = new GridData();
			gd.verticalSpan = 5;
			image.setVisible(false);
			gd.widthHint = 92;
			gd.heightHint = 110;
			gd.horizontalIndent = 10;
			image.setVisible(true);
			image.setImage(this.getCallerImage());
			image.setLayoutData(gd);
		}
		
		// date 
		Label l = new Label(c, SWT.NONE);
		l.setText(
			this.getI18nManager().getString(this.getNamespace(), "date_label", "label", this.getLanguage())+
			this.getParsedDate()
		);
		Font initialFont = l.getFont();
		FontData[] fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(this.getFontSize()-3);
		}
		Font newFont = new Font(DisplayManager.getDefaultDisplay(), fontData[0]);
		l.setFont(newFont);
		l.setBackground(color);
		
		// MSN
		l = new Label(c, SWT.NONE);
		l.setText(
			this.getI18nManager().getString(this.getNamespace(), "msn_label", "label", this.getLanguage())+
			this.getParsedMsn()
		);
		initialFont = l.getFont();
		fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(this.getFontSize()-3);
			fontData[i].setStyle(SWT.BOLD);
		}
		newFont = new Font(DisplayManager.getDefaultDisplay(), fontData[0]);
		l.setFont(newFont);
		l.setBackground(color);
		
		// caller name +  additional
		
		l = new Label(c, SWT.LEFT);
		l.setText(this.getParsedCaller());
	    l.setBackground(color);
	    l.setForeground(new Color(DisplayManager.getDefaultDisplay(), this.getColor()));
		initialFont = l.getFont();
		fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(this.getFontSize());
			fontData[i].setStyle(SWT.BOLD);
		}
		newFont = new Font(DisplayManager.getDefaultDisplay(), fontData[0]);
		l.setFont(newFont);
		l.pack();
		this.checkCallerLength(l);
		l.pack();
		

		// number
		
		// added 2008/04/08: add provider image if present
		Composite numberbar = new Composite(c, SWT.NONE);
		numberbar.setBackground(color);
		ITableCellRenderer tr = RendererRegistry.getInstance().getRenderer("ProviderLogo".toLowerCase());
		Image img = null;
		if (tr!=null && this.m_call!=null && !isCliredCaller()) {
			tr.updateData(this.m_call);
			img = tr.renderAsImage();
		}
		numberbar.setLayout(new GridLayout((img!=null ? 2 : 1), false));
		if (img!=null) {
			l = new Label(numberbar, SWT.LEFT);
			 l.setBackground(color);
			l.setImage(new Image(DisplayManager.getDefaultDisplay(), img.getImageData().scaledTo(32, 32)));
		}
		
		l = new Label(numberbar, SWT.LEFT);

		l.setText(this.getParsedNumber());
		
	    l.setBackground(color);
	    l.setForeground(new Color(DisplayManager.getDefaultDisplay(), this.getColor()));
		initialFont = l.getFont();
		fontData = initialFont.getFontData();
		for (int i = 0; i < fontData.length; i++) {
			fontData[i].setHeight(this.getFontSize()-3);
		}
		newFont = new Font(DisplayManager.getDefaultDisplay(), fontData[0]);
		l.setFont(newFont);
		l.pack();
		
		// CIP
		l = new Label(c, SWT.LEFT);
		l.setText(this.getParsedCip());
	    l.setBackground(color);
	    l.pack();

	    // Buttons
	    Composite buttonBar = new Composite(c, SWT.NONE);
	    GridData gd = new GridData();
	    gd.horizontalSpan = 2;
	    
	    // check for plugins
	    List plugins = this.getPlugins(this.getConfiguration().getProperty("pluginlist", ""));
	    
	    GridLayout gl = new GridLayout(Math.max(5, (plugins.size()+2)), false);
	    gl.marginRight = 20;
	    
	    buttonBar.setLayout(gl);
	    buttonBar.setLayoutData(gd);
	    buttonBar.setBackground(color);

//		Label la = new Label(buttonBar, SWT.LEFT);
//		la.setText("       ");
//		la.setBackground(color);
		
		// check for active reject service
		IService rejectService = this.getRuntime().getServiceFactory().getService("Reject");
		if (rejectService!=null && rejectService.isEnabled()) {
			final HyperLink reject = new HyperLink(buttonBar, SWT.LEFT);
			reject.setBackground(color);
			reject.setText(
				this.getI18nManager().getString(this.getNamespace(), "reject", "label", this.getLanguage())	
			);
			reject.pack();
			
			reject.addMouseListener( 
				new MouseAdapter() {
					public void mouseDown(MouseEvent e) {
						if (e.button==1) {
							reject.setEnabled(false);
							reject.setText(
								getI18nManager().getString(getNamespace(), "rejected", "label", getLanguage())	
							);
							reject.setUnderline(color);
							reject.setActiveUnderline(color);
							reject.setHoverUnderline(color);
							reject.pack(true);
							IEventBroker eventBroker = getRuntime().getEventBroker();
							if (m_call!=null)
								m_call.setAttribute(getRuntime().getCallFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS, IJAMConst.ATTRIBUTE_VALUE_REJECTED));
							eventBroker.send(BalloonDialog.this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLREJECTED, m_call));
						}
					}
				}
			);
		}

		if (this.isAssignement() && !this.isCliredCaller()) {
			final IDialogPlugin assignPlugin = new AssignPlugin();
			assignPlugin.setDialog(this);
			
			HyperLink hl = new HyperLink(buttonBar, SWT.LEFT);
			hl.setText(assignPlugin.getLabel());
		    hl.setBackground(color);
			hl.pack();
			hl.addMouseListener( 
				new MouseAdapter() {
					public void mouseDown(MouseEvent e) {
						if (e.button==1) {
							assignPlugin.run();
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
						HyperLink hl = new HyperLink(buttonBar, SWT.LEFT);
						hl.setText(plugin.getLabel());
					    hl.setBackground(color);
						hl.pack();
						hl.addMouseListener( 
							new MouseAdapter() {
								public void mouseDown(MouseEvent e) {
									if (e.button==1) {
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
		color.dispose();
		c.pack();
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
	
	private RGB getColor() {
		int red = 0;
		int green = 0;
		int blue = 0;
    	
		String colors = this.getConfiguration().getProperty(CFG_FONTCOLOR, "255,0,0");
		StringTokenizer st = new StringTokenizer(colors, ",");
		red = new Integer(st.nextToken()).intValue();
		green = new Integer(st.nextToken()).intValue();
		blue = new Integer(st.nextToken()).intValue();
		return new RGB(red, green, blue);
	}
	
	private void checkCallerLength(Label caller) {
		if (caller.getBounds().width > 300) {
			int max = 300;
			StringTokenizer st = new StringTokenizer(caller.getText(), " ");
			caller.setText("");
			caller.pack();
			while (st.hasMoreTokens()) {
				caller.setText(caller.getText() + st.nextToken() + " ");
				caller.pack();
				if (caller.getBounds().width > max) {
					max = caller.getBounds().width;
					caller.setText(caller.getText().trim() +IJAMConst.CRLF);
				}
			}
		}
	}
	
	private int getFontSize() {
		int size = 0;
		String sizeString = this.getConfiguration().getProperty(CFG_FONTSIZE, "11");
		size = new Integer(sizeString).intValue();
		return size;   
	}
	
	private boolean hasCallerImage() {
		if (this.m_call.getCaller()!=null) {
			return ImageHandler.getInstance().hasImage(this.m_call.getCaller());
		}
		return false;
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
	
	public String getNamespace() {
		return BalloonDialog.NAMESPACE;
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

	private Properties getConfiguration() {
		return (this.m_configuration!=null ? this.m_configuration : new Properties());
	}
	
	private IRuntime getRuntime() {
		return PIMRuntime.getInstance();
	}
	
	
	private String getParsedDate() {
		Formatter formatter = Formatter.getInstance(this.getRuntime());
		if (this.m_call!=null)
			return formatter.parse(IJAMConst.GLOBAL_VARIABLE_CALLTIME, this.m_call.getDate());
		
		return "";
	}
	
	private String getParsedMsn() {
		Formatter formatter = Formatter.getInstance(this.getRuntime());
		if (this.m_call!=null)
			return formatter.parse(IJAMConst.GLOBAL_VARIABLE_MSNFORMAT, this.m_call.getMSN());
		
		return "";
	}
	
	private String getParsedCip() {
		if (this.m_call!=null)
			return this.getRuntime().getCipManager().getCipLabel(this.m_call.getCIP(), this.getLanguage());
		return "";
	}

	private String getParsedCaller() {
		Formatter formatter = Formatter.getInstance(this.getRuntime());

		String name = "";
		if (this.m_call.getCaller()!=null && this.m_call.getCaller().getPhoneNumber().isClired()) {
			name = this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_CLIR);
		} else {
			name = formatter.parse(IJAMConst.GLOBAL_VARIABLE_CALLERNAME, this.m_call);
	          
			// hack for 4.2.1 & sign is not shown
			name = StringUtils.replaceString(name, "&", "%amp%");
			name = StringUtils.replaceString(name, "%amp%", "&&");
		}
		return name;
	}
	
	private String getParsedNumber() {
		Formatter formatter = Formatter.getInstance(this.getRuntime());
		String number = "";
		if (this.m_call.getCaller()!=null && !this.m_call.getCaller().getPhoneNumber().isClired()) {
			number = formatter.parse(IJAMConst.GLOBAL_VARIABLE_CALLERNUMBER, this.m_call);
		} 
		return number;
	}
	
	
	public void open() {
		this.getShell().pack();
		this.setDialogPosition(this.getShell());
		
		// added 2007/10/24:
		if (this.getConfiguration().getProperty(CFG_FOCUS, "false").equalsIgnoreCase("true")) {
			this.getShell().forceFocus();
			this.getShell().forceActive();
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

		
		super.open();
		
		while (this.getShell()!=null && !this.getShell().isDisposed()) {
			if (!this.getShell().getDisplay().readAndDispatch ()) this.getShell().getDisplay().sleep ();
		}
	}
	
	public void close() {
		IEventBroker eventBroker = this.getRuntime().getEventBroker();
		eventBroker.unregister(this);
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLCLEARED));
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLACCEPTED));  
		eventBroker.unregister(this, eventBroker.createEvent(IEventConst.EVENT_TYPE_CALLREJECTED));  

		m_instance_count--;
		m_instance_count = Math.max(m_instance_count, -1);
		
		if (!this.getShell().isDisposed())
			super.close();
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
				} 
			}	
	}
	
	private int getShowTime(){
		String sTime = this.getConfiguration().getProperty(CFG_SHOWTIME);
		return Integer.parseInt(((sTime == null || sTime.length()==0) ? "0" : sTime));
	}
	
	private int getShowDuration(){
		String sTime = this.getConfiguration().getProperty(CFG_SHOWDURATION);
		return Integer.parseInt(((sTime == null || sTime.length()==0) ? "0" : sTime));
	}
	
	private String getID() {
		return "BalloonDialog";
	}

	public String getReceiverID() {
		return this.getID();
	}

	public int getPriority() {
		return 0;
	}
	
	private void setDialogPosition(Shell shell) {
		String position = this.getConfiguration().getProperty(CFG_POSITION, "left-top");
		if (DisplayManager.getDefaultDisplay()==null) {
			this.m_logger.severe("Could not get monitor device object for dialog.");
			return;
		}
		Monitor primaryMonitor = DisplayManager.getDefaultDisplay().getPrimaryMonitor ();

		int offset = m_instance_count * 15;
		
		int x = 0, y = 0;

		// added 2008/04/07: set minimal width and height
		shell.setBounds(
			x,
			y,
			Math.max(shell.getBounds().width, 165),
			Math.max(shell.getBounds().height, 115)
		);

		if (position.equalsIgnoreCase("left-top")) {
			x=5 + offset;
			y=5 + offset;
		}
		if (position.equalsIgnoreCase("right-top") || position.length()==0) {
			y=5 + offset;
			x=primaryMonitor.getClientArea().width - shell.getBounds().width - 30 - offset;
		}
		if (position.equalsIgnoreCase("left-bottom")) {
			x=5;
			y=primaryMonitor.getClientArea().height - shell.getBounds().height - 35 - offset;
		}
		if (position.equalsIgnoreCase("right-bottom")) {
			x=primaryMonitor.getClientArea().width - shell.getBounds().width - 30 - offset;
			y=primaryMonitor.getClientArea().height - shell.getBounds().height - 35 - offset;
		}
		if (position.equalsIgnoreCase("center")) {
			x=(primaryMonitor.getClientArea().width/2) - (shell.getBounds().width/2) - 1 - offset;
			y=(primaryMonitor.getClientArea().height/2) - (shell.getBounds().height/2) - 35 - offset;
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

	private int getFreePosX() {
		String value = this.getConfiguration().getProperty(CFG_FREE_POSX, "0");
		try {
			return Integer.parseInt(value);
		} catch (Exception e){
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
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
	
	private boolean isFreePositioning() {
		return this.getConfiguration().getProperty(CFG_FREE_POS, "false").equalsIgnoreCase("true");
	}
	
	private boolean isAssignement() {
		return this.getConfiguration().getProperty(CFG_ASSIGNEMENT, "false").equalsIgnoreCase("true");
	}
	
	private boolean isCliredCaller() {
		return (this.m_call.getCaller()!=null && this.m_call.getCaller().getPhoneNumber().isClired());
	}

	public String getSenderID() {
		return this.getID();
	}

	public ICall getCall() {
		return this.m_call;
	}
	
	private boolean isOutgoing(ICall c) {
		if (c==null) return false;
		IAttribute outgoing = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLSTATUS);
		return (outgoing!=null && outgoing.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_OUTGOING));
	}
}
