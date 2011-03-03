package de.janrufmonitor.ui.jface.application.action;

import java.util.Properties;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.program.Program;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.util.formatter.Formatter;

public class GenericWebAction extends AbstractAction {

	private IRuntime m_runtime;

	public GenericWebAction() {
		super();
	}
	
	public void setID(String id) {
		super.setID(id);
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

	public String getNamespace() {
		return "gcc.action."+this.getID();
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v!=null) {
			IStructuredSelection selection = (IStructuredSelection) v.getSelection();
			if (!selection.isEmpty()) {
				Object o = selection.getFirstElement();
				if (o instanceof ICall || o instanceof ICaller) {
					Properties configuration = getRuntime().getConfigManagerFactory().getConfigManager().getProperties(this.getNamespace());
					if (configuration!=null) {
						String url = configuration.getProperty("url");
						if (url!=null && url.trim().length()>0) {
							this.m_logger.info("Found valid web url to execute: "+url);
							Formatter f = Formatter.getInstance(this.getRuntime());
							url = f.parse(url, o);
							this.m_logger.info("Parsed web url to execute: "+url);
							Program.launch(url);
						}
					} else {
						this.m_logger.warning("Found invalid configuration for namespace: "+this.getNamespace());
					}
				}				
			}
		}
	}

	
}
