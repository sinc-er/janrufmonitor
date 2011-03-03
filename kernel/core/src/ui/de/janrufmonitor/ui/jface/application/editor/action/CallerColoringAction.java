package de.janrufmonitor.ui.jface.application.editor.action;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Tree;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.types.IReadCallerRepository;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.editor.EditorController;
import de.janrufmonitor.ui.jface.application.journal.JournalConfigConst;

public class CallerColoringAction extends AbstractAction implements JournalConfigConst {

	private static String NAMESPACE = "ui.jface.application.editor.action.CallerColoringAction";
	
	private IRuntime m_runtime;
	public CallerColoringAction() {
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
		return "editor_coloring";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null && v instanceof TreeViewer) {
			Tree t = ((TreeViewer)v).getTree();
			ICallerList cl = ((EditorController)this.m_app.getController()).getCallerList();
			
			ICaller c = null;
			for (int i=0,n=cl.size();i<n;i++) {
				c = cl.get(i);
				Color color = this.getColor(c, t);
				if (color!=null) {
					t.getItem(i).setForeground(color);
				}
				if (i % 2 ==0) {
					t.getItem(i).setBackground(getIterationColor(t));
				}
			}
		}
	}
	
	private boolean isRejectable(ICaller c) {
		IAttribute att = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_REJECT);
		if (att != null) {
			return att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_YES);
		}
		return false;
	}
	
	private Color getIterationColor(Tree t) {
		return new Color(t.getDisplay(), 230,230,230);
	}
	
	private Color getColor(ICaller c, Tree t) {
		if (this.isRejectable(c)) {
			return new Color(t.getDisplay(), 255,0,0);
		}
		if (this.isColored(c)) {
			return new Color(t.getDisplay(), 128,128,128);
		}
		return null;
	}

	private boolean isColored(ICaller c) {
		IAttribute att = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLERMANAGER);
		if (att != null) {
			if (this.isCallerManagerReadOnly(att.getValue())) {
				return true;	
			}
		}
		return false;
	}
	
	private boolean isCallerManagerReadOnly(String man) {
		ICallerManager m = PIMRuntime.getInstance().getCallerManagerFactory().getCallerManager(man);
		if (m!=null) {
			return (m.isSupported(IReadCallerRepository.class) && !m.isSupported(IWriteCallerRepository.class));
		}
		return false;
	}
	
}
