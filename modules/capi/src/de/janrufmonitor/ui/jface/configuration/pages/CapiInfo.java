package de.janrufmonitor.ui.jface.configuration.pages;


import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import de.janrufmonitor.framework.monitor.IMonitor;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.configuration.AbstractConfigPage;

public class CapiInfo extends AbstractConfigPage {
	
    private String NAMESPACE = "ui.jface.configuration.pages.CapiInfo";
    
	private IRuntime m_runtime;

	public String getParentNodeID() {
		return "BasicIsdnSettings".toLowerCase();
	}
	
	public String getNodeID() {
		return "CapiInfo".toLowerCase();
	}

	public int getNodePosition() {
		return 999;
	}

	public String getNamespace() {
		return this.NAMESPACE;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null) 
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}

	public String getConfigNamespace() {
		return "";
	}
	
	protected Control createContents(Composite parent) {
		this.setTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));
		this.noDefaultAndApplyButton();
		
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(2, false));
		
		IMonitor capiMonitor = this.getRuntime().getMonitorListener().getMonitor("CapiMonitor");
		if (capiMonitor!=null) {
			String[] capiInfos = capiMonitor.getDescription();
			
			Label capi_label = new Label(c, 0);
			capi_label.setText(this.m_i18n.getString(this.getNamespace(), "capiinfo", "label", this.m_language));

			for (int i=capiInfos.length-1;i>=0;i--) {
				if (capiInfos[i].trim().length()>0 && !capiInfos[i].startsWith("PIM") && !capiInfos[i].startsWith("0000")) {
					Label capi = new Label(c, SWT.NULL);
					capi.setText(capiInfos[i]);
					capi = new Label(c, SWT.NULL);
				}
			}
		} else {
			this.m_logger.warning("No CAPI monitor object found.");
		}
		return c;
	}
	

}
