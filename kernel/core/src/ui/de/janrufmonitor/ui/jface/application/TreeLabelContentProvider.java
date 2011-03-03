package de.janrufmonitor.ui.jface.application;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IMultiPhoneCaller;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.ui.jface.AbstractTableLabelProvider;
import de.janrufmonitor.ui.jface.application.rendering.ITableCellRenderer;

public class TreeLabelContentProvider extends AbstractTableLabelProvider implements 
		ITreeContentProvider, IConfigConst {

	class TreeItemCallerData implements IExtendedTreeItemCallerData {

		IAttributeMap m_m;
		IPhonenumber m_pn;
		ICaller m_caller;
		
		public TreeItemCallerData(ICaller c, IAttributeMap m, IPhonenumber pn) {
			m_m = m;
			m_pn = pn;
			m_caller = c;
		}
		
		public IAttributeMap getAttributes() {
			return m_m;
		}

		public IPhonenumber getPhone() {
			return m_pn;
		}

		public ICaller getCaller() {
			return m_caller;
		}
		
	}
	
	protected Properties m_configuration;

	protected Map m_rendererMapping;

	public TreeLabelContentProvider(Properties configuration) {
		this.m_configuration = configuration;
		this.m_rendererMapping = new HashMap();
	}

	public Object[] getElements(Object o) {
		if (o instanceof IApplicationController) {
			return ((IApplicationController) o).getElementArray();
		}
		return null;
	}

	protected String getRendererID(int column) {
		if (this.m_rendererMapping.size() == 0) {
			this.buildRendererMapping();
		}
		return (String) this.m_rendererMapping.get(new Integer(column));
	}

	private void buildRendererMapping() {
		String renderers = this.m_configuration.getProperty(CFG_RENDERER_LIST,
				"");
		StringTokenizer st = new StringTokenizer(renderers, ",");
		int i = 0;
		while (st.hasMoreTokens()) {
			this.m_rendererMapping.put(new Integer(i), st.nextToken());
			i++;
		}
	}

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		
	}

	public Object[] getChildren(Object o) {
		if (o instanceof IMultiPhoneCaller) {

			List items = new ArrayList();
			List pns = ((IMultiPhoneCaller) o).getPhonenumbers();
			for (int i=0,j=pns.size();i<j;i++) {
				items.add(new TreeItemCallerData(
						((IMultiPhoneCaller) o),
						((IMultiPhoneCaller) o).getAttributes(),
						(IPhonenumber) pns.get(i)));
			}

			if (items.size()>1)
				items.remove(0);
			return items.toArray();
		}
		return null;
	}

	public Object getParent(Object o) {
		return null;
	}

	public boolean hasChildren(Object o) {
		if (o instanceof IMultiPhoneCaller) {
			return ((IMultiPhoneCaller) o).getPhonenumbers().size() > 1;
		}
		return false;
	}

	public Image getColumnImage(Object o, int column) {
		ITableCellRenderer r = RendererRegistry.getInstance().getRenderer(
			this.getRendererID(column)
		);
		if (r!=null) {
			r.updateData(o);
			return r.renderAsImage();
		}
		return null;
	}
	
	public String getColumnText(Object o, int column) {
		ITableCellRenderer r = RendererRegistry.getInstance().getRenderer(
			this.getRendererID(column)
		);
		if (r!=null) {
			r.updateData(o);
			return r.renderAsText();
		}
		return "";
	}



}