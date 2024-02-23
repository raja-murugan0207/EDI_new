package com.bsc.qa.stt;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * ExcelUtils class that gets test data from excel file to be used
 */
public class ExcelUtils
{
	/**
	 * 
	 */
	private static XSSFSheet excelWSheet;
	/**
	 * 
	 */
	private static XSSFWorkbook excelWBook;
	/**
	 * 
	 */
	private static XSSFCell cell;
	
	// @param xlsPath
	public Map<String, Map<String, String>> cacheAllExcelData(String xlsPath) {
		Map<String, Map<String, String>> excelDataMap = null;

		String sheetName = System.getenv("ENVNAME");

		if (sheetName == null || "".equals(sheetName)) {
			sheetName = "Sheet1";
		}

		excelDataMap = getAllExcelDataMap(getColumnArray(xlsPath, sheetName), getTableArray(xlsPath, sheetName));

		return excelDataMap;
	}
	
	private static Map<String, Map<String, String>> getAllExcelDataMap(Object[][] columnArray,
			Object[][] testDataArray) {
		Map<String, Map<String, String>> dataMap = new HashMap<String, Map<String, String>>();
		int testMethodIndex = 0;
		for (testMethodIndex = 0; testMethodIndex < columnArray.length; testMethodIndex++) {
			if ("S_No".equals(columnArray[0][testMethodIndex])) {
				break;
			}
		}		
		if (testDataArray != null) {
			for (int i = 0; i < testDataArray.length; i++) {
				Map<String, String> rowMap = new HashMap<String, String>();
				for (int j = 0; j < columnArray[0].length; j++) {
					rowMap.put(columnArray[0][j].toString(), testDataArray[i][j].toString());
				}
				dataMap.put((String) testDataArray[i][testMethodIndex], rowMap);
			}
		}
		return dataMap;
	}



	
	private static List<Map<String, String>> getAllExcelData(Object[][] columnArray, Object[][] testDataArray) {
		
		Map<String, String> dataMap = new HashMap<String, String>();
		List<Map<String, String>> dataMapList = new ArrayList<Map<String, String>>();

		if (testDataArray != null) {
			for (int i=0; i<testDataArray.length; i++) {
					
					for (int j=0; j< columnArray[0].length; j++) {
						dataMap.put(columnArray[0][j].toString(), testDataArray[i][j].toString());
					}
					dataMapList.add(dataMap);
			}
		}
		return dataMapList;
	}

	// @param xlsPath
	
	public static List<Map <String, String>> getAllExcelDataOnly(String xlsPath) 
	{
		List<Map <String, String>> excelDataMapList = null;

		String sheetName = System.getenv("ENVNAME");

		if (sheetName == null || "".equals(sheetName)) {
			sheetName = "Sheet1";
		}
		
		excelDataMapList = getAllExcelData(getColumnArray(xlsPath, sheetName), getTableArray(xlsPath, sheetName));
		
		return excelDataMapList;
	}


	/**
	 * Returns the cell value as a String
	 * 
	 * @param rowNum	Row number
	 * @param colNum	Column Number
	 * @return cell value as a string
	 */
	public static String getCellData(int rowNum, int colNum) 
	{
		String returnData;
		try
		{
			cell = excelWSheet.getRow(rowNum).getCell(colNum);
			String cellData = null;
			if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
				cellData = cell.getStringCellValue();
			}
			else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) 
			{
				cellData = String.valueOf(cell.getNumericCellValue());
			}
			else if (cell.getCellType() == org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK)
			{
				cellData = "";
			}
			returnData= cellData;
		} 
		catch (Exception e)
		{
			returnData= "";
		}
		return returnData;
	}

	/**
	 * Get Excel data as an array
	 * 
	 * @param filePath	Excel file path
	 * @param sheetName	Excel sheet name
	 * @return	Object array of data in the table
	 */
	public static Object[][] getTableArray(String filePath, String sheetName) {   
		String[][] tableArray = null;
		int startRow = 1;
		int startCol = 0;
		int coli;
		int colj;
		int totalRows;
		int totalCols;
	
		try {
			FileInputStream excelFile = new FileInputStream(filePath);			
			excelWBook = new XSSFWorkbook(excelFile);
			excelWSheet = excelWBook.getSheet(sheetName);
			if (excelWSheet == null) {
				excelWSheet = excelWBook.getSheet("Sheet1");
			}
			totalRows = excelWSheet.getPhysicalNumberOfRows()-1;
			totalCols = excelWSheet.getRow(0).getPhysicalNumberOfCells()-1;
			tableArray=new String[totalRows][totalCols+1];
			coli=0;
			for (int i=startRow;i<=totalRows;i++, coli++) {           	   
				colj=0;
				for (int j=startCol;j<=totalCols;j++, colj++){
					if(getCellData(i,j) != null && !"".equals( getCellData(i,j).trim())){
						tableArray[coli][colj]=getCellData(i,j);
					}
					else {
						tableArray[coli][colj]="";
					}
				}
			}
		}catch (FileNotFoundException e){
			System.out.println("Could not read the Excel sheet");
		}catch (IOException e){
			System.out.println("Could not read the Excel sheet");
		}
		return tableArray;
	}


	/**
	 * Get columns names array
	 * 
	 * @param filePath	Excel file path
	 * @param sheetName	Excel sheet name
	 * @return	Object array of column names
	 */
	public static Object[][] getColumnArray(String filePath, String sheetName) {   
		String[][] columnArray = null;
		int coli;
		int totalRows;
		int totalCols;
	
		try {
			FileInputStream excelFile = new FileInputStream(filePath);			
			excelWBook = new XSSFWorkbook(excelFile);
			excelWSheet = excelWBook.getSheet(sheetName);
			if (excelWSheet == null) {
				excelWSheet = excelWBook.getSheet("Sheet1");
				
			}
			System.out.println("sheetname  getColumnArray : "+excelWSheet.getSheetName());
			totalRows = excelWSheet.getPhysicalNumberOfRows()-1;
			//System.out.println("get"+excelWSheet.getRow(0).toString());
			totalCols = excelWSheet.getRow(0).getPhysicalNumberOfCells()-1;
			columnArray=new String[totalRows][totalCols+1];
			coli=0;

			for (int j=0;j<=totalCols;j++){
				if(!"".equals(getCellData(coli,j).trim())){
					columnArray[coli][j]=getCellData(coli,j);
				}
			}
		}catch (FileNotFoundException e){
			System.out.println("Could not read the Excel sheet: " + sheetName);
		}catch (IOException e){
			System.out.println("Could not read the Excel sheet: " + sheetName);
		}
		return columnArray;
	}
	
	/**
	 * Returns a count of the number of columns
	 * 
	 * @return column count as an integer
	 */
	public static int getColumnCount()  
	{
		int rowNum = 0;
		int colNum = 0;
		int colCt = 0;
		try 
		{
			while (getCellData(rowNum, colNum) == null || getCellData(rowNum, colNum).isEmpty())
			{
				colCt++;
				colNum++;
			}
		} 
		catch (Exception e)
		{
			System.out.println("exception occured "+e);	
		}
		return colCt;
	}



}