package de.janrufmonitor.ui.jface.application.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Tree;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.IApplication;
import de.janrufmonitor.ui.jface.application.IConfigConst;

public class ShowGridAction extends AbstractAction implements IConfigConst {

	private static String NAMESPACE = "ui.jface.application.action.ShowGridAction";
	
	private IRuntime m_runtime;

	public ShowGridAction() {
		super("", IAction.AS_CHECK_BOX);
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
		return "showgrid";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null && v instanceof TableViewer) {
			Table t = ((TableViewer)v).getTable();
			if (!this.isChecked()) {
				t.setLinesVisible(false);
				this.setChecked(false);
				this.m_app.getApplication().getConfiguration().setProperty(CFG_SHOW_GRID, "false");
			} else {
				t.setLinesVisible(true);
				this.setChecked(true);
				this.m_app.getApplication().getConfiguration().setProperty(CFG_SHOW_GRID, "true");
			}
			this.m_app.updateViews(false);
			this.m_app.getApplication().storeConfiguration();
		}
		if (v!=null && v instanceof TreeViewer) {
			Tree t = ((TreeViewer)v).getTree();
			if (!this.isChecked()) {
				t.setLinesVisible(false);
				this.setChecked(false);
				this.m_app.getApplication().getConfiguration().setProperty(CFG_SHOW_GRID, "false");
			} else {
				t.setLinesVisible(true);
				this.setChecked(true);
				this.m_app.getApplication().getConfiguration().setProperty(CFG_SHOW_GRID, "true");
			}
			this.m_app.updateViews(false);
			this.m_app.getApplication().storeConfiguration();
		}
	}
	
	public void setApplication(IApplication app) {
		super.setApplication(app);
		this.setChecked(this.m_app.getApplication().getConfiguration().getProperty(CFG_SHOW_GRID, "true").equalsIgnoreCase("true"));
	}
}
