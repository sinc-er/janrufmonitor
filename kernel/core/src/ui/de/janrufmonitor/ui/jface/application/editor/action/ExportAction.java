package de.janrufmonitor.ui.jface.application.editor.action;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.repository.imexport.ICallerExporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.repository.imexport.ImExportFactory;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.ApplicationImageDescriptor;
import de.janrufmonitor.ui.jface.application.editor.Editor;
import de.janrufmonitor.ui.jface.application.editor.EditorController;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTExecuter;
import de.janrufmonitor.util.io.PathResolver;

public class ExportAction extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.editor.action.ExportAction";
	
	private IRuntime m_runtime;

	public ExportAction() {
		super();
		this.setText(
			this.getI18nManager().getString(
				this.getNamespace(),
				"title",
				"label",
				this.getLanguage()
			)
		);
		this.setImageDescriptor(new ApplicationImageDescriptor(
			PathResolver.getInstance(this.getRuntime()).getImageDirectory() + "export.gif"
		));
	}
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "editor_export";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		try {
			FileDialog dialog = new FileDialog (new Shell(DisplayManager.getDefaultDisplay()), SWT.SAVE);
			dialog.setText(this.getI18nManager().getString(this.getNamespace(), "title", "label", this.getLanguage()));
					
			List ids = ImExportFactory.getInstance().getAllExporterIds(IImExporter.CALLER_MODE);		
			String[] filternames = new String[ids.size()];
			String[] extensions = new String[ids.size()];
			IImExporter ie = null;
			for (int i=0;i<ids.size();i++){
				ie = ImExportFactory.getInstance().getExporter((String)ids.get(i));
				filternames[i]=ie.getFilterName();
				extensions[i]=ie.getExtension();
			}
	
			dialog.setFilterNames(filternames);
			dialog.setFilterExtensions(extensions);
			
			String filter = getRuntime().getConfigManagerFactory().getConfigManager().getProperty(getNamespace(), "lastopeneddir");
			if (filter == null || filter.length() == 0 || !new File(filter).exists())
				filter = PathResolver.getInstance(getRuntime()).getDataDirectory();
			dialog.setFilterPath(filter);
			
			final String filename = dialog.open();
			if (filename==null) return;
			
			filter = new File(filename).getParentFile().getAbsolutePath();
			getRuntime().getConfigManagerFactory().getConfigManager().setProperty(getNamespace(), "lastopeneddir", filter);
			
			String ext = "";
			for (int i=0;i<ids.size();i++){
				final IImExporter exp = ImExportFactory.getInstance().getExporter((String)ids.get(i));
				ext = exp.getExtension().substring(1);
				if (filename.toLowerCase().endsWith(ext) && exp.getMode() == IImExporter.CALLER_MODE) {
					try {
						ProgressMonitorDialog pmd = new ProgressMonitorDialog(DisplayManager.getDefaultDisplay().getActiveShell());	

						IRunnableWithProgress r = new IRunnableWithProgress() {
							public void run(IProgressMonitor progressMonitor) {
								progressMonitor.beginTask(getI18nManager()
										.getString(getNamespace(),
												"exportprogress", "label",
												getLanguage()), IProgressMonitor.UNKNOWN);
							
								((ICallerExporter)exp).setCallerList(
										((EditorController)m_app.getController()).getCallerList()
									);
									
								exp.setFilename(filename);
								
								if (((ICallerExporter)exp).doExport()) {
									progressMonitor.done();
						
									PropagationFactory.getInstance().fire(
											new Message(Message.INFO, 
													getI18nManager().getString(Editor.NAMESPACE,
													"title", "label",
													getLanguage()), 
													new Exception(getI18nManager()
															.getString(getNamespace(),
																	"success", "description",
																	getLanguage()))),
											"Tray");
																				
								} else {
									progressMonitor.done();
									new SWTExecuter() {

										protected void execute() {
											MessageDialog.openError(
													DisplayManager.getDefaultDisplay().getActiveShell(),
													getI18nManager()
													.getString(getNamespace(),
															"error", "label",
															getLanguage()),
													getI18nManager()
													.getString(getNamespace(),
															"error", "description",
															getLanguage())
												);
												m_logger.warning("Export of contacts failed.");
												
										}
										
									}.start();
								}
							}
						};
						pmd.setBlockOnOpen(false);
						pmd.run(true, false, r);
					} catch (InterruptedException e) {
						throw e;
					} catch (InvocationTargetException e) {
						throw e;
					}
					//m_app.updateViews(false);
				}
			}
		} catch (Exception ex)  {
			this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);
			PropagationFactory.getInstance().fire(new Message(Message.ERROR, getNamespace(), "error", ex));			
		}
	}
		
}
