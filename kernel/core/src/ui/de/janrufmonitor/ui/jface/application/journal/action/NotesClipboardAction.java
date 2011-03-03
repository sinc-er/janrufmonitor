package de.janrufmonitor.ui.jface.application.journal.action;

import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.ApplicationImageDescriptor;
import de.janrufmonitor.ui.jface.application.RendererRegistry;
import de.janrufmonitor.ui.jface.application.journal.Journal;
import de.janrufmonitor.ui.jface.application.journal.JournalConfigConst;
import de.janrufmonitor.ui.jface.application.rendering.ITableCellRenderer;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class NotesClipboardAction extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.journal.action.NotesClipboardAction";
	
	private IRuntime m_runtime;

	public NotesClipboardAction() {
		super();
		this.setText(
			this.getI18nManager().getString(
				this.getNamespace(),
				"title",
				"label",
				this.getLanguage()
			)
		);
		this.setImageDescriptor(new ApplicationImageDescriptor(
			SWTImageManager.getInstance(this.getRuntime()).getImagePath(IJAMConst.IMAGE_KEY_CLP_GIF)
		));			
	}
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "notesclipboard";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public boolean isEnabled() {
		if (this.m_app!=null && this.m_app instanceof Journal) {
			return getColumnNumber(((Journal)this.m_app).getConfiguration(), "notes")>=0;
		}
		return false;
	}
	
	private int getColumnNumber(Properties config, String id) {
		String renderers = config.getProperty(
				JournalConfigConst.CFG_RENDERER_LIST, "");
		StringTokenizer st = new StringTokenizer(renderers, ",");
		int i = 0;
		while (st.hasMoreTokens()) {
			if (id.equalsIgnoreCase(st.nextToken()))
				return i;
			i++;
		}
		return -1;
	}
	
	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null && v instanceof Viewer) {
			IStructuredSelection selection = (IStructuredSelection) v.getSelection();
			if (!selection.isEmpty()) {
				Object o = selection.getFirstElement();
				ITableCellRenderer tcr = RendererRegistry.getInstance().getRenderer("notes");
				if (tcr!=null) {
					tcr.updateData(o);
					Clipboard cb = new Clipboard(DisplayManager.getDefaultDisplay());
					String textData = tcr.renderAsText();
					TextTransfer textTransfer = TextTransfer.getInstance();
					cb.setContents(new Object[]{(textData!=null ? textData : "")}, new Transfer[]{textTransfer});
				}
			}
		}
	}
}
