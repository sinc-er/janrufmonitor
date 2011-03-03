package de.janrufmonitor.util.io;

import java.io.InputStream;

import de.janrufmonitor.framework.ICaller;

/**
 *  This interface could be implemented by classes which
 * provides access for caller images with the framework.
 * 
 *@author     Thilo Brandt
 *@created    2008/05/23
 */
public interface IImageStreamProvider extends IImageProvider {

	/**
	 * Returns an InputStream object representation of the image.
	 * 
	 * @param caller the caller object to be checked
	 * @return
	 */
	public InputStream getImageInputStream(ICaller caller);
	
}
