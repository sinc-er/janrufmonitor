package de.janrufmonitor.ui.jface.wizards.pages;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IMultiPhoneCaller;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.repository.CallerNotFoundException;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.imexport.ITracker;
import de.janrufmonitor.repository.types.IIdentifyCallerRepository;
import de.janrufmonitor.repository.types.ILocalRepository;
import de.janrufmonitor.repository.types.IReadCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.AbstractTableLabelProvider;
import de.janrufmonitor.ui.swt.SWTExecuter;
import de.janrufmonitor.util.formatter.Formatter;
import de.janrufmonitor.util.string.StringUtils;

public abstract class AbstractPhonesPage extends AbstractPage {

	
	private class CallerContentProvider implements IStructuredContentProvider {

		public Object[] getElements(Object o) {
			List l = (List)o;
			return l.toArray();
		}

		public void dispose() {
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {	
		}
		
	}
	
	private class CallerLabelProvider extends AbstractTableLabelProvider {
		public String getColumnText(Object o, int column) {
			ICaller caller = (ICaller)o;
			
		    switch (column) {
		    case 0:
		      return getCallerToString(caller);
		    }
			return null;
		}
	}
	
	private IPhonenumber[] m_phones;
	private ICallerManager m_currentCallerManager;
	private Button all;
	
	private List m_selectedCallers;
	private IRuntime m_runtime;
	
	public AbstractPhonesPage(IPhonenumber[] phones) {
		super(AbstractPhonesPage.class.getName());

		this.m_phones = phones;
	}
	
	public IPhonenumber[] getResult() {
		if (all!=null && all.getSelection())
			return null;
		
		if (this.m_selectedCallers!=null && this.m_selectedCallers.size()>0) {
			List phoneList = new ArrayList();
			
			
			ICaller c =null;
			for (int i=0,j=this.m_selectedCallers.size();i<j;i++) {
				c = ((ICaller)this.m_selectedCallers.get(i));
				if (c instanceof IMultiPhoneCaller) {
					phoneList.addAll(((IMultiPhoneCaller)c).getPhonenumbers());
				} else {
					phoneList.add(c.getPhoneNumber());
				}
			}
			
			if (phoneList.size()>0) {
				this.m_phones = new IPhonenumber[phoneList.size()];
				for (int i=0,j=phoneList.size();i<j;i++) {
					this.m_phones[i] = (IPhonenumber) phoneList.get(i);
				}				
			}
		}
		
		if (this.m_phones!=null && this.m_phones.length==0)	
			this.m_phones = null;
		
		return this.m_phones;
	}

	public void createControl(Composite parent) {
		setTitle(this.m_i18n.getString(getNamespace(), "title", "label", this.m_language));
		setDescription(this.m_i18n.getString(getNamespace(), "description", "label", this.m_language));

	    Composite c = new Composite(parent, SWT.NONE);
	    c.setLayout(new GridLayout(1, false));
	    c.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	    all = new Button(c, SWT.CHECK);
	    all.setText(
	    	this.m_i18n.getString(this.getNamespace(), "*", "label", this.m_language)	
	    );
	    
	    all.setSelection((this.getCallers().size()==0 ? true : false));
	    
	    Label ln = new Label(c, SWT.LEFT);
	    
	    Composite select = new Composite(c, SWT.NONE);
	    select.setLayout(new GridLayout(3, false));
	    select.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	    ln = new Label(select, SWT.LEFT);
	    ln.setText(this.m_i18n.getString(this.getNamespace(), "allphones", "label", this.m_language));

	    ln = new Label(select, SWT.LEFT);

	    ln = new Label(select, SWT.LEFT);
	    ln.setText(this.m_i18n.getString(this.getNamespace(), "selectedphones", "label", this.m_language));

	    final CheckboxTableViewer call = CheckboxTableViewer.newCheckList(select, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL);
	    call.setLabelProvider(new CallerLabelProvider());
	    call.setContentProvider(new CallerContentProvider());
	    call.setInput(null);
	    
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(getShell());	
		try {				
			IRunnableWithProgress r = new IRunnableWithProgress() {
				public void run(IProgressMonitor progressMonitor) {
					progressMonitor.beginTask(
							m_i18n.getString("ui.jface.wizards.pages.AbstractPhonesPage", "refreshprogress", "label", m_language)
							, IProgressMonitor.UNKNOWN);
					final List callers = new ArrayList();
										
					Thread t = new Thread() {
						public void run() {
							// preload data
							try {
								callers.addAll(getAllCallers());
							} catch (Exception ex) {
								m_logger.log(Level.SEVERE, ex.getMessage(), ex);
							}										
						}
					};
					t.setName("JAM-ContactsRead-Thread-(non-deamon)");
					t.start();
					
					int lastamount = -1;
					do {
						try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
						}
						m_currentCallerManager = CallerReaderFactory.getInstance().getCurrent();
						if (m_currentCallerManager!=null && m_currentCallerManager instanceof ITracker) {
							int leftover = Math.max(0, (((ITracker)m_currentCallerManager).getTotal() - ((ITracker)m_currentCallerManager).getCurrent()));
							if (lastamount==leftover) {
								String msg = m_i18n.getString("ui.jface.wizards.pages.AbstractPhonesPage", 
										"working", "label",
										m_language);
								progressMonitor.subTask(msg);
							} else {
								String msg = m_i18n.getString("ui.jface.wizards.pages.AbstractPhonesPage", 
										"tracking", "label",
										m_language);
								msg = StringUtils.replaceString(msg, "{%1}", Integer.toString(leftover));
								if (m_currentCallerManager instanceof IConfigurable) {
									msg = StringUtils.replaceString(msg, "{%2}", m_i18n.getString(((IConfigurable)m_currentCallerManager).getNamespace(), "title", "label", m_language));
								} else {
									msg = StringUtils.replaceString(msg, "{%2}", m_i18n.getString("ui.jface.wizards.pages.AbstractPhonesPage", 
											"nocallermanager", "label",
											m_language));
								}
								progressMonitor.subTask(msg);
							}
							lastamount = leftover;

							
						} else {
							String msg = m_i18n.getString("ui.jface.wizards.pages.AbstractPhonesPage", 
									"notracking", "label",
									m_language);
								
							if (m_currentCallerManager instanceof IConfigurable) {
								msg = StringUtils.replaceString(msg, "{%1}", m_i18n.getString(((IConfigurable)m_currentCallerManager).getNamespace(), "title", "label", m_language));
							} else {
								msg = StringUtils.replaceString(msg, "{%1}", m_i18n.getString("ui.jface.wizards.pages.AbstractPhonesPage", 
										"nocallermanager", "label",
										m_language));
							}	
							progressMonitor.subTask(msg);
						}		
					} while (t.isAlive());
					
					
				    SWTExecuter tt = new SWTExecuter("ContactsRefresh") {
						protected void execute() {
							call.setInput(callers);
						}
				    };
				    tt.start();
										
					progressMonitor.done();
				}
			};
			pmd.setBlockOnOpen(false);
			pmd.run(true, false, r);

			//ModalContext.run(r, true, pmd.getProgressMonitor(), DisplayManager.getDefaultDisplay());
		} catch (InterruptedException e) {
			m_logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (InvocationTargetException e) {
			m_logger.log(Level.SEVERE, e.getMessage(), e);
		} 	

		GridData gd = new GridData(200,200);
	    call.getTable().setLayoutData(gd);
	    
	    Composite btns = new Composite(select, SWT.NONE);
	    btns.setLayout(new GridLayout(1, false));
	    btns.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		final Button add = new Button(btns, SWT.PUSH);
		add.setText(
			this.m_i18n.getString(this.getNamespace(), "add", "label", this.m_language)
		);
		
		final Button remove = new Button(btns, SWT.PUSH);
		remove.setText(
			this.m_i18n.getString(this.getNamespace(), "remove", "label", this.m_language)
		);
	    
	    final CheckboxTableViewer cselect = CheckboxTableViewer.newCheckList(select, SWT.CHECK | SWT.BORDER);
	    cselect.setLabelProvider(new CallerLabelProvider());
	    cselect.setContentProvider(new CallerContentProvider());
		List selected = this.getCallers();
		cselect.setInput(selected);
		gd = new GridData(200,200);
		cselect.getTable().setLayoutData(gd);

		all.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {				
					add.setEnabled(!all.getSelection());
					remove.setEnabled(!all.getSelection());
					call.setAllGrayed(all.getSelection());
					cselect.setAllGrayed(all.getSelection());
				}
			}
		);
		
		add.setEnabled(!all.getSelection());
		remove.setEnabled(!all.getSelection());
		call.setAllGrayed(all.getSelection());
		cselect.setAllGrayed(all.getSelection());
		
		add.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {				
					Object[] checked = call.getCheckedElements();
					if (checked!=null && checked.length>0) {
						for (int i=0;i<checked.length;i++) {
							if (!m_selectedCallers.contains(checked[i]))
								m_selectedCallers.add(checked[i]);
						}
						cselect.setInput(m_selectedCallers);
					}
				}
			}
		);
		
		remove.addSelectionListener(
			new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {				
					Object[] checked = cselect.getCheckedElements();
					if (checked!=null && checked.length>0) {
						for (int i=0;i<checked.length;i++) {
							if (m_selectedCallers.contains(checked[i]))
								m_selectedCallers.remove(checked[i]);
						}
						cselect.setInput(m_selectedCallers);
					}
				}
			}
		);
		
	    setPageComplete(true);
	    setControl(c);
	}
	
	private List getCallers() {
		if (this.m_selectedCallers==null || this.m_selectedCallers.size()==0) {
			this.m_selectedCallers = new ArrayList();
			
			if (this.m_phones!=null && this.m_phones.length>0) {
				List managers = this.getActiveCallerManagers();
				ICaller c = null;
				for (int i=0;i<this.m_phones.length;i++) {
					c = this.getCaller(this.m_phones[i], managers);
					if (c!=null && !this.m_selectedCallers.contains(c))
						this.m_selectedCallers.add(c);
				}
			}
		}
		return this.m_selectedCallers;
	}

	private List getAllCallers() {
		return CallerReaderFactory.getInstance().getAllCallers();
	}
	
	private String getCallerToString(ICaller caller) {
		if (caller.getPhoneNumber()!=null && caller.getPhoneNumber().isClired()) {
			return 
			this.m_i18n.getString(this.getNamespace(), "clir", "label", this.m_language)
			;
		}
		if (caller.getPhoneNumber()!=null && caller.getPhoneNumber().getCallNumber().equalsIgnoreCase(IJAMConst.INTERNAL_CALL_NUMBER_SYMBOL) && caller.getPhoneNumber().getIntAreaCode().equalsIgnoreCase(IJAMConst.INTERNAL_CALL)) {
			return 
			this.m_i18n.getString(this.getNamespace(), "internal", "label", this.m_language)
			;
		}
		String displayName = Formatter.getInstance(this.getRuntime()).parse(IJAMConst.GLOBAL_VARIABLE_CALLERNAME, caller) + ", "+Formatter.getInstance(this.getRuntime()).parse(IJAMConst.GLOBAL_VARIABLE_CALLERNUMBER, caller.getPhoneNumber());
		// remove CRLF
		displayName = StringUtils.replaceString(displayName, IJAMConst.CRLF, " ");
		return displayName;
	}
	
	private List getActiveCallerManagers() {
		List managers = this.getRuntime().getCallerManagerFactory().getAllCallerManagers();
		
		ICallerManager man = null;
		for (int i=managers.size()-1;i>=0;i--) {
			man = (ICallerManager) managers.get(i);
			// removed: 2009/04/30: if (!man.isActive() || !(man.isSupported(IReadCallerRepository.class) && man.isSupported(IWriteCallerRepository.class))){
			if (!man.isActive() || !man.isSupported(IReadCallerRepository.class)){
				managers.remove(i);
			}
		}
		return managers;
	}
	
	private ICaller getCaller(IPhonenumber pn, List managers) {
		if (pn.isClired()) {
			return this.getRuntime().getCallerFactory().createCaller(
					this.getRuntime().getCallerFactory().createName("", ""),
					this.getRuntime().getCallerFactory().createPhonenumber(true)
				);
		}
		if (pn.getIntAreaCode().equalsIgnoreCase(IJAMConst.INTERNAL_CALL) && pn.getCallNumber().equalsIgnoreCase(IJAMConst.INTERNAL_CALL_NUMBER_SYMBOL)) {
			return (this.getRuntime().getCallerFactory().createCaller(
				this.getRuntime().getCallerFactory().createName("", ""),
				this.getRuntime().getCallerFactory().createPhonenumber(IJAMConst.INTERNAL_CALL, "", IJAMConst.INTERNAL_CALL_NUMBER_SYMBOL)
			));
		}

		if (managers==null)
			managers = this.getActiveCallerManagers();
		
		ICallerManager man = null;
		List remoteManagers = new ArrayList();
		for (int i=0;i<managers.size();i++) {
			man = (ICallerManager) managers.get(i);
			// first only check local repository managers for performance
			if (!(man instanceof ILocalRepository)) {
				remoteManagers.add(man);
			} else {
				try {
					if (man!=null && man.isActive() && man.isSupported(IIdentifyCallerRepository.class))
						return ((IIdentifyCallerRepository)man).getCaller(pn);
				} catch (CallerNotFoundException e) {
					this.m_logger.warning(e.getMessage());
				}
			}
		}
		// check for all non-local repositorymanagers
		for (int i=0;i<remoteManagers.size();i++) {
			man = (ICallerManager) remoteManagers.get(i);
			try {
				if (man!=null && man.isActive() && man.isSupported(IIdentifyCallerRepository.class))
					return ((IIdentifyCallerRepository)man).getCaller(pn);
			} catch (CallerNotFoundException e) {
				this.m_logger.warning(e.getMessage());
			}
		}
		return null;
	}

	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}
}
