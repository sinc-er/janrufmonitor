package de.janrufmonitor.util.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import de.janrufmonitor.framework.IAttribute;
import de.janrufmonitor.framework.ICaller;
import de.janrufmonitor.framework.IJAMConst;
import de.janrufmonitor.util.io.IImageProvider;

public class ImageHandler {
	
	private class DefaultImageProvider implements IImageProvider, IImageStreamProvider {

		public boolean hasImage(ICaller caller) {
			if (caller.getPhoneNumber().isClired()) {
				m_logger.info("No image set. Caller is clir.");
				return false;	
			}
			
			IAttribute imagepath = caller.getAttribute(IJAMConst.ATTRIBUTE_NAME_IMAGEPATH);
			if (imagepath!=null) {
				String path = PathResolver.getInstance().resolve(imagepath.getValue());
				
				if (path.length()>0 && new File(path).exists()) {
					return true;
				}
			}
			
			File jpg = new File(PathResolver.getInstance().getPhotoDirectory(), caller.getPhoneNumber().getTelephoneNumber()+".jpg");
			if (jpg.exists()) return true;
			
			File png = new File(PathResolver.getInstance().getPhotoDirectory(), caller.getPhoneNumber().getTelephoneNumber()+".png");
			if (png.exists()) return true;
			
			return false;
		}

		public String getImagePath(ICaller caller) {
			File f = this.getImage(caller);
			if (f!=null) return f.getAbsolutePath();
			
			return "";
		}

		public File getImage(ICaller caller) {
			if (!hasImage(caller)) {
				return null;
			}
			
			IAttribute imagepath = caller.getAttribute(IJAMConst.ATTRIBUTE_NAME_IMAGEPATH);
			if (imagepath!=null) {
				String path = PathResolver.getInstance().resolve(imagepath.getValue());		
				File f = new File(path);
				if (path.length()>0 && f.exists()) {
					return f;
				}
			}
			
			File jpg = new File(PathResolver.getInstance().getPhotoDirectory(), caller.getPhoneNumber().getTelephoneNumber()+".jpg");
			if (jpg.exists()) {
				m_logger.info("Image set for caller on central image store: "+jpg.getAbsolutePath());
				return jpg;
			}
			
			File png = new File(PathResolver.getInstance().getPhotoDirectory(), caller.getPhoneNumber().getTelephoneNumber()+".png");
			if (png.exists()) {
				m_logger.info("Image set for caller on central image store: "+png.getAbsolutePath());
				return png;
			}
			
			m_logger.info("No image attribute set for caller "+caller);
			return null;
		}

		public String getID() {
			return "default";
		}

		public InputStream getImageInputStream(ICaller caller) {
			File f = this.getImage(caller);
			if (f!=null) {
				try {
					return new FileInputStream(f);
				} catch (FileNotFoundException e) {
					m_logger.log(Level.SEVERE, e.getMessage(), e);
				}
			}
			return null;
		}
		
	}

	protected Logger m_logger;
	protected Map m_providers;
	protected static ImageHandler m_instance = null;
	
	private ImageHandler() { 
		this.m_providers = new HashMap();
		this.removeAllProvider();
		this.m_logger = LogManager.getLogManager().getLogger(IJAMConst.DEFAULT_LOGGER);
	}
	
    public static synchronized ImageHandler getInstance() {
        if (ImageHandler.m_instance == null) {
        	ImageHandler.m_instance = new ImageHandler();
        }
        return ImageHandler.m_instance;
    }
	
	public void addProvider(IImageProvider provider) {
		this.m_providers.put(provider.getID(), provider);
	}
	
	public void removeProvider(IImageProvider provider) {
		this.m_providers.remove(provider.getID());
	}
	
	public void removeAllProvider() {
		this.m_providers.clear();
		this.m_providers.put("default", new DefaultImageProvider());
	}
	
	public boolean hasImage(ICaller caller) {
		IImageProvider provider = null;
		IAttribute cm_att = caller.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLERMANAGER);
		if (cm_att!=null && cm_att.getValue().length()>0) {
			provider = (IImageProvider) this.m_providers.get(cm_att.getValue());
			if (provider!=null) {
				if (this.m_logger.isLoggable(Level.INFO))
					this.m_logger.info("Using IImageProvider <"+provider.getID()+"> for existance check.");
				boolean isForceImage = Boolean.parseBoolean(System.getProperty(IJAMConst.SYSTEM_UI_FORCEIMAGE, "false"));
				if (isForceImage) {
					if (provider.hasImage(caller)) return true;
				} else {
					return provider.hasImage(caller);
				}
			}
		}		
		return this.hasImageDefault(caller);
	}
	
	public String getImagePath(ICaller caller) {
		IImageProvider provider = null;
		String imagePath = null;
		IAttribute cm_att = caller.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLERMANAGER);
		if (cm_att!=null && cm_att.getValue().length()>0) {
			provider = (IImageProvider) this.m_providers.get(cm_att.getValue());
			if (provider!=null) {
				if (this.m_logger.isLoggable(Level.INFO))
					this.m_logger.info("Using IImageProvider <"+provider.getID()+"> for getting image path.");
				
				imagePath = provider.getImagePath(caller);
				boolean isForceImage = Boolean.parseBoolean(System.getProperty(IJAMConst.SYSTEM_UI_FORCEIMAGE, "false"));
				if (isForceImage) {
					if (imagePath!=null && imagePath.length()>0) return imagePath;
				} else {
					return ((imagePath!=null && imagePath.length()>0) ? imagePath : "");
				}
			}
		}
		return getImagePathDefault(caller);
	}

	public File getImage(ICaller caller) {
		IImageProvider provider = null;
		File image = null;
		IAttribute cm_att = caller.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLERMANAGER);
		if (cm_att!=null && cm_att.getValue().length()>0) {
			provider = (IImageProvider) this.m_providers.get(cm_att.getValue());
			if (provider!=null) {
				if (this.m_logger.isLoggable(Level.INFO))
					this.m_logger.info("Using IImageProvider <"+provider.getID()+"> for getting image file.");
				
				image = provider.getImage(caller);
				boolean isForceImage = Boolean.parseBoolean(System.getProperty(IJAMConst.SYSTEM_UI_FORCEIMAGE, "false"));
				if (isForceImage) {
					if (image!=null && image.exists()) return image;
				} else {
					return  ((image!=null && image.exists()) ? image : null);
				}
			}
		}
		return this.getImageDefault(caller);
	}

	public InputStream getImageStream(ICaller caller) {
		Object provider = null;
		IAttribute cm_att = caller.getAttribute(IJAMConst.ATTRIBUTE_NAME_CALLERMANAGER);
		if (cm_att!=null && cm_att.getValue().length()>0) {
			provider = (IImageProvider) this.m_providers.get(cm_att.getValue());
			if (provider==null) provider = (IImageProvider) this.m_providers.get("default");
			if (provider instanceof IImageStreamProvider) {
				if (this.m_logger.isLoggable(Level.INFO))
					this.m_logger.info("Using IImageProvider <"+((IImageProvider)provider).getID()+"> for getting image stream.");
				
				InputStream in = ((IImageStreamProvider) provider).getImageInputStream(caller);
				boolean isForceImage = Boolean.parseBoolean(System.getProperty(IJAMConst.SYSTEM_UI_FORCEIMAGE, "false"));
				if (isForceImage) {
					if (in!=null) return in;
				} else {
					return  (in!=null ? in : null);
				}
			}
		}
		return this.getImageStreamFromDefault(caller);
	}
	
	private boolean hasImageDefault(ICaller caller) {
		IImageProvider provider  = (IImageProvider) this.m_providers.get("default");
			
		if (this.m_logger.isLoggable(Level.INFO))
			this.m_logger.info("Using IImageProvider <"+provider.getID()+"> for existance check.");
		
		return provider.hasImage(caller);
	}
	
	private String getImagePathDefault(ICaller caller) {
		Object provider = (IImageProvider) this.m_providers.get("default");
		if (provider instanceof IImageStreamProvider) {
			if (this.m_logger.isLoggable(Level.INFO))
				this.m_logger.info("Using IImageProvider <"+((IImageProvider)provider).getID()+"> for getting image path.");
			
			return ((IImageStreamProvider) provider).getImagePath(caller);
		}
		return "";
	}
	
	
	private File getImageDefault(ICaller caller) {
		Object provider = (IImageProvider) this.m_providers.get("default");
		if (provider instanceof IImageStreamProvider) {
			if (this.m_logger.isLoggable(Level.INFO))
				this.m_logger.info("Using IImageProvider <"+((IImageProvider)provider).getID()+"> for getting image file.");
			
			return ((IImageStreamProvider) provider).getImage(caller);
		}
		return null;
	}

	
	private InputStream getImageStreamFromDefault(ICaller caller) {
		Object provider = (IImageProvider) this.m_providers.get("default");
		if (provider instanceof IImageStreamProvider) {
			if (this.m_logger.isLoggable(Level.INFO))
				this.m_logger.info("Using IImageProvider <"+((IImageProvider)provider).getID()+"> for getting image stream.");
			
			InputStream in = ((IImageStreamProvider) provider).getImageInputStream(caller);
			if (in!=null) return in;
		}
		return null;
	}
}
