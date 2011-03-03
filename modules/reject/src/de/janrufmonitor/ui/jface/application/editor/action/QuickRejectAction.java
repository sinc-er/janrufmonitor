package de.janrufmonitor.ui.jface.application.editor.action;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.editor.EditorConfigConst;

public class QuickRejectAction extends AbstractAction implements EditorConfigConst {

	private static String NAMESPACE = "ui.jface.application.editor.action.QuickRejectAction";
	
	private IRuntime m_runtime;

	public QuickRejectAction() {
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
	
	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null && v instanceof Viewer) {
			final IStructuredSelection selection = (IStructuredSelection) v.getSelection();
			if (!selection.isEmpty()) {
				Iterator i = selection.iterator();
				ICallerList list = this.getRuntime().getCallerFactory().createCallerList(selection.size());
				Object o = null;
				while (i.hasNext()) {
					o = i.next();
					if (o instanceof ICaller) {
						if (this.isRejectable(((ICaller)o))) {
							((ICaller)o).setAttribute(getRuntime().getCallerFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_REJECT, IJAMConst.ATTRIBUTE_VALUE_NO));
						} else
							((ICaller)o).setAttribute(getRuntime().getCallerFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_REJECT, IJAMConst.ATTRIBUTE_VALUE_YES));
						list.add((ICaller)o);
					}
				}
				if (list.size()>0)  {
					this.m_app.getController().deleteElements(list);
					this.m_app.getController().addElements(list);
				}
				
				m_app.updateViews(true);
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

	public String getID() {
		return "editor_quickreject";
	}

	public String getNamespace() {
		return NAMESPACE;
	}
	
	public boolean isEnabled() {
		if (this.m_app!=null) {
			Object o = this.m_app.getController().getRepository();
			return (o instanceof IWriteCallerRepository);
		}
		return false;
	}

}

