package de.janrufmonitor.service.server.http.simple.handler;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.framework.IPhonenumber;
import de.janrufmonitor.repository.CallerNotFoundException;
import de.janrufmonitor.repository.ICallerManager;
import de.janrufmonitor.repository.types.IIdentifyCallerRepository;
import de.janrufmonitor.service.commons.http.handler.HandlerException;
import de.janrufmonitor.service.commons.http.IHttpRequest;
import de.janrufmonitor.service.commons.http.IMutableHttpResponse;
import de.janrufmonitor.service.commons.http.simple.handler.AbstractHandler;
import de.janrufmonitor.util.io.ImageHandler;
import de.janrufmonitor.util.io.PathResolver;

public class Image extends AbstractHandler {

	public void handleWithException(IHttpRequest req, IMutableHttpResponse resp) throws HandlerException {
		ICallerManager mgr = null;
		String manager = null;
		try {
			manager = req.getParameter(Image.PARAMETER_CALLERMANAGER);
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
		
		if (manager==null) mgr = this.getRuntime().getCallerManagerFactory().getDefaultCallerManager();
		
		if (manager!=null && manager.length()>0)
			mgr = this.getRuntime().getCallerManagerFactory().getCallerManager(manager);
		 
		if (mgr==null || !mgr.isActive() || !mgr.isSupported(IIdentifyCallerRepository.class)) {
			throw new HandlerException("Requested Callermanager does not exist or is not active.", 404);
		}
		
		String number = null;
		try {
			number = req.getParameter(Image.PARAMETER_NUMBER);
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}
		if (number==null || number.length()==0) {
			this.m_logger.severe("Parameter &number= was empty or not set.");
			throw new HandlerException("Parameter &number= was empty or not set.", 404);
		}
		
		IPhonenumber pn =null;
		
		StringTokenizer st = new StringTokenizer(number, ";");
		if (st.countTokens()==3) {
			pn = this.getRuntime().getCallerFactory().createPhonenumber(
				st.nextToken().trim(),
				st.nextToken().trim(),
				st.nextToken().trim()
			);

		} 
		if (st.countTokens()==2) {
			pn = this.getRuntime().getCallerFactory().createPhonenumber(
				st.nextToken().trim(),
				"",
				st.nextToken().trim()
			);
		} 
		if (st.countTokens()==1) {
			pn = this.getRuntime().getCallerFactory().createPhonenumber(
				st.nextToken().trim()
			);
		} 
		
		try {
			ICaller caller = ((IIdentifyCallerRepository)mgr).getCaller(pn);
			IAttribute imageAtt = caller.getAttribute(IJAMConst.ATTRIBUTE_NAME_IMAGEPATH);
			
			if (ImageHandler.getInstance().hasImage(caller)) {
				InputStream in = ImageHandler.getInstance().getImageStream(caller);
				if (in!=null) {
					resp.setParameter("Content-Type", this.getMimetype("jpg"));
					OutputStream ps = resp.getContentStreamForWrite();
					byte[] buffer = new byte[8092];
					int bytesRead;
					while ((bytesRead = in.read(buffer)) != -1) {
						ps.write(buffer, 0, bytesRead);
					}  
					in.close(); 
					
					ps.flush();
					ps.close();
				}
				
			} else if (imageAtt!=null) {
				String pathToImage = PathResolver.getInstance(getRuntime()).resolve(imageAtt.getValue());
				File image = new File(pathToImage);
				if (image.exists()){
					resp.setParameter("Content-Type", this.getMimetype(pathToImage));
					resp.setParameter("Content-Length", Long.toString(image.length()));
					OutputStream ps = resp.getContentStreamForWrite();
					
					FileInputStream in = new FileInputStream(image);
					
					byte[] buffer = new byte[8092];
					int bytesRead;
					while ((bytesRead = in.read(buffer)) != -1) {
						ps.write(buffer, 0, bytesRead);
					}  
					in.close(); 
					
					ps.flush();
					ps.close();
				} else {
					throw new CallerNotFoundException("Image "+pathToImage+" not found");
				}
			} else {
				throw new CallerNotFoundException("No image assigned for caller "+caller);
			}
		} catch (CallerNotFoundException e) {
			throw new HandlerException(e.getMessage(), 404);
		} catch (Exception e) {
			throw new HandlerException(e.getMessage(), 500);
		}	
	}
	
	private String getMimetype(String ext) {
		if (ext.toLowerCase().endsWith("jpg")) return "image/jpeg";
		if (ext.toLowerCase().endsWith("gif")) return "image/gif";
		if (ext.toLowerCase().endsWith("png")) return "image/png";
		return "application/octet-stream";
	}

}
