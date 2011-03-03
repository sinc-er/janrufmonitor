package de.janrufmonitor.ui.jface.application.editor.action;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.filter.AttributeFilter;
import de.janrufmonitor.repository.filter.FilterType;
import de.janrufmonitor.repository.filter.IFilter;
import de.janrufmonitor.repository.imexport.ICallerImporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.repository.imexport.ITracker;
import de.janrufmonitor.repository.imexport.ImExportFactory;
import de.janrufmonitor.repository.imexporter.OldDatFileCallerImporter;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.editor.Editor;
import de.janrufmonitor.ui.jface.application.editor.EditorConfigConst;
import de.janrufmonitor.ui.jface.application.editor.EditorFilterManager;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTExecuter;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.string.StringUtils;

public class ImportAction extends AbstractAction implements EditorConfigConst {

	private class ImportThread implements Runnable {

		ICallerImporter m_ci;
		ICallerList m_cl;
		IAttribute m_cat;
		
		public ImportThread(ICallerImporter ci, IAttribute category) {
			this.m_ci = ci;
			this.m_cat = category;
		}
		
		public void run() {
			if (this.m_ci!=null) { 
				this.m_cl = this.m_ci.doImport();
				if (m_cat!=null) {
					if (m_logger.isLoggable(Level.INFO))
						m_logger.info("Assigning category <"+this.m_cat+"> to all callers.");
					for (int i=0, j=this.m_cl.size();i<j;i++) {
						//this.m_cl.get(i).getAttributes().remove(this.m_cat.getName());
						this.m_cl.get(i).setAttribute(this.m_cat);
					}
				}
			}
			
		}
		public ICallerList getResult() {
			return this.m_cl;
		}
		
	}
	
	private static String NAMESPACE = "ui.jface.application.editor.action.ImportAction";

	private IRuntime m_runtime;


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
		return "editor_import";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void setSupressDialogs(boolean s) {

	}

	public void run(String[] filenames) {
		List ids = ImExportFactory.getInstance().getAllImporterIds(
				IImExporter.CALLER_MODE);

		String filename = null;
		for (int h = 0; h < filenames.length; h++) {
			filename = filenames[h];

			if (!filename.endsWith(this.m_app.getApplication()
					.getConfiguration().getProperty(CFG_OLD_EDITOR,
							"cpnumber.dat"))) {
				String ext = "";
				for (int i = 0; i < ids.size(); i++) {
					final IImExporter imp = ImExportFactory.getInstance()
							.getImporter((String) ids.get(i));

					ext = imp.getExtension();
					if (ext.startsWith("*"))
						ext = ext.substring(1);

					if (filename.toLowerCase().endsWith(ext)
							&& imp.getMode() == IImExporter.CALLER_MODE) {
						imp.setFilename(filename);
						//chedk for categorie filters
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
										this.getNamespace(), "assigncategoryconfirm", "label",
										this.getLanguage());
								text = StringUtils.replaceString(text, "{%1}", cat);
								messageBox.setMessage(text);
								if (messageBox.open() == SWT.YES) {
									category.setValue(cat);
								} 
							}							
						}
													
							
						final String f = filename;
						try {
							ProgressMonitorDialog pmd = new ProgressMonitorDialog(
									DisplayManager.getDefaultDisplay()
											.getActiveShell());

							IRunnableWithProgress r = new IRunnableWithProgress() {
								public void run(IProgressMonitor progressMonitor) {
									progressMonitor.beginTask(getI18nManager()
											.getString(getNamespace(),
													"importprogress", "label",
													getLanguage()),
											IProgressMonitor.UNKNOWN);

									ICallerList importedCallers = (ICallerList) getRuntime()
											.getCallerFactory()
											.createCallerList();

									if (imp.getMode() == IImExporter.CALLER_MODE) {
										ImportThread ti = new ImportThread((ICallerImporter) imp, (category.getValue().length()==0 ? null : category));
										Thread t = new Thread(ti);
										t.start();
										
										while(t.isAlive()) {
											if (imp instanceof ITracker) {
												String text = getI18nManager()
												.getString(getNamespace(),
														"importprogress2", "label",
														getLanguage());
												
												text = StringUtils.replaceString(text, "{%1}", Integer.toString(((ITracker)imp).getCurrent()));
												text = StringUtils.replaceString(text, "{%2}", Integer.toString(((ITracker)imp).getTotal()));
												progressMonitor.beginTask(text,
														IProgressMonitor.UNKNOWN);		
											} 																		
											
											try {
												Thread.sleep(500);
											} catch (InterruptedException e) {
											}											
										}
										
										if (ti!=null) {
											importedCallers = ti.getResult();
											
											m_app.getController().addElements(
													importedCallers);
										}
										
									}

									if (importedCallers.size() > 0) {
										progressMonitor.done();

										String msg = 
											getI18nManager().getString(
												getNamespace(),
												"success",
												"description",
												getLanguage());

										msg = StringUtils
												.replaceString(
														msg,
														"{%1}",
														Integer
																.toString(importedCallers.size()));
										msg = StringUtils
												.replaceString(msg,
														"{%2}", f);
				
										PropagationFactory.getInstance().fire(
												new Message(Message.INFO, 
														getI18nManager().getString(Editor.NAMESPACE,
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
										progressMonitor.done();
										
										PropagationFactory.getInstance().fire(
												new Message(Message.ERROR, 
														getI18nManager().getString(Editor.NAMESPACE,
														"title", "label",
														getLanguage()), 
														new Exception(getI18nManager().getString(
																getNamespace(),
																"error", "description",
																getLanguage()))),
												"Tray");
										
										new SWTExecuter() {

											protected void execute() {
												MessageDialog
														.openError(
																DisplayManager
																		.getDefaultDisplay()
																		.getActiveShell(),
																getI18nManager()
																		.getString(
																				getNamespace(),
																				"error",
																				"label",
																				getLanguage()),
																getI18nManager()
																		.getString(
																				getNamespace(),
																				"error",
																				"description",
																				getLanguage()));
												m_logger
														.warning("Import of data failed.");

											}
										}.start();
									}

								}
							};
							pmd.setBlockOnOpen(false);
							pmd.run(true, false, r);
						} catch (InterruptedException e) {
							this.m_logger.log(Level.SEVERE, e.getMessage(), e);
						} catch (InvocationTargetException e) {
							this.m_logger.log(Level.SEVERE, e.getMessage(), e);
						}

					}
				}
			} else {
				// do cpnumber.dat migration
				int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
				MessageBox messageBox = new MessageBox(new Shell(DisplayManager
						.getDefaultDisplay()), style);
				messageBox.setMessage(this.getI18nManager().getString(
						this.getNamespace(), "migrationconfirm", "label",
						this.getLanguage()));
				if (messageBox.open() == SWT.YES) {
					IImExporter imp = ImExportFactory.getInstance()
							.getImporter("OldDatFileCallerImporter");
					if (imp != null
							&& (imp instanceof OldDatFileCallerImporter)) {
						((OldDatFileCallerImporter) imp).setFilename(filename);

						ICallerList importedCallers = ((OldDatFileCallerImporter) imp)
								.doImport();
						if (importedCallers != null) {
							final String f = filename;
							this.m_app.getController().addElements(
									importedCallers);

							String msg = getI18nManager().getString(
									getNamespace(), "success", "description",
									getLanguage());

							msg = StringUtils.replaceString(msg, "{%1}",
									Integer.toString(importedCallers.size()));
							msg = StringUtils.replaceString(msg, "{%2}", f);

							PropagationFactory.getInstance().fire(
									new Message(Message.INFO, 
											getI18nManager().getString(Editor.NAMESPACE,
											"title", "label",
											getLanguage()), 
											new Exception(msg)),
									"Tray");
														
							m_app.updateViews(true);
						}
					}
				} else {
					return;
				}
			}
		}
	}

	public void run() {
		try {

			FileDialog dialog = new FileDialog(new Shell(DisplayManager
					.getDefaultDisplay()), SWT.OPEN | SWT.MULTI);
			dialog.setText(this.getI18nManager().getString(this.getNamespace(),
					"title", "label", this.getLanguage()));

			List ids = ImExportFactory.getInstance().getAllImporterIds(
					IImExporter.CALLER_MODE);

			String[] filternames = new String[ids.size()];
			String[] extensions = new String[ids.size()];
			IImExporter ie = null;
			for (int i = 0; i < ids.size(); i++) {
				ie = ImExportFactory.getInstance().getImporter(
						(String) ids.get(i));
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
			
			TreeViewer viewer = (TreeViewer) this.m_app.getApplication()
					.getViewer();
			Cursor c = new Cursor(viewer.getTree().getDisplay(),
					SWT.CURSOR_WAIT);
			viewer.getTree().getShell().setCursor(c);
			String[] filenames = dialog.getFileNames();

			for (int h = 0; h < filenames.length; h++) {
				filenames[h] = dialog.getFilterPath() + File.separator
						+ filenames[h];
			}
			this.run(filenames);

			viewer.getTree().getShell().setCursor(null);
			c.dispose();
			// this.m_app.updateViews(true);

		} catch (Exception ex) {
			this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);
			PropagationFactory.getInstance().fire(
					new Message(Message.ERROR, getNamespace(), "error", ex));
		}
	}

	public boolean isEnabled() {
		if (this.m_app != null && this.m_app.getController() != null) {
			Object o = this.m_app.getController().getRepository();
			if (o instanceof ICallerManager) {
				return ((ICallerManager)o).isSupported(IWriteCallerRepository.class);
			}
		}
		return false;
	}
}
