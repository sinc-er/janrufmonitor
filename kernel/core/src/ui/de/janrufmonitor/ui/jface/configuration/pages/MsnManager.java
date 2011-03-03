package de.janrufmonitor.ui.jface.configuration.pages;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import de.janrufmonitor.framework.IMsn;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.AbstractTableLabelProvider;
import de.janrufmonitor.ui.jface.configuration.AbstractConfigPage;
import de.janrufmonitor.ui.jface.configuration.controls.BooleanFieldEditor;
import de.janrufmonitor.ui.jface.wizards.MsnWizard;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class MsnManager extends AbstractConfigPage {
	
	private class MsnContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object o) {
			List l = (List)o;
			return l.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {	
		}
		
	}
	
	private class MsnLabelProvider extends AbstractTableLabelProvider {

		public String getColumnText(Object o, int column) {
			IMsn msn = (IMsn)o;

		    switch (column) {
		    case 0:
		      return msn.getMSN();
		    case 1:
		      return msn.getAdditional();
		    }
			return null;
		}
	}
	
	private class MsnViewerSorter extends ViewerSorter {
		private static final int ASCENDING = 0;
		private static final int DESCENDING = 1;
	
		private int column;
		private int direction;
	
		public void doSort(int column) {
			if (column == this.column) {
				direction = 1 - direction;
			} else {
				this.column = column;
				direction = ASCENDING;
			}
		}
		
		public int compare(Viewer viewer, Object o1, Object o2) {
			int rc = 0;
			IMsn msn1 = (IMsn)o1;
			IMsn msn2 = (IMsn)o2;
			
		    switch (column) {
		    case 0:
		      rc = getComparator().compare(msn1.getMSN(), msn2.getMSN());
		      break;
		    case 1:
		      rc = getComparator().compare(msn1.getAdditional(), msn2.getAdditional());
		      break;
		    }
		    
		    if (direction == DESCENDING) rc = -rc;

		    return rc;
		}
	}
	
    private String NAMESPACE = "ui.jface.configuration.pages.MsnManager";
    private String CONFIG_NAMESPACE = "manager.MsnManager";
    
	private IRuntime m_runtime;
	private TableViewer tv;
	private BooleanFieldEditor allMsns;
	private List msns;
	private List removeMsns;

	public String getParentNodeID() {
		return "BasicSettings".toLowerCase();
	}
	
	public String getNodeID() {
		return "MsnManager".toLowerCase();
	}

	public int getNodePosition() {
		return 0;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null) 
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	protected Control createContents(Composite parent) {
		this.setTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, false));
	
		tv = new TableViewer(c);
		tv.setSorter(new MsnViewerSorter());
		tv.setContentProvider(new MsnContentProvider());
		tv.setLabelProvider(new MsnLabelProvider());
		
		Table t = tv.getTable();
		t.setLayoutData(new GridData(GridData.FILL_BOTH));
		TableColumn tc = new TableColumn(t, SWT.LEFT);
		tc.setText(this.m_i18n.getString(this.getNamespace(), "msn", "label", this.m_language));
		tc.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        ((MsnViewerSorter) tv.getSorter())
	            .doSort(0);
	        tv.refresh();
	      }
	    });
		
		tc = new TableColumn(t, SWT.LEFT);
		tc.setText(this.m_i18n.getString(this.getNamespace(), "alias", "label", this.m_language));
		tc.addSelectionListener(new SelectionAdapter() {
	      public void widgetSelected(SelectionEvent event) {
	        ((MsnViewerSorter) tv.getSorter())
	            .doSort(1);
	        tv.refresh();
	      }
	    });
		
		tv.setInput(this.getMsnList());
		
	    for (int i = 0, n = t.getColumnCount(); i < n; i++) {
	      t.getColumn(i).pack();
	    }

	    t.setHeaderVisible(true);
	    t.setLinesVisible(true);	
	    this.createPopupMenu();
		
	    allMsns = new BooleanFieldEditor(
	    	IJAMConst.GLOBAL_NAMESPACE+SEPARATOR+"detectallmsn",
			this.m_i18n.getString(this.getNamespace(), "detectallmsn", "label", this.m_language),
			c
		);
	    allMsns.setPreferenceStore(this.getPreferenceStore());
	    allMsns.doLoad();
	    
	    this.resizeTable(tv.getTable());
	    
		return c;
	}
	
	private void createPopupMenu() {
		final Table t = tv.getTable();
		
		Menu popUpMenu = new Menu (t.getShell(), SWT.POP_UP);
		
		MenuItem item = new MenuItem (popUpMenu, SWT.PUSH);
		item.setText (this.m_i18n.getString(this.getNamespace(), "addmsn", "label", this.m_language));
		item.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					openMsnWizard("", "");
				}
			}
		);		
		item = new MenuItem (popUpMenu, SWT.PUSH);
		item.setText (this.m_i18n.getString(this.getNamespace(), "removemsn", "label", this.m_language));
		item.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (t.getSelectionCount()>0) {
						TableItem i = t.getSelection()[0];
						removeMsn(i.getText(0), i.getText(1));
					}
				}
			}
		);		
		item = new MenuItem (popUpMenu, SWT.PUSH);
		item.setText (this.m_i18n.getString(this.getNamespace(), "editmsn", "label", this.m_language));
		item.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (t.getSelectionCount()>0) {
						TableItem i = t.getSelection()[0];
						openMsnWizard(i.getText(0), i.getText(1));
					}
				}
			}
		);		
		t.setMenu(popUpMenu);
		
		t.addMouseListener(
			new MouseAdapter() {
				public void mouseDoubleClick(MouseEvent e) {
					if ((e.widget instanceof Table)) {
						if (t.getSelectionCount()>0) {
							if (t.getSelectionCount()>0) {
								TableItem i = ((Table) e.widget).getSelection()[0];
								openMsnWizard(i.getText(0), i.getText(1));
							}
						}
						if (t.getSelectionCount()==0) {
							openMsnWizard("", "");
						}						
					}
				}
			}
		);
		
		t.addKeyListener(
			new KeyAdapter() {
				public void keyPressed(KeyEvent e){
					if (e.character == SWT.DEL) {
						if (t.getSelectionCount()>0) {
							TableItem i = t.getSelection()[0];
							removeMsn(i.getText(0), i.getText(1));
						}
					}
				}	
			}		
		);

	}
	
	private void removeMsn(String msn, String additional) {
		if (MessageDialog.openConfirm(
				Display.getCurrent().getActiveShell(), 
				this.m_i18n.getString(this.getNamespace(), "ctitle", "label", this.m_language),
				this.m_i18n.getString(this.getNamespace(), "confirm", "label", this.m_language)
		)) {
			IMsn oldMsn = this.getRuntime().getCallFactory().createMsn(
					msn,
					additional
				);
				this.msns.remove(oldMsn);
				this.removeMsns.add(oldMsn);
		}
		tv.setInput(this.msns);
		this.resizeTable(tv.getTable());
	}
	
	private void resizeTable(Table t) {
		for (int i=0;i<t.getColumnCount();i++) {
			TableColumn tc = t.getColumn(i);
			tc.pack();
		}
	}
	
	private void openMsnWizard(String msn, String additional) {
	    Display display = DisplayManager.getDefaultDisplay();
		Shell shell = new Shell(display);

	    // Create the dialog
		IMsn oldMsn = this.getRuntime().getCallFactory().createMsn(
			msn,
			additional
		);
	    WizardDialog.setDefaultImage(SWTImageManager.getInstance(this.getRuntime()).get(IJAMConst.IMAGE_KEY_PIM_ICON));
	    MsnWizard msnW = new MsnWizard(oldMsn);
	    WizardDialog dlg = new WizardDialog(shell, msnW);
	    dlg.open();
	    if (dlg.getReturnCode() == WizardDialog.OK) {
	    	IMsn result = msnW.getResult();
	    	this.msns.remove(oldMsn);
	    	this.msns.add(result);
	    }
	    tv.setInput(this.msns);
	    this.resizeTable(tv.getTable());
	}
	
	private List getMsnList() {
		if (this.msns==null || this.msns.size()==0) {
			this.msns = new ArrayList();
			this.removeMsns = new ArrayList();
			String msnList = this.getPreferenceStore().getString(this.CONFIG_NAMESPACE + SEPARATOR + "list");
			if (msnList.trim().length()>0) {
				StringTokenizer st = new StringTokenizer(msnList, ",");
				while (st.hasMoreTokens()) {
					String number = st.nextToken().trim();
					if (number.length()>0) {
						IMsn msn = this.getRuntime().getCallFactory().createMsn(
							this.getPreferenceStore().getString(this.CONFIG_NAMESPACE + SEPARATOR + number+ "_msn"),
							this.getPreferenceStore().getString(this.CONFIG_NAMESPACE + SEPARATOR + number+ "_label")
						);
						this.msns.add(msn);
					}
				}
			}
		}
		return this.msns;
	}
	
	private void createDefaultMsnList() {
		this.msns = new ArrayList();
		this.removeMsns = new ArrayList();
		String msnList = this.getPreferenceStore().getDefaultString(this.CONFIG_NAMESPACE + SEPARATOR + "list");
		if (msnList.trim().length()>0) {
			StringTokenizer st = new StringTokenizer(msnList, ",");
			while (st.hasMoreTokens()) {
				String number = st.nextToken().trim();
				if (number.length()>0) {
					IMsn msn = this.getRuntime().getCallFactory().createMsn(
						this.getPreferenceStore().getString(this.CONFIG_NAMESPACE + SEPARATOR + number+ "_msn"),
						this.getPreferenceStore().getString(this.CONFIG_NAMESPACE + SEPARATOR + number+ "_label")
					);
					this.msns.add(msn);
				}
			}
		}
	}
	
	protected void performDefaults() {
		super.performDefaults();
		this.createDefaultMsnList();
		allMsns.doLoadDefault();
		tv.setInput(this.msns);
	}
	
	public String getConfigNamespace() {
		return this.CONFIG_NAMESPACE;
	}
	
	public boolean performOk() {
		for (int i=0;i<this.removeMsns.size();i++) {
			IMsn m = (IMsn)this.removeMsns.get(i);
			this.getRuntime().getConfigManagerFactory().getConfigManager().removeProperty(this.CONFIG_NAMESPACE, m.getMSN()+"_label");
			this.getRuntime().getConfigManagerFactory().getConfigManager().removeProperty(this.CONFIG_NAMESPACE, m.getMSN()+"_msn");
		}
		
		this.removeMsns.clear();
		StringBuffer list = new StringBuffer();
		for (int i=0;i<this.msns.size();i++) {
			IMsn m = (IMsn)this.msns.get(i);
			this.getPreferenceStore().setValue(this.CONFIG_NAMESPACE+SEPARATOR+m.getMSN()+"_msn", m.getMSN());
			this.getPreferenceStore().setValue(this.CONFIG_NAMESPACE+SEPARATOR+m.getMSN()+"_label", m.getAdditional());
			list.append(m.getMSN());
			list.append(",");
		}
		this.getPreferenceStore().setValue(this.CONFIG_NAMESPACE + SEPARATOR + "list", list.toString());
		this.getPreferenceStore().setValue(IJAMConst.GLOBAL_NAMESPACE+SEPARATOR+"detectallmsn", allMsns.getBooleanValue());
		
		return super.performOk();
	}
}
