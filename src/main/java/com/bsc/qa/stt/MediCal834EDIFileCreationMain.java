package com.bsc.qa.stt;

import java.io.File;

/**
 * 
 * @author vkodig01
 *
 */
public class MediCal834EDIFileCreationMain {

	// @param args
	public static void main(String[] args) {

		File folder = new File(System.getenv("PHP_834_FILE_INPUT"));
		File[] listOfFiles = folder.listFiles();
		// Looping on the SDCal and LACal input sheet
		for (File listOfFile : listOfFiles) {

			// Calls LACal file generator based on input file name
			if (listOfFile.getName().contains("LACal")) {
				LACalFileParser laCalFileParser = new LACalFileParser();
				laCalFileParser.laCalFileParsing(listOfFile);
			} 
			// Calls SDCal file generator based on input file name
			else if (listOfFile.getName().contains("SDCal")) {
				SDCalFileParser sdCalFileParser = new SDCalFileParser();
				sdCalFileParser.sdCalFileParsing(listOfFile);
			}
		}
	}
}