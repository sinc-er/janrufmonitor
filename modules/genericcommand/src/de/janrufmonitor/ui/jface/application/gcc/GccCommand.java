package de.janrufmonitor.ui.jface.application.gcc;

import java.util.Properties;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigurable;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAsyncDisplayCommand;
import de.janrufmonitor.ui.jface.application.gcc.wizard.GccWizard;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class GccCommand extends AbstractAsyncDisplayCommand implements IConfigurable {

	private static String NAMESPACE = "ui.jface.application.gcc.GccCommand";
	
	private IRuntime m_runtime;
	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getNamespace() {
		return GccCommand.NAMESPACE;
	}

	public boolean isExecutable() {
		return true;
	}

	public String getID() {
		return "GccCommand";
	}

	public String getConfigurableID() {
		return this.getID();
	}

	public void setConfiguration(Properties configuration) {
	}

	public void asyncExecute() {
		try {
			WizardDialog.setDefaultImage(SWTImageManager.getInstance(this.getRuntime()).get(IJAMConst.IMAGE_KEY_PIM_ICON));
			GccWizard gccw = new GccWizard();
		    WizardDialog dlg = new WizardDialog(new Shell(DisplayManager.getDefaultDisplay()), gccw);
		    dlg.open();
		    if (dlg.getReturnCode() == WizardDialog.OK) {
		    	
		    }
		} catch (Throwable t) {
			t.printStackTrace();
			this.m_logger.severe(t.getMessage()+" : "+t.toString());
			PropagationFactory.getInstance().fire(new Message(Message.ERROR, this.getNamespace(), t.toString().toLowerCase(), t));
		} finally {
			this.isExecuting = false;
		}
	}

}
