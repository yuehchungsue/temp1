package com.fubon.tp.util;

import java.io.FileReader;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

/**
 * @author YC
 *
 */
public class FileReaderUtils {
	
	private static final String TEXT_FILENAME = "src/TRANSFORMATION.txt";
	/**private static final String TRANSFORMATION1 = "AES";*/

	/** Constructor Set */
	private static FileReaderUtils fileReader = null;

	private FileReaderUtils() {
	}

	/**
	 * Gget Instance
	 * 
	 * @return fileReader
	 */
	public static FileReaderUtils getInstance() {

		if (fileReader == null) {
			fileReader = new FileReaderUtils();
		}
		return fileReader;
	}

	public List<String> generateArrayListFromFile(String filename) throws IOException {

		ArrayList<String> result = new ArrayList<>();

		try (FileReader f = new FileReader(filename)) {

			/** StringBuilder */
			StringBuilder sb = new StringBuilder();

			while (f.ready()) {
				char c = (char) f.read();
				if (c == '\n') {
					result.add(sb.toString());
					sb = new StringBuilder();
				} else {
					sb.append(c);
				}
			}
			/** System.out.println(sb.toString()); */
			if (sb.length() > 0) {
				result.add(sb.toString());
			}
		}

		return result;
	}

	public static void main(String[] args) {
		List<String> lines = null;
		try {
			FileReaderUtils obj = FileReaderUtils.getInstance();
			lines = obj.generateArrayListFromFile(TEXT_FILENAME);
			System.out.println(lines.get(0));
		} catch (IOException e) {
			e.printStackTrace();
		}		
		Cipher cipher = null;
		if(lines!=null) {
			try {
				cipher = Cipher.getInstance(lines.get(0));
				cipher.getAlgorithm();
				System.out.println(cipher.getAlgorithm());
			} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
				e.printStackTrace();
			}
		}		
	}
}
