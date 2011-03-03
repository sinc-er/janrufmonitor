package de.janrufmonitor.ui.jface.wizards;

import de.janrufmonitor.framework.rules.IRule;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.wizards.pages.AbstractPage;
import de.janrufmonitor.ui.jface.wizards.pages.CallerReaderFactory;
import de.janrufmonitor.ui.jface.wizards.pages.RuleCipPage;
import de.janrufmonitor.ui.jface.wizards.pages.RuleExPhonesPage;
import de.janrufmonitor.ui.jface.wizards.pages.RuleMsnPage;
import de.janrufmonitor.ui.jface.wizards.pages.RuleNamePage;
import de.janrufmonitor.ui.jface.wizards.pages.RulePhonesPage;
import de.janrufmonitor.ui.jface.wizards.pages.RuleServicePage;
import de.janrufmonitor.ui.jface.wizards.pages.RuleTimeslotPage;

public class RuleWizard extends AbstractWizard {

	private String NAMESPACE = "ui.jface.wizards.RuleWizard"; 
	
	private IRule m_rule;
	private AbstractPage[] m_pages;
	private IRuntime m_runtime;
	
	public RuleWizard(IRule rule) {
		super();
    	setWindowTitle(this.m_i18n.getString(this.getNamespace(), "title", "label", this.m_language));
		
    	this.m_rule = rule;
    	CallerReaderFactory.invalidate();
		
		this.m_pages = new AbstractPage[7];
		this.m_pages[0] = new RuleNamePage((rule!=null ? rule.getName() : ""));
		this.m_pages[1] = new RuleServicePage((rule!=null ? rule.getServiceID() : ""));
		this.m_pages[2] = new RuleMsnPage((rule!=null ? rule.getMsn() : null));
		this.m_pages[3] = new RuleCipPage((rule!=null ? rule.getCip() : null));
		this.m_pages[4] = new RulePhonesPage((rule!=null ? rule.getPhonenumbers() : null));
		this.m_pages[5] = new RuleExPhonesPage((rule!=null ? rule.getExcludePhonenumbers() : null));
		this.m_pages[6] = new RuleTimeslotPage((rule!=null ? rule.getTimeslot() : null));

		this.addPage(this.m_pages[0]);
		this.addPage(this.m_pages[1]);
		this.addPage(this.m_pages[2]);
		this.addPage(this.m_pages[3]);
		this.addPage(this.m_pages[4]);
		this.addPage(this.m_pages[5]);
		this.addPage(this.m_pages[6]);
	}

	public String getID() {
		return RuleWizard.class.getName();
	}
	
	public String getNamespace() {
		return this.NAMESPACE;
	}

	public boolean performFinish() {
		if (this.m_pages[0].isPageComplete() &&
			this.m_pages[1].isPageComplete() &&
			this.m_pages[2].isPageComplete() &&
			this.m_pages[3].isPageComplete()) {
			
			this.m_rule = this.getRuntime().getRuleEngine().createRule(
					((RuleServicePage)	this.m_pages[1]).getResult(),
					((RuleMsnPage)	this.m_pages[2]).getResult(),
					((RuleCipPage)	this.m_pages[3]).getResult(),
					true,
					null
			);
			
			this.m_rule.setName(((RuleNamePage)	this.m_pages[0]).getResult());
			this.m_rule.setPhonenumbers(((RulePhonesPage)this.m_pages[4]).getResult());
			this.m_rule.setExcludePhonenumbers(((RuleExPhonesPage)this.m_pages[5]).getResult());
			this.m_rule.setTimeslot(((RuleTimeslotPage)this.m_pages[6]).getResult());
			
			return true;
		}
		this.m_rule = null;
		return false;
	}
	
	public IRule getResult() {
		return this.m_rule;
	}
	
	public boolean performCancel() {
		this.m_rule = null;
		return super.performCancel();
	}

	protected IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}
}
