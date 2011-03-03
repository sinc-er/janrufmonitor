package de.janrufmonitor.ui.jface.application.editor.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;

import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.editor.EditorConfigConst;
import de.janrufmonitor.util.io.ImageHandler;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.io.Stream;

public class QuickImagePathAction extends AbstractAction implements EditorConfigConst {

	private static String NAMESPACE = "ui.jface.application.editor.action.QuickImagePathAction";
	
	private IRuntime m_runtime;

	public QuickImagePathAction() {
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

	public String getID() {
		return "";
	}

	public String getNamespace() {
		return NAMESPACE;
	}
	
	public boolean isEnabled() {
		if (this.m_app!=null) {
			Object o = this.m_app.getController().getRepository();
			if (o instanceof ICallerManager) {
				return ((ICallerManager)o).isSupported(IWriteCallerRepository.class);
			}
		}
		return false;
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null && v instanceof Viewer) {
			final IStructuredSelection selection = (IStructuredSelection) v.getSelection();
			if (!selection.isEmpty()) {
				Iterator i = selection.iterator();
				ICallerList list = this.getRuntime().getCallerFactory().createCallerList(selection.size());
				Object o = null;
				while (i.hasNext()) {
					o = i.next();
					if (o instanceof ICaller) {
						ICaller caller = ((ICaller)o);
						if (this.hasCallerImage(caller)) {
							File image = getCallerImageFile(caller);
							if (image!=null) {
								if (!isCentralImageStore(caller)) {
									String name = image.getName();
									File image_in_store = new File(getCentralImageStorePath(), name);
									if (!image_in_store.exists()) {
										try {
											FileInputStream fin = new FileInputStream(image);
											FileOutputStream fout = new FileOutputStream(image_in_store);
											if (m_logger.isLoggable(Level.INFO))
												m_logger.info("Copiing "+image.getAbsolutePath()+" to "+image_in_store.getAbsolutePath());
											Stream.copy(fin, fout, true);	
											
											setAttribute(caller, IJAMConst.ATTRIBUTE_NAME_IMAGEPATH, PathResolver.getInstance(getRuntime()).encode(image_in_store.getAbsolutePath()));
											list.add(caller);
										} catch (IOException e) {
											m_logger.log(Level.SEVERE, e.getMessage(), e);
										}						
									} else {
										m_logger.warning("Image file "+image_in_store.getAbsolutePath()+" already exists in central image store. File gets overwritten.");
										try {
											FileInputStream fin = new FileInputStream(image);
											FileOutputStream fout = new FileOutputStream(image_in_store);
											if (m_logger.isLoggable(Level.INFO))
												m_logger.info("Copiing "+image.getAbsolutePath()+" to "+image_in_store.getAbsolutePath());
											Stream.copy(fin, fout, true);	
											setAttribute(caller, IJAMConst.ATTRIBUTE_NAME_IMAGEPATH, PathResolver.getInstance(getRuntime()).encode(image_in_store.getAbsolutePath()));
											list.add(caller);
										} catch (IOException e) {
											m_logger.log(Level.SEVERE, e.getMessage(), e);
										}			
									}					
								} else {
									if (m_logger.isLoggable(Level.INFO))
										m_logger.info("Image file "+image.getAbsolutePath()+" is already in central image store.");
								}
							} else {
								m_logger.warning("Image file could not be copied to central image store. Image is not set.");
							}					
							
						}				
					}
				}
				if (list.size()>0)  {
					this.m_app.getController().updateElement(list);
				}
				
				m_app.updateViews(true);
			}
		}
	}
	
	private void setAttribute(ICaller caller, String attName, String value) {
		caller.setAttribute(this.getRuntime().getCallerFactory()
				.createAttribute(attName, value));
	}
	
	private boolean hasCallerImage(ICaller caller) {
		String filename = "";

		if (caller != null) {
			ImageHandler ih = ImageHandler.getInstance();
			filename = ih.getImagePath(caller);
		}

		if (filename.length() == 0)
			filename = PathResolver.getInstance(getRuntime()).resolve(this.getAttribute(caller, IJAMConst.ATTRIBUTE_NAME_IMAGEPATH));

		if (filename.length() > 0) {
			if (new File(filename).exists()) {
				return true;
			}
		}
		return false;
	}
	
	private File getCallerImageFile(ICaller caller) {
		String filename = "";

		if (caller != null) {
			ImageHandler ih = ImageHandler.getInstance();
			filename = ih.getImagePath(caller);
		}

		if (filename.length() == 0)
			filename = PathResolver.getInstance(getRuntime()).resolve(this.getAttribute(caller, IJAMConst.ATTRIBUTE_NAME_IMAGEPATH));

		if (filename.length() > 0) {
			File f = new File(filename);
			if (f.exists()) {
				return f;
			}
		}
		return null;
	}
	
	private String getAttribute(ICaller caller, String attName) {
		if (caller.getAttributes().contains(attName)) {
			return caller.getAttribute(attName).getValue();
		}
		return "";
	}
	
	private boolean isCentralImageStore(ICaller caller) {
		File cis = new File(getCentralImageStorePath());
		File image = this.getCallerImageFile(caller);
		if (image!=null) {
			return image.getAbsolutePath().startsWith(cis.getAbsolutePath());
		}
		return false;
	}
	
	private String getCentralImageStorePath() {
		File cis = new File (PathResolver.getInstance(getRuntime()).getDataDirectory() + File.separator + "photos" + File.separator + "contacts");
		if (!cis.exists()) {
			cis.mkdirs();
		}
		return cis.getAbsolutePath();
	}

}

