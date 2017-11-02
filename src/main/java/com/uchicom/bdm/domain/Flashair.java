// (c) 2017 uchicom
package com.uchicom.bdm.domain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Flashair {

	private static final String urlRoot = "http://flashair";
	private String r_uri;
	private String fname;
	private String fsize;
	private String attr;
	private String fdate;
	private String ftime;
	
	public static List<URL> getUrlList() {
		List<URL> urlList = new ArrayList<>();
		try {
			URL url = new URL(urlRoot);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			try (InputStream is = url.openStream()) {
				int length = 0;
				byte[] bytes = new byte[4 * 1024 * 1024];
				while ((length = is.read(bytes)) < 0) {
					baos.write(bytes, 0, length);
				}
				String html = baos.toString();
				String[] lines = html.split("\n");
				for (String line : lines) {
					if (line.startsWith("wlansd.push")) {
						urlList.add(parse(line.substring(12, line.length() - 2)).toURL());
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (MalformedURLException e1) {
			e1.printStackTrace();
		}
		return urlList;
	}
	
	public static Flashair parse(String json) {
		Flashair flashair = new Flashair();;
		Pattern pattern = Pattern.compile("^\\{\"([^\"]+)\"\\:\"([^\"]+)\", \"([^\"]+)\"\\:\"([^\"]+)\", \"([^\"]+)\"\\:([1234567890]+),\"([^\"]+)\"\\:([1234567890]+),\"([^\"]+)\"\\:([1234567890]+),\"([^\"]+)\"\\:([1234567890]+)\\}$");
		Matcher matcher = pattern.matcher(json);
		if (matcher.find()) {
			int max = matcher.groupCount();
			for (int group = 1; group < max; group += 2) {
				String value = matcher.group(group + 1);
				switch (matcher.group(group)) {
				case "r_uri":
					flashair.r_uri = value;
					break;
				case "fname":
					flashair.fname = value;
					break;
				case "fsize":
					flashair.fsize = value;
					break;
				case "attr":
					flashair.attr = value;
					break;
				case "fdate":
					flashair.fdate = value;
					break;
				case "ftime":
					flashair.ftime = value;
					break;
					default:
						break;
				}
			}
		}
		return flashair;
	}
	
	public URL toURL() throws MalformedURLException {
		if (r_uri == null || fname == null) return null;
		URL url = new URL(urlRoot + r_uri + "/" + fname);
		return url;
	}

	@Override
	public String toString() {
		return "Flashair [r_uri=" + r_uri + ", fname=" + fname + ", fsize=" + fsize + ", attr=" + attr + ", fdate="
				+ fdate + ", ftime=" + ftime + "]";
	}
	
	
}
