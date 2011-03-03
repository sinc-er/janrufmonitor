package de.janrufmonitor.ui.jface.application.dialog;

import java.util.Properties;

import org.eclipse.swt.program.Program;

import de.janrufmonitor.util.formatter.Formatter;

public class GenericWebDialogPlugin extends AbstractDialogPlugin {

	public GenericWebDialogPlugin() {
		super();
	}

	public String getLabel() {
		return this.getI18nManager().getString(this.getNamespace(), "title", "label", this.getLanguage());
	}

	private String getNamespace() {
		return "gcc.action."+this.ID;
	}

	public void run() {
		Properties configuration = getRuntime().getConfigManagerFactory().getConfigManager().getProperties(this.getNamespace());
		if (configuration!=null) {
			String url = configuration.getProperty("url");
			if (url!=null && url.trim().length()>0) {
				this.m_logger.info("Found valid web url to execute: "+url);
				Formatter f = Formatter.getInstance(this.getRuntime());
				url = f.parse(url, this.m_dialog.getCall());
				this.m_logger.info("Parsed web url to execute: "+url);
				Program.launch(url);
			}
		} else {
			this.m_logger.warning("Found invalid configuration for namespace: "+this.getNamespace());
		}
	}

	public boolean isEnabled() {
		return !this.m_dialog.getCall().getCaller().getPhoneNumber().isClired();
	}

}
