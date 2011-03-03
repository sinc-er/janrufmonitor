package de.janrufmonitor.ui.jface.application.journal.rendering;

import java.util.Properties;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.rendering.AbstractTableCellEditorRenderer;
import de.janrufmonitor.ui.jface.application.rendering.IJournalCellRenderer;
/**
 * Sample to add new custom columns (renderer) to journal:
 * 
 * ui.jface.application.RendererRegistry\:renderer_<ID>\:value=de.janrufmonitor.ui.jface.application.journal.rendering.GenericUserDefinedRenderer
 * ui.jface.application.RendererRegistry\:renderer_<ID>\:access=system
 * ui.jface.application.RendererRegistry\:<ID>_id\:value=<ID>
 * ui.jface.application.RendererRegistry\:<ID>_id\:access=system
 * ui.jface.application.journal.rendering.generic.<ID>\:attribute-id\:value=<ID>
 * ui.jface.application.journal.rendering.generic.<ID>\:attribute-label\:value=<COLUMN LABEL>
 * 
 * @author brandt
 *
 */
public class GenericUserDefinedRenderer extends AbstractTableCellEditorRenderer implements IJournalCellRenderer, IConfigurable {

	private static String NAMESPACE = "ui.jface.application.journal.rendering.generic.";

	private static String CFG_ATTRIBUTE_ID = "attribute-id";
	private static String CFG_ATTRIBUTE_LABEL = "attribute-label";
	private static String CFG_ATTRIBUTE_EDITABLE = "attribute-editable";
	private static String CFG_ATTRIBUTE_TYPE = "attribute-type";
	private static String CFG_ATTRIBUTE_VALUES = "attribute-values";
	
	private String m_externalID;
	private Properties m_configuration;
	
	public GenericUserDefinedRenderer() {
		super();
	}
	
	public String renderAsText() {
		if (this.m_o!=null) {
			if (this.m_o instanceof ICall && this.doesAttributeExist()) {
				IAttribute att = ((ICall)this.m_o).getAttribute(this.m_configuration.getProperty(CFG_ATTRIBUTE_ID));
				if (att!=null) {
					switch (this.getType()) {
						case 1: {
							if (att.getValue().length()>0) {
								Integer i = Integer.valueOf(0);
								try {
									i = Integer.valueOf(att.getValue());
								} catch(Exception e) {}		
								
								if (i.intValue()<getValues().length)
									return getValues()[i.intValue()];
							}					
							break;
						}
						default:					
					}
					return att.getValue();
				}
			}
		}
		return "";
	}
	
	public String getID() {
		return (this.m_externalID==null ? "Generic" : this.m_externalID.toLowerCase());
	}

	public String getNamespace() {
		return NAMESPACE + getID();
	}

	public IAttribute getAttribute() {
		if (this.doesAttributeExist())
			return PIMRuntime.getInstance().getCallFactory().createAttribute(this.m_configuration.getProperty(CFG_ATTRIBUTE_ID), "");
		return null;
	}

	public void applyAttributeChanges(Object o, IAttribute att, Object value) {
		if (att!=null && o instanceof ICall && this.doesAttributeExist()) {
			if (value instanceof Integer) {
				att.setValue(((Integer)value).toString());
				((ICall)o).setAttribute(att);
			}
			
			if (value instanceof Boolean) {
				att.setValue(((Boolean)value).toString());
				((ICall)o).setAttribute(att);
			}
			
			if (value instanceof String) {
				if (((String)value).length()>0) {
					att.setValue((String)value);
					((ICall)o).setAttribute(att);				
				} else {
					((ICall)o).getAttributes().remove(this.m_configuration.getProperty(CFG_ATTRIBUTE_ID));
				}
			}
		}		
	}
	
	private boolean doesAttributeExist() {
		return (this.m_configuration.getProperty(CFG_ATTRIBUTE_ID)!=null);
	}
	
	private boolean doesAttributeLabelExist() {
		return (this.m_configuration.getProperty(CFG_ATTRIBUTE_LABEL)!=null);
	}
	
	public String getHeader() {
		return this.getLabel();
	}

	public String getLabel() {
		return (this.doesAttributeLabelExist() ? this.m_configuration.getProperty(CFG_ATTRIBUTE_LABEL) : super.getLabel());
	}

	public void setID(String id) {
		this.m_externalID = id;
		PIMRuntime.getInstance().getConfigurableNotifier().register(this);
	}

	public String getConfigurableID() {
		return getID();
	}

	public void setConfiguration(Properties configuration) {
		this.m_configuration = configuration;
	}
	
	public boolean isEditable() {
		return (this.m_configuration.getProperty(CFG_ATTRIBUTE_EDITABLE, "true").equalsIgnoreCase("true"));
	}

	public int getType() {
		return Integer.parseInt(this.m_configuration.getProperty(CFG_ATTRIBUTE_TYPE, "0"));
	}

	public String[] getValues() {
		return this.m_configuration.getProperty(CFG_ATTRIBUTE_VALUES, "").split(",");
	}
}
