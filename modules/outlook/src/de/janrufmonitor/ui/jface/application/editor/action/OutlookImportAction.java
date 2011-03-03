package de.janrufmonitor.ui.jface.application.editor.action;

import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.repository.CallerNotFoundException;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.OutlookContactManager;
import de.janrufmonitor.repository.OutlookTransformer;
import de.janrufmonitor.repository.filter.AttributeFilter;
import de.janrufmonitor.repository.filter.FilterType;
import de.janrufmonitor.repository.filter.IFilter;
import de.janrufmonitor.repository.types.IIdentifyCallerRepository;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.editor.EditorConfigConst;
import de.janrufmonitor.ui.jface.application.editor.EditorFilterManager;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.util.formatter.Formatter;
import de.janrufmonitor.util.string.StringUtils;

public class OutlookImportAction extends AbstractAction implements EditorConfigConst {
	
	private static String NAMESPACE = "ui.jface.application.editor.action.OutlookImportAction";
	
	private IRuntime m_runtime;
	public OutlookImportAction() {
		super();
		this.setText(
			this.getI18nManager().getString(
				this.getNamespace(),
				"title",
				"label",
				this.getLanguage()
			)
		);
	}
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}
	
	public boolean isEnabled() {
		ICallerManager c = getRuntime().getCallerManagerFactory().getCallerManager("OutlookCallerManager");
		return !(c!=null && c.isActive());
	}

	public String getID() {
		return "editor_outlookimport";
	}

	public String getNamespace() {
		return NAMESPACE;
	}
	
	public void run() {
		String filter = this.m_app.getApplication().getConfiguration().getProperty("filter", "");
		EditorFilterManager efm = new EditorFilterManager();
		final IAttribute category = getRuntime().getCallerFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_CATEGORY, "");
		
		if (filter.length()>0) {
			String cat = null;
			IFilter[] filters = efm.getFiltersFromString(filter);
			for (int z=0;z<filters.length;z++) {
				if (filters[z].getType().equals(FilterType.ATTRIBUTE)) {
					AttributeFilter cf = ((AttributeFilter) filters[z]);
					IAttributeMap m = cf.getAttributeMap();
					if (m!=null && m.size()>0) {
						Iterator it = m.iterator();
						IAttribute a = null;
						while(it.hasNext()) {
							a = (IAttribute) it.next();							
							if (a.getName().equalsIgnoreCase(IJAMConst.ATTRIBUTE_NAME_CATEGORY)) {												
								cat = a.getValue();
							}
						}
					}
				}
			}
			if (cat!=null) {
				int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
				MessageBox messageBox = new MessageBox(new Shell(DisplayManager
						.getDefaultDisplay()), style);
				String text = this.getI18nManager().getString(
						"ui.jface.application.editor.action.ImportAction", "assigncategoryconfirm", "label",
						this.getLanguage());
				text = StringUtils.replaceString(text, "{%1}", cat);
				messageBox.setMessage(text);
				if (messageBox.open() == SWT.YES) {
					category.setValue(cat);
				} 
			}							
		}
		
		// determine subfolders
		OutlookTransformer ot = new OutlookTransformer();
		
		List folders = ot.getAllContactFolders();
		if (folders.size()>0) {
			int itemCount = 0;
			String folder = null;
			for (int i=0,j=folders.size();i<j;i++) {
				folder = (String)folders.get(i);
				itemCount = ot.getContactCount(folder);
				if (itemCount>0) {
					getRuntime().getConfigManagerFactory().getConfigManager().setProperty(OutlookContactManager.NAMESPACE, "subfolder_"+folder, "true");
				}
			}
			getRuntime().getConfigManagerFactory().getConfigManager().saveConfiguration();
		}
		
		ProgressMonitorDialog pmd = new ProgressMonitorDialog(DisplayManager.getDefaultDisplay().getActiveShell());	
		try {				
			IRunnableWithProgress r = new IRunnableWithProgress() {
				public void run(IProgressMonitor progressMonitor) {
					progressMonitor.beginTask(getI18nManager()
							.getString(getNamespace(),
									"importprogress", "label",
									getLanguage()), IProgressMonitor.UNKNOWN);
					
					progressMonitor.worked(1);

					OutlookTransformer otf = new OutlookTransformer();
					
					ICallerList callers = otf.getCallerListFromAllContacts();
					
					progressMonitor.setTaskName(getI18nManager()
							.getString(getNamespace(),
									"reduce", "label",
									getLanguage()));
					
					otf.removeCallerManagerID(callers);
					
					// determine duplicates
					
					String currentCallerManagerID = m_app.getApplication().getConfiguration().getProperty(CFG_REPOSITORY);
					if (currentCallerManagerID != null && currentCallerManagerID.trim().length()>0) {
						ICallerManager currentCallerManager = getRuntime().getCallerManagerFactory().getCallerManager(currentCallerManagerID);
						if (currentCallerManager!=null && currentCallerManager.isSupported(IWriteCallerRepository.class) && currentCallerManager.isSupported(IIdentifyCallerRepository.class)) {
							ICallerList addCallers = getRuntime().getCallerFactory().createCallerList();
//							ICallerList removeCallers = getRuntime().getCallerFactory().createCallerList();
							ICaller currentCaller = null;
							String text = null;
							for (int i=0,j=callers.size();i<j;i++) {
//								ICaller testCaller = null;
								currentCaller = callers.get(i);
								text = getI18nManager()
								.getString(getNamespace(),
										"check", "label",
										getLanguage());
								
								text = StringUtils.replaceString(text, "{%1}", Formatter.getInstance(getRuntime()).parse("%a:ln%, %a:fn%", currentCaller));
								progressMonitor.setTaskName(text);
								try {
									Thread.sleep(100);
								} catch (InterruptedException e1) {
								}
								
								try {
									//testCaller = ((IIdentifyCallerRepository)currentCallerManager).getCaller(currentCaller.getPhoneNumber());
									((IIdentifyCallerRepository)currentCallerManager).getCaller(currentCaller.getPhoneNumber());
								} catch (CallerNotFoundException e) {
									if (category!=null && category.getValue().length()>0)
										currentCaller.setAttribute(category);
									
									addCallers.add(currentCaller);
								}
//								if (testCaller!=null) {
//									removeCallers.add(testCaller);
//								}
							}
							progressMonitor.setTaskName(getI18nManager()
									.getString(getNamespace(),
											"add", "label",
											getLanguage()));
							
							try {
								Thread.sleep(1250);
							} catch (InterruptedException e1) {
							}
							
//							if (removeCallers.size()>0)
//								m_app.getController().deleteElements(removeCallers);
							m_app.getController().addElements(addCallers);
						}
						
					} else {
						progressMonitor.setTaskName(getI18nManager()
								.getString(getNamespace(),
										"add", "label",
										getLanguage()));
						
						
						m_app.getController().addElements(callers);
					}

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
		m_app.updateViews(true);
		return;
	}
}

