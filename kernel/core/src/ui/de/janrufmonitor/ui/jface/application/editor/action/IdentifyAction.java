package de.janrufmonitor.ui.jface.application.editor.action;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IMultiPhoneCaller;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.RepositoryManagerComparator;
import de.janrufmonitor.repository.identify.Identifier;
import de.janrufmonitor.repository.types.IIdentifyCallerRepository;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTExecuter;

public class IdentifyAction extends CallerAction {

	private static String NAMESPACE = "ui.jface.application.editor.action.IdentifyAction";
	
	private IRuntime m_runtime;
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "editor_identify";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public synchronized void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null && v instanceof Viewer) {
			final IStructuredSelection selection = (IStructuredSelection) v.getSelection();
			if (!selection.isEmpty()) {
				if (selection.getFirstElement() instanceof ICaller) {
				
					ProgressMonitorDialog pmd = new ProgressMonitorDialog(DisplayManager.getDefaultDisplay().getActiveShell());	
					try {				
						IRunnableWithProgress r = new IRunnableWithProgress() {
							public void run(IProgressMonitor progressMonitor) {
								progressMonitor.beginTask(getI18nManager()
										.getString(getNamespace(),
												"identifyprogress", "label",
												getLanguage()), IProgressMonitor.UNKNOWN);
								
								progressMonitor.worked(1);

								final ICaller caller = (ICaller) selection.getFirstElement();
								
								List phones = new ArrayList();
								if (caller instanceof IMultiPhoneCaller) {
									phones.addAll(((IMultiPhoneCaller)caller).getPhonenumbers());
								} else {
									phones.add(caller.getPhoneNumber());
								}
								
								String ignoreCM = "";
								IAttribute att = caller.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLERMANAGER);
								if (att != null) {
									ignoreCM = att.getValue();
								}
								
								List activeCMs = getAllActiveCallerManagers(getRuntime(), ignoreCM);
								
								ICaller identifiedCaller = null;		
								// identification only makes sence if there are active CMs or if there are numbers
								if (activeCMs.size()>0 && phones.size()>0) {
									IPhonenumber pn = null;
									for (int i=0;i<phones.size();i++) {
										pn = (IPhonenumber) phones.get(i);
										identifiedCaller = Identifier.identify(getRuntime(), pn, activeCMs);
										if (identifiedCaller!=null) break;
									}
								}	
								progressMonitor.done();
								
								if (identifiedCaller==null) {
									progressMonitor.beginTask(getI18nManager()
											.getString(getNamespace(),
													"failedidentify", "label",
													getLanguage()), IProgressMonitor.UNKNOWN);
									
									
									PropagationFactory.getInstance().fire(
											new Message(Message.INFO,
											getNamespace(),
											"failedidentify",	
											new Exception("Caller with number "+caller.getPhoneNumber()+" not identified...")));
									return;
								}
								
								// remove repository flag and set all numbers
								if (!(identifiedCaller instanceof IMultiPhoneCaller)) {
									identifiedCaller = getRuntime().getCallerFactory().toMultiPhoneCaller(identifiedCaller);
								}					
								((IMultiPhoneCaller)identifiedCaller).setPhonenumbers(phones);
								((IMultiPhoneCaller)identifiedCaller).setPhoneNumber((IPhonenumber) phones.get(0));
								
								IAttributeMap m = getRuntime().getCallerFactory().createAttributeMap();
								m.addAll(caller.getAttributes());
								m.addAll(identifiedCaller.getAttributes());	
								m.remove(IJAMConst.ATTRIBUTE_NAME_CALLERMANAGER);
								identifiedCaller.setAttributes(m);
								
								final ICaller theIdentifiedCaller = identifiedCaller;	
								new SWTExecuter(){

									protected void execute() {
										if (MessageDialog.openConfirm(
												new Shell(DisplayManager.getDefaultDisplay()),
												getI18nManager().getString(getNamespace(), "identify", "label", getLanguage()),
												getI18nManager().getString(getNamespace(), "identify", "description", getLanguage())
											)) {

											ICaller newCaller = openCallerWizard(theIdentifiedCaller);
											if (newCaller!=null) {
												ICallerList list = getRuntime().getCallerFactory().createCallerList(1);
												list.add(caller);
												m_app.getController().deleteElements(list);
												
												list.clear();
												list.add(newCaller);
												
												m_app.getController().addElements(list);
												m_app.updateViews(true);
											}
										}					
									}
									
								}.start();
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
					
				}
			}
		}
	}
	
	private List getAllActiveCallerManagers(IRuntime r, String ignoreManager) {
		List allManagers = r.getCallerManagerFactory().getAllCallerManagers();
		List activeManager = new ArrayList();
		Object o = null;
		ICallerManager cm = null;
		for (int i=0;i<allManagers.size();i++) {
			o = allManagers.get(i);
			if (o!=null && o instanceof ICallerManager) {
				cm = (ICallerManager)o;
				if (cm.isActive() && !cm.getManagerID().equalsIgnoreCase(ignoreManager) && !cm.getManagerID().equalsIgnoreCase("CountryDirectory")) {
					activeManager.add(cm);
				}	
			}
		}
		Collections.sort(activeManager, new RepositoryManagerComparator());
		return activeManager;
	}

	public boolean isEnabled() {
		List l = this.getRuntime().getCallerManagerFactory().getTypedCallerManagers(IIdentifyCallerRepository.class);
		if (l.size()>1) {
			if (this.m_app!=null && this.m_app.getController()!=null) {
				Object o = this.m_app.getController().getRepository();
				if (o instanceof ICallerManager) {
					return ((ICallerManager)o).isSupported(IWriteCallerRepository.class);
				}
			}	
		}
		return false;
	}

}
