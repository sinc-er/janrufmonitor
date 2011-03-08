package de.janrufmonitor.repository;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.janrufmonitor.framework.ICall;
import de.janrufmonitor.framework.ICallList;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.i18n.II18nManager;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.ui.jface.application.RendererRegistry;
import de.janrufmonitor.ui.jface.application.rendering.ITableCellRenderer;
import de.janrufmonitor.util.formatter.Formatter;
import de.janrufmonitor.util.io.Base64Encoder;
import de.janrufmonitor.util.io.PathResolver;
import de.janrufmonitor.util.io.Stream;
import de.janrufmonitor.util.string.StringUtils;

public class JournalBuilder {

	private static String CONFIG_TEMPLATE = "template";
	
	public static synchronized StringBuffer parseFromTemplate(ICallList calls, Properties cfg) {
		Logger m_logger = Logger.getLogger(IJAMConst.DEFAULT_LOGGER);
		
		if (cfg==null) {
			m_logger.warning("Invalid configuration for HTML journal");
			return null;
		}
		
		String template = cfg.getProperty(CONFIG_TEMPLATE, "");
		String templatePath = PathResolver.getInstance(PIMRuntime.getInstance()).getConfigDirectory() + File.separator + "templates"+ File.separator + "journals";
		File templateDir = new File(templatePath);
		templateDir.mkdirs(); // generate path if not exists
		
		File templateFile = new File(templateDir, template+".template");
		if (!templateFile.exists()) {
			m_logger.warning(templateFile.getName() +" not found.");
			return null;
		}

        StringBuffer rawcontent = new StringBuffer((int) templateFile.length());
        
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
			Stream.copy(new FileInputStream(templateFile), bos, true);
			rawcontent.append(translate(parseImage(resolvePaths(parse(new String(bos.toByteArray()), calls)))));
		} catch (FileNotFoundException e) {
			m_logger.log(Level.SEVERE, e.toString(), e);
			return null;
		} catch (IOException e) {
			m_logger.log(Level.SEVERE, e.toString(), e);
			return null;
		}
		
		return rawcontent;
	}
	
	private static String parseRenderer(String text, ICall call) {
		String prefix = "%r:";
		String postfix = "%";
		if (text.indexOf(prefix)>-1) {
			String id = text.substring(text.indexOf(prefix) + prefix.length(), text.indexOf(postfix, text.indexOf(prefix) + prefix.length()));
			ITableCellRenderer r = RendererRegistry.getInstance().getRenderer(id.toLowerCase());
			if (r!=null) {
				r.updateData(call);
				text = StringUtils.replaceString(text, prefix + id + postfix, r.renderAsText());
			} else {
				text = StringUtils.replaceString(text, prefix + id + postfix, "");
			}
		}
		return text;
	}
	
	private static String removeSingleCall(String text) throws IOException{
		String prefix = "<!-- start_call:";
		String postfix = ":end_call-->";
		String scall = text.substring(text.indexOf(prefix), text.indexOf(postfix)+postfix.length());
		return StringUtils.replaceString(text, scall, "");
	}
	
	private static String parseSingleCall(String text, ICall call) throws IOException{
		String prefix = "<!-- start_call:";
		String postfix = ":end_call-->";
		String scall = text.substring(text.indexOf(prefix) + prefix.length(), text.indexOf(postfix));
		Formatter f = Formatter.getInstance(PIMRuntime.getInstance());		
		return StringUtils.replaceString(text, prefix + scall + postfix, f.parse(parseRenderer(scall, call), call) + IJAMConst.CRLF+prefix + scall + postfix);
	}
	
	private static String parseImage(String text) throws IOException{
		String prefix = "<!-- start_image:";
		String postfix = ":end_image-->";
		while (text.indexOf(prefix)>=0) {
			String file = text.substring(text.indexOf(prefix) + prefix.length(), text.indexOf(postfix));
			File f = new File(file);
			if (f.exists()) {
		
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				Base64Encoder b64 = new Base64Encoder(bos);
				FileInputStream fin = new FileInputStream(f);
				Stream.copy(new BufferedInputStream(fin), b64, true);	
				text = StringUtils.replaceString(text, prefix + file + postfix, "data:image/"+getImageExtension(f.getName())+";base64,"+bos.toString());
			} else {
				text = StringUtils.replaceString(text, prefix + file + postfix, "");
			}
			
		}
		return parseHtmlImage(text);
	}
	
	private static String parseHtmlImage(String text) throws IOException{
		String prefix = "<!-- start_html_image:";
		String postfix = ":end_html_image-->";
		while (text.indexOf(prefix)>=0) {
			String token = text.substring(text.indexOf(prefix) + prefix.length(), text.indexOf(postfix));
			String file = token;
			String[] elements = token.split(";");
			if (elements.length>3) {
				continue;
			}
			if (elements.length==2) {
				text = StringUtils.replaceString(text, prefix + token + postfix, "");
				continue;
			}
			if (elements.length==3) {
				file = elements[2];
			}
			File f = new File(file);
			if (f.exists()) {
		
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				Base64Encoder b64 = new Base64Encoder(bos);
				FileInputStream fin = new FileInputStream(f);
				Stream.copy(new BufferedInputStream(fin), b64, true);	
				text = StringUtils.replaceString(text, prefix + token + postfix, "<img src=\"data:image/"+getImageExtension(f.getName())+";base64,"+bos.toString()+"\" "+(elements.length==3 ? "width=\""+elements[0]+"\" height=\""+elements[1]+"\" " : "")+"/>");
			} else if(file.length()>128) {
				// assume this is bas64 encoded content
				text = StringUtils.replaceString(text, prefix + token + postfix, "<img src=\"data:image/jpeg;base64,"+file+"\" "+(elements.length==3 ? "width=\""+elements[0]+"\" height=\""+elements[1]+"\" " : "")+"/>");
			} else {
				text = StringUtils.replaceString(text, prefix + token + postfix, "");
			}
					
		}
		return text;
	}
	
	private static String getImageExtension(String image) {
		if (image.indexOf(".")>0) {
			return image.substring(image.lastIndexOf(".")+1);
		}
		return "gif";
	}
	
	private static String resolvePaths(String t) {
		t = StringUtils.replaceString(t, IJAMConst.PATHKEY_CONFIGPATH, PathResolver.getInstance(PIMRuntime.getInstance()).getConfigDirectory());
		t = StringUtils.replaceString(t, IJAMConst.PATHKEY_IMAGEPATH, PathResolver.getInstance(PIMRuntime.getInstance()).getImageDirectory());
		t = StringUtils.replaceString(t, IJAMConst.PATHKEY_INSTALLPATH, PathResolver.getInstance(PIMRuntime.getInstance()).getInstallDirectory());
		t = StringUtils.replaceString(t, IJAMConst.PATHKEY_DATAPATH, PathResolver.getInstance(PIMRuntime.getInstance()).getDataDirectory());
		t = StringUtils.replaceString(t, IJAMConst.PATHKEY_PHOTOPATH, PathResolver.getInstance(PIMRuntime.getInstance()).getPhotoDirectory());
		t = StringUtils.replaceString(t, IJAMConst.PATHKEY_USERHOME, PathResolver.getInstance(PIMRuntime.getInstance()).getUserhomeDirectory());
		return t;
	}
	
	private static String translate(String text) {
		II18nManager i18n = PIMRuntime.getInstance().getI18nManagerFactory().getI18nManager();
		String prefix = "<!-- start_translate:";
		String postfix = ":end_translate-->";
		while (text.indexOf(prefix)>=0) {
			String token = text.substring(text.indexOf(prefix) + prefix.length(), text.indexOf(postfix));
			String[] elements = token.split(",");
			if (elements.length==4) {
				String translation = i18n.getString(elements[0], elements[1], elements[2], elements[3]);
				text = StringUtils.replaceString(text, prefix + token + postfix, translation);
			}
			if (elements.length==3) {
				String translation = i18n.getString(elements[0], elements[1], "label", elements[2]);
				text = StringUtils.replaceString(text, prefix + token + postfix, translation);
			}
			if (elements.length==2) {
				String translation = i18n.getString(IJAMConst.GLOBAL_NAMESPACE, elements[0], "label", elements[1]);
				text = StringUtils.replaceString(text, prefix + token + postfix, translation);
			}			

		}
		return text;
	}
	
	private static String parse(String text, ICallList calls) throws IOException {
		ICall c = null;
		for (int i=0,j=calls.size();i<j;i++) {
			c = calls.get(i);
			text = parseSingleCall(text, c);
		}
		return removeSingleCall(text);
	}

	
}
