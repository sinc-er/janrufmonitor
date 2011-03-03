package de.janrufmonitor.ui.jface.application.journal.action;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.exception.Message;
import de.janrufmonitor.exception.PropagationFactory;
import de.janrufmonitor.repository.ICallManager;
import de.janrufmonitor.repository.types.ILocalRepository;
import de.janrufmonitor.repository.types.IReadCallRepository;
import de.janrufmonitor.repository.types.IRemoteRepository;
import de.janrufmonitor.repository.types.IWriteCallRepository;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.jface.application.action.IAction;
import de.janrufmonitor.ui.jface.application.journal.Journal;
import de.janrufmonitor.ui.jface.application.journal.JournalConfigConst;
import de.janrufmonitor.ui.swt.DisplayManager;
import de.janrufmonitor.util.io.PathResolver;

public class NewJournalAction extends AbstractAction {

	private class SubAction extends AbstractAction implements
			JournalConfigConst {

		private ILocalRepository m_lr;

		public SubAction(ILocalRepository lr) {
			super();
			this.m_lr = lr;
			this.setText(this.getI18nManager().getString(this.getNamespace(),
					"title", "label", this.getLanguage())
					+ "...");
		}

		public IRuntime getRuntime() {
			return NewJournalAction.this.getRuntime();
		}

		public String getNamespace() {
			return m_lr.getNamespace();
		}

		public void run() {
			try {
				FileDialog dialog = new FileDialog(new Shell(DisplayManager
						.getDefaultDisplay()), SWT.SAVE);
				dialog.setText(this.getI18nManager().getString(
						this.getNamespace(), "title", "label",
						this.getLanguage()));
				String filter = getRuntime().getConfigManagerFactory().getConfigManager().getProperty(getNamespace(), "lastopeneddir");
				if (filter == null || filter.length() == 0 || !new File(filter).exists())
					filter = PathResolver.getInstance(getRuntime()).getUserDataDirectory();
				
				dialog.setFilterPath(filter);
				
				
				dialog.setFilterNames(new String[] { this.getI18nManager()
						.getString(this.getNamespace(), "title", "label",
								this.getLanguage())
						+ " (" + this.m_lr.getFileType() + ")" });
				dialog.setFilterExtensions(new String[] { this.m_lr
						.getFileType() });

				final String filename = dialog.open();
				if (filename == null)
					return;

				filter = new File(filename).getParentFile().getAbsolutePath();
				getRuntime().getConfigManagerFactory().getConfigManager().setProperty(getNamespace(), "lastopeneddir", filter);

				File f = new File(filename);

				if (f.exists()) {
					int style = SWT.APPLICATION_MODAL | SWT.YES | SWT.NO;
					MessageBox messageBox = new MessageBox(new Shell(
							DisplayManager.getDefaultDisplay()), style);
					messageBox.setMessage(NewJournalAction.this
							.getI18nManager().getString(
									NewJournalAction.this.getNamespace(),
									"override", "label", this.getLanguage()));
					if (messageBox.open() == SWT.NO) {
						return;
					}
					//f.delete();
				}
				if (this.m_lr instanceof ICallManager) {
					NewJournalAction.this.m_app.getApplication().getConfiguration().setProperty(
							CFG_REPOSITORY,
							((ICallManager) this.m_lr).getManagerID());
					NewJournalAction.this.m_app.getApplication().storeConfiguration();
				}

				this.m_lr.setFile(filename);
				getRuntime().getConfigurableNotifier().notifyByNamespace(
						this.m_lr.getNamespace());

				updateLastOpenJournalEntries((ICallManager)this.m_lr);
				
				NewJournalAction.this.m_app.updateViews(true);

			} catch (Exception ex) {
				this.m_logger.log(Level.SEVERE, ex.getMessage(), ex);
				PropagationFactory.getInstance()
						.fire(
								new Message(Message.ERROR, getNamespace(),
										"error", ex));
			}
		}
		
		public boolean isEnabled() {
			return (this.m_lr instanceof ICallManager && ((ICallManager) this.m_lr).isActive() && ((ICallManager) this.m_lr).isSupported(IWriteCallRepository.class));
		}

		public String getID() {
			return "journal_new_journal_" + m_lr;
		}

	}

	private static String NAMESPACE = "ui.jface.application.journal.action.NewJournalAction";

	private IRuntime m_runtime;

	public NewJournalAction() {
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
		return "journal_new_journal";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public IAction[] getSubActions() {
		List localCallerManagers = getRuntime().getCallManagerFactory()
				.getTypedCallManagers(ILocalRepository.class);

		filterReadOnlyRepositories(localCallerManagers);
		
		IAction[] actions = new IAction[localCallerManagers.size()];

		for (int i = 0; i < localCallerManagers.size(); i++) {
			actions[i] = new SubAction((ILocalRepository) localCallerManagers
					.get(i));
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
	
	private void filterReadOnlyRepositories(List rms) {
		ILocalRepository l = null;
		for (int i=rms.size()-1,j=0;i>=j;i--) {
			l = (ILocalRepository) rms.get(i);
			if (l instanceof ICallManager && ((ICallManager)l).isSupported(IReadCallRepository.class) && !((ICallManager)l).isSupported(IWriteCallRepository.class)) {
				rms.remove(i);
			}
		}
	}
	
	private void updateLastOpenJournalEntries(ICallManager mgr) {
		List cms = new ArrayList();


		Properties config = getRuntime().getConfigManagerFactory().getConfigManager().getProperties(LastOpenJournalAction.NAMESPACE);
		String lastOpen = config.getProperty(JournalConfigConst.CFG_LASTOPEN, "");
		if (lastOpen.length()>0) {
			String[] locm = lastOpen.split(";");
			if (locm!=null && locm.length>0) {
				String l = null;
				for (int i=0;i<locm.length;i++) {
					l = locm[i];
					cms.add(l);
				}
			}			
		}
		if (mgr instanceof ILocalRepository) {
			String newcm = mgr.getManagerID()+"%"+((ILocalRepository)mgr).getFile();
			if (!cms.contains(newcm))
				cms.add(0,newcm);	
		}
		if (mgr instanceof IRemoteRepository) {
			String newcm = mgr.getManagerID();
			if (!cms.contains(newcm))
				cms.add(0,newcm);	
		}
		cms = cms.subList(0, Math.min(cms.size(), 5));
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<cms.size();i++) {
			sb.append(cms.get(i));
			sb.append(";");
		}
		config.setProperty(JournalConfigConst.CFG_LASTOPEN, sb.toString());
		getRuntime().getConfigManagerFactory().getConfigManager().setProperties(LastOpenJournalAction.NAMESPACE, config);
		getRuntime().getConfigurableNotifier().notifyByNamespace(Journal.NAMESPACE);
	}

}
