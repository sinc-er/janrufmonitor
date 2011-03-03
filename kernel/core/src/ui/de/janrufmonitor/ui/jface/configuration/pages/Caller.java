package de.janrufmonitor.ui.jface.configuration.pages;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.RepositoryManagerComparator;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.AbstractTableLabelProvider;
import de.janrufmonitor.ui.jface.configuration.AbstractConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;

public class Caller extends AbstractConfigPage {

	private class CallerManagerContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object o) {
			List l = (List)o;
			return l.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {	
		}
		
	}
	
	private class CallerManagerLabelProvider extends AbstractTableLabelProvider {

		public String getColumnText(Object o, int column) {
			String id = (String)o;
			
		    switch (column) {
		    case 0: {
		    	ICallerManager cm = getRuntime().getCallerManagerFactory().getCallerManager(id);
		    	if (cm!=null && cm instanceof IConfigurable) {
		    		String ns = ((IConfigurable)cm).getNamespace();
		    		return getI18nManager().getString(
		    			ns, 
						"title",
						"label",
						getLanguage()
		    		);
		    	}
		    }
		    }
			return null;
		}		
	}

    private String NAMESPACE = "ui.jface.configuration.pages.Caller";
    
	private IRuntime m_runtime;
	private List managers;
	
	public String getParentNodeID() {
		return IConfigPage.ROOT_NODE;
	}
	
	public String getNodeID() {
		return IConfigPage.CALLER_NODE;
	}

	public int getNodePosition() {
		return 4;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) 
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	public String getConfigNamespace() {
		return this.NAMESPACE;
	}
	
	public boolean performOk() {
		if (this.managers!=null && this.managers.size()>0) {
			IConfigurable conf = null;
			ICallerManager o;
			for (int i=0;i<managers.size();i++) {
				o = this.getRuntime().getCallerManagerFactory().getCallerManager((String)managers.get(i));
				if (o!=null && o instanceof IConfigurable) {
					conf = (IConfigurable) o;
					this.getPreferenceStore().setValue(
						conf.getNamespace() + SEPARATOR + "priority",
						i + 10
					);
				}
			}
		}
		
		boolean ok = super.performOk();
		if (ok) {
			// notify all changed namespaces
			if (this.managers!=null && this.managers.size()>0) {
				IConfigurable conf = null;
				ICallerManager o = null;
				for (int i=0;i<managers.size();i++) {
					o = this.getRuntime().getCallerManagerFactory().getCallerManager((String)managers.get(i));
					if (o!=null && o instanceof IConfigurable) {
						conf = (IConfigurable) o;
						this.getRuntime().getConfigurableNotifier().notifyByNamespace(
							conf.getNamespace()
						);
					}
				}
			}
		}
		return ok;
	}

	protected Control createContents(Composite parent) {
		this.setTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));
		
		final Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, false));
		
		new Label(c, SWT.NONE).setText(
			getI18nManager().getString(
				getNamespace(),
				"current",
				"label",
				getLanguage()
			)		
		);
		
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.widthHint = 150;
		
	    final TableViewer current = new TableViewer(c, SWT.BORDER | SWT.V_SCROLL);
	    current.setLabelProvider(new CallerManagerLabelProvider());
	    current.setContentProvider(new CallerManagerContentProvider());
		current.getTable().setLayoutData(gd);
	   	this.managers = this.getInitialManagers();
		current.setInput(managers);
		

		Composite buttonsArea2 = new Composite(c, SWT.NONE);
		buttonsArea2.setLayout(new GridLayout(2, false));

		Button up = new Button(buttonsArea2, SWT.PUSH);
		up.setText(
			getI18nManager().getString(
				getNamespace(),
				"up",
				"label",
				getLanguage()
			)						
		);
		
		up.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {						
					IStructuredSelection s = (IStructuredSelection) current.getSelection();
					if (!s.isEmpty()) {
						List currentCols = getCurrentManagers();
						String move_col = (String) s.getFirstElement();
						
						int pos = -1;
						for (int i=0;i<currentCols.size();i++) {
							if (move_col.equalsIgnoreCase((String) currentCols.get(i))) pos = i-1;
						}
						currentCols.remove(move_col);
						currentCols.add(Math.max(0, pos), move_col);
						
						current.setInput(currentCols);
						setCurrentManagers(currentCols);
					}
				}
			}	
		);
	
		Button down = new Button(buttonsArea2, SWT.PUSH);
		down.setText(
			getI18nManager().getString(
				getNamespace(),
				"down",
				"label",
				getLanguage()
			)						
		);
		down.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {						
					IStructuredSelection s = (IStructuredSelection) current.getSelection();
					if (!s.isEmpty()) {
						List currentCols = getCurrentManagers();
						String move_col = (String) s.getFirstElement();
						
						int pos = -1;
						for (int i=0;i<currentCols.size();i++) {
							if (move_col.equalsIgnoreCase((String) currentCols.get(i))) pos = i+1;
						}
						currentCols.remove(move_col);
						currentCols.add(Math.min(currentCols.size(), pos), move_col);
						current.setInput(currentCols);
						setCurrentManagers(currentCols);
					}
				}
			}	
		);
		
		return c;
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
	
	private List getInitialManagers() {
		List ids = this.getRuntime().getCallerManagerFactory().getAllCallerManagers();
		Collections.sort(ids, new RepositoryManagerComparator());
		List idList = new ArrayList();
		String manager = null;
		for (int i=0;i<ids.size();i++){
			manager = ((ICallerManager)ids.get(i)).getManagerID();
			if (!manager.startsWith("TelephoneSystemCallerManager")) {
				idList.add(manager);
			}
		}
		return idList;
	}
	
	private List getCurrentManagers() {
		return this.managers;
	}

	private void setCurrentManagers(List l) {
		this.managers = l;
	}
	
}
