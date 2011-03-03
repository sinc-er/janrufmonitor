package de.janrufmonitor.ui.jface.application.action;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.ApplicationImageDescriptor;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class ZoomInAction extends FontAction {

	private static String NAMESPACE = "ui.jface.application.action.ZoomInAction";
	
	private IRuntime m_runtime;

	public ZoomInAction() {
		super();
		this.setText(
			this.getI18nManager().getString(
				this.getNamespace(),
				"title",
				"label",
				this.getLanguage()
			)
		);
		this.setAccelerator(SWT.CTRL+'I');
		this.setImageDescriptor(new ApplicationImageDescriptor(
			SWTImageManager.getInstance(this.getRuntime()).getImagePath(IJAMConst.IMAGE_KEY_ZIN_GIF)
		));			
	}
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "zoomin";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null && v instanceof TableViewer) {
			Table t = ((TableViewer)v).getTable();
			t.setFont(this.getSizedFont(t.getFont().getFontData()[0], 1, true));
			this.saveFontData(t.getFont());
			this.m_app.updateViews(false);
		}
		if (v!=null && v instanceof TreeViewer) {
			Tree t = ((TreeViewer)v).getTree();
			t.setFont(this.getSizedFont(t.getFont().getFontData()[0], 1, true));
			this.saveFontData(t.getFont());
			this.m_app.updateViews(false);
		}		
	}
	
}
