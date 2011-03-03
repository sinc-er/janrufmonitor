package de.janrufmonitor.ui.jface.application.rendering;

import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;

public class GeoCoding extends AbstractTableCellRenderer implements IJournalCellRenderer, IEditorCellRenderer {

	private static String NAMESPACE = "ui.jface.application.rendering.GeoCoding";
	
	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall) {
				this.m_o = ((ICall)this.m_o).getCaller();
			}
			if (this.m_o instanceof ICaller) {
				IAttributeMap m = ((ICaller)(this.m_o)).getAttributes();
				if (m.contains(IJAMConst.ATTRIBUTE_NAME_GEO_LAT) && m.contains(IJAMConst.ATTRIBUTE_NAME_GEO_LNG)) {
					StringBuffer co = new StringBuffer();
					co.append(getLatitute(m.get(IJAMConst.ATTRIBUTE_NAME_GEO_LAT).getValue()));
					co.append(", ");
					co.append(getLongitute(m.get(IJAMConst.ATTRIBUTE_NAME_GEO_LNG).getValue()));
					return co.toString();
				}				
			}
		}
		return "";
	}
	
	public String getID() {
		return "GeoCoding".toLowerCase();
	}

	public String getNamespace() {
		return NAMESPACE;
	}
	
	private String getLatitute(String v) {
		double lng = Double.parseDouble(v);
		
		double h = this.truncate(lng);		
		int h_int = (int)h;
		
		// get minutes
		lng -= h_int;
		
		double m = lng * 60;
		m = this.truncate(m);
		
		int m_int = (int)m;
		
		// get seconds
		lng -=((double)m_int/60);
		
		double s = lng * 60 *60;
		int s_int = (int)s;
		
		String unit = (h_int<0 ? "S" : "N");
		
		StringBuffer lngstring = new StringBuffer(16);
		lngstring.append(Math.abs(h_int));
		lngstring.append("\u00BA ");
		lngstring.append(Math.abs(m_int));
		lngstring.append("' ");
		lngstring.append(Math.abs(s_int));
		lngstring.append("'' ");
		lngstring.append(unit);
		return lngstring.toString();
	}
	
	private String getLongitute(String v) {
		double lng = Double.parseDouble(v);
		
		double h = this.truncate(lng);		
		int h_int = (int)h;
		
		// get minutes
		lng -= h_int;
		
		double m = lng * 60;
		m = this.truncate(m);
		
		int m_int = (int)m;
		
		// get seconds
		lng -=((double)m_int/60);
		
		double s = lng * 60 *60;
		int s_int = (int)s;
		
		String unit = (h_int<0 ? "W" : "E");
		
		StringBuffer lngstring = new StringBuffer(16);
		lngstring.append(Math.abs(h_int));
		lngstring.append("\u00BA ");
		lngstring.append(Math.abs(m_int));
		lngstring.append("' ");
		lngstring.append(Math.abs(s_int));
		lngstring.append("'' ");
		lngstring.append(unit);
		return lngstring.toString();
	}
	
	private double truncate(double x){
	  if ( x > 0 )
	    return Math.floor(x * 100)/100;
	  else
	    return Math.ceil(x * 100)/100;
	}
	
}
