package de.janrufmonitor.repository.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ReverseLookup {

	
	public Properties createConfiguration() {
		Properties p = new Properties();
		
		p.setProperty("goyellow.url", "http://www.goyellow.de/schnellsuche/?MDN=");
		p.setProperty("goyellow.lastname", "title=\"Detailinformationen zu ([^<]*)[\\W][^<]* in");
		p.setProperty("goyellow.firstname", "title=\"Detailinformationen zu [^<]*[\\W]([^<]*) in");
		p.setProperty("goyellow.additional", "branchCategory\">([^<]*)</div>");		
		p.setProperty("goyellow.street", "class=\"address\">([^_]*)[\\W][^,>]*<br />[\\d]*[\\W][^<]*</p>");
		p.setProperty("goyellow.streetno", "class=\"address\">[^_]*[\\W]([^,>]*)<br />[\\d]*[\\W][^<]*</p>");
		p.setProperty("goyellow.pcode", "class=\"address\">[^_]*[\\W][^,>]*<br />([\\d]*)[\\W][^<]*</p>");
		p.setProperty("goyellow.city", "class=\"address\">[^_]*[\\W][^,>]*<br />[\\d]*[\\W]([^<]*)</p>");		
		p.setProperty("goyellow.phone.areacode","\">([\\d]*)[\\W]*-[\\W]*[\\d]*</"); 
		p.setProperty("goyellow.phone.number","\">[\\d]*[\\W]*-[\\W]*([\\d]*)</"); 
		
		p.setProperty("dasoertliche.url", "http://www.dasoertliche.de/Controller?page=RUECKSUCHE&context=RUECKSUCHE&action=SUCHEN&buc=&la=de&form_name=search_inv&ph=");
		p.setProperty("dasoertliche.lastname", "class=\"entry\">([^<]*)[\\s][^<]*</a>");
		p.setProperty("dasoertliche.firstname", "class=\"entry\">[^<]*[\\s]([^<]*)</a>");
		p.setProperty("dasoertliche.street", "([^,>]*)&nbsp;[^,>]*,&nbsp;[^<]*&nbsp;[^<]*<br/>");
		p.setProperty("dasoertliche.streetno", "[^,>]*&nbsp;([^,>]*),&nbsp;[^<]*&nbsp;[^<]*<br/>");
		p.setProperty("dasoertliche.pcode", "[^,>]*&nbsp;[^,>]*,&nbsp;([^<]*)&nbsp;[^<]*<br/>");
		p.setProperty("dasoertliche.city", "[^,>]*&nbsp;[^,>]*,&nbsp;[^<]*&nbsp;([^<]*)<br/>");
		p.setProperty("dasoertliche.phone.areacode","&nbsp;\\(([\\d\\W]*)\\)[\\W\\s][\\d\\W]*<br");
		p.setProperty("dasoertliche.phone.number","&nbsp;\\([\\d\\W]*\\)[\\W]([\\d\\W]*)<br");

		p.setProperty("dastelefonbuch.url", "http://www4.dastelefonbuch.de/?cmd=search&kw=");
		p.setProperty("dastelefonbuch.lastname", " title=\"([^<]*)[\\W][^<]*\">[^<]*</a>");
		p.setProperty("dastelefonbuch.firstname", " title=\"[^<]*[\\W]([^<]*)\">[^<]*</a>");
		p.setProperty("dastelefonbuch.street", "<td class=\"celstreet\">([^<]*)&nbsp;[^<]*</td>");
		p.setProperty("dastelefonbuch.streetno", "<td class=\"celstreet\">[^<]*&nbsp;([^<]*)</td>");
		p.setProperty("dastelefonbuch.pcode", "<td class=\"celcity\">([\\d]*)&nbsp;[^<]*</td>");
		p.setProperty("dastelefonbuch.city", "<td class=\"celcity\">[\\d]*&nbsp;([^<]*)</td>");
		p.setProperty("dastelefonbuch.phone.areacode","<div class=\"phonenr\">[\\D]*([\\d]*)");
		p.setProperty("dastelefonbuch.phone.number","<div class=\"phonenr\">[\\D]*[\\d]* ([^<]*)</div>");

		p.setProperty("gelbeseite.url", "http://www1.gelbeseiten.de/yp/search.yp?at=yp&location=&distance=0&execute=Suchen&kindOfSearch=tradesearch&subject=");
		p.setProperty("gelbeseite.lastname", "\">([^<]*)</a><br />");
		p.setProperty("gelbeseite.street", "</a><br />([^<]*)[\\W][^<]*,[\\D][\\d]*[\\D][^<]*[\\s]");
		p.setProperty("gelbeseite.streetno", "</a><br />[^<]*[\\W]([^<]*),[\\D][\\d]*[\\D][^<]*[\\s]");
		p.setProperty("gelbeseite.pcode", "</a><br />[^<]*[\\W][^<]*,[\\D]([\\d]*)[\\D][^<]*[\\s]");
		p.setProperty("gelbeseite.city", "</a><br />[^<]*[\\W][^<]*,[\\D][\\d]*[\\D]([^<]*)[\\s]");
		p.setProperty("gelbeseite.phone.areacode","Telefon:<br>[\\D]*([\\d]*)"); 
		p.setProperty("gelbeseite.phone.number","Telefon:<br>[\\D]*[\\d]*[^<]{2}([\\d]*)");; 
		p.setProperty("gelbeseite.additional", "t_head\"[^!]*<span>([^<]*)</span>");
		
		p.setProperty("weisseseiten.ch.url", "http://www.directories.ch/weisseseiten/base.aspx?do=search&name=");
		p.setProperty("weisseseiten.ch.lastname", "\"><span>([^<]*)[\\W][^<]*</span><br>");
		p.setProperty("weisseseiten.ch.firstname", "\"><span>[^<]*[\\W]([^<]*)</span><br>");
		p.setProperty("weisseseiten.ch.street", "</span><br>([^<]*)<br>[\\d]*[\\W][^<]*<br>");
		p.setProperty("weisseseiten.ch.pcode", "</span><br>[^<]*<br>([\\d]*)[\\W][^<]*<br>");
		p.setProperty("weisseseiten.ch.city", "</span><br>[^<]*<br>[\\d]*[\\W]([^<]*)<br>");
		p.setProperty("weisseseiten.ch.phone.areacode","<b class=\"searchWords\">([\\d]*)[\\D][^<]*</b>"); 
		p.setProperty("weisseseiten.ch.phone.number","<b class=\"searchWords\">[\\d]*[\\D]([^<]*)</b>");

		p.setProperty("gebeld.nl.url", "http://www.gebeld.nl/content.asp?zapp=zapp&land=Nederland&zoek=numm&searchfield1=fullnumber&queryfield1=");
		p.setProperty("gebeld.nl.lastname", "\">([^<]*),&nbsp;[^<]*</font>");
		p.setProperty("gebeld.nl.firstname", "\">[^<]*,&nbsp;([^<]*)</font>");
		p.setProperty("gebeld.nl.street", "\">([^<]*)&nbsp;[\\d]*</font>");
		p.setProperty("gebeld.nl.streetno", "\">[^<]*&nbsp;([\\d]*)</font>");
		p.setProperty("gebeld.nl.pcode", "<tr><td></td><td>[^<]*&nbsp;([^<]*)</td></tr>");
		p.setProperty("gebeld.nl.city", "<tr><td></td><td>([^<]*)&nbsp;[^<]*</td></tr>");
		p.setProperty("gebeld.nl.phone.areacode","<tr><td></td><td>0([\\d]*)&nbsp;[\\d]*</td></tr>"); 
		p.setProperty("gebeld.nl.phone.number","<tr><td></td><td>0[\\d]*&nbsp;([\\d]*)</td></tr>");

		
		return p;
	}
	
	public static void main(String[] args) {
		if (args==null || args.length==0) {
			System.out.println("Aufruf mit: java ReverseLookup <rufnummer1> <rufnummer2> ...");
		}
		
		try {
			ReverseLookup rl = new ReverseLookup();
			for (int i = 0 ; i<args.length; i++) {
				rl.doLookup("goyellow", args[i]);
				rl.doLookup("dasoertliche", args[i]);
				rl.doLookup("dastelefonbuch", args[i]);
				rl.doLookup("gelbeseite", args[i]);
				rl.doLookup("weisseseiten.ch", args[i]);
				rl.doLookup("gebeld.nl", args[i]);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}		
	}

	public void doLookup(String id, String number) throws IOException, InterruptedException {		
		Properties config = this.createConfiguration();
		System.out.println();
		System.out.println("Using reverse lookup "+number+" for ID: "+id);

		URL url = new URL(config.getProperty(id+".url") + number);
		URLConnection c = url.openConnection();

		c.setDoInput(true);
		c.setRequestProperty(
			"User-Agent",
			"Mozilla/4.0 (compatible; MSIE; Windows NT; PIM)");
		c.connect();

		Thread.sleep(50);

		Object o = c.getContent();
		if (o instanceof InputStream) {

			InputStreamReader isr = new InputStreamReader((InputStream) o);

			BufferedReader br = new BufferedReader(isr);

			br.skip(100);

			StringBuffer content = new StringBuffer();
			while (br.ready()) {
				content.append(br.readLine());
			}
			
			br.close();
			isr.close();

			System.out.println(id+".lastname: "+find(Pattern.compile(config.getProperty(id+".lastname", "")), content));
			System.out.println(id+".firstname: "+find(Pattern.compile(config.getProperty(id+".firstname", "")), content));
			System.out.println(id+".additional: "+find(Pattern.compile(config.getProperty(id+".additional", "")), content));
			System.out.println(id+".street: "+find(Pattern.compile(config.getProperty(id+".street", "")), content));
			System.out.println(id+".streetno: "+find(Pattern.compile(config.getProperty(id+".streetno", "")), content));
			System.out.println(id+".pcode: "+find(Pattern.compile(config.getProperty(id+".pcode", "")), content));
			System.out.println(id+".city: "+find(Pattern.compile(config.getProperty(id+".city", "")), content));
			System.out.println(id+".phone.areacode: "+find(Pattern.compile(config.getProperty(id+".phone.areacode", "")), content));
			System.out.println(id+".phone.number: "+find(Pattern.compile(config.getProperty(id+".phone.number", "")), content));	
		}
	}
	
	private String find(Pattern p, StringBuffer c){
		Matcher m = p.matcher(c);
		if (m.find() && m.groupCount()==1) {
			return m.group(1).trim();
		}
		return null;		
	}
		
}
