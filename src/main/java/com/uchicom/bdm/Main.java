// (c) 2017 uchicom
package com.uchicom.bdm;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.List;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if (args.length != 2) return;
		try {
			List<String> urlList = Files.readAllLines(new File(args[0]).toPath(), Charset.forName("utf-8"));

			File outputDir = new File(args[1]);
			Downloader downloader = new Downloader(urlList, outputDir);
			downloader.execute();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}

	}

}
