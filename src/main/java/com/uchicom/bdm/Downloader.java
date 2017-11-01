// (c) 2017 uchicom
package com.uchicom.bdm;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Downloader {

	List<String> urlList;
	File outputDir;
	public Downloader(List<String> urlList, File outputDir) {
		this.urlList = urlList;
		this.outputDir = outputDir;
	}
	public void execute() {
		for (String urlString : urlList) {
			try {
				URL url = new URL(urlString);
				System.out.println(urlString.substring(urlString.lastIndexOf('/') + 1));
				try (InputStream stream = url.openStream()) {
					Files.copy(stream, new File(outputDir,urlString.substring(urlString.lastIndexOf('/') + 1)).toPath());
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}
			} catch (MalformedURLException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}
}
