package de.janrufmonitor.ui.jface.application.editor.rendering;

import java.util.List;

import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.repository.ICallManager;
import de.janrufmonitor.repository.filter.IFilter;
import de.janrufmonitor.repository.filter.ItemCountFilter;
import de.janrufmonitor.repository.filter.PhonenumberFilter;
import de.janrufmonitor.repository.types.IReadCallRepository;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.ITreeItemCallerData;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IEditorCellRenderer;
import de.janrufmonitor.util.formatter.Formatter;

public class LastCalled extends AbstractTableCellRenderer implements IEditorCellRenderer {

	class TreeItemCallerData implements ITreeItemCallerData {

		IAttributeMap m_m;
		IPhonenumber m_pn;
		
		public TreeItemCallerData(IAttributeMap m, IPhonenumber pn) {
			m_m = m;
			m_pn = pn;
		}
		
		public IAttributeMap getAttributes() {
			return m_m;
		}

		public IPhonenumber getPhone() {
			return m_pn;
		}
		
	}
	
	private static String NAMESPACE = "ui.jface.application.editor.rendering.LastCalled";
	
	private Formatter m_f;
	
	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICaller) {
				this.m_o = new TreeItemCallerData(((ICaller)this.m_o).getAttributes(), ((ICaller)this.m_o).getPhoneNumber());
			}	
	
			if (this.m_o instanceof ITreeItemCallerData) {
				IPhonenumber pn = ((ITreeItemCallerData)m_o).getPhone();
				List cms = PIMRuntime.getInstance().getCallManagerFactory().getTypedCallManagers(IReadCallRepository.class);
				if (cms.size()>0) {
					ICallManager cm = null;
					for (int i=0,j=cms.size();i<j;i++) {
						cm = (ICallManager) cms.get(i);
						if (cm.isActive() && cm.isSupported(IReadCallRepository.class)) {
							IFilter[] filters = new IFilter [] {new PhonenumberFilter(pn), new ItemCountFilter(1)};
							ICallList cl = ((IReadCallRepository)cm).getCalls(filters, 1, 0);
							cl.sort(0, false);
							if (cl.size()>0) {
								ICall c = cl.get(0);
								return getFormatter().parse(IJAMConst.GLOBAL_VARIABLE_CALLTIME, c.getDate());
							}
						}
					}
				}
			}					
		}
		return "";
	}
	
	private Formatter getFormatter() {
		if (this.m_f==null) {
			this.m_f = Formatter.getInstance(PIMRuntime.getInstance());
		}
		return this.m_f;
	}
	
	public String getID() {
		return "LastCalled".toLowerCase();
	}

	public String getNamespace() {
		return NAMESPACE;
	}
}
