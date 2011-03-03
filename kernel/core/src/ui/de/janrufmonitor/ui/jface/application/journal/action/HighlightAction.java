package de.janrufmonitor.ui.jface.application.journal.action;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.widgets.Table;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.journal.JournalConfigConst;
import de.janrufmonitor.ui.jface.application.journal.JournalController;
import de.janrufmonitor.ui.swt.DisplayManager;

public class HighlightAction extends AbstractAction implements JournalConfigConst {

	private static String NAMESPACE = "ui.jface.application.journal.action.HighlightAction";
	
	private IRuntime m_runtime;

	public HighlightAction() {
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
		return "journal_highlight";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		long m_timestamp = -1;
		
		try {
			m_timestamp = Long.parseLong(this.m_app.getApplication().getConfiguration().getProperty(CFG_HIGHLIGHT_TIME, "0"));
		} catch (Exception e) {
			this.m_logger.warning("Cannot parse long value: " + e.getMessage());
		}
		
		// highlight is disabled
		if (m_timestamp==-1) return;
				
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null && v instanceof TableViewer) {
			
			Table t = ((TableViewer)v).getTable();
			ICallList cl = ((JournalController)this.m_app.getController()).getCallList();
			
			ICall c = null;
			Font itemFont = null;
			for (int i=0,n=cl.size();i<n;i++) {
				c = cl.get(i);
				if (this.isHighlight(m_timestamp, c.getDate().getTime())) {
					itemFont = t.getItem(i).getFont();
					t.getItem(i).setFont(this.getBoldFont(itemFont));
				}
			}
			this.m_app.getApplication().getConfiguration().setProperty(CFG_HIGHLIGHT_TIME, Long.toString(System.currentTimeMillis()));
			this.m_app.getApplication().storeConfiguration();
		}
	}
	
	private boolean isHighlight(long ts, long calltime) {
		// only call since last refresh 
		if (ts>0) {
			return calltime>(ts - 1000);
		}
		return false;
	}
	
	private Font getBoldFont(Font f) {
		FontData[] fd = f.getFontData();
		if (fd==null || fd.length==0) return f;
		
		for (int i=0;i<fd.length;i++) {
			fd[i].setStyle(SWT.BOLD);
		}
		f = new Font(DisplayManager.getDefaultDisplay(), fd);		
		return f;
	}
	
}
