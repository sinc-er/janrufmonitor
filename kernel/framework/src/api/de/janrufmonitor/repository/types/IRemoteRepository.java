package de.janrufmonitor.repository.types;

/**
 * This type is used for remote storage repositories. These kind of repositories store their 
 * information on a remote machine.
 * 
 * @author brandt
 *
 */
public interface IRemoteRepository {

	public String getNamespace();
	
}
