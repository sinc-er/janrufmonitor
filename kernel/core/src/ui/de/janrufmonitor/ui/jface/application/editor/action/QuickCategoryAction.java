package de.janrufmonitor.ui.jface.application.editor.action;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;

import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.action.IAction;
import de.janrufmonitor.ui.jface.application.editor.Editor;
import de.janrufmonitor.ui.jface.application.editor.EditorConfigConst;

public class QuickCategoryAction extends AbstractAction implements EditorConfigConst {

	private class CategoryAssignAction extends AbstractAction {

		String m_category;
		
		public CategoryAssignAction(String cat) {
			super();
			this.setText(
				this.getI18nManager().getString(
					this.getNamespace(),
					"title_subcat",
					"label",
					this.getLanguage()
				) + cat
			);	
			m_category = cat;
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
							((ICaller)o).setAttribute(getRuntime().getCallerFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_CATEGORY, m_category));
							list.add((ICaller)o);
						}
					}
					if (list.size()>0)  {
						this.m_app.getController().updateElement(list);
					}
					
					m_app.updateViews(true);
				}
			}
		}

		public void setData(Object data) {
			if (data instanceof String)
				m_category = (String) data;
		}

		public IRuntime getRuntime() {
			if (m_runtime==null) {
				m_runtime = PIMRuntime.getInstance();
			}
			return m_runtime;
		}

		public String getNamespace() {
			return NAMESPACE;
		}
		
	}
	
	private static String NAMESPACE = "ui.jface.application.editor.action.CategoryAction";
	
	private IRuntime m_runtime;

	public QuickCategoryAction() {
		super();
		this.setText(
			this.getI18nManager().getString(
				this.getNamespace(),
				"title_assign",
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
		return "editor_quickcategory";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public IAction[] getSubActions() {
		String categories = getRuntime().getConfigManagerFactory().getConfigManager().getProperty(Editor.NAMESPACE, "categories");
		String[] tmp = categories.split(",");

		IAction[] actions = new IAction[tmp.length];
		for (int i=tmp.length-1, k=0;i>=0;i--,k++) {
			actions[k] = new CategoryAssignAction(tmp[i]);
			actions[k].setApplication(this.m_app);
		}

		return actions;
	}

	public boolean hasSubActions() {
		return isEnabled();
	}
	
	public boolean isEnabled() {
		if (this.m_app!=null) {
			Object o = this.m_app.getController().getRepository();
			if (o instanceof ICallerManager) {
				return ((ICallerManager)o).isSupported(IWriteCallerRepository.class);
			}
		}
		return false;
	}

}

