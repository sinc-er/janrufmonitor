package de.janrufmonitor.repository.imexporter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.JxlWriteException;
import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.repository.imexport.ICallExporter;
import de.janrufmonitor.repository.imexport.IImExporter;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.RendererRegistry;
import de.janrufmonitor.ui.jface.application.rendering.ITableCellRenderer;

public class XlsFilter implements ICallExporter{
	
	private String ID = "XlsFilter";
	private String NAMESPACE = "repository.XlsFilter";

	private IRuntime m_runtime;
	
	Logger m_logger;
	II18nManager m_i18n;
	String m_language;
	String m_filename;
	ICallList m_callList;

	public XlsFilter() {
		m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
		m_i18n = getRuntime().getI18nManagerFactory().getI18nManager();
		m_language = getRuntime().getConfigManagerFactory().getConfigManager().getProperty(IJAMConst.GLOBAL_NAMESPACE, IJAMConst.GLOBAL_LANGUAGE);
	}

	public void setCallList(ICallList callList) {
		this.m_callList = callList;
	}

	public boolean doExport() {
		if (m_filename==null || m_filename.length()==0) return false;
		
		WritableWorkbook wb = null;

	    try {
			wb = Workbook.createWorkbook(new File(m_filename));

			WritableSheet sheet = wb.createSheet("journal", 0);
		
			// get renderers
			List renderer = new ArrayList();
			String renderer_config = this.getRuntime().getConfigManagerFactory().getConfigManager().getProperty("ui.jface.application.journal.Journal", "renderer");
			if (renderer_config!=null && renderer_config.length()>0) {
				StringTokenizer s = new StringTokenizer(renderer_config, ",");
				while (s.hasMoreTokens()) {
					renderer.add(RendererRegistry.getInstance().getRenderer(s.nextToken()));
				}
			}
			
			ITableCellRenderer t = null;
			Label cell = null;
			for (int i=0,j=renderer.size();i<j;i++) {
				t = (ITableCellRenderer) renderer.get(i);
				if (t==null) {
					this.m_logger.severe("No renderer found for ID: "+(String) renderer.get(i));
					this.m_logger.severe("Export to XLS format canceled...");
					return false;
				}
				cell = new Label(i,0, t.getHeader());
				sheet.addCell(cell);
			}
		
			int[] widths = new int[renderer.size()];
			
			ICall c = null;
			String cellContent = null;
			for (int k=0, i=0,j=this.m_callList.size();i<j;i++) {
				try {
					c = this.m_callList.get(i);
					k = 0;
					for (int m=renderer.size();k<m;k++) {
						t = (ITableCellRenderer) renderer.get(k);
						t.updateData(c);
						cellContent = t.renderAsText();
						if (cellContent!=null && cellContent.length()>0) {
							widths[k]= Math.max(widths[k], cellContent.length());
							cell = new Label(k,i+1, cellContent);
							sheet.addCell(cell);
						} else {
							cellContent = t.renderAsImageID();
							if (cellContent!=null && cellContent.length()>0) {
								if (cellContent.indexOf(".")>-1)
									cellContent = cellContent.substring(0, cellContent.indexOf("."));
								
								widths[k]= Math.max(widths[k], cellContent.length());
								cell = new Label(k,i+1, cellContent);
							} else
								cell = new Label(k,i+1, " ");
							sheet.addCell(cell);
						}
						cell = null;
					}
				}catch (JxlWriteException e) {
					this.m_logger.severe(e.getMessage());
					this.m_logger.severe("Ignored data ["+(k+1)+", "+(i+1)+"] for Excel export: "+cellContent);
				}
			}
			
			// calculate width
			for (int i=0,j=renderer.size();i<j;i++) {
				sheet.setColumnView(i, widths[i]);
			}
			
			wb.write();
		} catch (Exception e) {
			this.m_logger.severe(e.getMessage());
			return false;
		} finally {
			try {
				if (wb!=null)
					wb.close();
			} catch (WriteException ex) {
				this.m_logger.severe(ex.getMessage());
				return false;
			} catch (IOException ex) {
				this.m_logger.severe(ex.getMessage());
				return false;
			}
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
		return IImExporter.CALL_MODE;
	}

	public int getType() {
		return IImExporter.EXPORT_TYPE;
	}

	public String getFilterName() {
		return this.m_i18n.getString(this.NAMESPACE, "filtername", "label", this.m_language);
	}

	public String getExtension() {
		return "*.xls";
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
