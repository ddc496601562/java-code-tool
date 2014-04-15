package com.java.jdk.tool;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;


public class ReadJarTestMain {

	/**
	 * @param args
	 * @throws IOException
	 * @throws URISyntaxException 
	 * @throws ClassNotFoundException 
	 */
	public static void main(String[] args) throws IOException, URISyntaxException, ClassNotFoundException {
		// TODO Auto-generated method stub
		File toDir = new File("D:/lucene-core-3.6.0");
		JarFile jar = new JarFile(new File("D:/lucene-core-3.6.0.jar"));
		Enumeration<JarEntry> entryJar = jar.entries();
		while (entryJar.hasMoreElements()) {
			JarEntry entry = entryJar.nextElement();
			System.out.println(entry.getName());
			if (!entry.isDirectory()) {
				InputStream in = jar.getInputStream(entry);
				try {
					File file = new File(toDir, entry.getName());
					if (!file.getParentFile().mkdirs()) {
						if (!file.getParentFile().isDirectory()) {
							throw new IOException("Mkdirs failed to create "
									+ file.getParentFile().toString());
						}
					}
					OutputStream out = new FileOutputStream(file);
					try {
						byte[] buffer = new byte[8192];
						int i;
						while ((i = in.read(buffer)) != -1) {
							out.write(buffer, 0, i);
						}
					} finally {
						out.close();
					}
				} finally {
					in.close();
				}
			}
		}
		URI uri=new URI("file:///lucene-core-3.6.0/");
		ClassLoader loader = new URLClassLoader(new URL[]{uri.toURL()});
		Thread.currentThread().setContextClassLoader(loader);
		Class classDocument2=loader.loadClass("org.apache.lucene.document.Document");
		System.out.println(classDocument2.getName());
	}

}
