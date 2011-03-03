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
import de.janrufmonitor.service.geo.GeoCoder;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.editor.EditorConfigConst;
import de.janrufmonitor.util.math.Point;

public class GeoCodingAction extends AbstractAction implements EditorConfigConst {

	private static String NAMESPACE = "ui.jface.application.editor.action.GeoCodingAction";
	
	private IRuntime m_runtime;

	public GeoCodingAction() {
		super();
		this.setText(this.getI18nManager().getString(this.getNamespace(),
				"title", "label", this.getLanguage()));
	}
	
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "editor_geocoding";
	}

	public String getNamespace() {
		return NAMESPACE;
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
						Point p = GeoCoder.getInstance().getCoordinates(((ICaller)o).getAttributes());
						if (p!=null) {
							((ICaller)o).setAttribute(getRuntime().getCallerFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_GEO_ACC, Integer.toString(p.getAccurance())));
							((ICaller)o).setAttribute(getRuntime().getCallerFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_GEO_LNG, Double.toString(p.getLongitude())));
							((ICaller)o).setAttribute(getRuntime().getCallerFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_GEO_LAT, Double.toString(p.getLatitude())));
							list.add((ICaller)o);
						}
					}
				}
				if (list.size()>0)  {
					this.m_app.getController().updateElement(list);
				}
				
				m_app.updateViews(true);
			}
		}
	}
}

