package de.janrufmonitor.ui.jface.application.journal.action;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.repository.ICallManager;
import de.janrufmonitor.repository.imexport.ICallImporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.repository.imexport.ImExportFactory;
import de.janrufmonitor.repository.imexporter.OldDatFileCallImporter;
import de.janrufmonitor.repository.types.IWriteCallRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.journal.Journal;
import de.janrufmonitor.ui.jface.application.journal.JournalConfigConst;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTExecuter;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.string.StringUtils;

public class ImportAction extends AbstractAction implements JournalConfigConst {

	private static String NAMESPACE = "ui.jface.application.journal.action.ImportAction";

	private IRuntime m_runtime;

	private boolean suppressDialogs;

	public ImportAction() {
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
		return "journal_import";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void setSupressDialogs(boolean s) {
		this.suppressDialogs = s;
	}

	public void run(String[] filenames) {
		try {

			String filename = filenames[0];
			TableViewer viewer = (TableViewer) this.m_app.getApplication()
					.getViewer();
			Cursor c = new Cursor(viewer.getTable().getDisplay(),
					SWT.CURSOR_WAIT);

			if (!filename.endsWith(this.m_app.getApplication()
					.getConfiguration().getProperty(CFG_OLD_JOURNAL,
							"ajournal.dat"))) {

				File f = new File(filename);

				String ext = f.getName();
				IImExporter ie = ImExportFactory.getInstance()
						.getImporterByExtension(ext);
				if (ie == null && ext.lastIndexOf(".") >= 0) {
					ext = "*"+ext.substring(ext.lastIndexOf("."));
					ie = ImExportFactory.getInstance().getImporterByExtension(
							ext);
					if (ie == null) {
						PropagationFactory.getInstance().fire(
								new Message(Message.ERROR, 
										getI18nManager().getString(Journal.NAMESPACE,
										"title", "label",
										getLanguage()), 
										new Exception(getI18nManager().getString(
												getNamespace(),
												"error", "description",
												getLanguage()))),
								"Tray");
						
						new SWTExecuter() {					
							protected void execute() {
								MessageDialog.openError(DisplayManager
										.getDefaultDisplay().getActiveShell(),
										getI18nManager().getString(
												getNamespace(), "error",
												"label", getLanguage()),
										getI18nManager().getString(
												getNamespace(), "error",
												"description", getLanguage()));
								m_logger.warning("Import of data failed.");
							
							}

						}.start();
					}
				}

				if (ie!=null && ie.getMode()!=IImExporter.CALL_MODE) return;
				
				final IImExporter imp = ie;
				imp.setFilename(filename);

				viewer.getTable().getShell().setCursor(c);

				ProgressMonitorDialog pmd = new ProgressMonitorDialog(
						DisplayManager.getDefaultDisplay().getActiveShell());
				try {
					IRunnableWithProgress r = new IRunnableWithProgress() {
						public void run(IProgressMonitor progressMonitor) {
							progressMonitor.beginTask(getI18nManager()
									.getString(getNamespace(),
											"importprogress", "label",
											getLanguage()),
									IProgressMonitor.UNKNOWN);

							progressMonitor.worked(1);

							final ICallList importedCalls = getRuntime()
									.getCallFactory().createCallList();

							if (imp.getMode() == IImExporter.CALL_MODE) {
								importedCalls.add(((ICallImporter) imp)
										.doImport());
								m_app.getController()
										.addElements(importedCalls);								
							}
							
							if (importedCalls.size() > 0) {
								
								final String msg = StringUtils.replaceString(
										getI18nManager().getString(
												getNamespace(), "success",
												"description", getLanguage()),
										"{%1}", Integer.toString(importedCalls
												.size()));

								PropagationFactory.getInstance().fire(
										new Message(Message.INFO, 
												getI18nManager().getString(Journal.NAMESPACE,
												"title", "label",
												getLanguage()), 
												new Exception(msg)),
										"Tray");
								
								new SWTExecuter() {
									protected void execute() {
										m_app.updateViews(true);
									}

								}.start();
							} else {

								PropagationFactory.getInstance().fire(
										new Message(Message.ERROR, 
												getI18nManager().getString(Journal.NAMESPACE,
												"title", "label",
												getLanguage()), 
												new Exception(getI18nManager().getString(
														getNamespace(),
														"error", "description",
														getLanguage()))),
										"Tray");	
								
								new SWTExecuter() {

									protected void execute() {
										MessageDialog.openError(DisplayManager
												.getDefaultDisplay()
												.getActiveShell(),
												getI18nManager().getString(
														getNamespace(),
														"error", "label",
														getLanguage()),
												getI18nManager().getString(
														getNamespace(),
														"error", "description",
														getLanguage()));
										m_logger
												.warning("Import of data failed.");
										// m_app.getController().addElements(importedCalls);
										m_app.updateViews(true);
									}

								}.start();
							}

							progressMonitor.done();
						}
					};
					pmd.setBlockOnOpen(false);
					pmd.run(true, false, r);

					// ModalContext.run(r, true,
					// pmd.getProgressMonitor(),
					// DisplayManager.getDefaultDisplay());
				} catch (InterruptedException e) {
					throw e;
				} catch (InvocationTargetException e) {
					throw e;
				}

				viewer.getTable().getShell().setCursor(null);
				c.dispose();

			} else {
				// do ajournal.dat migration
				int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
				MessageBox messageBox = new MessageBox(new Shell(DisplayManager
						.getDefaultDisplay()), style);
				messageBox.setMessage(this.getI18nManager().getString(
						this.getNamespace(), "migrationconfirm", "label",
						this.getLanguage()));
				if (messageBox.open() == SWT.YES) {
					viewer.getTable().getShell().setCursor(c);

					IImExporter imp = ImExportFactory.getInstance()
							.getImporter("OldDatFileCallImporter");
					if (imp != null && (imp instanceof OldDatFileCallImporter)) {
						((OldDatFileCallImporter) imp)
								.setDatePattern(this.m_app.getApplication()
										.getConfiguration().getProperty(
												CFG_OLD_DATE,
												"dd.MM.yyyy HH:mm:ss"));
						((OldDatFileCallImporter) imp).setFilename(filename);

						ICallList importedCalls = ((OldDatFileCallImporter) imp)
								.doImport();
						if (importedCalls != null) {
							this.m_app.getController().addElements(
									importedCalls);

							String msg = getI18nManager().getString(
									getNamespace(), "success", "description",
									getLanguage());

							msg = StringUtils.replaceString(msg, "{%1}",
									Integer.toString(importedCalls.size()));

							if (!suppressDialogs)
								MessageDialog.openInformation(new Shell(
										DisplayManager.getDefaultDisplay()),
										getI18nManager().getString(
												getNamespace(), "success",
												"label", getLanguage()), msg);
							m_app.updateViews(true);
						}
					}

					viewer.getTable().getShell().setCursor(null);
					c.dispose();
				} else {
					return;
				}
			}
		} catch (Exception ex) {
			this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);
			PropagationFactory.getInstance().fire(
					new Message(Message.ERROR, getNamespace(), "error", ex));
		}
	}

	public void run() {
		FileDialog dialog = new FileDialog(new Shell(DisplayManager
				.getDefaultDisplay()), SWT.OPEN);
		dialog.setText(this.getI18nManager().getString(this.getNamespace(),
				"title", "label", this.getLanguage()));
		
		List ids = ImExportFactory.getInstance().getAllImporterIds(
				IImExporter.CALL_MODE);

		String[] filternames = new String[ids.size()];
		String[] extensions = new String[ids.size()];
		IImExporter ie = null;
		for (int i = 0; i < ids.size(); i++) {
			ie = ImExportFactory.getInstance().getImporter((String) ids.get(i));
			filternames[i] = ie.getFilterName();
			extensions[i] = ie.getExtension();
		}

		dialog.setFilterNames(filternames);
		dialog.setFilterExtensions(extensions);
		String filter = getRuntime().getConfigManagerFactory().getConfigManager().getProperty(getNamespace(), "lastopeneddir");
		if (filter == null || filter.length() == 0 || !new File(filter).exists())
			filter = PathResolver.getInstance(getRuntime()).getUserDataDirectory();
		
		dialog.setFilterPath(filter);
		
		String filename = dialog.open();
		if (filename == null)
			return;

		filter = new File(filename).getParentFile().getAbsolutePath();
		getRuntime().getConfigManagerFactory().getConfigManager().setProperty(getNamespace(), "lastopeneddir", filter);
		this.run(new String[] { filename });

		// this.m_app.updateViews(true);
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