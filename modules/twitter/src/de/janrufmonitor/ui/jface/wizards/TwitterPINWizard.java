package de.janrufmonitor.ui.jface.wizards;

import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.wizards.pages.AbstractPage;
import de.janrufmonitor.ui.jface.wizards.pages.TwitterPINPage;

public class TwitterPINWizard extends AbstractWizard {

	private String NAMESPACE = "ui.jface.wizards.TwitterPINWizard"; 

	private IRuntime m_runtime;
	private AbstractPage[] m_pages;
	
	private String[] m_tokens;
	
	public TwitterPINWizard() {
		super();
		setWindowTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));
		this.m_pages = new AbstractPage[1];
		this.m_pages[0] = new TwitterPINPage(TwitterPINPage.class.getName());
		
		this.addPage(this.m_pages[0]);
	}
	
	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

	public String getID() {
		return TwitterPINWizard.class.getName();
	}

	public String getNamespace() {
		return NAMESPACE;
	}


	public boolean performFinish() {
		String[] tokens = ((TwitterPINPage)this.m_pages[0]).getResult();
		if (tokens!=null) {
			this.m_tokens = tokens;
			this.getRuntime().getConfigManagerFactory().getConfigManager().setProperty("service.TwitterService", "auth1", tokens[0]);
			this.getRuntime().getConfigManagerFactory().getConfigManager().setProperty("service.TwitterService", "auth2", tokens[1]);
			this.getRuntime().getConfigManagerFactory().getConfigManager().saveConfiguration();
			return true;
		}
		return false;
	}
	
	public String[] getResults() {
		return this.m_tokens;
	}

}
