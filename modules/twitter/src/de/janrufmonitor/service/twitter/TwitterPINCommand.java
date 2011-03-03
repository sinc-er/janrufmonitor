package de.janrufmonitor.service.twitter;

import java.util.logging.Level;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAsyncDisplayCommand;
import de.janrufmonitor.ui.jface.wizards.TwitterPINWizard;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class TwitterPINCommand extends AbstractAsyncDisplayCommand {

	private static String NAMESPACE = "service.twitter.TwitterPINCommand";
	
	private IRuntime m_runtime;
	
	public boolean isExecutable() {
		return true;
	}

	public String getID() {
		return "TwitterPINCommand";
	}

	public void asyncExecute() {
		try {
			WizardDialog.setDefaultImage(SWTImageManager.getInstance(getRuntime()).get(IJAMConst.IMAGE_KEY_PIM_ICON));
			TwitterPINWizard tpw = new TwitterPINWizard();
		    WizardDialog dlg = new WizardDialog(new Shell(DisplayManager.getDefaultDisplay()), tpw);
		    dlg.open();
		} catch (Throwable t) {
			this.m_logger.log(Level.SEVERE, t.getMessage(), t);
			PropagationFactory.getInstance().fire(new Message(Message.ERROR, this.getNamespace(), t.toString().toLowerCase(), t));
		} finally {
			this.isExecuting = false;
		}
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getNamespace() {
		return NAMESPACE;
	}

}
