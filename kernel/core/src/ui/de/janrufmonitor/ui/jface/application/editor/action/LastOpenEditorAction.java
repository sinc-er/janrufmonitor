package de.janrufmonitor.ui.jface.application.editor.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;

import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.types.ILocalRepository;
import de.janrufmonitor.repository.types.IRemoteRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.action.IAction;
import de.janrufmonitor.ui.jface.application.editor.EditorConfigConst;

import de.janrufmonitor.ui.swt.DisplayManager;

public class LastOpenEditorAction extends AbstractAction {

	private class RemoteAction extends AbstractAction implements
			EditorConfigConst {

		private IRemoteRepository m_lr;

		public RemoteAction(IRemoteRepository lr) {
			super();
			this.m_lr = lr;
			this.setText(this.getI18nManager().getString(this.getNamespace(),
					"title", "label", this.getLanguage()));
		}

		public IRuntime getRuntime() {
			return LastOpenEditorAction.this.getRuntime();
		}

		public String getNamespace() {
			return m_lr.getNamespace();
		}

		public void run() {
			try {
				if (this.m_lr instanceof ICallerManager) {
					LastOpenEditorAction.this.m_app.getApplication()
							.getConfiguration().setProperty(
									CFG_REPOSITORY,
									((ICallerManager) this.m_lr)
											.getManagerID());
					LastOpenEditorAction.this.m_app.getApplication()
							.storeConfiguration();
				}

				getRuntime().getConfigurableNotifier().notifyByNamespace(
						this.m_lr.getNamespace());

				LastOpenEditorAction.this.m_app.updateViews(true);

			} catch (Exception ex) {
				this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);
				PropagationFactory.getInstance()
						.fire(
								new Message(Message.ERROR, getNamespace(),
										"error", ex));
			}
		}

		public boolean isEnabled() {
			return (this.m_lr instanceof ICallerManager && ((ICallerManager) this.m_lr).isActive());
		}
		
		public String getID() {
			return "editor_last_editor_" + m_lr;
		}

	}

	private class LocalAction extends AbstractAction implements
			EditorConfigConst {
	
		private ILocalRepository m_lr;
		private String m_path;
	
		public LocalAction(ILocalRepository lr, String path) {
			super();
			this.m_lr = lr;
			this.m_path = path;
			this.setText(path + " - "+this.getI18nManager().getString(this.getNamespace(),
					"title", "label", this.getLanguage()));
		}
	
		public IRuntime getRuntime() {
			return LastOpenEditorAction.this.getRuntime();
		}
	
		public String getNamespace() {
			return m_lr.getNamespace();
		}
	
		public void run() {
			try {
	
				File f = new File(this.m_path);
	
				if (f.exists()) {
					if (this.m_lr instanceof ICallerManager) {
						LastOpenEditorAction.this.m_app.getApplication()
								.getConfiguration().setProperty(
										CFG_REPOSITORY,
										((ICallerManager) this.m_lr)
												.getManagerID());
						LastOpenEditorAction.this.m_app.getApplication()
								.storeConfiguration();
					}
	
					this.m_lr.setFile(this.m_path);
					getRuntime().getConfigurableNotifier().notifyByNamespace(
							this.m_lr.getNamespace());
	
					LastOpenEditorAction.this.m_app.updateViews(true);
	
				}
	
			} catch (Exception ex) {
				this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);
				PropagationFactory.getInstance()
						.fire(
								new Message(Message.ERROR, getNamespace(),
										"error", ex));
			}
		}
		
		public boolean isEnabled() {
			return (this.m_lr instanceof ICallerManager && ((ICallerManager) this.m_lr).isActive());
		}
	
		public String getID() {
			return "editor_last_editor_" + m_lr;
		}
	
	}

	public static String NAMESPACE = "ui.jface.application.editor.action.LastOpenEditorAction";

	private IRuntime m_runtime;

	public LastOpenEditorAction() {
		super();
		this.setText(this.getI18nManager().getString(this.getNamespace(),
				"title", "label", this.getLanguage()));
	}

	public IRuntime getRuntime() {
		if (this.m_runtime == null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return "editor_lastopen_editor";
	}

	public String getNamespace() {
		return NAMESPACE;
	}
	
	
	private List getLastopenedRepositories()  {
		List cms = new ArrayList();
		Properties config = getRuntime().getConfigManagerFactory().getConfigManager().getProperties(NAMESPACE);
		String lastOpen = config.getProperty(EditorConfigConst.CFG_LASTOPEN, "");
		if (lastOpen.length()>0) {
			String[] locm = lastOpen.split(";");
			if (locm!=null && locm.length>0) {
				String l = null;
				for (int i=0;i<locm.length;i++) {
					l = locm[i];
					if (l.split("%").length==1) {
						ICallerManager mgr = getRuntime().getCallerManagerFactory().getCallerManager(l.split("%")[0]);
						if (mgr!=null && mgr.isActive() && mgr.isSupported(IRemoteRepository.class)) {
							cms.add(new RemoteAction((IRemoteRepository)mgr));
						}
						continue;
					}
					if (l.split("%").length==2) {
						ICallerManager mgr = getRuntime().getCallerManagerFactory().getCallerManager(l.split("%")[0]);
						if (mgr!=null && mgr.isActive() && mgr.isSupported(ILocalRepository.class)) {
							if (new File(l.split("%")[1]).exists())
								cms.add(new LocalAction((ILocalRepository)mgr, l.split("%")[1]));
						}
					}
				}
			}			
		}
		return cms;
	}



	public IAction[] getSubActions() {
		List lactions = this.getLastopenedRepositories();
		
		IAction[] actions = new IAction[lactions.size()];

		for (int i = 0; i < lactions.size(); i++) {
			actions[i] = (IAction) lactions.get(i);
		}
		return actions;
	}

	public boolean hasSubActions() {
		return true;
	}
	
	public void run() {	
		MessageDialog.openInformation(
				new Shell(DisplayManager.getDefaultDisplay()),
				"",
				this.getI18nManager().getString(getNamespace(), "nocms", "label", this.getLanguage()+this.getID())			
		);
	}

}
