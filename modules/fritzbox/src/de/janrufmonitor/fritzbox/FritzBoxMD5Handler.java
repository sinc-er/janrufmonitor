package de.janrufmonitor.fritzbox;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FritzBoxMD5Handler {

	public static String getResponse(String challenge, String password) {
		StringBuffer response = new StringBuffer();
		response.append(challenge);
		response.append("-");
		response.append(convertToHex(getMD5(challenge+"-"+password)));
		return response.toString();		
	}
	
	private static byte[] getMD5(String passwd) {
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("MD5");
            md.update(passwd.getBytes("UnicodeLittleUnmarked"));
        } catch (NoSuchAlgorithmException ex) {
        } catch (UnsupportedEncodingException e) {
		}

        return md.digest();
    }
	
	private static String convertToHex(byte[] data) {
	    StringBuffer buf = new StringBuffer();
	    for (int i = 0; i < data.length; i++) {
	      int halfbyte = (data[i] >>> 4) & 0x0F;
	      int two_halfs = 0;
	      do {
	        if ((0 <= halfbyte) && (halfbyte <= 9))
	          buf.append((char) ('0' + halfbyte));
	        else
	          buf.append((char) ('a' + (halfbyte - 10)));
	        halfbyte = data[i] & 0x0F;
	      } while(two_halfs++ < 1);
	    }
	    return buf.toString();
	  }

	
}
