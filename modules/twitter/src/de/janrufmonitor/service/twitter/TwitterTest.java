package de.janrufmonitor.service.twitter;

import java.net.MalformedURLException;

import org.eclipse.swt.program.Program;

import winterwell.jtwitter.OAuthSignpostClient;
import winterwell.jtwitter.Twitter;

public class TwitterTest {

	public static void main(String[] args) {
		OAuthSignpostClient oauthClient = new OAuthSignpostClient("5hnhcrLzvwA1AFuKS3YUg", 
		"Xv27A4fJ7DalYQ4diA9kDUuSlLApcKHLuMV7NsU", "oob");
		//oauthClient.setName("jAnrufmonitor Twitter Service");
		// Open the authorisation page in the user's browser
		// On Android, you'd direct the user to URI url = client.authorizeUrl();
		// On a desktop, we can do that like this:
		try {
			Program.launch(oauthClient.authorizeUrl().toURL().toExternalForm());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// get the pin
		String v = null; //oauthClient.askUser("Please enter the verification PIN from Twitter");
		oauthClient.setAuthorizationCode(v);

		// Store the authorisation token details for future use
		String[] accessToken = oauthClient.getAccessToken();
		// Next time we can use new OAuthSignpostClient(OAUTH_KEY, OAUTH_SECRET, 
		// accessToken[0], accessToken[1]) to avoid authenticating again.
		System.out.println(accessToken);

		// Make a Twitter object
		Twitter twitter = new Twitter(null, oauthClient);
		System.out.println(twitter.getScreenName());
		// Print Daniel Winterstein's status
		System.out.println(twitter.getStatus("winterstein"));
		// Set my status
		twitter.setStatus("Messing about in Java-"+System.currentTimeMillis());
	}

}
