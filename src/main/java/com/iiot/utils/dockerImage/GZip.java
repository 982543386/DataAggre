package com.iiot.utils.dockerImage;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.ArchiveInputStream;
import org.apache.commons.compress.archivers.ArchiveStreamFactory;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

/**
 * 解压tar.gz文件包
 *
 */
public class GZip {

	private BufferedOutputStream bufferedOutputStream;

	String zipfileName = null;

	public GZip(String fileName) {
		this.zipfileName = fileName;
	}

	/*
	 * 执行入口,rarFileName为需要解压的文件路径(具体到文件),destDir为解压目标路径
	 */
	public void unTargzFile(String destDir) throws Exception {
		String outputDirectory = destDir;
		File file = new File(outputDirectory);
		if (!file.exists()) {
			file.mkdir();
		}
		try{
			unzipOarFile(outputDirectory);
		}catch(Exception e){
			if(e.getMessage().indexOf("Not in GZIP format")>=0){
				extTarFileList(outputDirectory);
			}else{
				throw e;
			}
		}
		

	}

	public void unTarFile(String destDir) throws Exception {
		String outputDirectory = destDir;
		File file = new File(outputDirectory);
		if (!file.exists()) {
			file.mkdir();
		}
		try{
			extTarFileList(outputDirectory);
		}catch(Exception e){
			if(e.getMessage().indexOf("Error detected parsing the header")>=0){
				unzipOarFile(outputDirectory);
			}else{
				throw e;
			}
		}

	}

	public void unzipOarFile(String outputDirectory) throws Exception {
		FileInputStream fis = null;
		ArchiveInputStream in = null;
		BufferedInputStream bufferedInputStream = null;
		try {
			fis = new FileInputStream(zipfileName);
			GZIPInputStream is = new GZIPInputStream(new BufferedInputStream(
					fis));
			in = new ArchiveStreamFactory().createArchiveInputStream("tar", is);
			bufferedInputStream = new BufferedInputStream(in);
			TarArchiveEntry entry = (TarArchiveEntry) in.getNextEntry();
			while (entry != null) {
				String name = entry.getName();
				String[] names = name.split("/");
				String fileName = outputDirectory;
				for (int i = 0; i < names.length; i++) {
					String str = names[i];
					fileName = fileName + File.separator + str;
				}
				if (name.endsWith("/")) {
					mkFolder(fileName);
				} else {
					File file = mkFile(fileName);
					bufferedOutputStream = new BufferedOutputStream(
							new FileOutputStream(file));
					int b;
					while ((b = bufferedInputStream.read()) != -1) {
						bufferedOutputStream.write(b);
					}
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
				}
				entry = (TarArchiveEntry) in.getNextEntry();
			}

		} finally {
			try {
				if (bufferedInputStream != null) {
					bufferedInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void extTarFileList(String outputDirectory) throws Exception {
		FileInputStream fis = null;
		ArchiveInputStream in = null;
		BufferedInputStream bufferedInputStream = null;
		try {
			fis = new FileInputStream(zipfileName);
			in = new ArchiveStreamFactory().createArchiveInputStream("tar", fis);
			bufferedInputStream = new BufferedInputStream(in);
			TarArchiveEntry entry = (TarArchiveEntry) in.getNextEntry();
			while (entry != null) {
				String name = entry.getName();
				String[] names = name.split("/");
				String fileName = outputDirectory;
				for (int i = 0; i < names.length; i++) {
					String str = names[i];
					fileName = fileName + File.separator + str;
				}
				if (name.endsWith("/")) {
					mkFolder(fileName);
				} else {
					File file = mkFile(fileName);
					bufferedOutputStream = new BufferedOutputStream(
							new FileOutputStream(file));
					int b;
					while ((b = bufferedInputStream.read()) != -1) {
						bufferedOutputStream.write(b);
					}
					bufferedOutputStream.flush();
					bufferedOutputStream.close();
				}
				entry = (TarArchiveEntry) in.getNextEntry();
			}

		} finally {
			try {
				if (bufferedInputStream != null) {
					bufferedInputStream.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void mkFolder(String fileName) {
		File f = new File(fileName);
		if (!f.exists()) {
			f.mkdir();
		}
	}

	private File mkFile(String fileName) {
		File f = new File(fileName);
		try {
			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}
}