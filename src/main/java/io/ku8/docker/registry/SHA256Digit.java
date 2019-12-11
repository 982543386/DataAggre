package io.ku8.docker.registry;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256Digit {

	public static String hash(File file) throws Exception {
		BufferedInputStream in = null;
		try {
			in = new BufferedInputStream(new FileInputStream(file));
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] buffer = new byte[1024 * 1024];
			int sizeRead = -1;
			while ((sizeRead = in.read(buffer)) != -1) {
				digest.update(buffer, 0, sizeRead);
			}
			in.close();

			byte[] hash = null;
			hash = new byte[digest.getDigestLength()];
			hash = digest.digest();
			return bytes2Hex(hash);
		} finally {
			if (in != null) {
				in.close();
			}
		}
	}

	/**
	 * 
	 * The string encryption, using MD5 encryption algorithm, SHA - 1, SHA - 256, using the SHA - 256 by default
	 *
	 * 
	 * 
	 * @param strSrc
	 * 
	 *            To be encrypted string
	 * 
	 * @param encName
	 * 
	 *            Encryption type
	 * 
	 * @return
	 * 
	 */

	public static String Encrypt(String strSrc, String encName) {

		MessageDigest md = null;

		String strDes = null;

		byte[] bt = strSrc.getBytes();

		try {

			if (encName == null || encName.equals("")) {

				encName = "SHA-256";

			}

			md = MessageDigest.getInstance(encName);

			md.update(bt);

			strDes = bytes2Hex(md.digest()); // to HexString

		} catch (NoSuchAlgorithmException e) {

			return null;

		}

		return strDes;

	}

	public static String bytes2Hex(byte[] bts) {

		String des = "";

		String tmp = null;

		for (int i = 0; i < bts.length; i++) {

			tmp = (Integer.toHexString(bts[i] & 0xFF));

			if (tmp.length() == 1) {

				des += "0";

			}

			des += tmp;

		}

		return des;

	}

	public static void main(String args[]) throws Exception {

		String enString = "1caommwer";// �������

		String s = SHA256Digit.Encrypt(enString, "");

		System.out.println("Encrypt the data" + enString);

		System.out.println("Encryption result" + s);

		System.out.println("file  :" + hash(new File("c:\\1.tar")));

	}

}