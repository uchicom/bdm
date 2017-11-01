// (c) 2017 uchicom
package com.uchicom.bdm;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Flashair {

	public List<String> getUrlList() {
		List<String> urlList = new ArrayList<>();
		try {
			URL url = new URL("http://flashair");
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
						urlList.add(line.substring(12, line.length() - 2));
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
}
