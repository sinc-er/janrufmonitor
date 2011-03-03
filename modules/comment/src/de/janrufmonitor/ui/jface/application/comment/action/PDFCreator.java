package de.janrufmonitor.ui.jface.application.comment.action;

import java.awt.Color;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.comment.api.IComment;
import de.janrufmonitor.service.comment.api.ICommentCaller;
import de.janrufmonitor.ui.jface.application.RendererRegistry;
import de.janrufmonitor.ui.jface.application.rendering.ITableCellRenderer;
import de.janrufmonitor.util.formatter.Formatter;

public class PDFCreator {
	
	private class CommentComparator implements Comparator {
		public int compare(Object o1, Object o2) {
			IComment c1 = (IComment)o1;
			IComment c2 = (IComment)o2;
			return c2.getDate().compareTo(c1.getDate());
		}
	}
	
	private ICommentCaller m_cc;
	private String m_file;
	private IRuntime m_runtime;
	private II18nManager m_i18n;
	private String m_language;
	private Logger m_logger;
	private Formatter m_f;
	
	public PDFCreator(ICommentCaller c, String f) {
		m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		this.m_cc = c;
		this.m_file = f;
	}
	
	public void createPdf() {
		Document document = new Document(PageSize.A4);
		document.addCreationDate();
		document.addCreator("jAnrufmonitor");
		
		try {
			PdfWriter.getInstance(document, new FileOutputStream(this.m_file));
			document.open();

			document.add(new Paragraph(this.getI18nManager().getString(getNamespce(), "pdftitle", "label", getLanguage()),
					FontFactory.getFont(FontFactory.HELVETICA, 16f, Font.BOLD | Font.UNDERLINE)));
			
			document.add(new Paragraph(" "));
			String msg = "";
			
			ITableCellRenderer tcr = RendererRegistry.getInstance().getRenderer("name");
			if (tcr!=null) {
				tcr.updateData(m_cc.getCaller());
				msg += tcr.renderAsText();
			}
			tcr = RendererRegistry.getInstance().getRenderer("number");
			if (tcr!=null) {
				tcr.updateData(m_cc.getCaller());
				msg += "\n"+tcr.renderAsText()+"\n";
			}
			
			document.add(new Paragraph(msg));
			document.add(new Paragraph(" "));
			
			List comments = m_cc.getComments();
			Collections.sort(comments, new CommentComparator());
			
			IComment c = null;
			
			PdfPTable table = new PdfPTable(1);
			table.setWidthPercentage(100f);
			
			PdfPCell cp = null;
			Paragraph pp = null;
			Color iterateColor1 = new Color(0xDD, 0xDD, 0xDD);
			Color iterateColor2 = new Color(0xFF, 0xFF, 0xFF);
			for (int i=0;i<comments.size();i++) {
				cp = new PdfPCell();
				cp.setBackgroundColor((i%2==0 ? iterateColor1 : iterateColor2));
				pp = new Paragraph();
				Paragraph p = new Paragraph();
				c = (IComment) comments.get(i);
				
				IAttribute att = c.getAttributes().get(IComment.COMMENT_ATTRIBUTE_SUBJECT);
				if (att!=null && att.getValue().length()>0) {
					p.add(new Chunk(
							this.getI18nManager().getString(getNamespce(), "pdfsubject", "label", getLanguage()),
							FontFactory.getFont(FontFactory.HELVETICA, 14f, Font.BOLD)
					));
					p.add(new Chunk(
						att.getValue(),
						FontFactory.getFont(FontFactory.HELVETICA, 14f, Font.BOLD)
					));
					
					pp.add(p);

					p = new Paragraph();
				}
				
				p.add(new Chunk(
						this.getI18nManager().getString(getNamespce(), "pdfdate", "label", getLanguage()),
						FontFactory.getFont(FontFactory.HELVETICA, 12f, Font.BOLD)
				));
				
				p.add(new Chunk(
					getFormatter().parse(IJAMConst.GLOBAL_VARIABLE_CALLTIME, c.getDate())
				));
				pp.add(p);

				p = new Paragraph();
				p.add(new Chunk(
						this.getI18nManager().getString(getNamespce(), "pdfstatus", "label", getLanguage()),
						FontFactory.getFont(FontFactory.HELVETICA, 12f, Font.BOLD)
				));				
				p.add(new Chunk(c.getAttributes().get(IComment.COMMENT_ATTRIBUTE_STATUS).getValue()));
				pp.add(p);
				
				att = c.getAttributes().get(IComment.COMMENT_ATTRIBUTE_FOLLOWUP);
				if (att!=null && att.getValue().equalsIgnoreCase(IJAMConst.ATTRIBUTE_VALUE_YES)) {
					p = new Paragraph();
					Chunk cu = new Chunk(
							this.getI18nManager().getString(getNamespce(), "pdffollowup", "label", getLanguage()),
							FontFactory.getFont(FontFactory.HELVETICA, 12f, Font.BOLD)
					);
					
					cu.setBackground(new Color(0xFF, 0xFF, 0x00));
					
					p.add(cu);				
					pp.add(p);
				}
				
				pp.add(new Paragraph(" "));
				p = new Paragraph(c.getText());
				pp.add(p);
				cp.addElement(pp);
				table.addCell(cp);
				
			}
			document.add(table);
			
		} catch (DocumentException de) {
			this.m_logger.severe(de.getMessage());
		} catch (IOException ioe) {
			this.m_logger.severe(ioe.getMessage());
		} finally {
			document.close();
		}
	}
	
	private Formatter getFormatter() {
		if (this.m_f==null) {
			this.m_f = Formatter.getInstance(PIMRuntime.getInstance());
		}
		return this.m_f;
	}
	
	private String getNamespce() {
		return "ui.jface.application.comment.action.PDFHistoryAction";
	}

	private IRuntime getRuntime() {
		if(this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
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
	
	
}
