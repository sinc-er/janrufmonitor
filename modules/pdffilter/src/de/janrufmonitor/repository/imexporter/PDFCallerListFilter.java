package de.janrufmonitor.repository.imexporter;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import de.janrufmonitor.framework.IAttributeMap;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.ICallerList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IMultiPhoneCaller;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.imexport.ICallerExporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.ITreeItemCallerData;
import de.janrufmonitor.ui.jface.application.RendererRegistry;
import de.janrufmonitor.ui.jface.application.rendering.ITableCellRenderer;
import de.janrufmonitor.ui.swt.SWTImageManager;
import de.janrufmonitor.util.io.ImageHandler;
import de.janrufmonitor.util.io.Stream;

public class PDFCallerListFilter implements ICallerExporter{
	
	private String ID = "PDFCallerListFilter";
	private String NAMESPACE = "repository.PDFCallerListFilter";

	private IRuntime m_runtime;
	
	Logger m_logger;
	II18nManager m_i18n;
	String m_language;
	String m_filename;
	ICallerList m_callerList;

	public PDFCallerListFilter() {
		m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		m_i18n = getRuntime().getI18nManagerFactory().getI18nManager();
		m_language = getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
	}


	public void setCallerList(ICallerList callerList) {
		this.m_callerList = callerList;
	}	
	
	public boolean doExport() {
		Document document = new Document(PageSize.A4.rotate());
		document.addCreationDate();
		document.addCreator("jAnrufmonitor");
		
		try {
			PdfWriter.getInstance(document, new FileOutputStream(this.m_filename));
			document.open();
			
			// get renderers
			List renderer = new ArrayList();
			String renderer_config = this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty("ui.jface.application.editor.Editor", "renderer");
			if (renderer_config!=null && renderer_config.length()>0) {
				StringTokenizer s = new StringTokenizer(renderer_config, ",");
				while (s.hasMoreTokens()) {
					renderer.add(RendererRegistry.getInstance().getRenderer(s.nextToken()));
				}
			}
			
			// get column width
			float totalWidth = 0;
			String[] cWidth = new String[renderer.size()];
			for (int i=0,j=renderer.size();i<j;i++) {
				cWidth[i] = getRuntime().getConfigManagerFactory().getConfigManager().getProperty("ui.jface.application.editor.Editor", "col_size_"+((ITableCellRenderer)renderer.get(i)).getID());
				if (cWidth[i]!=null && cWidth[i].length()>0) {
					totalWidth += Float.parseFloat(cWidth[i]);
				}
				if (cWidth[i]!=null && cWidth[i].length()==0) {
					cWidth[i] = "0";
				}
			}
						
			float[] widths = new float[renderer.size()];
			for (int i=0,j=renderer.size();i<j;i++) {
				widths[i] = Float.parseFloat(cWidth[i]) / totalWidth;
			}
			
			PdfPTable table = new PdfPTable(widths);
			table.setHeaderRows(1);
			table.setWidthPercentage(100f);

			ITableCellRenderer t = null;
			PdfPCell cell = null;
			for (int i=0,j=renderer.size();i<j;i++) {
				t = (ITableCellRenderer) renderer.get(i);
				if (t==null) {
					this.m_logger.severe("No renderer found for ID: "+(String) renderer.get(i));
					this.m_logger.severe("Export to PDF format canceled...");
					return false;
				}
				cell = new PdfPCell(new Paragraph(t.getHeader()));
				cell.setBackgroundColor(new Color(0xC0, 0xC0, 0xC0));
				table.addCell(cell);
			}
		
			ICaller c = null;
			String cellContent = null;
			Color iterateColor1 = new Color(0xDD, 0xDD, 0xDD);
			Color iterateColor2 = new Color(0xFF, 0xFF, 0xFF);
			cell = null;
			int type_col = -1, num_col = -1;
			for (int i=0,j=this.m_callerList.size();i<j;i++) {
				c = this.m_callerList.get(i);
				for (int k=0,m=renderer.size();k<m;k++) {
					t = (ITableCellRenderer) renderer.get(k);
					t.updateData(c);
					// find number and typ column for multiline callers
					if (t.getID().equalsIgnoreCase("NumberType")) type_col = k;
					if (t.getID().equalsIgnoreCase("Number")) num_col = k;
					
					cellContent = t.renderAsText();
					if (cellContent!=null && cellContent.length()>0) {
						cell = new PdfPCell(new Phrase(cellContent));
						cell.setBackgroundColor((i%2==0 ? iterateColor1 : iterateColor2));
						table.addCell(cell);
					} else {
						cellContent = t.renderAsImageID();
						if (cellContent!=null && cellContent.length()>0) {
							if (cellContent.startsWith("db://")) {
								InputStream in = ImageHandler.getInstance().getImageStream(c);
								if (in!=null) {
									ByteArrayOutputStream out = new ByteArrayOutputStream();
									Stream.copy(in, out, true);
									in.close(); out.close();
									Image pdfImage = Image.getInstance(out.toByteArray());
									//pdfImage.scaleAbsolute(90.0f, 45.0f);
									table.addCell(pdfImage);
								} else {
									table.addCell(" ");
								}
							} else {
								Image pdfImage = Image.getInstance(SWTImageManager.getInstance(PIMRuntime.getInstance()).getImagePath(cellContent));
								table.addCell(pdfImage);
							}
							//((Image pdfImage = Image.getInstance(cellContent);
							//cell = new PdfPCell(pdfImage);
							//cell.setBackgroundColor((i%2==0 ? iterateColor1 : iterateColor2));
							//table.addCell(pdfImage);
						} else {
							cell = new PdfPCell(new Phrase(" "));
							cell.setBackgroundColor((i%2==0 ? iterateColor1 : iterateColor2));
							table.addCell(cell);
						}
					}
					// last column reached check for multiline caller
					if (k==m-1 && (type_col>-1 || num_col>-1)&& (c instanceof IMultiPhoneCaller) && (((IMultiPhoneCaller)c).getPhonenumbers().size()>1)) {
						List phones = ((IMultiPhoneCaller)c).getPhonenumbers();
						IPhonenumber pn = null;
						for (int y=1,z=phones.size();y<z;y++) {
							pn = (IPhonenumber) phones.get(y);
							for (int w=0,x=renderer.size();w<x;w++) {
								t = (ITableCellRenderer) renderer.get(w);
								if (t.getID().equalsIgnoreCase("NumberType") || t.getID().equalsIgnoreCase("Number")){
									if (t.getID().equalsIgnoreCase("NumberType")) {
										final IPhonenumber lpn = pn;
										final IAttributeMap lam = c.getAttributes();
										t.updateData(new ITreeItemCallerData() {
											public IAttributeMap getAttributes() {
												return lam;
											}

											public IPhonenumber getPhone() {
												return lpn;
											}
											
										});
									}
									else 
										t.updateData(pn);
									
									cellContent = t.renderAsText();
									if (cellContent!=null && cellContent.length()>0) {
										cell = new PdfPCell(new Phrase(cellContent));
										cell.setBackgroundColor((i%2==0 ? iterateColor1 : iterateColor2));
										table.addCell(cell);
									} else {
										cell = new PdfPCell(new Phrase(" "));
										cell.setBackgroundColor((i%2==0 ? iterateColor1 : iterateColor2));
										table.addCell(cell);
									}		
								} else {
									cell = new PdfPCell(new Phrase(" "));
									cell.setBackgroundColor((i%2==0 ? iterateColor1 : iterateColor2));
									table.addCell(cell);
								}								
							}
						}
					}
				}
			}
			document.add(table);
		} catch (DocumentException de) {
			this.m_logger.severe(de.getMessage());
			return false;
		} catch (IOException ioe) {
			this.m_logger.severe(ioe.getMessage());
			return false;
		} finally {
			document.close();
		}

		return true;
	}
	
	public void setFilename(String filename) {
		this.m_filename = filename;
	}

	public String getID() {
		return ID;
	}

	public int getMode() {
		return IImExporter.CALLER_MODE;
	}

	public int getType() {
		return IImExporter.EXPORT_TYPE;
	}

	public String getFilterName() {
		return this.m_i18n.getString(this.NAMESPACE, "filtername", "label", this.m_language);
	}

	public String getExtension() {
		return "*.pdf";
	}

	protected II18nManager getI18nManager() {
		if (this.m_i18n==null) {
			this.m_i18n = this.getRuntime().getI18nManagerFactory().getI18nManager();
		}
		return this.m_i18n;
	}
	
	protected String getLanguage() {
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
		if(this.m_runtime==null) {
			this.m_runtime = PIMRuntime.getInstance();
		}
		return this.m_runtime;
	}

}
