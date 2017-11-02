// (c) 2017 uchicom
package com.uchicom.bdm;

import java.net.MalformedURLException;

import com.uchicom.bdm.domain.Flashair;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//サンプルコード
		String line = "wlansd.push({\"r_uri\":\"/DCIM/100__TSB\", \"fname\":\"IMG_1660.CR2\", \"fsize\":30785552,\"attr\":32,\"fdate\":19088,\"ftime\":23641});";
		System.out.println(line.substring(12, line.length() - 2));
		Flashair flashair = Flashair.parse(line.substring(12, line.length() - 2));
		System.out.println(flashair);
		try {
			System.out.println(flashair.toURL());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

}
