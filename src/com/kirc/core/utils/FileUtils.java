/********************************************************************************************************************
 * FILENAME: FileUtil.java
 * 
 * ROLE: it contains several utilities used to build knowledge structure
 * 		
 * VARIABLES: - 
 * 
 * METHODS:
 * 	public static boolean saveModel(String filePath, Object source)								// save model
 * 	public static String readStringFromFile(String path)										// read string from file
 * 	public static Object loadModel(String filePath)												// load saved model
 * 	public static void writeStringToFile(String text, String path)								// write string to file
 * 	public static void writeStringToFile(String text, String path, boolean isAppend)			// append string to file
 * 	public static String readStringFromFileWithLineSapartor(String path)						// read string from file
 * 	public static String readStringFromFile(File f)												// read string from file
 * 	public static boolean deleteFile(String filePath)											// delete file
 * 	public static boolean moveFile(String from, String to)										// move file
 * 	public static boolean copyFile(String from, String to)										// copy file
 * 	public static String getFileHeaderName(String filePath)										// get file header name 
 * 	public static ArrayList<String> readLineStringFromFile(String filePath)						// read string with line from file
 * 
 ********************************************************************************************************************/
package com.kirc.core.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

public class FileUtils {

	static ArrayList<String> fileNameList = new ArrayList<String>();
	
	public boolean saveModel(String filePath, Object source) {
		
		boolean result = false;
		try {
			Gson gson = new Gson();
			writeStringToFile(gson.toJson(source), filePath);
			result = true;
			gson = null;
			
		} catch (Exception e) {
			System.out.println("ex saveModel = " + e.toString());
		}
		return result;
	}
	
	public static String readStringFromFile(String path) {
		
		String text = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line;
			while ((line = br.readLine()) != null) {
				text += (line+"\n");
			}
			br.close();
			br = null;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}

	public Object loadModel(String filePath) {
		
		Object result = null;

		try {
			String source = readStringFromFile(filePath);
			Gson gson = new Gson();
			result = gson.fromJson(source, Object.class);

		} catch (Exception e) {
			result = null;
			System.out.println("ex loadModel = " + e.toString());
		}
		return result;
	}

	public void writeStringToFile(String text, String path) {
		
		try {
			deleteFile(path);
			BufferedWriter bw = new BufferedWriter(new FileWriter(path));
			bw.write(text);
			bw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public void writeStringToFile(String text, String path, boolean isAppend) {
			
		try {
			if(!isAppend)
				deleteFile(path);
			BufferedWriter bw = new BufferedWriter(new FileWriter(path, isAppend));
				
			bw.write(text);
			bw.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public String readStringFromFileWithLineSapartor(String path) {
		String text = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(path));
			String line;
			while ((line = br.readLine()) != null) 		text += (line+"\r\n");
			
			br.close();
			br = null;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}
	
	public static String readStringFromFile(File f) {
		String text = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while ((line = br.readLine()) != null) 		text += (line+"\n");

			br.close();
			br = null;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return text;
	}

	public boolean deleteFile(String filePath) {

		boolean result = false;

		try {
			File target = new File(filePath);

			if (target.exists()) {
				target.delete();
				result = true;
			}
		} catch (Exception e) {
			System.out.println("ex delete file =" + e.toString());
			result = false;
		}
		return result;
	}

	public boolean moveFile(String from, String to) {

		boolean result = false;

		try {
			File fromFile = new File(from);
			File toFile = new File(to);
			fromFile.renameTo(toFile);
			result = true;
		} catch (Exception e) {
			System.out.println("ex moveFile file =" + e.toString());
			result = false;
		}
		return result;
	}

	public boolean copyFile(String from, String to) {

		boolean result = false;

		try {
			FileInputStream fis = new FileInputStream(from);
			FileOutputStream fos = new FileOutputStream(to);

			int data = 0;
			while ((data = fis.read()) != -1) {
				fos.write(data);
			}
			fis.close();
			fos.close();

			fis = null;
			fos = null;

			result = true;

		} catch (IOException e) {
			System.out.println("ex copyFile file =" + e.toString());
			e.printStackTrace();
		}
		return result;
	}
	   
	public String getFileHeaderName(String filePath) {
		
		String result = null;
		try {
			File file = new File(filePath);
			String fileName = file.getName();
			int pos = fileName.lastIndexOf(".");
			result = fileName.substring(0, pos);
			file = null;

		} catch (Exception e) {
			System.out.println("getFileHeaderName exception = " + e.toString());
		}
		return result;
	}
	
	public ArrayList<String> readLineStringFromFile(String filePath){
		
		ArrayList<String> result = new ArrayList<String>();
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			String line;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if(!result.contains(line)){
					result.add(line);
				}
			}
			br.close();
			br = null;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static ArrayList<File> read_folder(String directoryName) {
		File directory = new File(directoryName);
		ArrayList<File> files = new ArrayList<File>();
		
		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				files.add(file);
			} 
		}
		return files;
	}

	public static ArrayList<File> readFileName(String directoryName, ArrayList<File> files) {
		File directory = new File(directoryName);

		// get all the files from a directory
		File[] fList = directory.listFiles();
		for (File file : fList) {
			if (file.isFile()) {
				String doc_title = file.getName().split("\\.")[0];
				String doc_id = doc_title.replaceAll("[^\uAC00-\uD7A3xfe0-9a-zA-Z\\s]", " ");
				fileNameList.add(doc_id);
			} else if (file.isDirectory()) {
				readFileName(file.getAbsolutePath(), files);
			}
		}
		return files;
	}
}