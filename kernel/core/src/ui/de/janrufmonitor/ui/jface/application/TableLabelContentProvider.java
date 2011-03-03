package de.janrufmonitor.ui.jface.application;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.graphics.Image;

import de.janrufmonitor.ui.jface.AbstractTableLabelProvider;
import de.janrufmonitor.ui.jface.application.rendering.ITableCellRenderer;

public class TableLabelContentProvider extends AbstractTableLabelProvider implements IStructuredContentProvider, IConfigConst {
	
		protected Properties m_configuration;
		protected Map m_rendererMapping; 
		
		public TableLabelContentProvider(Properties configuration) {
			this.m_configuration = configuration;
			this.m_rendererMapping = new HashMap();
		}
		
		public Object[] getElements(Object o) {
			if (o instanceof IApplicationController) { 
				return ((IApplicationController)o).getElementArray();
			}
			return null;
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

		protected String getRendererID(int column) {
			if (this.m_rendererMapping.size()==0) {
				this.buildRendererMapping();
			}
			return (String) this.m_rendererMapping.get(new Integer(column));
		}
		
		private void buildRendererMapping() {
			String renderers = this.m_configuration.getProperty(CFG_RENDERER_LIST, "");
			StringTokenizer st = new StringTokenizer(renderers, ",");
			int i=0;
			while (st.hasMoreTokens()) {
				this.m_rendererMapping.put(new Integer(i), st.nextToken());
				i++;
			}
		}

		public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
		}
	}