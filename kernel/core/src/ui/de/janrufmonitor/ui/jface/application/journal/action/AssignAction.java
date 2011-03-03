package de.janrufmonitor.ui.jface.application.journal.action;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.repository.ICallManager;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.types.IWriteCallRepository;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.IExtendedApplicationController;
import de.janrufmonitor.ui.jface.wizards.JournalCallerWizard;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTExecuter;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class AssignAction extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.journal.action.AssignAction";

	private IRuntime m_runtime;

	public AssignAction() {
		super();
		this.setText(this.getI18nManager().getString(this.getNamespace(),
				"title", "label", this.getLanguage()));
	}

	public IRuntime getRuntime() {
		if (this.m_runtime == null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "journal_assign";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v != null && v instanceof TableViewer) {
			IStructuredSelection selection = (IStructuredSelection) v
					.getSelection();
			if (!selection.isEmpty()) {
				final ICall call = (ICall) selection.getFirstElement();
				ICaller caller = call.getCaller();

				final ICaller newCaller = openCallerWizard(caller);
				if (newCaller != null) {

					int l_status = SWT.NO;
					if (!newCaller.getPhoneNumber().isClired()) {
						int style = SWT.APPLICATION_MODAL
						| SWT.YES | SWT.NO;
						MessageBox messageBox = new MessageBox(
								new Shell(DisplayManager.getDefaultDisplay()),
								style);
						messageBox
								.setMessage(getI18nManager()
										.getString(
												getNamespace(),
												"setall",
												"label",
												getLanguage()));
						l_status = messageBox.open();
					}
				
					final int status = l_status;
								
					ProgressMonitorDialog pmd = new ProgressMonitorDialog(
							DisplayManager.getDefaultDisplay().getActiveShell());
					try {
						IRunnableWithProgress r = new IRunnableWithProgress() {
							public void run(IProgressMonitor progressMonitor) {
								progressMonitor.beginTask(getI18nManager()
										.getString(getNamespace(),
												"assignprogress", "label",
												getLanguage()),
										IProgressMonitor.UNKNOWN);

								progressMonitor.worked(1);

								call.setCaller(newCaller);

								if (status == SWT.NO && m_app.getController() instanceof IExtendedApplicationController) {
									// added 2009/12/31: only update single entry
									progressMonitor.setTaskName(getI18nManager()
											.getString(getNamespace(),
													"assignprogress", "label",
													getLanguage()));
									((IExtendedApplicationController)m_app.getController()).updateElement(call, false);
								} else {
									progressMonitor.setTaskName(getI18nManager()
											.getString(getNamespace(),
													"assignall", "label",
													getLanguage()));
									m_app.getController().updateElement(call);
								}

								progressMonitor.done();
							}
						};
						pmd.setBlockOnOpen(false);
						pmd.run(true, false, r);

						// ModalContext.run(r, true, pmd.getProgressMonitor(),
						// DisplayManager.getDefaultDisplay());
					} catch (InterruptedException e) {
						m_logger.log(Level.SEVERE, e.getMessage(), e);
					} catch (InvocationTargetException e) {
						m_logger.log(Level.SEVERE, e.getMessage(), e);
					}
					
					if (!newCaller.getPhoneNumber().isClired()) {
						new SWTExecuter() {
	
							protected void execute() {
								int style = SWT.APPLICATION_MODAL
										| SWT.YES | SWT.NO;
								MessageBox messageBox = new MessageBox(
										new Shell(DisplayManager
												.getDefaultDisplay()),
										style);
								messageBox
										.setMessage(getI18nManager()
												.getString(
														getNamespace(),
														"addressbookconfirm",
														"label",
														getLanguage()));
								if (messageBox.open() == SWT.YES) {
									List cpms = getRuntime()
											.getCallerManagerFactory()
											.getTypedCallerManagers(
													IWriteCallerRepository.class);
									ICallerManager cmgr = null;
									for (int i = 0; i < cpms.size(); i++) {
										cmgr = (ICallerManager) cpms
												.get(i);
										if (cmgr.isActive()
												&& cmgr
														.isSupported(IWriteCallerRepository.class))
											((IWriteCallerRepository) cmgr)
													.updateCaller(newCaller);
									}
								}
								m_app.updateViews(true);
							}
						}.start();
					} else {
						m_app.updateViews(true);
					}

					//this.m_app.updateViews(true);
				}
			}
		}
	}

	private ICaller openCallerWizard(ICaller caller) {
		Display display = DisplayManager.getDefaultDisplay();
		Shell shell = new Shell(display);

		WizardDialog.setDefaultImage(SWTImageManager.getInstance(
				this.getRuntime()).get(IJAMConst.IMAGE_KEY_PIM_ICON));
		JournalCallerWizard callerWiz = new JournalCallerWizard(caller);
		WizardDialog dlg = new WizardDialog(shell, callerWiz);
		dlg.open();
		if (dlg.getReturnCode() == WizardDialog.OK) {
			return callerWiz.getResult();
		}
		return null;
	}

	public boolean isEnabled() {
		if (this.m_app != null && this.m_app.getController() != null) {
			Object o = this.m_app.getController().getRepository();
			if (o instanceof ICallManager) {
				return ((ICallManager)o).isSupported(IWriteCallRepository.class);
			}
		}
		return false;
	}
}
