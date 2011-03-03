package jmork;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.List;

import mork.MorkDocument;
import mork.Row;
import mork.Table;

public class jMorkTest {
	
	public static void main(String[] args) {
	
		try {
			MorkDocument md = new MorkDocument(new InputStreamReader(new FileInputStream("c:\\temp\\impab.mab")));
			List l = md.getRows();
			Row r = null;
			for (int i=0,j=l.size();i<j;i++) {
				r = (Row) l.get(i);
				System.out.println(r.getValues());
			}
			
			l = md.getTables();
			Table t =null;
			for (int i=0,j=l.size();i<j;i++) {
				t = (Table) l.get(i);
				for (int k=0;k<t.getRows().size();k++) {
					r = (Row) t.getRows().get(k);
					System.out.println(r.getValues());
				}
			}
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		
		
	}

}
