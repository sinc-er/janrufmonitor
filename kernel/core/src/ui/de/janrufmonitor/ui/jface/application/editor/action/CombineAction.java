package de.janrufmonitor.ui.jface.application.editor.action;

import java.util.Iterator;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IMultiPhoneCaller;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.wizards.CallerCombineWizard;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.ui.swt.SWTImageManager;

public class CombineAction extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.editor.action.CombineAction";

	private IRuntime m_runtime;

	public CombineAction() {
		super();
		this.setText(this.getI18nManager().getString(this.getNamespace(),
				"title", "label", this.getLanguage()));
	}

	public String getID() {
		return "editor_combinet";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime == null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public void run() {
		Viewer v = this.m_app.getApplication().getViewer();
		if (v != null) {
			IStructuredSelection selection = (IStructuredSelection) v
					.getSelection();
			if (!selection.isEmpty()) {
				Iterator i = selection.iterator();
				ICallerList list = getRuntime().getCallerFactory()
						.createCallerList();
				
				ICallerList olist = getRuntime().getCallerFactory()
				.createCallerList();
				
				Object o = null;
				while (i.hasNext()) {
					o = i.next();
					olist.add((ICaller) o);
					if (o instanceof IMultiPhoneCaller) {
						list.add((IMultiPhoneCaller) o);
					} else if (o instanceof ICaller) {
						list.add(getRuntime().getCallerFactory()
								.toMultiPhoneCaller((ICaller) o));
					}
				}

				if (list.size() < 2)
					return;

				Display display = DisplayManager.getDefaultDisplay();
				Shell shell = new Shell(display);

				WizardDialog.setDefaultImage(SWTImageManager.getInstance(
						this.getRuntime()).get(IJAMConst.IMAGE_KEY_PIM_ICON));
				CallerCombineWizard callerWiz = new CallerCombineWizard(list);
				WizardDialog dlg = new WizardDialog(shell, callerWiz);
				dlg.open();
				if (dlg.getReturnCode() == WizardDialog.OK) {
					IMultiPhoneCaller c = callerWiz.getResult();
					if (c != null) {
						// remove merged contact from olist
						ICaller ca, rmc = null;
						for (int j=olist.size()-1;j>=0;j--) {
							ca = olist.get(j);
							if (ca.getUUID().equalsIgnoreCase(c.getUUID())) {
								rmc = ca;
								m_logger.info("Merged user detected: "+rmc);
							}
						}
						if (rmc!=null) olist.remove(rmc);
						
						this.m_app.getController().deleteElements(olist);
						this.m_app.getController().updateElement(c);
						this.m_app.updateViews(true);
					}
				}
			}
		}
	}
	
	public boolean isEnabled() {
		if (this.m_app!=null && this.m_app.getController()!=null) {
			Object o = this.m_app.getController().getRepository();
			if (o instanceof ICallerManager) {
				return ((ICallerManager)o).isSupported(IWriteCallerRepository.class);
			}
		}
		return false;
	}

}
