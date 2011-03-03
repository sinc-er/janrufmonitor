package de.janrufmonitor.ui.jface.wizards.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import de.janrufmonitor.framework.installer.InstallerConst;
import de.janrufmonitor.framework.installer.InstallerEngine;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.update.UpdateManager;
import de.janrufmonitor.ui.jface.AbstractTableLabelProvider;
import de.janrufmonitor.ui.swt.DisplayManager;

public class UpdatesPage extends AbstractPage {
	
	private class UpdateContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object o) {
			List l = (List)o;
			return l.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {	
		}
		
	}
	
	private class UpdateLabelProvider extends AbstractTableLabelProvider {
		public String getColumnText(Object o, int column) {
			if (o instanceof Properties){
				Properties p = (Properties)o;
				
				switch (column) {
					case 0: return getNamespaceLabel(p.getProperty(InstallerConst.DESCRIPTOR_NAMESPACE, "-"));					
					case 1: {					
						Properties cp = InstallerEngine.getInstance().getDescriptor(p.getProperty(InstallerConst.DESCRIPTOR_NAMESPACE));
						if (cp!=null) {
							return cp.getProperty(InstallerConst.DESCRIPTOR_VERSION);
						}
						return "-";						
					}
					case 2: return p.getProperty(InstallerConst.DESCRIPTOR_VERSION);
					//case 3: return "Description";
				}
			}
			
			return null;
		}
	}
	
	private String NAMESPACE = "ui.jface.wizards.pages.UpdatesPage";
	
	private IRuntime m_runtime;
	private CheckboxTableViewer updates; 
	private UpdateManager m_um;
	private boolean m_preload;
	
	public UpdatesPage(String name) {
		this(null, false);
	}

	public UpdatesPage(String name, boolean preload) {
		super(RuleServicePage.class.getName());
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));
		this.m_um = new UpdateManager();
		this.m_preload = preload;
	}
	
	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}	

	public String getNamespace() {
		return NAMESPACE;
	}
	
	public boolean isMoreUpdates() {
		return this.m_um!=null ? this.m_um.isMoreUpdates() : false;
	}
	
	public List getResult() {
		Object[] o = this.updates.getCheckedElements();
		List l = new ArrayList();
		
		for (int i=0;i<o.length;i++) {
			l.add(o[i]);
		}
		
		return l;
	}

	public void createControl(Composite c) {
	    Composite select = new Composite(c, SWT.NONE);
	    select.setLayout(new GridLayout(1, false));
	    select.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	    Label ln = new Label(select, SWT.LEFT);
	    ln.setText(this.m_i18n.getString(this.getNamespace(), "updates", "label", this.m_language));

	    updates = CheckboxTableViewer.newCheckList(select, SWT.FULL_SELECTION | SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
	    	    
	    updates.setLabelProvider(new UpdateLabelProvider());
	    updates.setContentProvider(new UpdateContentProvider());
	    updates.getTable().setHeaderVisible(true);
	    TableColumn col = new TableColumn(updates.getTable(), SWT.LEFT | SWT.TOP);
	    col.setWidth(200);
	    col.setText(this.m_i18n.getString(this.getNamespace(), "modname", "label", this.m_language));
	    col = new TableColumn(updates.getTable(), SWT.LEFT | SWT.TOP);
	    col.setWidth(50);
	    col.setText(this.m_i18n.getString(this.getNamespace(), "currentversion", "label", this.m_language));
	    col = new TableColumn(updates.getTable(), SWT.LEFT | SWT.TOP);
	    col.setWidth(50);
	    col.setText(this.m_i18n.getString(this.getNamespace(), "newversion", "label", this.m_language));
//	    col = new TableColumn(updates.getTable(), SWT.LEFT | SWT.TOP);
//	    col.setWidth(350);
	    
	    Listener paintListener = new Listener() {
			public void handleEvent(Event event) {
				if (event.index!=3) return;
				
				switch(event.type) {		
					case SWT.MeasureItem: {
						TableItem item = (TableItem)event.item;
						String text = item.getText(event.index);
						Point size = event.gc.textExtent(text);
						event.width = size.x;
						event.height = Math.max(event.height, size.y + 7);
						break;
					}
					case SWT.PaintItem: {
						TableItem item = (TableItem)event.item;
						String text = item.getText(event.index);
						Point size = event.gc.textExtent(text);					
						int offset2 = (event.index == 0 ? Math.max(0, (event.height - size.y) / 2) : 0) + 3;
						event.gc.drawText(text, event.x + offset2, event.y + offset2, true);
						break;
					}
					case SWT.EraseItem: {	
						event.detail &= ~SWT.FOREGROUND;
						break;
					}
				}
			}

		};
		updates.getTable().addListener(SWT.MeasureItem, paintListener);
		updates.getTable().addListener(SWT.PaintItem, paintListener);
		updates.getTable().addListener(SWT.EraseItem, paintListener);
	    
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 220;
		//gd.minimumWidth = 490;
		gd.grabExcessHorizontalSpace = true;
		updates.getTable().setLayoutData(gd);
		
		updates.addCheckStateListener(
			new ICheckStateListener() {
				public void checkStateChanged(CheckStateChangedEvent e) {
					Object[] o = updates.getCheckedElements();
					setPageComplete(o.length>0);
				}
			}
		);
		
		final Button refresh = new Button(select, SWT.PUSH);
		refresh.setText(
			this.m_i18n.getString(this.getNamespace(), "refresh", "label", this.m_language)
		);
		
		refresh.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent ev) {
					try {
					   IRunnableWithProgress op = new IRunnableWithProgress() {
							public void run(IProgressMonitor pm) throws InvocationTargetException, InterruptedException {
								pm.setTaskName(m_i18n.getString(getNamespace(), "task", "label", m_language));
								
								final List l = m_um.getUpdates();
								DisplayManager.getDefaultDisplay().asyncExec(
									new Runnable () {
										public void run () {
											updates.setInput(l);
											updates.refresh(true);
										}
									}
								);
								if(l.size()==0) {
									DisplayManager.getDefaultDisplay().asyncExec(
										new Runnable () {
											public void run () {
												MessageDialog.openInformation(
													getShell(), 
													"",
													m_i18n.getString(getNamespace(), "noupdates", "label", m_language));
											}
										}
									);			
								}
							}
					   };
					   ProgressMonitorDialog pmd = new ProgressMonitorDialog(getShell());
					   pmd.run(true, true, op);
					} catch (InvocationTargetException e) {
						m_logger.log(Level.SEVERE, e.getMessage(), e);
					} catch (InterruptedException e) {
						m_logger.log(Level.SEVERE, e.getMessage(), e);
					}
				}
			}
		);
		
		ln = new Label(select, SWT.LEFT | SWT.WRAP);
	    ln.setText(this.m_i18n.getString(this.getNamespace(), "updates", "description", this.m_language));
	    gd = new GridData(480,50);
	    gd.horizontalSpan = 2;
	    gd.minimumWidth = 480;
	    ln.setLayoutData(gd);
		
		this.setControl(c);
		
		if (this.m_preload) {
			try {
			   IRunnableWithProgress op = new IRunnableWithProgress() {
					public void run(IProgressMonitor pm) throws InvocationTargetException, InterruptedException {
						pm.setTaskName(m_i18n.getString(getNamespace(), "task", "label", m_language));
						
						final List l = m_um.getUpdates();
						DisplayManager.getDefaultDisplay().asyncExec(
							new Runnable () {
								public void run () {
									updates.setInput(l);
									updates.refresh(true);
								}
							}
						);
						if(l.size()==0) {
							DisplayManager.getDefaultDisplay().asyncExec(
								new Runnable () {
									public void run () {
										MessageDialog.openInformation(
											getShell(), 
											"",
											m_i18n.getString(getNamespace(), "noupdates", "label", m_language));
									}
								}
							);			
						}
					}
			   };
			   ProgressMonitorDialog pmd = new ProgressMonitorDialog(getShell());
			   pmd.run(false, true, op);
			} catch (InvocationTargetException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			} catch (InterruptedException e) {
				this.m_logger.log(Level.SEVERE, e.getMessage(), e);
			}			
		}
	}
	
	private String getNamespaceLabel(String ns) {
		return this.m_i18n.getString(ns,"title","label",this.m_language);
	}

}
