package com.josemambrudo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Util {

	public static void resultProcess(File pdfFile, String msg) throws FileNotFoundException {

		String str = pdfFile.getPath().replaceAll("\\." + FileExtension.PDF.getExtension() + "$",
				"." + FileExtension.TXT.getExtension());

		File toWrite = new File(str);

		verifyDelete(toWrite);

		PrintWriter out = new PrintWriter(toWrite);

		out.println(msg);

		out.close();

	}

	public static void verifyDelete(File file) {
		if (file.exists())
			file.delete();
	}

	public static FileReader readFileUTF8(File f) throws IOException {
		
		FileReader fileReader = null;
		
		return fileReader;
	}
	
	public static JSONObject getJSONObject(File file) throws FileNotFoundException, IOException, ParseException {

		JSONParser parser = new JSONParser();
		
		
		BufferedReader in = new BufferedReader(
		           new InputStreamReader(
		                      new FileInputStream(file), "UTF8"));
		
		
		Object object = parser.parse( in);

		JSONObject jsonObject = (JSONObject) object;

		return jsonObject;
	}
}

enum FileExtension {
	DOCX("docx"), XLSX("xlsx"), PDF("pdf"), TXT("txt"),  JSON("json");

	private String extension;

	public String getExtension() {
		return this.extension;
	}

	private FileExtension(String extension) {
		this.extension = extension;
	}
}