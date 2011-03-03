package de.janrufmonitor.ui.jface.application.comment.action;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;

import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.repository.identify.Identifier;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.IService;
import de.janrufmonitor.service.comment.CommentCallerHandler;
import de.janrufmonitor.service.comment.CommentService;
import de.janrufmonitor.service.comment.api.IComment;
import de.janrufmonitor.service.comment.api.ICommentCaller;
import de.janrufmonitor.ui.jface.application.AbstractAction;
import de.janrufmonitor.ui.swt.DisplayManager;

public class OldImportAction extends AbstractAction {

	private static String NAMESPACE = "ui.jface.application.comment.action.OldImportAction";
	
	private IRuntime m_runtime;

	public OldImportAction() {
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
		return "comment_import";
	}

	public String getNamespace() {
		return NAMESPACE;
	}

	public void run() {
		DirectoryDialog dialog = new DirectoryDialog (new Shell(DisplayManager.getDefaultDisplay()), SWT.OPEN);
		dialog.setText(this.getI18nManager().getString(getNamespace(), "dirtitle", "label", this.getLanguage()));
		dialog.setMessage(this.getI18nManager().getString(getNamespace(), "dirtitle", "description", this.getLanguage()));											
		String filename = dialog.open();
		if (filename!=null) {
			File dir = new File(filename);
			if (dir.exists() && dir.isDirectory()) {
				List files = this.getCommentFiles(dir);
				if (files.size()==0) {
					int style = SWT.APPLICATION_MODAL | SWT.OK;
					MessageBox messageBox = new MessageBox (new Shell(DisplayManager.getDefaultDisplay()), style);
					messageBox.setMessage (this.getI18nManager().getString(this.getNamespace(), "nomigration", "label", this.getLanguage()));
					if (messageBox.open () == SWT.OK);
				} else {
					
					this.doImport(files);
					
					int style = SWT.APPLICATION_MODAL | SWT.OK;
					MessageBox messageBox = new MessageBox (new Shell(DisplayManager.getDefaultDisplay()), style);
					messageBox.setMessage (files.size() + this.getI18nManager().getString(this.getNamespace(), "migration", "label", this.getLanguage()));
					if (messageBox.open () == SWT.OK);
				}
			}
			this.m_app.updateViews(false);
		}
	}
	
	private String getCommentContent(InputStream in) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buffer = new byte[8*1024];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1) {
			bout.write(buffer, 0, bytesRead);
		}
		bout.flush();
		in.close();
		bout.close();
		return new String(bout.toByteArray());
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
	
	private synchronized void doImport(List fileList) {
		this.m_logger.info("Starting import of "+fileList.size()+" comments.");
		
		for (int i=0;i<fileList.size();i++) {
			File f = (File)fileList.get(i);
			String number = f.getName().substring(1, f.getName().indexOf("."));			
			IPhonenumber pn = this.getRuntime().getCallerFactory().createPhonenumber(number);
			try {
				ICaller c = Identifier.identifyDefault(PIMRuntime.getInstance(), pn);
				
				ICommentCaller cc = null;
				if (this.getHandler().hasCommentCaller(c)) {
					cc = this.getHandler().getCommentCaller(c);
				} else {
					cc = this.getHandler().createCommentCaller(c);
				}

				IComment comment = this.getHandler().createComment();

				String content = this.getCommentContent(new FileInputStream(f));
				comment.setText(content);
				comment.setDate(new Date(f.lastModified()));
		
				cc.addComment(comment);
				this.getHandler().setCommentCaller(cc);
				
			} catch (FileNotFoundException e) {
				this.m_logger.severe(e.toString()+": "+e.getMessage());				
			} catch (IOException e) {
				this.m_logger.severe(e.toString()+": "+e.getMessage());
			}
		}
	}
	
	private List getCommentFiles(File dir) {
		List l = new ArrayList();
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (int i=0;i<files.length;i++)
				l.addAll(this.getCommentFiles(files[i]));
		}
		if (dir.getName().toLowerCase().endsWith(".pim"))
			l.add(dir);
		return l;
	}
}
