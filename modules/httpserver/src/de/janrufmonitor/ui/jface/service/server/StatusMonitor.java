package de.janrufmonitor.ui.jface.service.server;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.server.Client;
import de.janrufmonitor.service.server.ClientHistoryItem;
import de.janrufmonitor.service.server.ClientRegistry;
import de.janrufmonitor.ui.jface.application.AbstractApplication;
import de.janrufmonitor.ui.jface.application.AbstractMenuBuilder;
import de.janrufmonitor.ui.jface.application.IApplication;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.util.io.PathResolver;

public class StatusMonitor extends AbstractApplication {

	private class StatusMonitorMenuBuilder extends AbstractMenuBuilder {

		private IRuntime m_runtime;
		
		public StatusMonitorMenuBuilder(IApplication app, List l, List popupActions) {
			super(app, l, popupActions);
		}

		public MenuManager createMenu() {
			MenuManager master = new MenuManager();
			
			// create file menu
			MenuManager view = new MenuManager(
				this.getI18nManager().getString(
					this.m_app.getNamespace(),
					"file",
					"label",
					this.getLanguage()
				)
			);
			master.add(view);
		
			this.addAction(view, "close");
			
			// create ? menu
			MenuManager q = new MenuManager(
				this.getI18nManager().getString(
					this.m_app.getNamespace(),
					"q",
					"label",
					this.getLanguage()
				)
			);
			master.add(q);
			this.addAction(q, "help");
			
			return master;
		}
		
		public IRuntime getRuntime() {
			if (this.m_runtime==null) {
				this.m_runtime = PIMRuntime.getInstance();
			}
			return this.m_runtime;
		}

		public Menu createPopupMenu(Control c) {
			MenuManager master = new MenuManager();
			return master.createContextMenu(c);
		}

	}

	private String NAMESPACE = "ui.jface.application.server.StatusMonitor";

	private IRuntime m_runtime;
	private AbstractMenuBuilder m_mb;
	
	private Table connectedClientTable;
	
	public StatusMonitor() {
		super();
		addMenuBar();
	}
	
	protected MenuManager createMenuManager() {
		return this.getMenuBuilder().createMenu();
	}
	
	protected AbstractMenuBuilder getMenuBuilder() {
		if (this.m_mb==null)
			this.m_mb = new StatusMonitorMenuBuilder(this, null, null);
		return m_mb;
	}
	
	public IRuntime getRuntime() {
		if (this.m_runtime == null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public String getID() {
		return "StatusMonitor";
	}

	protected Control createContents(final Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, true));
		
		Group tabGroup = new Group(composite, SWT.SHADOW_ETCHED_IN);
		tabGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		
		Label isServiceActiveLbl = new Label(tabGroup, 0);
		isServiceActiveLbl.setText(this.getI18nManager().getString(
				this.NAMESPACE, 
				"isactive",
				"label",
				this.getLanguage()
			));
		isServiceActiveLbl.setBounds(10,15,0,0);
		isServiceActiveLbl.pack();

		Label isServiceActiveValueLbl = new Label(tabGroup, 0);
		String valueLbl = "";
		valueLbl = PIMRuntime.getInstance().getConfigManagerFactory().getConfigManager().getProperty("service.Server", "enabled");
		if (valueLbl.equalsIgnoreCase("true")) {
			valueLbl = this.getI18nManager().getString(
				this.NAMESPACE, 
				"active",
				"label",
				this.getLanguage()
			);
		} else {
			valueLbl = this.getI18nManager().getString(
				this.NAMESPACE, 
				"notactive",
				"label",
				this.getLanguage()
			);
		}
			
		isServiceActiveValueLbl.setText(valueLbl);			
		isServiceActiveValueLbl.setBounds(isServiceActiveLbl.getBounds().x + isServiceActiveLbl.getBounds().width + 10,15,0,0);
		isServiceActiveValueLbl.pack();
			
		Label isPlActiveLbl = new Label(tabGroup, 0);
		isPlActiveLbl.setText(this.getI18nManager().getString(
				this.NAMESPACE, 
				"isplactive",
				"label",
				this.getLanguage()
			));
		isPlActiveLbl.setBounds(10,35,0,0);
		isPlActiveLbl.pack();

		Label isPlActiveValueLbl = new Label(tabGroup, 0);
		valueLbl = "";
		valueLbl = this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty("service.Server", "enabled");
		if (valueLbl.equalsIgnoreCase("true")) {
			valueLbl = this.getI18nManager().getString(
				this.NAMESPACE, 
				"active",
				"label",
				this.getLanguage()
			) + 
			this.getI18nManager().getString(
				this.NAMESPACE, 
				"addplactive",
				"label",
				this.getLanguage()
			);
			valueLbl += this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty("service.Server", "port");

		} else {
			valueLbl = this.getI18nManager().getString(
				this.NAMESPACE, 
				"notactive",
				"label",
				this.getLanguage()
			);
		}

		isPlActiveValueLbl.setText(valueLbl);			
		isPlActiveValueLbl.setBounds(isPlActiveLbl.getBounds().x + isPlActiveLbl.getBounds().width + 10,35,0,0);
		isPlActiveValueLbl.pack();			
			
		Label activeLbl = new Label(tabGroup, 0);
		activeLbl.setText(this.getI18nManager().getString(
			this.NAMESPACE, 
			"activeconnections",
			"label",
			this.getLanguage()
		));
		activeLbl.setBounds(10,55,0,0);
		activeLbl.pack();
			
		final Label activeValueLbl = new Label(tabGroup, 0);
		activeValueLbl.setText(
			Integer.toString(ClientRegistry.getInstance().getClientCount())
		);

		activeValueLbl.setBounds(activeLbl.getBounds().x + activeLbl.getBounds().width + 10,55,0,0);
		activeValueLbl.pack();

		// IP Table
		Label clientTableLbl = new Label(tabGroup, 0);
		clientTableLbl.setText(this.getI18nManager().getString(
			this.NAMESPACE, 
			"connectedclients",
			"label",
			this.getLanguage()
		));
		clientTableLbl.setBounds(10,95,0,0);
		clientTableLbl.pack();

		this.connectedClientTable = new Table(tabGroup, SWT.BORDER | SWT.V_SCROLL | SWT.FULL_SELECTION);
		this.connectedClientTable.setBounds(10, 115, 180, 220);
					
		new TableColumn (this.connectedClientTable, SWT.LEFT);
		new TableColumn (this.connectedClientTable, SWT.LEFT);

		this.addClients();
		
		this.resizeIpTable();
		
		Button refresh = new Button(tabGroup, SWT.PUSH);
		refresh.setText(this.getI18nManager().getString(
			this.NAMESPACE, 
			"refresh",
			"label",
			this.getLanguage())
		);
		refresh.setBounds(210, 115, 100,18);

	
		// disconnect
		final Button disconnect = new Button(tabGroup, SWT.PUSH);
		disconnect.setText(this.getI18nManager().getString(
			this.NAMESPACE, 
			"disconnect",
			"label",
			this.getLanguage())
		);
		disconnect.setBounds(210, 195, 100,18);

		// detail
		final Button detail = new Button(tabGroup, SWT.PUSH);
		detail.setText(this.getI18nManager().getString(
			this.NAMESPACE, 
			"detail",
			"label",
			this.getLanguage())
		);
		detail.setBounds(210, 145, 100,18);			
			
		// add detail info
		final Group detailGroup = new Group(tabGroup, SWT.SHADOW_ETCHED_IN);
		detailGroup.setText(this.getI18nManager().getString(
			this.NAMESPACE, 
			"detailtitle",
			"label",
			this.getLanguage()
		));
			
		detailGroup.setVisible(false);
						
		detailGroup.setBounds(325, 95, 250, 240);
			
		Label detailStationLbl = new Label(detailGroup, 0);
		detailStationLbl.setText(this.getI18nManager().getString(
			this.NAMESPACE, 
			"detailstation",
			"label",
			this.getLanguage()
		));
		detailStationLbl.setBounds(10,20,0,0);
		detailStationLbl.pack();
			
		final Label detailStation = new Label(detailGroup, 0);
		detailStation.setText("");
		detailStation.setBounds(detailStationLbl.getBounds().x + detailStationLbl.getBounds().width + 10,20,0,0);
		detailStation.pack();
			
		Label detailIpLbl = new Label(detailGroup, 0);
		detailIpLbl.setText(this.getI18nManager().getString(
			this.NAMESPACE, 
			"detailip",
			"label",
			this.getLanguage()
		));
		detailIpLbl.setBounds(10,40,0,0);
		detailIpLbl.pack();		
			
		final Label detailIp = new Label(detailGroup, 0);
		detailIp.setText("");
		detailIp.setBounds(detailIpLbl.getBounds().x + detailIpLbl.getBounds().width + 10,40,0,0);
		detailIp.pack();	

		Label detailPortLbl = new Label(detailGroup, 0);
		detailPortLbl.setText(this.getI18nManager().getString(
			this.NAMESPACE, 
			"detailport",
			"label",
			this.getLanguage()
		));
		detailPortLbl.setBounds(10,60,0,0);
		detailPortLbl.pack();	

		final Label detailPort = new Label(detailGroup, 0);
		detailPort.setText("");
		detailPort.setBounds(detailPortLbl.getBounds().x + detailPortLbl.getBounds().width + 10,60,0,0);
		detailPort.pack();

		Label registeredLbl = new Label(detailGroup, 0);
		registeredLbl.setText(this.getI18nManager().getString(
			this.NAMESPACE, 
			"registered",
			"label",
			this.getLanguage()
		));
		registeredLbl.setBounds(10,80,0,0);
		registeredLbl.pack();
			
		final Label registered = new Label(detailGroup, 0);
		registered.setText("");
		registered.setBounds(registeredLbl.getBounds().x + registeredLbl.getBounds().width + 10,80,0,0);
		registered.pack();
		
		Label durationLbl = new Label(detailGroup, 0);
		durationLbl.setText(this.getI18nManager().getString(
			this.NAMESPACE, 
			"duration",
			"label",
			this.getLanguage()
		));
		durationLbl.setBounds(10,100,0,0);
		durationLbl.pack();
			
		final Label duration = new Label(detailGroup, 0);
		duration.setText("");
		duration.setBounds(durationLbl.getBounds().x + durationLbl.getBounds().width + 10,100,0,0);
		duration.pack();
		
		Label transferedLbl = new Label(detailGroup, 0);
		transferedLbl.setText(this.getI18nManager().getString(
			this.NAMESPACE, 
			"transfered",
			"label",
			this.getLanguage()
		));
		transferedLbl.setBounds(10,120,0,0);
		transferedLbl.pack();
			
		final Label transfered = new Label(detailGroup, 0);
		transfered.setText("");
		transfered.setBounds(transferedLbl.getBounds().x + transferedLbl.getBounds().width + 10,120,0,0);
		transfered.pack();

		Label lastActionLbl = new Label(detailGroup, 0);
		lastActionLbl.setText(this.getI18nManager().getString(
			this.NAMESPACE, 
			"lastaction",
			"label",
			this.getLanguage()
		));
		lastActionLbl.setBounds(10,140,0,0);
		lastActionLbl.pack();
			
		final Label lastAction = new Label(detailGroup, 0);
		lastAction.setText("");
		lastAction.setBounds(20,160,0,0);
		lastAction.pack();

		refresh.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					activeValueLbl.setText(
							Integer.toString(ClientRegistry.getInstance().getClientCount())
						);
					detailGroup.setVisible(false);
					refresh();
					
					if (connectedClientTable.getItemCount()>0) {
						detail.setEnabled(true);
						disconnect.setEnabled(true);
					}else {
						detail.setEnabled(false);
						disconnect.setEnabled(false);
					}
				}
			}
		);
		
		disconnect.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (connectedClientTable.getSelectionCount()>0) {
						Client c = (Client)connectedClientTable.getSelection()[0].getData();
						if (c!=null && c instanceof Client)
							ClientRegistry.getInstance().unregister(c);
						activeValueLbl.setText(
								Integer.toString(ClientRegistry.getInstance().getClientCount())
							);
						detailGroup.setVisible(true);
						refresh();
						
						if (connectedClientTable.getItemCount()>0) {
							detail.setEnabled(true);
							disconnect.setEnabled(true);
						}else {
							detail.setEnabled(false);
							disconnect.setEnabled(false);
						}
					}
				}
			}
		);
		
		detail.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (connectedClientTable.getSelectionCount()>0) {
						Client c = (Client)connectedClientTable.getSelection()[0].getData();
						if (c!=null) {
							detailStation.setText(c.getClientName());
							detailStation.pack();
							detailIp.setText(c.getClientIP());
							detailIp.pack();
							detailPort.setText(Integer.toString(c.getClientPort()));
							detailPort.pack();
							SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
							registered.setText(sdf.format(new Date(c.getTimestamp())));
							registered.pack();
							duration.setText(calcDuration(c.getTimestamp()));
							duration.pack();
							transfered.setText(new DecimalFormat().format((c.getByteReceived()/1024)+1));
							transfered.pack();
							
							List history = c.getHistory();
							if (history.size()>0)
								lastAction.setText(getI18nManager().getString(
									NAMESPACE, 
									((ClientHistoryItem)history.get(history.size()-1)).getEvent().toLowerCase(),
									"label",
									getLanguage()
								));
							lastAction.pack();
							detailGroup.setVisible(true);
						}
					}
				}
			}
		);
			
		if (this.connectedClientTable.getItemCount()>0) {
			detail.setEnabled(true);
			disconnect.setEnabled(true);
		}else {
			detail.setEnabled(false);
			disconnect.setEnabled(false);
		}
		
		tabGroup.pack();

		return composite;
	}
	
	private void refresh() {
		addClients();
		resizeIpTable();
	}
	
	private void resizeIpTable() {
		for (int i=0;i<this.connectedClientTable.getColumnCount();i++) {
			TableColumn c = this.connectedClientTable.getColumn(i);
			c.pack();
		}
	}
	
	protected String calcDuration(long date) {
		long now = System.currentTimeMillis();
		long ctime = date;
		
		long diff = now - ctime;
		diff = diff / 1000; // sec

		String sec = Long.toString(diff % (60)) + " sec.";
		diff = diff / 60;
		if (diff % (60)==0)
			return sec;
			
		String min = Long.toString(diff % (60)) + " min. ";
		diff = diff / 60;
		if (diff % (24)==0)
			return min + sec;
					
		String h = Long.toString(diff % (24)) + " h ";
		diff = diff / 24;
		if (diff % (365)==0)
			return h + min + sec;
		
		String d = Long.toString(diff % (365)) + " d ";
			
		return d + h + min + sec;
	}
	
	private void addClients() {
		this.connectedClientTable.removeAll();
		
		List clients = ClientRegistry.getInstance().getAllClients();
		for (int i=0;i<clients.size();i++) {
			Client c = (Client)clients.get(i);
			TableItem item = new TableItem (this.connectedClientTable, SWT.NULL);
			item.setImage(0, new Image(DisplayManager.getDefaultDisplay(), PathResolver.getInstance(this.getRuntime()).getImageDirectory() + "clients.gif"));
			item.setText(1, c.getClientName() + " ("+c.getClientIP()+")");
			item.setData(c);
		}
	}
}
