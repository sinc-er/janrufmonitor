package de.janrufmonitor.repository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.Link;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.extensions.Organization;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.data.extensions.PostalAddress;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public class GoogleContactsFetcher {

	public static void main(String[] args) {
//		SimpleDateFormat sfd = new SimpleDateFormat("HH:mm:ss");
//		//1238838480000
//		System.out.println(sfd.format(new Date(1238609940000L)));
//		
//		sfd = new SimpleDateFormat("HH:mm");
//		System.out.println(sfd.format(new Date(1238609940000L)));
//		
//		
//		sfd = new SimpleDateFormat("HH:mm:ss");
//		System.out.println(sfd.format(new Date(1238609880000L)));
//		
//		sfd = new SimpleDateFormat("HH:mm");
//		System.out.println(sfd.format(new Date(1238609880000L)));
//		System.exit(0);
//
//		
//		System.out.println(System.currentTimeMillis());
		
		System.out.println("74918".matches("[\\d]+[\\D]*"));
		
		
		ContactsService cs = new ContactsService(
				"jam-googlecontacts-callermanager");
		try {
			cs.setUserCredentials("thilo.brandt@googlemail.com", "diplom2001");

			URL feedUrl = new URL(
					"http://www.google.com/m8/feeds/contacts/thilo.brandt@googlemail.com/full");
			feedUrl = new URL(
			"http://www.google.com/m8/feeds/contacts/thilo.brandt%40googlemail.com/full/4dbaf7080d386b");
			
			
//			Query q = new Query(feedUrl);
			//q.setMaxResults(2);
			//q.setUpdatedMin(new DateTime(System.currentTimeMillis()-1000000));
			  
			ContactEntry entry = (ContactEntry) cs.getEntry(feedUrl,
					ContactEntry.class);
//			System.out.print("["+resultFeed.getEntries().size()+"] ");
//			System.out.println(resultFeed.getTitle().getPlainText());
//			for (int i = 0; i < resultFeed.getEntries().size(); i++) {
//				ContactEntry entry = (ContactEntry) resultFeed.getEntries()
//						.get(i);
				System.out.println(entry.getSelfLink().getHref());
				System.out.println(entry.getTitle().getPlainText()+" (UUID:"+entry.getId()+")");
				if (entry.getOrganizations().size()>0)
					System.out.println(((Organization)entry.getOrganizations().get(0)).getOrgName().getValue());
				
						
				if (entry.getPostalAddresses().size()>0) {
					System.out.println(((PostalAddress)entry.getPostalAddresses().get(0)).getValue());
				}
				
				Link photoLink = entry.getContactPhotoLink();
				  if (photoLink != null && photoLink.getEtag()!=null) {
					  System.out.print("Bild: " + entry.getContactPhotoLink().getHref());
					  try {
				    InputStream in = cs.createLinkQueryRequest(photoLink).getResponseStream();
				    ByteArrayOutputStream out = new ByteArrayOutputStream();
				    RandomAccessFile file = new RandomAccessFile(
				        "x:\\" + entry.getSelfLink().getHref().substring(
				         entry.getSelfLink().getHref().lastIndexOf('/') + 1), "rw");
				    byte[] buffer = new byte[4096];
				    for (int read = 0; (read = in.read(buffer)) != -1;
				        out.write(buffer, 0, read));
				    file.write(out.toByteArray());
				    file.close();
					 } catch (Exception e){
						// e.printStackTrace();
					 }
				  }


				List phones = entry.getPhoneNumbers();
				for (int j=0;j<phones.size();j++) {
					System.out.print(((PhoneNumber)phones.get(j)).getPhoneNumber());
					System.out.println(" (" + ((PhoneNumber)phones.get(j)).getRel()+")");
				}
				List s = entry.getGroupMembershipInfos();
				Iterator iter = s.iterator();
				while (iter.hasNext())  {
					System.out.println(((GroupMembershipInfo)iter.next()).getHref());
					URL feedUrlg = new URL("http://www.google.com/m8/feeds/groups/thilo.brandt@googlemail.com/full");
				    ContactGroupFeed resultFeedg = (ContactGroupFeed) cs.getFeed(feedUrlg, ContactGroupFeed.class);
				    // Print the results
				    for (int k = 0; k < resultFeedg.getEntries().size(); k++) {
				        ContactGroupEntry groupEntry = (ContactGroupEntry) resultFeedg.getEntries().get(k);
				        System.out.println("Id: " + groupEntry.getId());
				        System.out.println("Group Name: " + groupEntry.getTitle().getPlainText());
				    }

				}
					
				System.out.println();
				
				System.out.println("---");
				
		//	}

		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}

}
