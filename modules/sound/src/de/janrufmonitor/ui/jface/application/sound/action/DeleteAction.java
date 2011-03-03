package de.janrufmonitor.ui.jface.application.sound.action;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.repository.types.IWriteCallerRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.sound.SoundConst;
import de.janrufmonitor.ui.jface.application.AbstractAction;

public class DeleteAction extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.sound.action.DeleteAction";
	
	private IRuntime m_runtime;

	public DeleteAction() {
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
		return "sound_delete";
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
					if (hasCallerSound((ICaller)o) && MessageDialog.openConfirm(
							new Shell(Display.getCurrent()),
							this.getI18nManager().getString(this.getNamespace(), "delete", "label", this.getLanguage()),
							this.getI18nManager().getString(this.getNamespace(), "delete", "description", this.getLanguage())
						)) {
						IAttribute att = ((ICaller)o).getAttribute(SoundConst.ATTRIBUTE_USER_SOUNDFILE);
						att.setValue("");
						((ICaller)o).setAttribute(att);
						
						this.m_app.getController().updateElement(o);
						
						this.m_app.updateViews(false);
					}
				}
			}
		}
	}
	
	private boolean hasCallerSound(ICaller c) {
		IAttribute att = c.getAttribute(SoundConst.ATTRIBUTE_USER_SOUNDFILE);
		if (att != null) {
			if (att != null && att.getValue().length()>0) {          		
				return true;
			}
		}
		return false;
	}
	
	public boolean isEnabled() {
		if (this.m_app!=null && this.m_app.getController()!=null) {
			Object o = this.m_app.getController().getRepository();
			return (o instanceof IWriteCallerRepository);
		}
		return false;
	}
}
