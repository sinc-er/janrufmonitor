package de.janrufmonitor.util.io;

import java.io.File;

import de.janrufmonitor.framework.ICaller;

/**
 *  This interface could be implemented by classes which
 * provides access for caller images with the framework.
 * 
 *@author     Thilo Brandt
 *@created    2005/03/19
 */
public interface IImageProvider {

	/**
	 * Checks wether the given caller object has an image or not
	 * 
	 * @param caller the caller object to be checked
	 * @return
	 */
	public boolean hasImage(ICaller caller);
	
	/**
	 * Return the path to the image.
	 * 
	 * @param caller the caller object to be checked
	 * @return
	 */
	public String getImagePath(ICaller caller);
	
	/**
	 * Returns a file object representation of the image.
	 * 
	 * @param caller the caller object to be checked
	 * @return
	 */
	public File getImage(ICaller caller);
	
	/**
	 * Return an ID to identified this IImageProvider. 
	 * Typically return the ID of the underlaying CallerManager
	 * 
	 * @return
	 */
	public String getID();
	
}
