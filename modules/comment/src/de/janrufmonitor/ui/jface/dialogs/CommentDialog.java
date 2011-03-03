package de.janrufmonitor.ui.jface.dialogs;

import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.IService;
import de.janrufmonitor.service.comment.CommentCallerHandler;
import de.janrufmonitor.service.comment.CommentService;
import de.janrufmonitor.service.comment.api.IComment;
import de.janrufmonitor.util.formatter.Formatter;

public class CommentDialog extends TitleAreaDialog {

	private static String NAMESPACE = "ui.jface.dialogs.CommentDialog";
	
	private II18nManager m_i18n;
	private String m_language;
	private IRuntime m_runtime;
	private IComment m_comment;
	private Text m_textField;
	private Button m_followup;
	private Combo m_status;
	private Text m_subject;

	public CommentDialog(Shell shell, IComment comment, Object c) {
		super(shell);
		setShellStyle(SWT.BORDER | SWT.CLOSE | SWT.TITLE); 
		CommentCallerHandler ch = getHandler();
		if (comment==null && ch!=null) {
			this.m_comment = ch.createComment();
			String parsed = Formatter.getInstance(getRuntime()).parse(getCommentPrefix(), (c==null ? m_comment.getDate() :  c));
			this.m_comment.setText(parsed+ IJAMConst.CRLF);
		} else {
			this.m_comment = comment;
		}
	}
	
	public IComment getResult() {
			
		return m_comment;
	}

	protected Control createContents(Composite parent) {
		Control c = super.createContents(parent);
		
		setTitle(
			getI18nManager().getString(
				getNamespace(),
				"dialogtitle",
				"label",
				getLanguage()
			)
		);
		
		setMessage(
			getI18nManager().getString(
					getNamespace(),
					"dialogtitle",
					"description",
					getLanguage()
				)
			);
		return c;
	}
	
	protected Control createDialogArea(Composite parent) {
		Composite c = new Composite(parent, SWT.NONE);
		c.setLayout(new GridLayout(1, false));
		GridData gd = new GridData();
		gd = new GridData();
        gd.widthHint = 400;
       
		Label l = new Label(c, SWT.LEFT);
		l.setText(this.getI18nManager().getString(this.getNamespace(), "subject", "label", this.getLanguage()));
		l.pack();
		
		m_subject = new Text(c, SWT.SINGLE | SWT.BORDER);
		if (this.m_comment.getAttributes().get(IComment.COMMENT_ATTRIBUTE_SUBJECT)!=null && this.m_comment.getAttributes().get(IComment.COMMENT_ATTRIBUTE_SUBJECT).getValue().length()>0)
			m_subject.setText(this.m_comment.getAttributes().get(IComment.COMMENT_ATTRIBUTE_SUBJECT).getValue());
		m_subject.setLayoutData(gd);

//		Composite c1 = new Composite((isEndless()? parent : c), SWT.NONE);
//		c1.setLayout(new GridLayout(2, false));
		l = new Label(c, SWT.LEFT);
		l.setText(this.getI18nManager().getString(this.getNamespace(), "comments", "label", this.getLanguage()));
		l.pack();

		m_textField = new Text(c, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP);
		m_textField.setText(this.m_comment.getText());
		
		gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        gd.widthHint = 400;
		gd.heightHint = 200;
		m_textField.setLayoutData(gd);
		
		l = new Label(c, SWT.LEFT);
		l.setText(this.getI18nManager().getString(this.getNamespace(), "status", "label", this.getLanguage()));
		l.pack();
		
		this.m_status = new Combo (c, SWT.READ_ONLY);
		gd = new GridData();
        gd.horizontalAlignment = GridData.FILL;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 1;
        gd.widthHint = 200;
        this.m_status.setLayoutData(gd);
        this.buildCombobox(this.m_status, this.m_comment);
        
        m_followup = new Button(c, SWT.CHECK);
        m_followup.setText(this.getI18nManager().getString(this.getNamespace(), "followup", "label", this.getLanguage()));
        m_followup.setSelection(
        	(this.m_comment.getAttributes().get(IComment.COMMENT_ATTRIBUTE_FOLLOWUP)!=null ? this.m_comment.getAttributes().get(IComment.COMMENT_ATTRIBUTE_FOLLOWUP).getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_YES): false)
        );

//        Composite c2 = new Composite(c, SWT.NONE);
//        c2.setLayout(new GridLayout(2, false));
		return super.createDialogArea(parent);
	}
	
	private void buildCombobox(Combo combo, IComment c) {
		String values = getRuntime().getConfigManagerFactory().getConfigManager().
		getProperty("service.CommentService", "status");
		String[] v = values.split(",");
		combo.setItems(v);
		combo.select(0);
		String currentStatus = (c.getAttributes().get(IComment.COMMENT_ATTRIBUTE_STATUS)==null ? null : c.getAttributes().get(IComment.COMMENT_ATTRIBUTE_STATUS).getValue());
		for (int i=0;i<v.length;i++) {
			if (currentStatus!=null && currentStatus.equalsIgnoreCase(v[i])) {
				combo.select(i);
			}
		}		
	}
	
	protected void okPressed() {
		this.m_comment.setText(m_textField.getText());	
		IAttribute a = getRuntime().getCallerFactory().createAttribute(
				IComment.COMMENT_ATTRIBUTE_STATUS, this.m_status.getItem(this.m_status.getSelectionIndex()));
		this.m_comment.addAttribute(a);
		
		a = getRuntime().getCallerFactory().createAttribute(
				IComment.COMMENT_ATTRIBUTE_FOLLOWUP,
				(this.m_followup.getSelection() ? IJAMConst.ATTRIBUTE_VALUE_YES : IJAMConst.ATTRIBUTE_VALUE_NO)
		);
		this.m_comment.addAttribute(a);
		
		a = getRuntime().getCallerFactory().createAttribute(
				IComment.COMMENT_ATTRIBUTE_MODIFIED,
				Long.toString(System.currentTimeMillis())
		);
		this.m_comment.addAttribute(a);
		
		a = getRuntime().getCallerFactory().createAttribute(
				IComment.COMMENT_ATTRIBUTE_SUBJECT,
				m_subject.getText()
		);
		this.m_comment.addAttribute(a);
		
		super.okPressed();
	}
	
	// removed: 2008/02/12 not used anymore

//	private boolean isEndless() {
//		String endless = this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(
//			"service.CommentService",
//			"endless"
//		);
//		return endless.equalsIgnoreCase("true");
//	}
	
	private String getCommentPrefix()  {
		return this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(
				"service.CommentService",
				"starttext"
			);
	}
	
	private II18nManager getI18nManager() {
		if (this.m_i18n==null) {
			this.m_i18n = this.getRuntime().getI18nManagerFactory().getI18nManager();
		}
		return this.m_i18n;
	}

	private String getLanguage() {
		if (this.m_language==null) {
			this.m_language = 
				this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty(
					IJAMConst.GLOBAL_NAMESPACE,
					IJAMConst.GLOBAL_LANGUAGE
				);
		}
		return this.m_language;
	}
	
	private IRuntime getRuntime() {
		if (this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}
	
	private String getNamespace() {
		return NAMESPACE;
	}

	
	private CommentCallerHandler getHandler() {
		IService service = PIMRuntime.getInstance().getServiceFactory().getService("CommentService");
		if (service!=null) {
			if (service instanceof CommentService) {
				CommentService commentService = (CommentService)service;
				CommentCallerHandler cch = commentService.getHandler();
				return cch;
			}
		}
		return null;
	}
}
