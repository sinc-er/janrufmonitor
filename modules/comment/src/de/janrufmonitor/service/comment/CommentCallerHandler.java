package de.janrufmonitor.service.comment;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.repository.identify.Identifier;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.comment.api.IAttachment;
import de.janrufmonitor.service.comment.api.IComment;
import de.janrufmonitor.service.comment.api.ICommentCaller;
import de.janrufmonitor.service.comment.impl.Attachment;
import de.janrufmonitor.service.comment.impl.Comment;
import de.janrufmonitor.service.comment.impl.CommentCaller;
import de.janrufmonitor.util.io.PathResolver;

public class CommentCallerHandler {

	String CFG_DATA_FILENAME_EXTENSION = "fileext";
	
	Properties m_configuration;
	Logger m_logger;
	
	public CommentCallerHandler(Properties configuration) {
		this.m_configuration = configuration;
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
	} 
	
	public boolean hasCommentCaller(IPhonenumber pn) {
		ICaller caller = Identifier.identifyDefault(PIMRuntime.getInstance(), pn);
		if (caller!=null)
			return this.hasCommentCallerFromHierarchy(caller);
		return false;
	}
	
	public boolean hasCommentCaller(ICaller caller) {
		return this.hasCommentCallerFromHierarchy(caller);
	}
		
	public ICommentCaller getCommentCaller(IPhonenumber pn) {
		ICaller caller = Identifier.identifyDefault(PIMRuntime.getInstance(), pn);
		if (caller!=null)
			return this.getCommentCallerFromHierarchy(caller);
		return null;
	}
	
	public ICommentCaller getCommentCaller(ICaller caller) {
		return this.getCommentCallerFromHierarchy(caller);
	}
	
	public ICommentCaller createCommentCaller(ICaller caller) {
		return new CommentCaller(caller);
	}
	
	public void setCommentCaller(ICommentCaller commentCaller) {
		ICaller caller = commentCaller.getCaller();
		if (caller==null)
			return;
			
		String callerDirectoryName = this.getCommentRoot() + this.getCommentLocation(caller.getPhoneNumber());
		
		String callerPath = this.getCommentRoot() + this.getCommentLocation(caller.getPhoneNumber());
		
		File dir = new File(callerPath);
		dir.mkdirs();
		
		this.storeComments(commentCaller.getComments(), callerDirectoryName);
		
	}
	
	private boolean hasCommentCallerFromHierarchy(ICaller caller) {
		String callerDirectoryName = this.getCommentRoot() + this.getCommentLocation(caller.getPhoneNumber());
		
		File callerDir = new File(callerDirectoryName);
		
		if (callerDir.exists()) {
			this.createCommentCaller(caller);
			File[] comments = callerDir.listFiles(
				new FileFilter() {
					public boolean accept(File pathname) {
						if (pathname.getAbsolutePath().endsWith(getCommentFilenameExtension())){
							return true;
						}
						return false;
					}
				}
			);
	
			// added: 2006/01/22: only show Yes flag, if comments are not empty
			if (comments.length>0) {
				long size = 0;
				for (int i=0;i<comments.length;i++) {
					size += comments[i].length();
				}
				return size > 0;
			}
								
		}
		
		this.m_logger.info("No comments for caller "+caller.toString()+" found.");
		return false;
	}
	
	private ICommentCaller getCommentCallerFromHierarchy(ICaller caller) {
		String callerDirectoryName = this.getCommentRoot() + this.getCommentLocation(caller.getPhoneNumber());
		
		File callerDir = new File(callerDirectoryName);
		
		if (callerDir.exists()) {
			ICommentCaller commentCaller = this.createCommentCaller(caller);
			File[] comments = callerDir.listFiles(
				new FileFilter() {
					public boolean accept(File pathname) {
						if (pathname.getAbsolutePath().endsWith(getCommentFilenameExtension())){
							return true;
						}
						return false;
					}
				}
			);

			for (int i=0;i<comments.length;i++) {
				IComment comment = this.createComment(comments[i]);
				
				// TODO: removed due to performance reasons 
//				File attachmentFolder = new File(callerDirectoryName + comment.getID());
//				if (attachmentFolder.exists()) {
//					File[] attachments = attachmentFolder.listFiles();
//					for (int j=0;j<attachments.length;j++) {
//						IAttachment attachment = this.createAttachment(attachments[j]);
//						comment.addAttachment(attachment);
//					}
//				}
				commentCaller.addComment(comment);
			}
			
			return commentCaller;					
		}
		
		this.m_logger.info("Comments for caller "+caller.toString()+" does not exist.");
		return null;
	}
	
	private String getCommentRoot() {
		String root = this.m_configuration.getProperty("path","");
		
		root = PathResolver.getInstance(PIMRuntime.getInstance()).resolve(root);
		
		if (!root.endsWith(File.separator)) {
			root += File.separator;
		}
		return root;
	}
	
	private String getCommentLocation(IPhonenumber pn) {
		if (pn.isClired())
			return "";
		
		String location = pn.getIntAreaCode() + File.separator + 
						  pn.getAreaCode() + File.separator + 
						  pn.getCallNumber() + File.separator;
		
		return location;
	}
	
	private String getCommentFilenameExtension() {
		return this.m_configuration.getProperty(CFG_DATA_FILENAME_EXTENSION,"");
	}
	
	public IComment createComment() {
		return new Comment();
	}
	
	private IComment createComment(File commentFile) {
		IComment comment = new Comment();
		
		String commentID = commentFile.getName();
		commentID = commentID.substring(0, commentID.indexOf(this.getCommentFilenameExtension()));
		
		comment.setID(commentID);
		comment.setDate(new Date(commentFile.lastModified()));
		
		try {
			FileReader commentReader = new FileReader(commentFile);
			BufferedReader bufReader = new BufferedReader(commentReader);
			StringBuffer text = new StringBuffer();
			while (bufReader.ready()) {
				text.append(bufReader.readLine());
				text.append(IJAMConst.CRLF);
			}
			bufReader.close();
			commentReader.close();
			comment.setText(text.substring(0));
		} catch (FileNotFoundException ex) {
			this.m_logger.warning("Cannot find comment file " + commentFile);
		} catch (IOException ex) {
			this.m_logger.severe("IOException on file " + commentFile);
		}
		
		File commentAttributesFile = new File(commentFile.getAbsolutePath()+".attributes");
		if (commentAttributesFile.exists() && commentAttributesFile.isFile()) {
			Properties commentAttributes = new Properties();
			try {
				FileInputStream in = new FileInputStream(commentAttributesFile);
				commentAttributes.load(in);
				in.close();
				
				Iterator it = commentAttributes.keySet().iterator();
				IAttribute a = null;
				String key = null;
				while (it.hasNext()) {
					key = (String) it.next();
					a = PIMRuntime.getInstance().getCallerFactory().createAttribute(key, commentAttributes.getProperty(key));
					comment.addAttribute(a);
				}
			} catch (FileNotFoundException ex) {
				this.m_logger.severe("File not found: " + commentAttributesFile.getAbsolutePath());
			} catch (IOException ex) {
				this.m_logger.severe("IOException on file " + commentAttributesFile.getAbsolutePath());
			}

		}
		
		return comment;
	}
	
	public IAttachment createAttachment(File attachmentFile) {
		IAttachment attach = new Attachment(attachmentFile.getName());
		
		try {
			FileInputStream fis = new FileInputStream(attachmentFile);
			attach.setContent(fis);
		} catch (FileNotFoundException e) {
			this.m_logger.severe(e.getMessage());
		}
		
		return attach;
	}
	
	public void setConfiguration(Properties config) {
		this.m_configuration = config;
	}
	
	private void storeComments(List comments, String directory) {
		
		// clean up old comments
		File[] oldComments = new File(directory).listFiles(
			new FileFilter() {
				public boolean accept(File pathname) {
					if (pathname.getAbsolutePath().endsWith(getCommentFilenameExtension())){
						return true;
					}
					return false;
				}
			}
		);

		for (int i=0;i<oldComments.length;i++)
			oldComments[i].delete();		
							
		for (int i=0;i<comments.size();i++) {
					
			IComment comment = (IComment)comments.get(i);
			
			File commentFile = new File(directory + comment.getID() + this.getCommentFilenameExtension());
			try {
				FileWriter commentWriter = new FileWriter(commentFile);
				BufferedWriter bufWriter = new BufferedWriter(commentWriter);
		
				bufWriter.write(comment.getText());
				bufWriter.flush();
				
				bufWriter.close();
				commentWriter.close();
			} catch (FileNotFoundException ex) {
				this.m_logger.severe("File not found: " + directory + comment.getID());
			} catch (IOException ex) {
				this.m_logger.severe("IOException on file " + directory + comment.getID());
			}
			commentFile.setLastModified(comment.getDate().getTime());
			
			Properties commentAttributes = new Properties();
			IAttributeMap m = comment.getAttributes();
			Iterator it = m.iterator();
			IAttribute a = null;
			while (it.hasNext()) {
				a = (IAttribute) it.next();
				commentAttributes.setProperty(a.getName(), a.getValue());
			}
			
			File commentAttributesFile = new File(commentFile.getAbsolutePath()+".attributes");

			try {
				FileOutputStream fos = new FileOutputStream(commentAttributesFile);
				commentAttributes.store(fos, "");
				fos.flush();
				fos.close();
			} catch (FileNotFoundException ex) {
				this.m_logger.severe("File not found: " + directory + comment.getID());
			} catch (IOException ex) {
				this.m_logger.severe("IOException on file " + directory + comment.getID());
			}
			
			commentAttributesFile.setLastModified(comment.getDate().getTime());
			
			// TODO: removed due to performance reasons
			//this.storeAttachments(comment.getAttachments(), directory + comment.getID());
						
		}
	}
	
	/**
	private void storeAttachments(List attachments, String directory) {
		
		File attachmentDir = new File(directory);
		
		attachmentDir.mkdirs();
		
		File[] attachmentFiles = attachmentDir.listFiles();
		for (int i=0;i<attachmentFiles.length;i++) {
			attachmentFiles[i].delete();
		}
		
		for (int i=0;i<attachments.size();i++) {
			IAttachment attachment = (IAttachment) attachments.get(i);
			
			try {
				FileOutputStream attachmentFile = new FileOutputStream(directory + File.separator + attachment.getName());
				InputStream in = attachment.getConent();
			
				Stream.copy(in, attachmentFile);
			
			} catch (FileNotFoundException e) {
				this.m_logger.severe(e.getMessage());
			} catch (IOException e) {
				this.m_logger.severe(e.getMessage());
			}
		}
	}
	*/

}
