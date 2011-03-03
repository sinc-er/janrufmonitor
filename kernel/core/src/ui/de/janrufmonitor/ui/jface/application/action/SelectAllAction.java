package de.janrufmonitor.ui.jface.application.action;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;

public class SelectAllAction extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.action.SelectAllAction";
	
	private IRuntime m_runtime;

	public SelectAllAction() {
		super();
		this.setText(
			this.getI18nManager().getString(
				this.getNamespace(),
				"title",
				"label",
				this.getLanguage()
			)
		);
		this.setAccelerator(SWT.CTRL+'A');
	}
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "select_all";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null && v instanceof TableViewer) {
			Table t = ((TableViewer)v).getTable();
			t.selectAll();
		}
		if (v!=null && v instanceof TreeViewer) {
			Tree t = ((TreeViewer)v).getTree();
			t.selectAll();
		}		
	}

}
