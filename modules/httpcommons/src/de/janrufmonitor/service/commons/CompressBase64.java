package de.janrufmonitor.service.commons;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.util.zip.Deflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import de.janrufmonitor.util.io.Base64Decoder;
import de.janrufmonitor.util.io.Base64Encoder;

public class CompressBase64 {

	public synchronized static String compressBase64Encode(String raw) throws IOException {
		
		// create ByteArrayInputStream from String
		ByteArrayInputStream rawIn = new ByteArrayInputStream(raw.getBytes());
		
		// create a buffer for output
		ByteArrayOutputStream zippedOut = new ByteArrayOutputStream();
		ZipOutputStream out = new ZipOutputStream(zippedOut);
		out.setMethod(ZipOutputStream.DEFLATED);
		out.setLevel(Deflater.BEST_COMPRESSION);

		// write an data entry
		ZipEntry data = new ZipEntry("data");
		out.putNextEntry(data);
		transform(rawIn, out);
		out.closeEntry();
		out.close();
		//System.out.println(zippedOut.toString());
		// create a base64 encoded string
		ByteArrayInputStream zippedIn = new ByteArrayInputStream(zippedOut.toByteArray());
		ByteArrayOutputStream encodedOut = new ByteArrayOutputStream();
		Base64Encoder b64 = new Base64Encoder(encodedOut);
		transform(zippedIn, b64);
		b64.write(13);
		b64.flush();

		return new String(encodedOut.toByteArray());
	}
	
	public synchronized static byte[] decompressBase64Decode(byte[] encoded) throws IOException {
		// create a base64 decoded string
		ByteArrayInputStream encodedIn = new ByteArrayInputStream(encoded);
		Base64Decoder b64 = new Base64Decoder(encodedIn);
		ByteArrayOutputStream decodedOut = new ByteArrayOutputStream();
		transform(b64, decodedOut);
		b64.close();
		decodedOut.close();
		
		//System.out.println(decodedOut.toString());
		
		ByteArrayInputStream decodedIn = new ByteArrayInputStream(decodedOut.toByteArray());
		
		ZipInputStream zippedIn = new ZipInputStream(new BufferedInputStream(decodedIn));
		ZipEntry entry = zippedIn.getNextEntry();
		if (entry!=null) {
			if (entry.getName().equalsIgnoreCase("data") && !entry.isDirectory()) {
				byte[] decoded = toByteArray(zippedIn);
				zippedIn.closeEntry();
				zippedIn.close();
				return decoded;
			}
		}
		return new byte[] {};
	}
	
	private static void transform(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[8*1024];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
		}
		out.flush();
		in.close(); 
	}
	
	private static byte[] toByteArray(InputStream in) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		byte[] buffer = new byte[8*1024];
		int bytesRead;
		while ((bytesRead = in.read(buffer)) != -1) {
			bout.write(buffer, 0, bytesRead);
		}
		bout.flush();
		return bout.toByteArray();
	}

}
