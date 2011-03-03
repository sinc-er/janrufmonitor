package de.janrufmonitor.ui.jface.application.journal.action;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.IApplication;
import de.janrufmonitor.ui.jface.application.journal.JournalConfigConst;

public class HighlightSelectAction extends AbstractAction implements JournalConfigConst {

	private static String NAMESPACE = "ui.jface.application.journal.action.HighlightSelectAction";
	
	private IRuntime m_runtime;

	public HighlightSelectAction() {
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
		return "journal_highlightselect";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null && v instanceof TableViewer) {
			if (!this.isChecked()) {
				this.m_app.getApplication().getConfiguration().setProperty(CFG_HIGHLIGHT_TIME, "-1");
			} else {
				this.m_app.getApplication().getConfiguration().setProperty(CFG_HIGHLIGHT_TIME, "0");
			}
			this.m_app.updateViews(false);
			this.m_app.getApplication().storeConfiguration();
		}
	}
	
	public void setApplication(IApplication app) {
		super.setApplication(app);
		this.setChecked(!this.m_app.getApplication().getConfiguration().getProperty(CFG_HIGHLIGHT_TIME, "-1").equalsIgnoreCase("-1"));
	}
}
