package de.janrufmonitor.service.server.http.simple.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;

import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.configuration.IConfigManager;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.simple.handler.AbstractHandler;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.io.Stream;

public class Configuration extends AbstractHandler{

	private String m_language;
	private II18nManager m_i18n;
	private IConfigManager m_cfg_man;
	
	private void handleFile(String path, IMutableHttpResponse resp) throws Exception {
		String root = PathResolver.getInstance(getRuntime()).getInstallDirectory();
		File rootf = new File(root);
		File requestedFile = new File(rootf, path);
		if (!requestedFile.exists()) throw new HandlerException(404);
		
		if (requestedFile.getName().toLowerCase().endsWith(".css")) {
			resp.setParameter("Content-Type", "text/css");
			resp.setParameter("Content-Length", Long.toString(requestedFile.length()));
			OutputStream ps = resp.getContentStreamForWrite();
			Stream.copy(new FileInputStream(requestedFile), ps, true);
			return;
		}
		
		if (requestedFile.getName().toLowerCase().endsWith(".jpg")) {
			resp.setParameter("Content-Type", "image/jpeg");
			resp.setParameter("Content-Length", Long.toString(requestedFile.length()));
			OutputStream ps = resp.getContentStreamForWrite();
			Stream.copy(new FileInputStream(requestedFile), ps, true);
			return;
		}
		throw new HandlerException(404);
	}

	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException {
		try {
			String uri_path = req.getURI().getPath().substring(1);
			if (uri_path.indexOf("/")>0){
				uri_path = uri_path.substring(uri_path.indexOf("/"));
				this.handleFile(uri_path, resp);
				return ;
			}
			
			long start = System.currentTimeMillis();
			StringBuffer html = new StringBuffer();
			
			String namespace = req.getParameter(PARAMETER_CFG_NAMESPACE);
			String action = req.getParameter(PARAMETER_CFG_ACTION);
			if (action!=null && namespace!=null && action.indexOf(ACTION_CFG_SAVE)>=0) {
				// save the data from the POST request
				String[] names = req.getParameterNames();
				if (names!=null) {
					for (int i=0;i<names.length;i++){
						if (!names[i].equalsIgnoreCase(PARAMETER_CFG_NAMESPACE) && !names[i].equalsIgnoreCase(PARAMETER_CFG_ACTION)&& !names[i].equalsIgnoreCase("new_value")&& !names[i].equalsIgnoreCase("new_type")) {
							String value = req.getParameter(names[i]);
							String type = null;
							if (names[i].equalsIgnoreCase("new_key")) {
								names[i] = req.getParameter("new_key");
								value = req.getParameter("new_value");
								type = req.getParameter("new_type");
							}

							if (value!=null) {
								this.getConfigManager().setProperty(namespace, names[i], value);
							}
							if (type!=null) {
								this.getConfigManager().setProperty(namespace, names[i], "type", type);
							}
						}
					}
					this.getConfigManager().saveConfiguration();
				}	
			}
			if (action!=null && namespace!=null && action.indexOf(ACTION_CFG_DELETE)>=0) {
				String param = action.substring((action.indexOf(":")>=0 ? action.indexOf(":")+1 : 0), (action.indexOf(">")>=0 ? action.indexOf(">") : action.length()));
				this.getConfigManager().removeProperty(namespace, param);
				this.getConfigManager().saveConfiguration();
			}
			
			if (action!=null && namespace!=null && action.indexOf(ACTION_CFG_DEFAULT)>=0) {
				String param = action.substring((action.indexOf(":")>=0 ? action.indexOf(":")+1 : 0), (action.indexOf(">")>=0 ? action.indexOf(">") : action.length()));
				String value = this.getConfigManager().getProperty(namespace, param, "default");
				this.getConfigManager().setProperty(namespace, param, value);
				this.getConfigManager().saveConfiguration();
			}
			
			if (action!=null && namespace!=null && action.indexOf(ACTION_CFG_CLEAR)>=0) {
				String param = action.substring((action.indexOf(":")>=0 ? action.indexOf(":")+1 : 0), (action.indexOf(">")>=0 ? action.indexOf(">") : action.length()));
				this.getConfigManager().setProperty(namespace, param, "");
				this.getConfigManager().saveConfiguration();
			}
			
			html.append("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">"+IJAMConst.CRLF);
			html.append("<html>"+IJAMConst.CRLF);
			html.append("<head>"+IJAMConst.CRLF);
			html.append("<title>");
			html.append(getI18nManager().getString(getNamespace(), "title", "label", getLanguage()));
			html.append((namespace!=null ? " - "+this.getI18nManager().getString(namespace, "title", "label", getLanguage()): ""));
			html.append("</title>"+IJAMConst.CRLF);
			html.append("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\">"+IJAMConst.CRLF);
			html.append("<link rel=\"stylesheet\" href=\"/config/web-config/styles/jam-styles.css\" type=\"text/css\">"+IJAMConst.CRLF);
			html.append("<link rel=\"SHORTCUT ICON\" href=\"/config/web-config/images/favicon.ico\" type=\"image/ico\" />");
			html.append("</head>"+IJAMConst.CRLF);
			html.append("<body>"+IJAMConst.CRLF);
			
			if (namespace==null || namespace.trim().length()==0) {
				// render overview page

				html.append("<div id=\"header\"><h1>");
				html.append("<img src=\"/config/web-config/images/logo.jpg\" id=\"logo\">");

				html.append(getI18nManager().getString(getNamespace(), "title", "label", getLanguage())+IJAMConst.CRLF);
				html.append("&nbsp;-&nbsp;");
				html.append(getI18nManager().getString(getNamespace(), "overview", "label", getLanguage())+IJAMConst.CRLF);
				
				html.append("</h1></div>");
				html.append("<h2>");
				html.append(getI18nManager().getString(getNamespace(), "general", "label", getLanguage())+IJAMConst.CRLF);
				html.append("</h2>");
				html.append("<table id=\"overview\">"+IJAMConst.CRLF);
				String[] ns = this.getConfigManager().getConfigurationNamespaces();
				html.append("<tr class=\"tableheader\">"+IJAMConst.CRLF);
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_mod", "label", getLanguage()));
				html.append("</th>");
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_ns", "label", getLanguage()));
				html.append("</th>");
				html.append("</tr>"+IJAMConst.CRLF);
				String p = null;
				for (int i=0;i<ns.length;i++) {
					p = this.getI18nManager().getString(ns[i], "title", "label", getLanguage());
					if (!p.startsWith("title") && (ns[i].toLowerCase().startsWith("janrufmonitor") || ns[i].toLowerCase().startsWith("manager.msnmanager") || ns[i].toLowerCase().startsWith("rules"))) {
						html.append("<tr>"+IJAMConst.CRLF);
						html.append("<td>");
						html.append("<a href=\"");
						html.append(URI_PATH_CONFIGURATION+"?"+PARAMETER_CFG_NAMESPACE+"="+ns[i]);
						html.append("\" target=\"_self\">");
						html.append(p);
						html.append("</a>");
						html.append("</td>");
						html.append("<td>");
						html.append(ns[i]+IJAMConst.CRLF);
						html.append("</td>");
						html.append("</tr>"+IJAMConst.CRLF);
					}
				}
				html.append("</table>"+IJAMConst.CRLF);
				
				html.append("<h2>");
				html.append(getI18nManager().getString(getNamespace(), "repository", "label", getLanguage())+IJAMConst.CRLF);
				html.append("</h2>");
				html.append("<table id=\"overview\">"+IJAMConst.CRLF);
				ns = this.getConfigManager().getConfigurationNamespaces();
				html.append("<tr class=\"tableheader\">"+IJAMConst.CRLF);
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_mod", "label", getLanguage()));
				html.append("</th>");
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_ns", "label", getLanguage()));
				html.append("</th>");
				html.append("</tr>"+IJAMConst.CRLF);
				p = null;
				for (int i=0;i<ns.length;i++) {
					p = this.getI18nManager().getString(ns[i], "title", "label", getLanguage());
					if (!p.startsWith("title") && ns[i].toLowerCase().startsWith("repository")) {
						html.append("<tr>"+IJAMConst.CRLF);
						html.append("<td>");
						html.append("<a href=\"");
						html.append(URI_PATH_CONFIGURATION+"?"+PARAMETER_CFG_NAMESPACE+"="+ns[i]);
						html.append("\" target=\"_self\">");
						html.append(p);
						html.append("</a>");
						html.append("</td>");
						html.append("<td>");
						html.append(ns[i]+IJAMConst.CRLF);
						html.append("</td>");
						html.append("</tr>"+IJAMConst.CRLF);
					}
				}
				html.append("</table>"+IJAMConst.CRLF);
				
				html.append("<h2>");
				html.append(getI18nManager().getString(getNamespace(), "service", "label", getLanguage())+IJAMConst.CRLF);
				html.append("</h2>");
				html.append("<table id=\"overview\">"+IJAMConst.CRLF);
				ns = this.getConfigManager().getConfigurationNamespaces();
				html.append("<tr class=\"tableheader\">"+IJAMConst.CRLF);
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_mod", "label", getLanguage()));
				html.append("</th>");
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_ns", "label", getLanguage()));
				html.append("</th>");
				html.append("</tr>"+IJAMConst.CRLF);
				p = null;
				for (int i=0;i<ns.length;i++) {
					p = this.getI18nManager().getString(ns[i], "title", "label", getLanguage());
					if (!p.startsWith("title") && ns[i].toLowerCase().startsWith("service")) {
						html.append("<tr>"+IJAMConst.CRLF);
						html.append("<td>");
						html.append("<a href=\"");
						html.append(URI_PATH_CONFIGURATION+"?"+PARAMETER_CFG_NAMESPACE+"="+ns[i]);
						html.append("\" target=\"_self\">");
						html.append(p);
						html.append("</a>");
						html.append("</td>");
						html.append("<td>");
						html.append(ns[i]+IJAMConst.CRLF);
						html.append("</td>");
						html.append("</tr>"+IJAMConst.CRLF);
					}
				}
				html.append("</table>"+IJAMConst.CRLF);
				
				html.append("<h2>");
				html.append(getI18nManager().getString(getNamespace(), "monitor", "label", getLanguage())+IJAMConst.CRLF);
				html.append("</h2>");
				html.append("<table id=\"overview\">"+IJAMConst.CRLF);
				ns = this.getConfigManager().getConfigurationNamespaces();
				html.append("<tr class=\"tableheader\">"+IJAMConst.CRLF);
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_mod", "label", getLanguage()));
				html.append("</th>");
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_ns", "label", getLanguage()));
				html.append("</th>");
				html.append("</tr>"+IJAMConst.CRLF);
				p = null;
				for (int i=0;i<ns.length;i++) {
					p = this.getI18nManager().getString(ns[i], "title", "label", getLanguage());
					if (!p.startsWith("title") && ns[i].toLowerCase().startsWith("monitor")) {
						html.append("<tr>"+IJAMConst.CRLF);
						html.append("<td>");
						html.append("<a href=\"");
						html.append(URI_PATH_CONFIGURATION+"?"+PARAMETER_CFG_NAMESPACE+"="+ns[i]);
						html.append("\" target=\"_self\">");
						html.append(p);
						html.append("</a>");
						html.append("</td>");
						html.append("<td>");
						html.append(ns[i]+IJAMConst.CRLF);
						html.append("</td>");
						html.append("</tr>"+IJAMConst.CRLF);
					}
				}
				html.append("</table>"+IJAMConst.CRLF);
				
				html.append("<h2>");
				html.append(getI18nManager().getString(getNamespace(), "ui", "label", getLanguage())+IJAMConst.CRLF);
				html.append("</h2>");
				html.append("<table id=\"overview\">"+IJAMConst.CRLF);
				ns = this.getConfigManager().getConfigurationNamespaces();
				html.append("<tr class=\"tableheader\">"+IJAMConst.CRLF);
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_mod", "label", getLanguage()));
				html.append("</th>");
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_ns", "label", getLanguage()));
				html.append("</th>");
				html.append("</tr>"+IJAMConst.CRLF);
				p = null;
				for (int i=0;i<ns.length;i++) {
					p = this.getI18nManager().getString(ns[i], "title", "label", getLanguage());
					if (!p.startsWith("title") && ns[i].toLowerCase().startsWith("ui.jface.application")) {
						html.append("<tr>"+IJAMConst.CRLF);
						html.append("<td>");
						html.append("<a href=\"");
						html.append(URI_PATH_CONFIGURATION+"?"+PARAMETER_CFG_NAMESPACE+"="+ns[i]);
						html.append("\" target=\"_self\">");
						html.append(p);
						html.append("</a>");
						html.append("</td>");
						html.append("<td>");
						html.append(ns[i]+IJAMConst.CRLF);
						html.append("</td>");
						html.append("</tr>"+IJAMConst.CRLF);
					}
				}
				html.append("</table>"+IJAMConst.CRLF);
			} else {
				html.append("<div id=\"header\"><h1>");
				html.append("<img src=\"/config/web-config/images/logo.jpg\" id=\"logo\">");

				html.append(getI18nManager().getString(getNamespace(), "title", "label", getLanguage())+IJAMConst.CRLF);
				html.append("</h1></div>");
				
				Properties p = this.getConfigManager().getProperties(namespace);
				html.append("<a href=\""+URI_PATH_CONFIGURATION+"\" target=\"_self\" class=\"navlink\">"+getI18nManager().getString(getNamespace(), "tooverview", "label", getLanguage())+"</a>"+IJAMConst.CRLF);
				html.append("<a href=\""+URI_PATH_CONFIGURATION+"?"+PARAMETER_CFG_NAMESPACE+"="+namespace+"\" target=\"_self\" class=\"navlink\">"+getI18nManager().getString(getNamespace(), "refresh", "label", getLanguage())+"</a>"+IJAMConst.CRLF);
				html.append("<h2>");
				html.append(getI18nManager().getString(namespace, "title", "label", getLanguage())+IJAMConst.CRLF);
				html.append("</h2>");
				html.append("<form action=\""+URI_PATH_CONFIGURATION+"?"+PARAMETER_CFG_NAMESPACE+"="+namespace+"\" method=\"post\">");
				html.append("<table id=\"configuration\">"+IJAMConst.CRLF);
				
				html.append("<tr class=\"tableheader\">"+IJAMConst.CRLF);
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_parameter", "label", getLanguage()));
				html.append("</th>");
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_type", "label", getLanguage()));
				html.append("</th>");	
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_access", "label", getLanguage()));
				html.append("</th>");
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_curr_value", "label", getLanguage()));
				html.append("</th>");
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_new_value", "label", getLanguage()));
				html.append("</th>");
				html.append("<th>");
				html.append(getI18nManager().getString(getNamespace(), "column_action", "label", getLanguage()));
				html.append("</th>");
				html.append("</tr>"+IJAMConst.CRLF);
				
				Iterator i = p.keySet().iterator();
				String key = null;
				String type = null;
				String access = null;
				while(i.hasNext()) {
					key = (String) i.next();
					type = getConfigManager().getProperty(namespace, key, "type");
					access = getConfigManager().getProperty(namespace, key, "access");
					html.append("<tr class=\"configentry\" id=\""+access+"\">"+IJAMConst.CRLF);
					html.append("<td class=\"parameter\">");
					html.append(key);
					html.append("</td>");
					html.append("<td class=\"type\">");
					html.append(type);
					html.append("</td>");
					html.append("<td class=\"access\">");
					html.append(access);
					html.append("</td>");
					html.append("<td class=\"currentvalue\">");
					html.append((p.getProperty(key).length()==0 ? "(n/a)": p.getProperty(key)));
					html.append("</td>");
					html.append("<td class=\"newvalue\">");
					html.append((access.equalsIgnoreCase("system") ? "&nbsp;" : "<input type=\"text\" name=\""+key+"\" class=\"newvalueinput\">"));
					html.append("</td>");
					html.append("<td class=\"actions\">");
					html.append((access.equalsIgnoreCase("system") ? "&nbsp;" : "<button type=\"submit\" name=\"action\" class=\"actionbutton\" value=\"delete:"+key+"\"><div id=\"delete:"+key+"\">"+getI18nManager().getString(getNamespace(), "delete", "label", getLanguage())+"</div></button>"+IJAMConst.CRLF));
					html.append((access.equalsIgnoreCase("system") ? "&nbsp;" : "<button type=\"submit\" name=\"action\" class=\"actionbutton\" value=\"default:"+key+"\"><div id=\"default:"+key+"\">"+getI18nManager().getString(getNamespace(), "default", "label", getLanguage())+"</div></button>"+IJAMConst.CRLF));
					html.append((access.equalsIgnoreCase("system") ? "&nbsp;" : "<button type=\"submit\" name=\"action\" class=\"actionbutton\" value=\"clear:"+key+"\"><div id=\"clear:"+key+"\">"+getI18nManager().getString(getNamespace(), "clear", "label", getLanguage())+"</div></button>"+IJAMConst.CRLF));
					html.append("</td>");
					html.append("</tr>"+IJAMConst.CRLF);
				}
				
				// add new parameter
				html.append("<tr class=\"configentry\">"+IJAMConst.CRLF);
				html.append("<td class=\"parameter\">");
				html.append("<input type=\"text\" name=\"new_key\" class=\"newvalueinput\">");
				html.append("</td>");
				html.append("<td class=\"type\">");
				html.append("<input type=\"text\" name=\"new_type\" class=\"newvalueinput\">");
				html.append("</td>");
				html.append("<td class=\"type\">");
				html.append("user");
				html.append("</td>");
				html.append("<td class=\"currentvalue\">");
				html.append("&nbsp;");
				html.append("</td>");
				html.append("<td class=\"newvalue\">");
				html.append("<input type=\"text\" name=\"new_value\" class=\"newvalueinput\">");
				html.append("</td>");
				html.append("<td class=\"actions\">");
				html.append("&nbsp;");
				html.append("</td>");
				html.append("</tr>"+IJAMConst.CRLF);
				
				html.append("</table>"+IJAMConst.CRLF);
				html.append("<button type=\"submit\" id=\"savebutton\" name=\"action\" value=\"save\"><div id=\"save\">"+getI18nManager().getString(getNamespace(), "save", "label", getLanguage())+"</div></button>");
				html.append("</form>"+IJAMConst.CRLF);
			}
			html.append("<div id=\"footer\">Created by jAnrufmonitor ("+(System.currentTimeMillis()-start)+" ms) on "+new Date().toString()+"</div>");
			html.append("</body>"+IJAMConst.CRLF);
			html.append("</html>"+IJAMConst.CRLF);
			
			resp.setParameter("Content-Type", "text/html");
			resp.setParameter("Content-Length", Long.toString(html.length()));
			OutputStream ps = resp.getContentStreamForWrite();
			ps.write(html.toString().getBytes());
			ps.flush();
			ps.close();
		} catch (HandlerException e) {
			throw e;
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
	}
	

	private String getNamespace() {
		return "service.server.http.simple.handler.Configuration";
	}
	
	private IConfigManager getConfigManager() {
		if (this.m_cfg_man==null) {
			this.m_cfg_man = this.getRuntime().getConfigManagerFactory().getConfigManager();
		}
		return this.m_cfg_man;
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
