package de.janrufmonitor.ui.jface.application.action;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.TreeItem;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;

public class QuickSearchAction extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.action.QuickSearchAction";

	private IRuntime m_runtime;

	private String m_searchString;

	public QuickSearchAction() {
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
		return "quicksearch";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void setData(Object s) {
		if (s instanceof String)
			this.m_searchString = (String) s;
	}

	public void run() {
		if (this.m_searchString != null
				&& this.m_searchString.trim().length() == 0) {
			m_app.updateViews(null, true);
			return;
		}

		Viewer v = this.m_app.getApplication().getViewer();
		if (v instanceof TableViewer && this.m_searchString != null) {
			TableItem[] data = ((TableViewer) v).getTable().getItems();
			List result = new ArrayList();
			if (data.length > 0) {
				TableItem c = null;
				((TableViewer) v).getTable().deselectAll();
				for (int i = 0; i < data.length; i++) {
					c = data[i];
					if (found(c, this.m_searchString)) {
						result.add(c.getData());
					}
				}
				if (result.size() >= 0) {
					Object[] d = new Object[result.size()];
					for (int i = 0; i < result.size(); i++)
						d[i] = result.get(i);
					m_app.updateViews(d, true);
				}
			}
		}

		if (v instanceof TreeViewer && this.m_searchString != null) {
			TreeItem[] data = ((TreeViewer) v).getTree().getItems();
			List result = new ArrayList();
			if (data.length > 0) {
				TreeItem c = null;
				((TreeViewer) v).getTree().deselectAll();
				for (int i = 0; i < data.length; i++) {
					c = data[i];
					if (found(c, this.m_searchString)) {
						result.add(c.getData());
					}
				}
				if (result.size() >= 0) {
					Object[] d = new Object[result.size()];
					for (int i = 0; i < result.size(); i++)
						d[i] = result.get(i);
					m_app.updateViews(d, true);
				}
			}
		}
	}

	private boolean found(TreeItem c, String text) {
		Viewer v = this.m_app.getApplication().getViewer();
		int cols = ((TreeViewer) v).getTree().getColumnCount();

		String content = null;
		for (int i = 0; i < cols; i++) {
			content = c.getText(i).toLowerCase();
			if (content != null) {
				if (content.indexOf(text.toLowerCase()) > -1)
					return true;
			}
		}
		return false;
	}

	private boolean found(TableItem c, String text) {
		Viewer v = this.m_app.getApplication().getViewer();
		int cols = ((TableViewer) v).getTable().getColumnCount();

		String content = null;
		for (int i = 0; i < cols; i++) {
			content = c.getText(i).toLowerCase();
			if (content != null) {
				if (content.indexOf(text.toLowerCase()) > -1)
					return true;
			}
		}
		return false;
	}

}
