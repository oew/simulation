package com.ew.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

public class FileHelper {
	public static File getFile(String sFileName) {
		File fRet = new File(sFileName);
		if (!fRet.exists()) {
			try {
				fRet.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return fRet;
	}
	
	public static boolean exists(String sFileName) {
		File fRet = new File(sFileName);
		if (!fRet.exists()) {
			return false;
		}
		return true;
	}

	public static String getText(String fileName) {
		File file = getFile(fileName);
		List<String> lines = null;
		List<String> out = new LinkedList();
			
		if (!exists(fileName)) return "";

		try {
			lines = Files.readAllLines(Paths.get(fileName), Charset.forName("UTF-8"));
		} catch (IOException e) {
			try {
				lines = Files.readAllLines(Paths.get(fileName), Charset.forName("ISO-8859-1"));
			} catch (IOException e2) {
				
			}
		}
		StringBuffer sb = new StringBuffer();
		for (String line : lines) {
			sb.append(line);
		}
		return sb.toString();		
		}	

	public static byte[] getBytes(String fileName) {
		File file = getFile(fileName);
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);
			byte[] data = new byte[fis.available()];
			fis.read(data);
			return data;
		} catch (RuntimeException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void append(String sFile, String text) {
		try 
		{
			PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(sFile, true)));
			out.println(text);
			out.flush();
			out.close();
		} catch (IOException e) {
			}	
	}
	
	protected static BufferedWriter getWriter(String sFileName) throws IOException {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(sFileName, true));
			return writer;

		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}

	public static void writeFile(String name, byte[] data) {
		File file = FileHelper.getFile(name);

		if (!(file.length()>0)) {
			PrintStream wps;
			try {
				wps = new PrintStream(file);
				wps.write(data);
				wps.flush();
				wps.close();
			} catch (FileNotFoundException e) {
				// Bad filename
				e.printStackTrace();
			} catch (IOException e) {
				// error writing
				e.printStackTrace();
			}
	
		}
	}

}
