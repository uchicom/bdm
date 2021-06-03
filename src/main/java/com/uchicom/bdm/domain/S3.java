// (c) 2021 uchicom
package com.uchicom.bdm.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;

import javax.crypto.spec.SecretKeySpec;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.AmazonS3EncryptionClientV2Builder;
import com.amazonaws.services.s3.model.CryptoConfigurationV2;
import com.amazonaws.services.s3.model.CryptoMode;
import com.amazonaws.services.s3.model.EncryptionMaterials;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.StaticEncryptionMaterialsProvider;

/**
 * @author uchicom: Shigeki Uchiyama
 *
 */
public class S3 {

	public static void main(String[] args) {
		if (args.length > 2) {
			File file = new File(args[0]);
			if (!file.exists()) {
				System.out.println(args[0] + " file is not exist!");
				return;
			}
			S3 s3 = new S3(file);
			switch (args[1]) {
			case "list":
				s3.list(Arrays.copyOfRange(args, 2, args.length));
				break;
			case "download":
				s3.download(Arrays.copyOfRange(args, 2, args.length));
				break;
			default:
				System.out.println("list or download");
			}

		}

	}

	private Properties properties = new Properties();

	public S3(File file) {
		try (FileInputStream fis = new FileInputStream(file)) {
			properties.load(fis);
		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	/**
	 * パス配下のリストをファイル出力.
	 * 
	 * @param keys オブジェクトキーの配列
	 */
	public void list(String[] keys) {
		AmazonS3 client = createClient();
		String bucketName = properties.getProperty("bucket_name");
		for (String key : keys) {
			ListObjectsV2Result result = client.listObjectsV2(bucketName, key);
			result.getObjectSummaries().forEach(sum -> System.out.println(sum.getKey()));
		}
	}

	/**
	 * ダウンロードする
	 * 
	 * @param keys オブジェクトキーの配列
	 */
	public void download(String[] keys) {
		AmazonS3 client = createClient();
		String bucketName = properties.getProperty("bucket_name");
		for (String key : keys) {
			S3Object object = client.getObject(new GetObjectRequest(bucketName, key));
			byte[] bytes = new byte[4 * 1024 * 1024]; // 4M
			try (S3ObjectInputStream oc = object.getObjectContent(); FileOutputStream fos = new FileOutputStream(key)) {
				int length = 0;
				while ((length = oc.read(bytes)) != -1) {
					fos.write(bytes, 0, length);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public AmazonS3 createClient() {
		AmazonS3EncryptionClientV2Builder builder = AmazonS3EncryptionClientV2Builder.standard();

		builder.withClientConfiguration(createConfiguration()).withCredentials(createCredentials())
				.withRegion(createRegion());
		if (properties.getProperty("encrypt") != null) {
			builder.withCryptoConfiguration(createCryptoConfiguration())
					.withEncryptionMaterialsProvider(createEncryptionMaterialsProvider());
		}
		return builder.build();
	}

	public ClientConfiguration createConfiguration() {
		ClientConfiguration configuration = new ClientConfiguration();
		configuration.setConnectionTimeout(30_000);
		configuration.setMaxErrorRetry(3);
		configuration.setRequestTimeout(30_000);
		return configuration;
	}

	public AWSCredentialsProvider createCredentials() {
		return new AWSStaticCredentialsProvider(
				new BasicAWSCredentials(properties.getProperty("access_key"), properties.getProperty("secret_key")));
	}

	public Regions createRegion() {
		return Regions.valueOf(properties.getProperty("region"));
	}

	public AmazonS3 createClientWithEncrypt() {
		ClientConfiguration configuration = new ClientConfiguration();
		configuration.setConnectionTimeout(30_000);
		configuration.setMaxErrorRetry(3);
		configuration.setRequestTimeout(30_000);
		return AmazonS3ClientBuilder.standard().withClientConfiguration(configuration)
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
						properties.getProperty("access_key"), properties.getProperty("secret_key"))))
				.withRegion(Regions.valueOf(properties.getProperty("region"))).build();
	}

	public CryptoConfigurationV2 createCryptoConfiguration() {
		return new CryptoConfigurationV2().withCryptoMode(CryptoMode.AuthenticatedEncryption);
	}

	public StaticEncryptionMaterialsProvider createEncryptionMaterialsProvider() {
		return new StaticEncryptionMaterialsProvider(new EncryptionMaterials(new SecretKeySpec(
				Base64.getDecoder().decode(properties.getProperty("encrypt_key")), properties.getProperty("encrypt"))));
	}
}
