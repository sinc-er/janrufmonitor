package de.janrufmonitor.ui.jface.application.action;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Properties;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.program.Program;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.IService;
import de.janrufmonitor.service.google.Maps;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.swt.DisplayManager;

public class GoogleMapsLocalize extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.action.GoogleMapsLocalize";
	
	private IRuntime m_runtime;

	public GoogleMapsLocalize() {
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

	public String getID() {
		return "googlemapslocalize";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null) {
			IStructuredSelection selection = (IStructuredSelection) v.getSelection();
			if (!selection.isEmpty()) {
				Object o = selection.getFirstElement();
				if (o instanceof ICall) {
					o = ((ICall)o).getCaller();
				}
				
				if (o instanceof ICaller) {
					// check if maps.google.com service is enabled
					IService googlemap = getRuntime().getServiceFactory().getService(Maps.ID);
					if (googlemap!=null && googlemap.isEnabled()) {
						Properties configuration = getRuntime().getConfigManagerFactory().getConfigManager().getProperties(Maps.NAMESPACE);
						String url = configuration.getProperty("url");
						if (url==null || url.trim().length()==0) {
							MessageDialog.openError(
								new Shell(DisplayManager.getDefaultDisplay()),
								this.getI18nManager().getString(this.getNamespace(), "nourl", "label", this.getLanguage()),
								this.getI18nManager().getString(this.getNamespace(), "nourl", "description", this.getLanguage())
							);
							return;
						}
						
						IAttribute postalCode = ((ICaller)o).getAttribute(IJAMConst.ATTRIBUTE_NAME_COUNTRY);
						if (postalCode==null || postalCode.getValue().trim().length()==0) {
							MessageDialog.openError(
								new Shell(DisplayManager.getDefaultDisplay()),
								this.getI18nManager().getString(this.getNamespace(), "less", "label", this.getLanguage()),
								this.getI18nManager().getString(this.getNamespace(), "less", "description", this.getLanguage())
							);
							return;
						}
						
						IAttribute city = ((ICaller)o).getAttribute(IJAMConst.ATTRIBUTE_NAME_CITY);
						if (city==null || city.getValue().trim().length()==0) {
							MessageDialog.openError(
								new Shell(DisplayManager.getDefaultDisplay()),
								this.getI18nManager().getString(this.getNamespace(), "less", "label", this.getLanguage()),
								this.getI18nManager().getString(this.getNamespace(), "less", "description", this.getLanguage())
							);
							return;
						}						
						
						StringBuffer googlemapUrl = new StringBuffer();
						googlemapUrl.append(url);
						googlemapUrl.append(buildRequestParameters((ICaller)o));
						
						this.m_logger.info("Requesting maps.google.com URL: "+googlemapUrl.toString());
						Program.launch(googlemapUrl.toString());
					}
				}				
			}
		}
	}
	
	public boolean isEnabled() {
		IService googlemap = getRuntime().getServiceFactory().getService(Maps.ID);
		return googlemap!=null && googlemap.isEnabled();
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
}
