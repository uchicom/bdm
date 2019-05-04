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
	boolean alive;
	public void execute() {
		for (String urlString : urlList) {
			try {
				URL url = new URL(urlString);
				System.out.print(urlString.substring(urlString.lastIndexOf('/') + 1));
				System.out.print(" ");
				alive = true;
				Thread thread = new Thread(() -> {
					while (alive) {
						System.out.print("-");
						try {
							Thread.sleep(10_000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
				thread.start();
				try (InputStream stream = url.openStream()) {
					Files.copy(stream,
							new File(outputDir, urlString.substring(urlString.lastIndexOf('/') + 1)).toPath());
					alive = false;
					System.out.println("> OK");
				} catch (IOException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
					System.out.println("> NG");
				}
			} catch (MalformedURLException e) {
				// TODO 自動生成された catch ブロック
				e.printStackTrace();
			}
		}
	}
}
