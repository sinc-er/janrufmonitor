package de.janrufmonitor.ui.jface.application.journal.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.program.Program;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.journal.JournalConfigConst;

public class WebSearchAction extends AbstractAction implements JournalConfigConst {

	private static String NAMESPACE = "ui.jface.application.journal.action.WebSearchAction";
	 
	private IRuntime m_runtime;

	public WebSearchAction() {
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
		return "journal_websearch";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null && v instanceof TableViewer) {
			IStructuredSelection selection = (IStructuredSelection) v.getSelection();
			if (!selection.isEmpty()) {
				ICall selectedCall = (ICall) selection.getFirstElement();
			
				String searchString = 
					selectedCall.getCaller().getName().getFullname() + " " + 
					selectedCall.getCaller().getName().getAdditional();
				
				try {
					searchString = URLEncoder.encode(searchString.trim(), "ISO-8859-1");
				} catch (UnsupportedEncodingException e) {
					this.m_logger.warning("Encoding problem encountered: "+e.getMessage());
				}
			
				Program.launch(this.m_app.getApplication().getConfiguration().getProperty(CFG_WEB_QUERY, "") + searchString);
			}
		}
	}
}
