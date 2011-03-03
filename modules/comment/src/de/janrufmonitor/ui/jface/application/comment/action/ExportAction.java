package de.janrufmonitor.ui.jface.application.comment.action;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.comment.api.IComment;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.swt.DisplayManager;

public class ExportAction extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.comment.action.ExportAction";
	
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
	}
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "comment_export";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null) {
			IStructuredSelection selection = (IStructuredSelection) v.getSelection();
			if (!selection.isEmpty()) {
				List l = selection.toList();
				if (l.size()>0) {
					FileDialog dialog = new FileDialog (new Shell(DisplayManager.getDefaultDisplay()), SWT.SAVE);
					dialog.setFilterExtensions(new String[] {"*.txt"});
					dialog.setFilterNames(new String[] {getI18nManager().getString(getNamespace(), "exportfilter", "label", getLanguage())});
					dialog.setText(getI18nManager().getString(getNamespace(), "export", "label", getLanguage()));
					String filename = dialog.open();
					if (filename != null && filename.length()>0) {
						File f = new File(filename);
						f.getParentFile().mkdirs();
						
						try {
							FileOutputStream fos = new FileOutputStream(f);
							IComment c = null;
							for (int i=0;i<l.size();i++) {
								c = (IComment) l.get(i);
								fos.write(c.getText().getBytes());
								fos.write(IJAMConst.CRLF.getBytes());
							}
							
							fos.flush();
							fos.close();
						} catch (FileNotFoundException ex) {
							m_logger.severe(ex.getMessage());
						} catch (IOException ex) {
							m_logger.severe(ex.toString() + ": " +ex.getMessage());
						}
					}
				}
			}
		}
	}

}
