package de.janrufmonitor.ui.jface.application.editor.action;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.wizards.CallerWizard;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTImageManager;

public abstract class CallerAction extends AbstractAction {

	public CallerAction() {
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
	
	protected ICaller openCallerWizard(ICaller caller) {
	    Display display = DisplayManager.getDefaultDisplay();
		Shell shell = new Shell(display);

	    WizardDialog.setDefaultImage(SWTImageManager.getInstance(this.getRuntime()).get(IJAMConst.IMAGE_KEY_PIM_ICON));
	    CallerWizard callerWiz = new CallerWizard(caller);
	    WizardDialog dlg = new WizardDialog(shell, callerWiz);
	    dlg.open();
	    if (dlg.getReturnCode() == WizardDialog.OK) {
	    	return callerWiz.getResult();
	    }
	    return null;
	}
}
