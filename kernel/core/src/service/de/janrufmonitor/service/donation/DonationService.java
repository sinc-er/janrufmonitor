package de.janrufmonitor.service.donation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;
import java.util.logging.Level;

import de.janrufmonitor.framework.command.ICommand;
import de.janrufmonitor.runtime.IRuntime;
import de.janrufmonitor.runtime.PIMRuntime;
import de.janrufmonitor.service.AbstractConfigurableService;
import de.janrufmonitor.util.io.PathResolver;

public class DonationService extends AbstractConfigurableService {

	private String ID = "DonationService";
    private String NAMESPACE = "service.DonationService";

    private IRuntime m_runtime;
    
    public DonationService() {
        super();
        this.getRuntime().getConfigurableNotifier().register(this);
    }
    
	public String getNamespace() {
		return this.NAMESPACE;
	}

	public String getID() {
		return this.ID;
	}

	public IRuntime getRuntime() {
		if (this.m_runtime==null)
			this.m_runtime = PIMRuntime.getInstance();
		return this.m_runtime;
	}
	
    public void startup() {
		super.startup();
		
		try {
			if (this.hasDonated()) {
				if (m_logger.isLoggable(Level.INFO)) 
					m_logger.info("User has already donated. No popup shown at all.");
				return;
			}
		} catch (NoSuchAlgorithmException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e) {
			this.m_logger.log(Level.SEVERE, e.getMessage(), e);
		}

		if (this.isEnabled()) {
			int count = getDonationCount();
			if (m_logger.isLoggable(Level.INFO)) 
				m_logger.info("Donation service counter is #"+count+", display dialog = ("+(count % getDonationIntervall() == 0)+")");
			
			if (count % getDonationIntervall() == 0) {
				ICommand donationCommand = this.getRuntime().getCommandFactory().getCommand("DonationCommand");
				if (donationCommand!=null && donationCommand.isExecutable()) {
					try {
						this.m_logger.info("Executing DonationCommand...");					
						donationCommand.execute();
					} catch (Exception e) {
						this.m_logger.log(Level.SEVERE, e.getMessage(), e);
					}
				}
			}
		}
	}
    
    /**
     * Check wether a user mail is registered for donation or not
     * 
     * email=joe@sample.com
     * donation=5
     * md5=6ad3fe4c46f747ab7d
     * 
     * @return
     * @throws NoSuchAlgorithmException 
     * @throws IOException 
     */
    private boolean hasDonated() throws NoSuchAlgorithmException, IOException {
    	File donationfile = new File(PathResolver.getInstance(getRuntime()).getConfigDirectory(), "donation.key");
    	if (donationfile.exists() && donationfile.length()>0) {
    		Properties donation_key = new Properties();
    		FileInputStream fin = new FileInputStream(donationfile);
    		donation_key.load(fin);
    		fin.close();
    		
    		if (donation_key.getProperty("email")==null || donation_key.getProperty("email").trim().length()==0) return false;
    		if (donation_key.getProperty("donation")==null) return false;
    		
    		// joe@sample.org
    		String phrase = donation_key.getProperty("email")+donation_key.getProperty("donation");
    		
    		MessageDigest algorithm = MessageDigest.getInstance("MD5");
    	     algorithm.reset();
    	     algorithm.update(phrase.getBytes());
    	     byte result[] = algorithm.digest();

    	     StringBuffer hexString = new StringBuffer();
    	     for (int i=0;i<result.length;i++) {
    	    	 String hex = Integer.toHexString(0xFF & result[i]); 
    	    	 if(hex.length()==1)
    	    		 hexString.append('0');
    	    	 hexString.append(hex);
    	     }
    	   	if (m_logger.isLoggable(Level.INFO)) 
    			m_logger.info("Calculated donation key MD5 hash is: "+hexString);
    	   	if (m_logger.isLoggable(Level.INFO)) 
    			m_logger.info("Stored key MD5 hash is: "+donation_key.getProperty("md5"));
    	     return hexString.toString().equals(donation_key.getProperty("md5"));
    	}
    	
    	if (m_logger.isLoggable(Level.INFO)) 
			m_logger.info("Donation key does not exist: "+donationfile.getAbsolutePath());
    	return false;
    }
    
    private int getDonationCount() {
    	String count = this.m_configuration.getProperty("count");
    	if (count == null)  {
    		count = "0";
    	}
    	int c = Integer.parseInt(count);
    	c++;
    	getRuntime().getConfigManagerFactory().getConfigManager().setProperty(NAMESPACE, "count", Integer.toString(c));
		getRuntime().getConfigManagerFactory().getConfigManager().saveConfiguration();
		return c;    	
    }
    
    private int getDonationIntervall() {
    	return Integer.parseInt(this.m_configuration.getProperty("intervall", "50")); 	
    }

}
