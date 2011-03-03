package de.janrufmonitor.ui.jface.configuration.pages;

import org.eclipse.jface.preference.StringFieldEditor;

import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.repository.identify.Identifier;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.geo.GeoCoder;
import de.janrufmonitor.ui.jface.configuration.AbstractServiceFieldEditorConfigPage;
import de.janrufmonitor.ui.jface.configuration.IConfigPage;
import de.janrufmonitor.util.math.Point;

public class GeoCoding extends AbstractServiceFieldEditorConfigPage {

	private String NAMESPACE = "ui.jface.configuration.pages.GeoCoding";
    private String CONFIG_NAMESPACE = "service.GeoCoding";
    
	private IRuntime m_runtime;

	public String getConfigNamespace() {
		return this.CONFIG_NAMESPACE;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null) 
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	public String getParentNodeID() {
		return IConfigPage.SERVICE_NODE;
	}

	public String getNodeID() {
		return "GeoCoding".toLowerCase();
	}

	public int getNodePosition() {
		return 1;
	}

	protected void createFieldEditors() {
		super.createFieldEditors();
		
//		StringFieldEditor sfe = new StringFieldEditor(
//			this.getConfigNamespace()+SEPARATOR+"address",
//			this.m_i18n.getString(this.getNamespace(), "address", "label", this.m_language),
//			50,
//			this.getFieldEditorParent());
//		sfe.setEmptyStringAllowed(false);
//		addField(sfe);
		
		StringFieldEditor sfe = new StringFieldEditor(
			this.getConfigNamespace()+SEPARATOR+"local_street",
			this.m_i18n.getString(this.getNamespace(), "local_street", "label", this.m_language),
			25,
			this.getFieldEditorParent());
		sfe.setEmptyStringAllowed(true);
		addField(sfe);
		
		sfe = new StringFieldEditor(
			this.getConfigNamespace()+SEPARATOR+"local_streetno",
			this.m_i18n.getString(this.getNamespace(), "local_streetno", "label", this.m_language),
			4,
			this.getFieldEditorParent());
		sfe.setEmptyStringAllowed(true);
		addField(sfe);

		sfe = new StringFieldEditor(
			this.getConfigNamespace()+SEPARATOR+"local_pcode",
			this.m_i18n.getString(this.getNamespace(), "local_pcode", "label", this.m_language),
			5,
			this.getFieldEditorParent());
		sfe.setEmptyStringAllowed(true);
		addField(sfe);
		
		sfe = new StringFieldEditor(
			this.getConfigNamespace()+SEPARATOR+"local_city",
			this.m_i18n.getString(this.getNamespace(), "local_city", "label", this.m_language),
			25,
			this.getFieldEditorParent());
		sfe.setEmptyStringAllowed(true);
		addField(sfe);

		sfe = new StringFieldEditor(
			this.getConfigNamespace()+SEPARATOR+"local_country",
			this.m_i18n.getString(this.getNamespace(), "local_country", "label", this.m_language),
			25,
			this.getFieldEditorParent());
		sfe.setEmptyStringAllowed(true);
		addField(sfe);		
			
//		if (getRuntime().getConfigManagerFactory().getConfigManager().getProperty(CONFIG_NAMESPACE, "address").length()==0) {
//			IPhonenumber pn = getRuntime().getCallerFactory().createPhonenumber(getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_AREACODE).substring(1)+"123456789");
//			ICaller c = Identifier.identifyDefault(getRuntime(), pn);
//			if (c!=null && c.getAttributes().contains(IJAMConst.ATTRIBUTE_NAME_CITY)) {
//				getRuntime().getConfigManagerFactory().getConfigManager().setProperty(CONFIG_NAMESPACE, "address", c.getAttribute(IJAMConst.ATTRIBUTE_NAME_CITY).getValue());				
//			}			
//		}

		if (getRuntime().getConfigManagerFactory().getConfigManager().getProperty(CONFIG_NAMESPACE, "local_city").length()==0) {
			IPhonenumber pn = getRuntime().getCallerFactory().createPhonenumber(getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_AREACODE).substring(1)+"123456789");
			ICaller c = Identifier.identifyDefault(getRuntime(), pn);
			if (c!=null && c.getAttributes().contains(IJAMConst.ATTRIBUTE_NAME_CITY)) {
				getRuntime().getConfigManagerFactory().getConfigManager().setProperty(CONFIG_NAMESPACE, "local_city", c.getAttribute(IJAMConst.ATTRIBUTE_NAME_CITY).getValue());				
			}			
		}	
		if (getRuntime().getConfigManagerFactory().getConfigManager().getProperty(CONFIG_NAMESPACE, "local_country").length()==0) {
			IPhonenumber pn = getRuntime().getCallerFactory().createPhonenumber("111111111111111");
			ICaller c = Identifier.identifyDefault(getRuntime(), pn);
			if (c!=null && c.getAttributes().contains(IJAMConst.ATTRIBUTE_NAME_COUNTRY)) {
				getRuntime().getConfigManagerFactory().getConfigManager().setProperty(CONFIG_NAMESPACE, "local_country", c.getAttribute(IJAMConst.ATTRIBUTE_NAME_COUNTRY).getValue());				
			}			
		}			
	}

	public boolean performOk() {
		boolean ok = super.performOk();
		IAttributeMap address = getRuntime().getCallerFactory().createAttributeMap();
		if (getRuntime().getConfigManagerFactory().getConfigManager().getProperty(CONFIG_NAMESPACE, "local_street").trim().length()>0) {
			address.add(getRuntime().getCallerFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_STREET, 
				getRuntime().getConfigManagerFactory().getConfigManager().getProperty(CONFIG_NAMESPACE, "local_street").trim()));
		}
		if (getRuntime().getConfigManagerFactory().getConfigManager().getProperty(CONFIG_NAMESPACE, "local_streetno").trim().length()>0) {
			address.add(getRuntime().getCallerFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_STREET_NO, 
					getRuntime().getConfigManagerFactory().getConfigManager().getProperty(CONFIG_NAMESPACE, "local_streetno").trim()));
		}		
		if (getRuntime().getConfigManagerFactory().getConfigManager().getProperty(CONFIG_NAMESPACE, "local_pcode").trim().length()>0) {
			address.add(getRuntime().getCallerFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_POSTAL_CODE, 
					getRuntime().getConfigManagerFactory().getConfigManager().getProperty(CONFIG_NAMESPACE, "local_pcode").trim()));

		}				
		if (getRuntime().getConfigManagerFactory().getConfigManager().getProperty(CONFIG_NAMESPACE, "local_city").trim().length()>0) {
			address.add(getRuntime().getCallerFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_CITY, 
					getRuntime().getConfigManagerFactory().getConfigManager().getProperty(CONFIG_NAMESPACE, "local_city").trim()));

		}
		if (getRuntime().getConfigManagerFactory().getConfigManager().getProperty(CONFIG_NAMESPACE, "local_country").trim().length()>0) {
			address.add(getRuntime().getCallerFactory().createAttribute(IJAMConst.ATTRIBUTE_NAME_COUNTRY, 
					getRuntime().getConfigManagerFactory().getConfigManager().getProperty(CONFIG_NAMESPACE, "local_country").trim()));
		}				
		//String address = getRuntime().getConfigManagerFactory().getConfigManager().getProperty(CONFIG_NAMESPACE, "address");
		if (address!=null && address.size()>0) {
			Point loc = GeoCoder.getInstance().getCoordinates(address);
			if (loc!=null) {
				getRuntime().getConfigManagerFactory().getConfigManager().setProperty(CONFIG_NAMESPACE, "local-geo-lng", Double.toString(loc.getLongitude()));
				getRuntime().getConfigManagerFactory().getConfigManager().setProperty(CONFIG_NAMESPACE, "local-geo-lat", Double.toString(loc.getLatitude()));
				getRuntime().getConfigManagerFactory().getConfigManager().setProperty(CONFIG_NAMESPACE, "local-geo-acc", Integer.toString(loc.getAccurance()));
			}
			GeoCoder.invalidate();
		}		
		return ok;
	}
}