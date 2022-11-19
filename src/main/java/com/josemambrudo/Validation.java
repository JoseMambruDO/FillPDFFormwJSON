package com.josemambrudo;


import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.compress.utils.FileNameUtils;

public class Validation {

	public File validateFile(String strPath, FileExtension ext) throws FileNotFoundException {

		File file = new File(strPath);

		if (file.isFile() && file.exists() && FileNameUtils.getExtension(strPath).equals(ext.getExtension()))

			return file;

		else
			throw new FileNotFoundException("File: " + strPath);

	}

	public Integer validateNumber(String number) {
		int num = Integer.parseInt(number);
		num = Math.abs(num);

		if (num > 1_000_000)
			throw new NumberFormatException("No valid number");

		return num;

	}
	
	public String validateUserPass(String pass) throws Exception {
		if (pass.isEmpty())
			throw new Exception("Pass is empty");
		return pass;
			
	}

}
