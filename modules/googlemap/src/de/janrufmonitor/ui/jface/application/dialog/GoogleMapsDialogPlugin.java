package de.janrufmonitor.ui.jface.application.dialog;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.service.IService;
import de.janrufmonitor.service.google.Maps;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTExecuter;

public class GoogleMapsDialogPlugin extends AbstractDialogPlugin {

	private static String NAMESPACE = "ui.jface.application.action.GoogleMapsLocalize";
	
	public GoogleMapsDialogPlugin() {
		super();
	}
	
	public String getLabel() {
		return this.getI18nManager().getString(this.getNamespace(), "label", "label", this.getLanguage());
	}

	public void run() {
		new SWTExecuter(this.getLabel()) {
			protected void execute() {
				
				ICaller c = m_dialog.getCall().getCaller();
				Properties configuration = getRuntime().getConfigManagerFactory().getConfigManager().getProperties(Maps.NAMESPACE);
				String url = configuration.getProperty("url");
				if (url==null || url.trim().length()==0) {
					MessageDialog.openError(
						new Shell(DisplayManager.getDefaultDisplay()),
						getI18nManager().getString(getNamespace(), "nourl", "label", getLanguage()),
						getI18nManager().getString(getNamespace(), "nourl", "description", getLanguage())
					);
					return;
				}
				
				IAttribute postalCode = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_COUNTRY);
				if (postalCode==null || postalCode.getValue().trim().length()==0) {
					MessageDialog.openError(
						new Shell(DisplayManager.getDefaultDisplay()),
						getI18nManager().getString(getNamespace(), "less", "label", getLanguage()),
						getI18nManager().getString(getNamespace(), "less", "description", getLanguage())
					);
					return;
				}
				
				IAttribute city = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_CITY);
				if (city==null || city.getValue().trim().length()==0) {
					MessageDialog.openError(
						new Shell(DisplayManager.getDefaultDisplay()),
						getI18nManager().getString(getNamespace(), "less", "label", getLanguage()),
						getI18nManager().getString(getNamespace(), "less", "description", getLanguage())
					);
					return;
				}						
				
				StringBuffer map24Url = new StringBuffer();
				map24Url.append(url);
				map24Url.append(buildRequestParameters(c));
				
				m_logger.info("Requesting maps.google.com URL: "+map24Url.toString());
				Program.launch(map24Url.toString());

			}
		}.start();
	}

	public boolean isEnabled() {
		IService g = getRuntime().getServiceFactory().getService(Maps.ID);
		return (g!=null && g.isEnabled() && !this.m_dialog.getCall().getCaller().getPhoneNumber().isClired());
	}

	private String buildRequestParameters(ICaller c) {
		StringBuffer params = new StringBuffer();
		
		IAttribute att = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_STREET);
		if (att!=null){
			params.append("+");
			params.append(encode(att.getValue()));
		}
		
		att = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_STREET_NO);
		if (att!=null){
			params.append("+");
			params.append(encode(att.getValue()));
		}
		
		att = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_POSTAL_CODE);
		if (att!=null){
			params.append("+");
			params.append(encode(att.getValue()));
		}
		
		att = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_CITY);
		if (att!=null){
			params.append("+");
			params.append(encode(att.getValue()));
		}

			
		att = c.getAttribute(IJAMConst.ATTRIBUTE_NAME_COUNTRY);
		if (att!=null){
			params.append("+");
			params.append(encode(att.getValue()));
		}
		
		return params.toString();
	}
	
	private String encode(String text) {
		try {
			return URLEncoder.encode(text, "ISO-8859-1");
		} catch (UnsupportedEncodingException e) {
			this.m_logger.severe(e.getMessage());
		}
		return text;
	}
	
	private String getNamespace() {
		return NAMESPACE;
	}

}
