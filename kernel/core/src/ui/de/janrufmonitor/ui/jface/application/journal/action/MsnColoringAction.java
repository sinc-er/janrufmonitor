package de.janrufmonitor.ui.jface.application.journal.action;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Table;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.journal.JournalConfigConst;
import de.janrufmonitor.ui.jface.application.journal.JournalController;
import de.janrufmonitor.ui.swt.DisplayManager;

/**
 * This class is not used any longer since the introdcution of ITableColorProvider in Eclipse 3.1.
 * New implementation see <code>de.janrufmonitor.ui.jface.application.journal.JournalTableColorLabelContentProvider</code> 
 * 
 * @author brandt
 * @deprecated
 */
public class MsnColoringAction extends AbstractAction implements JournalConfigConst {

	private static String NAMESPACE = "ui.jface.application.journal.action.MsnColoringAction";
	
	private IRuntime m_runtime;
	private Map m_colors;

	public MsnColoringAction() {
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
		return "journal_msncoloring";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null && v instanceof TableViewer) {
			this.m_colors=null;
			Table t = ((TableViewer)v).getTable();
			ICallList cl = ((JournalController)this.m_app.getController()).getCallList();
			
			ICall c = null;
			Color color = null;
			for (int i=0,n=cl.size();i<n;i++) {
				c = cl.get(i);
				color = this.getMsnColor(c.getMSN().getMSN());
				if (color!=null) {
					t.getItem(i).setForeground(color);
				}
			}
		}
	}
	
	private Color getMsnColor(String msn){
		if (this.m_colors==null) {
			this.m_colors = new HashMap();
			String colors = m_app.getApplication().getConfiguration().getProperty(CFG_MSNFONTCOLOR, "[]");
			StringTokenizer st = new StringTokenizer(colors, "[");

			while (st.hasMoreTokens()) {
				String singleColor = st.nextToken();
				singleColor = singleColor.substring(0, singleColor.length()-1).trim();
				if (singleColor.length()>0) {
					StringTokenizer s = new StringTokenizer(singleColor, "%");
					while (s.hasMoreTokens()) {
						String key = s.nextToken();
						String color = s.nextToken();
						StringTokenizer cs = new StringTokenizer(color, ",");
						// only add if MSNs is existing
						if (this.getRuntime().getMsnManager().existMsn(
							this.getRuntime().getMsnManager().createMsn(key))) {
							
							this.m_colors.put(key, new Color(
								DisplayManager.getDefaultDisplay(),
								Integer.parseInt(cs.nextToken()),
								Integer.parseInt(cs.nextToken()),
								Integer.parseInt(cs.nextToken())
							));
						}
					}
				}
			}
		}
		
		if (this.m_colors.containsKey(msn)) {
			return (Color)this.m_colors.get(msn);
		}
		return null;	
	}
}
