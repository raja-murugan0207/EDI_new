package com.bsc.qa.stt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import com.bsc.qa.framework.utility.DBUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * 
 * @author vkodig01
 *
 */
public class LACalFileParser {

	// public
	// inputFileName :contains full file path

	public String inputFileName = null;

	/** csvDataList is used while writing to filename */
	public List<String[]> csvDataList = new ArrayList<String[]>();

	// time stamp is used to add time stamp to filename
	public static String timestamp = new SimpleDateFormat("yyyyMMdd").format(new Date());
	public static String timestampFileName = new SimpleDateFormat("MMddyy").format(new Date());
	public static String timestampMonth = new SimpleDateFormat("MMyy").format(new Date());
	public static String timestampwithsec = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date());

	// To fetch random date
	static LocalDate startDate = LocalDate.of(1990, 1, 1); // start date
	static long start = startDate.toEpochDay();

	static LocalDate endDate = LocalDate.now(); // end date
	static long end = endDate.toEpochDay();

	static long randomEpochDay = ThreadLocalRandom.current().longs(start, end).findAny().getAsLong();
	static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MMddyy");
	static String timestampFileName1 = LocalDate.ofEpochDay(randomEpochDay).format(dateTimeFormatter);

	static DateTimeFormatter dateTimeFormatter1 = DateTimeFormatter.ofPattern("MMyy");
	static String timestampMonth1 = LocalDate.ofEpochDay(randomEpochDay).format(dateTimeFormatter1);

	// suiteName to store the report location folder
	public static String suiteName = System.getProperty("user.dir")
			.substring(System.getProperty("user.dir").lastIndexOf("\\") + 1);

	// @param method

	public static void writeToEDI(List<String> ediList, String fileDate) {

		// iterating through each code
		try {
			String outputFile = "";
			FileWriter fileWriter = null;
			BufferedWriter bufferedWriter = null;
			outputFile = System.getenv("PHP_834_LA_FILE_OUTPUT") + File.separator + "CFST." + fileDate + ".834";
			fileWriter = new FileWriter(outputFile);
			bufferedWriter = new BufferedWriter(fileWriter);
			// looping through code blocks
			for (String code : ediList) {

				bufferedWriter.write(code + System.lineSeparator());

			}

			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			System.out.println("exception occured " + e);
		}

	}

	// @param sfdcList

	public static void writeToSFDC(List<String> sfdcList, String fileDate) {

		// iterating through each code
		try {
			String outputFile = "";
			FileWriter fileWriter = null;
			BufferedWriter bufferedWriter = null;
			String fileDate1 = "";

			if (fileDate.contains("D")) {
				fileDate1 = fileDate.replace("D", "");
			} else {
				fileDate1 = "01" + fileDate;
			}

			outputFile = System.getenv("PHP_834_LA_FILE_OUTPUT") + File.separator + "Monthly_Salesforce_PCP_Assign_"
					+ fileDate1 + ".txt";
			fileWriter = new FileWriter(outputFile);
			bufferedWriter = new BufferedWriter(fileWriter);
			// looping through code blocks
			for (String code : sfdcList) {

				bufferedWriter.write(code + System.lineSeparator());

			}

			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			System.out.println("exception occured " + e);
		}
	}

	// @param hcoList

	public static void writeToHCO(List<String> hcoList, String fileDate) {

		// iterating through each code
		try {
			String outputFile = "";
			FileWriter fileWriter = null;
			BufferedWriter bufferedWriter = null;

			String fileDate1 = "";

			if (fileDate.contains("D")) {
				fileDate1 = fileDate.replace("D", "");
			} else {
				fileDate1 = "01" + fileDate;
			}
			outputFile = System.getenv("PHP_834_LA_FILE_OUTPUT") + File.separator + "T_" + fileDate1 + "_HCO167_01.167";
			fileWriter = new FileWriter(outputFile);
			bufferedWriter = new BufferedWriter(fileWriter);
			// looping through code blocks
			for (String code : hcoList) {

				bufferedWriter.write(code + System.lineSeparator());

			}

			bufferedWriter.close();
			fileWriter.close();
		} catch (IOException e) {
			System.out.println("exception occured " + e);
		}
	}

	/**
	 * laCalParsing method creates LACal 834 EDI files
	 * 
	 */
	public void laCalFileParsing(File LA_Cal) {

		ExcelUtils excelUtils = new ExcelUtils();

		/**
		 * inputDataMap - contains input data from LACal excel sheet
		 */
		Map<String, Map<String, String>> inputDataMap = new HashMap<String, Map<String, String>>();

		inputDataMap.putAll(excelUtils.cacheAllExcelData(LA_Cal.getPath()));

		inputDataMap.remove("");

		String fileNameDate = LA_Cal.getName().split("\\_")[1];
		fileNameDate = fileNameDate.split("\\.")[0];

		/**
		 * dbMap - stores facets database value
		 */
		Map<String, String> dbMap = new TreeMap<String, String>();
		String xlsPath = System.getenv("PHP_834_RESOURCES") + "\\" + "MediCal834EDIFileCreation." + "xlsx";

		Map<String, Map<String, String>> outerdataMap = excelUtils.cacheAllExcelData(xlsPath);
		Map<String, String> dataMap = outerdataMap.get("MediCal834EDIFileCreation");

		/**
		 * ediList - Stores all EDI Segments
		 */
		List<String> ediList = new ArrayList<String>();
		List<String> ediTempList = new ArrayList<String>();

		List<String> sfdcList = new ArrayList<String>();
		List<String> hcoList = new ArrayList<String>();

		Map<String, String> npiMap = new HashMap<>();
		SortedMap<String, SortedMap<String, String>> refzzMap = new TreeMap<>();

		Random random = new Random();
		String mcn = "", pbpID = "", pbpSDT = "";
		int stcount = 0;
		int len = 0;
		String effDate = "", aidCode = "", firstName = "", lastName = "", midInit = "", addr1 = "", addr2 = "";
		String city = "", zip = "", dob = "", gender = "", hicn = "", telephone = "";
		String controlNum = "", ssnID = "", cinNum = "", medsID = "", hicnNum = "";
		int controlNum1 = 0, ssnID1 = 0, cinNum1 = 0, cinNum1Al = 0, medsID1 = 0, medsID1Al = 0, hicnNum1 = 0,
				hicnNum2 = 0, hicnNum1Al = 0;

		// Standard segments
		controlNum1 = random.nextInt((999999999 - 100000000) + 1) + 100000000;
		controlNum = String.valueOf(controlNum1);

		String isa = "ISA*00*          *00*          *ZZ*2136941250     *30*954468482      *170811*1710*^*00501*"
				+ controlNum + "*0*T* ~";
		ediList.add(isa);

		String gs = "GS*BE*LA CARE MHC*954468482*20211201*171005*1067*X*005010X220A1~";
		ediList.add(gs);

		// EDI File generation
		for (String key : inputDataMap.keySet()) {
			// NewEnrollment
			if (inputDataMap.get(key).get("ScenarioType").contains("NewEnrollment")) {
				ediTempList.clear();
				stcount++;

				String st = "ST*834*2045*005010X220A1~";
				ediList.add(st);
				String bgn = "BGN*00*2045*" + timestamp + "*2213****2~";
				ediList.add(bgn);
				String dtp007 = "DTP*007*D8*20221201~";
				ediList.add(dtp007);
				String n1p5 = "N1*P5*L.A. CARE*FI*954518790~";
				ediList.add(n1p5);
				String n1in = "N1*IN*CARE1ST*FI*954468482~";
				ediList.add(n1in);
				String insy = "INS*Y*18*021**A*E**FT~";
				ediList.add(insy);

				ssnID1 = random.nextInt((999999999 - 100000000) + 1) + 100000000;
				ssnID = String.valueOf(ssnID1);

				String ref0f = "REF*0F*" + ssnID + "*~";
				ediList.add(ref0f);
				String ref1l = "REF*1L*19" + inputDataMap.get(key).get("AIDCode") + "Q8C0418791~";
				ediList.add(ref1l);
				String ref3h = "REF*3H*Q8C0418~";
				ediList.add(ref3h);
				String refdx = "REF*DX*CFSTNFAL19~";
				ediList.add(refdx);

				medsID1 = random.nextInt((99999999 - 10000000) + 1) + 10000000;
				medsID1Al = random.nextInt((90 - 65) + 1) + 65;
				medsID = String.valueOf(medsID1) + (char) (medsID1Al);

				String refq4 = "REF*Q4*" + medsID + "~";
				ediList.add(refq4);

				hicnNum1 = random.nextInt((999999999 - 100000000) + 1) + 10000000;
				hicnNum2 = random.nextInt((9 - 0) + 1);
				hicnNum1Al = random.nextInt((90 - 65) + 1) + 65;
				hicnNum = String.valueOf(hicnNum1) + String.valueOf(hicnNum2) + (char) (hicnNum1Al);

				String reff6 = "REF*F6*" + hicnNum + "~";
				ediList.add(reff6);
				String refzz = "REF*ZZ*" + inputDataMap.get(key).get("MedicareContactNumber") + ";"
						+ inputDataMap.get(key).get("PBPID") + ";" + inputDataMap.get(key).get("PBP Start Date")
						+ ";S6946;C023;20230101~";
				ediList.add(refzz);
				String dtp303_1 = "DTP*303*D8*" + inputDataMap.get(key).get("EffectiveDate") + "~";
				ediList.add(dtp303_1);
				String dtp357 = "DTP*357*D8~";
				ediList.add(dtp357);
				String dtp356 = "DTP*356*D8*" + inputDataMap.get(key).get("EffectiveDate") + "~";
				ediList.add(dtp356);
				String dtp394 = "DTP*394*D8*" + inputDataMap.get(key).get("EffectiveDate") + "~";
				ediList.add(dtp394);
				String dtp473 = "DTP*473*D8*" + inputDataMap.get(key).get("EffectiveDate") + "~";
				ediList.add(dtp473);

				cinNum1 = random.nextInt((99999999 - 10000000) + 1) + 10000000;
				cinNum1Al = random.nextInt((90 - 65) + 1) + 65;
				cinNum = String.valueOf(cinNum1) + (char) (cinNum1Al);

				String nm1il_1 = "NM1*IL*1*" + inputDataMap.get(key).get("LastName") + "*"
						+ inputDataMap.get(key).get("FirstName") + "*" + inputDataMap.get(key).get("MiddleInitial")
						+ "***ZZ*" + cinNum + "~";
				ediList.add(nm1il_1);
				String perip = "PER*IP**TE*" + inputDataMap.get(key).get("Telephone") + "~";
				ediList.add(perip);
				String nm1il1_n3 = "N3*" + inputDataMap.get(key).get("Address1") + "*"
						+ inputDataMap.get(key).get("Address2") + "~";
				ediList.add(nm1il1_n3);
				String nm1il1_n4 = "N4*" + inputDataMap.get(key).get("City") + "*CA*"
						+ inputDataMap.get(key).get("ZipCode") + "**CY*1911~";
				ediList.add(nm1il1_n4);
				String dmgd8 = "DMG*D8*" + inputDataMap.get(key).get("DOB") + "*" + inputDataMap.get(key).get("Gender")
						+ "**>RET>2033-1~";
				ediList.add(dmgd8);
				String amtc1 = "AMT*C1*0~";
				ediList.add(amtc1);
				String luile_1 = "LUI*LE*" + inputDataMap.get(key).get("LanguageCode(Spoken)") + "**6~";
				ediList.add(luile_1);
				String luile_2 = "LUI*LE*" + inputDataMap.get(key).get("LanguageCode(Written)") + "*7*~";
				ediList.add(luile_2);
				String hd021_1 = "HD*021**HMO*" + inputDataMap.get(key).get("AIDCode") + "301      N22"
						+ inputDataMap.get(key).get("HCPStatusCode") + "   " + inputDataMap.get(key).get("AIDCode")
						+ "198                 A  ~";
				ediList.add(hd021_1);
				String dtp303_2 = "DTP*303*D8*" + inputDataMap.get(key).get("EffectiveDate") + "~";
				ediList.add(dtp303_2);
				String amtd2_1 = "AMT*D2*76~";
				ediList.add(amtd2_1);
				String refxx1_1 = "REF*XX1*;;;;;;1~";
				ediList.add(refxx1_1);
				String lx1 = "LX*1~";
				ediList.add(lx1);
				String nm1p3 = "NM1*P3*1******XX*123456789*72~";
				ediList.add(nm1p3);
				String peric = "PER*IC**EM*P0032226       FA0003415001S01Clinic Name~";
				ediList.add(peric);
				String cobp_1 = "COB*P*CFS*5~";
				ediList.add(cobp_1);
				String hd021_2 = "HD*021**HMO*" + inputDataMap.get(key).get("AIDCode") + "301      N22"
						+ inputDataMap.get(key).get("HCPStatusCode") + "   " + inputDataMap.get(key).get("AIDCode")
						+ "198                 A  ~";
				ediList.add(hd021_2);

				String effectiveDate = inputDataMap.get(key).get("EffectiveDate");
				String oneMonthAgoDate = getoneMonthAgoDate(effectiveDate);

				String dtp303_3 = "DTP*303*D8*" + oneMonthAgoDate + "~";
				ediList.add(dtp303_3);
				String amtd2_2 = "AMT*D2*76~";
				ediList.add(amtd2_2);
				String refxx1_2 = "REF*XX1*;;;;;;1~";
				ediList.add(refxx1_2);
				String cobp_2 = "COB*P*CFS*5~";
				ediList.add(cobp_2);

				// Repetitive segments
				String oneMonthAgoDate1 = getoneMonthAgoDate(oneMonthAgoDate);
				for (int counter = 1; counter <= 11; counter++) {
					String hd021_3 = "HD*021**HMO*" + inputDataMap.get(key).get("AIDCode") + "301      N22"
							+ inputDataMap.get(key).get("HCPStatusCode") + "   " + inputDataMap.get(key).get("AIDCode")
							+ "198                 A  ~";
					ediList.add(hd021_3);
					String dtp303_4 = "DTP*303*D8*" + oneMonthAgoDate1 + "~";
					ediList.add(dtp303_4);
					String amtd2_3 = "AMT*D2*76~";
					ediList.add(amtd2_3);
					String refxx1_3 = "REF*XX1*;;;;;;1~";
					ediList.add(refxx1_3);
					String cobp_3 = "COB*P*CFS*5~";
					ediList.add(cobp_3);
					oneMonthAgoDate1 = getoneMonthAgoDate(oneMonthAgoDate1);
				}

				len = len + 95;
				String linecount = String.valueOf(len);
				String se = "SE*" + linecount + "*2045~";
				ediList.add(se);

				if (inputDataMap.get(key).get("SFDC").equalsIgnoreCase("Yes")) {
					String sfdc = "\"" + cinNum + "\"|\"" + inputDataMap.get(key).get("DOB") + "\"|\"" + "0015"
							+ "\"|\"" + inputDataMap.get(key).get("EffectiveDate") + "\"|\""
							+ inputDataMap.get(key).get("SalesForceProviderID") + "\"";
					sfdcList.add(sfdc);
				}

				if (inputDataMap.get(key).get("HCO").equalsIgnoreCase("Yes")) {
					npiMap.putAll(getDataFromQuery("select PRPR_NPI as NPI from fc_cmc_prpr_prov where PRPR_ID = ?",
							inputDataMap.get(key).get("SalesForceProviderID")));

					String hco = "167\t" + inputDataMap.get(key).get("LastName") + "\t"
							+ inputDataMap.get(key).get("FirstName") + "\t" + inputDataMap.get(key).get("MiddleInitial")
							+ "\t" + ssnID + "\t" + "019\t" + inputDataMap.get(key).get("AIDCode") + "\t" + "H9QNO43\t"
							+ "8\t" + "81\t" + inputDataMap.get(key).get("DOB") + "\t" + "1\t" + "\t"
							+ inputDataMap.get(key).get("Address1") + "\t" + inputDataMap.get(key).get("Address2")
							+ "\t" + inputDataMap.get(key).get("City") + "\t" + inputDataMap.get(key).get("ZipCode")
							+ "\t" + "CA\t" + inputDataMap.get(key).get("Telephone") + "\t"
							+ inputDataMap.get(key).get("Gender") + "\t" + "7\t" + npiMap.get("NPI") + "\t"
							+ "N/A/N/A\t" + inputDataMap.get(key).get("EffectiveDate") + "\t" + "\t"
							+ "Emergency Enroll\t" + "\t" + "N/A\t" + cinNum + "\t" + "N/A\t" + "N/A/N/A\t" + hicnNum
							+ "\t" + "47802183\t" + inputDataMap.get(key).get("SalesForceProviderID") + "\t" + "N/A\t"
							+ npiMap.get("NPI") + "\t" + "N/A\t" + "H5928\t" + "005\t" + "20230101";
					hcoList.add(hco);
					npiMap.clear();
				}
			}

			// UpdateExistingUsers
			if (inputDataMap.get(key).get("ScenarioType").contains("UpdateExistingUsers")) {
				ediTempList.clear();
				stcount++;

				dbMap.putAll(
						getDataFromQuery(dataMap.get("Subscriber_Details"), inputDataMap.get(key).get("CIN Number")));

				String st = "ST*834*2045*005010X220A1~";
				ediList.add(st);
				String bgn = "BGN*00*2045*" + timestamp + "*2213****2~";
				ediList.add(bgn);
				String dtp007 = "DTP*007*D8*20221201~";
				ediList.add(dtp007);
				String n1p5 = "N1*P5*L.A. CARE*FI*954518790~";
				ediList.add(n1p5);
				String n1in = "N1*IN*CARE1ST*FI*954468482~";
				ediList.add(n1in);

				String insy = "INS*Y*18*001**A*E**FT~";
				ediList.add(insy);

				String ref0f = "REF*0F*" + inputDataMap.get(key).get("SSN") + "*~";
				ediList.add(ref0f);

				Map<String, String> aidMap = new HashMap<>();
				aidMap.putAll(
						getDataFromQuery("select mecd_mctr_aidc as AIDCD from fc_cmc_mecd_medicaid where MEME_CK = ?",
								dbMap.get("MCK")));
				if (inputDataMap.get(key).get("AIDCode").isEmpty()) {
					aidCode = aidMap.get("AIDCD");
				} else {
					aidCode = inputDataMap.get(key).get("AIDCode");
				}

				String ref1l = "REF*1L*19" + aidCode + "Q8C0418791~"; // Check once
				ediList.add(ref1l);
				String ref3h = "REF*3H*Q8C0418~";
				ediList.add(ref3h);
				String refdx = "REF*DX*CFSTNFAL19~";
				ediList.add(refdx);

				String refq4 = "REF*Q4*" + inputDataMap.get(key).get("MEDSID") + "~"; // Check once
				ediList.add(refq4);

				hicn = dbMap.get("HICN");

				String reff6 = "REF*F6*" + hicn + "~";
				ediList.add(reff6);

				if (inputDataMap.get(key).get("EffectiveDate").isEmpty()) {
					effDate = dbMap.get("EFFECTIVE_DATE");
				} else {
					effDate = inputDataMap.get(key).get("EffectiveDate");
				}

				refzzMap.putAll(getMultiRowsDataFromPreparedQuery(
						"select a.MEME_MEDCD_NO as CIN_NUMBER,c.CUSTM_DTA_ELEM_KEY AS ELM_KEY, "
								+ "c.CUSTM_DTA_ELEM_VAL_TXT AS ELM_VAL_TXT, To_CHAR(c.CUSTM_DTA_ELEM_VAL_DT,'YYYYMMDD') "
								+ "AS ELM_VAL_DT from FC_CMC_MEME_MEMBER a INNER JOIN fc_cmc_sbsb_subsc b ON "
								+ "b.sbsb_ck = a.meme_ck INNER JOIN CU_EXT_CUSTM_DTA_ELEM_VAL c ON "
								+ "c.FACETS_ID = b.sbsb_ID where  c.CUSTM_DTA_ELEM_KEY in ('486','487','488') and "
								+ "a.MEME_MEDCD_NO = ?",
						inputDataMap.get(key).get("CIN Number")));
				for (String refKeyset : refzzMap.keySet()) {
					if (refzzMap.get(refKeyset).get("ELM_KEY").contains("486")) {
						mcn = refzzMap.get(refKeyset).get("ELM_VAL_TXT");
					}
					if (refzzMap.get(refKeyset).get("ELM_KEY").contains("487")) {
						pbpID = refzzMap.get(refKeyset).get("ELM_VAL_TXT");
					}
					if (refzzMap.get(refKeyset).get("ELM_KEY").contains("488")) {
						pbpSDT = refzzMap.get(refKeyset).get("ELM_VAL_DT");
					}
				}

				if (!inputDataMap.get(key).get("MedicareContactNumber").isEmpty()) {
					mcn = inputDataMap.get(key).get("MedicareContactNumber");
				}
				if (!inputDataMap.get(key).get("PBPID").isEmpty()) {
					pbpID = inputDataMap.get(key).get("PBPID");
				}
				if (!inputDataMap.get(key).get("PBP Start Date").isEmpty()) {
					pbpSDT = inputDataMap.get(key).get("PBP Start Date");
				}

				String refzz = "REF*ZZ*" + mcn + ";" + pbpID + ";" + pbpSDT + ";S6946;C023;20230101~";
				ediList.add(refzz);
				String dtp303_1 = "DTP*303*D8*" + effDate + "~";
				ediList.add(dtp303_1);
				String dtp357 = "DTP*357*D8~";
				ediList.add(dtp357);
				String dtp356 = "DTP*356*D8*" + effDate + "~";
				ediList.add(dtp356);
				String dtp394 = "DTP*394*D8*" + effDate + "~";
				ediList.add(dtp394);
				String dtp473 = "DTP*473*D8*" + effDate + "~";
				ediList.add(dtp473);

				if (inputDataMap.get(key).get("FirstName").isEmpty()) {
					firstName = dbMap.get("FIRST_NAME");
				} else {
					firstName = inputDataMap.get(key).get("FirstName");
				}

				if (inputDataMap.get(key).get("LastName").isEmpty()) {
					lastName = dbMap.get("LAST_NAME");
				} else {
					lastName = inputDataMap.get(key).get("LastName");
				}

				if (inputDataMap.get(key).get("MiddleInitial").isEmpty()) {
					midInit = dbMap.get("MIDDLE_INITIAL");
				} else {
					midInit = inputDataMap.get(key).get("MiddleInitial");
				}

				String nm1il_1 = "NM1*IL*1*" + lastName + "*" + firstName + "*" + midInit + "***ZZ*"
						+ inputDataMap.get(key).get("CIN Number") + "~"; // Check once
				ediList.add(nm1il_1);

				if (inputDataMap.get(key).get("Telephone").isEmpty()) {
					telephone = dbMap.get("TELEPHONE");
				} else {
					telephone = inputDataMap.get(key).get("Telephone");
				}

				String perip = "PER*IP**TE*" + telephone + "~";
				ediList.add(perip);

				if (inputDataMap.get(key).get("Address1").isEmpty()) {
					addr1 = dbMap.get("ADDRESS1");
				} else {
					addr1 = inputDataMap.get(key).get("Address1");
				}

				if (inputDataMap.get(key).get("Address2").isEmpty()) {
					addr2 = dbMap.get("ADDRESS2");
				} else {
					addr2 = inputDataMap.get(key).get("Address2");
				}

				String nm1il1_n3 = "N3*" + addr1 + "*" + addr1 + "~"; // Check once
				ediList.add(nm1il1_n3);

				if (inputDataMap.get(key).get("City").isEmpty()) {
					city = dbMap.get("CITY");
				} else {
					city = inputDataMap.get(key).get("City");
				}

				if (inputDataMap.get(key).get("ZipCode").isEmpty()) {
					zip = dbMap.get("ZIP");
				} else {
					zip = inputDataMap.get(key).get("ZipCode");
				}

				String nm1il1_n4 = "N4*" + city + "*CA*" + zip + "**CY*1911~";
				ediList.add(nm1il1_n4);

				if (inputDataMap.get(key).get("DOB").isEmpty()) {
					dob = dbMap.get("DOB");
				} else {
					dob = inputDataMap.get(key).get("DOB");
				}

				if (inputDataMap.get(key).get("Gender").isEmpty()) {
					gender = dbMap.get("GENDER");
				} else {
					gender = inputDataMap.get(key).get("Gender");
				}

				String dmgd8 = "DMG*D8*" + dob + "*" + gender + "**>RET>2033-1~"; // Check once
				ediList.add(dmgd8);
				String amtc1 = "AMT*C1*0~";
				ediList.add(amtc1);

				String luile_1 = "LUI*LE*" + inputDataMap.get(key).get("LanguageCode(Spoken)") + "**6~";
				ediList.add(luile_1);
				String luile_2 = "LUI*LE*" + inputDataMap.get(key).get("LanguageCode(Written)") + "*7*~";
				ediList.add(luile_2); // Check these

				String hd021_1 = "HD*001**HMO*" + aidCode + "301      N22" + inputDataMap.get(key).get("HCPStatusCode")
						+ "   " + aidCode + "198                 A  ~";
				ediList.add(hd021_1); // Check these

				String dtp303_2 = "DTP*303*D8*" + effDate + "~";
				ediList.add(dtp303_2);
				String amtd2_1 = "AMT*D2*76~";
				ediList.add(amtd2_1);
				String refxx1_1 = "REF*XX1*;;;;;;1~";
				ediList.add(refxx1_1);
				String lx1 = "LX*1~";
				ediList.add(lx1);
				String nm1p3 = "NM1*P3*1******XX*123456789*72~";
				ediList.add(nm1p3);
				String peric = "PER*IC**EM*P0032226       FA0003415001S01Clinic Name~";
				ediList.add(peric);
				String cobp_1 = "COB*P*CFS*5~";
				ediList.add(cobp_1);

				String hd021_2 = "HD*001**HMO*" + aidCode + "301      N22" + inputDataMap.get(key).get("HCPStatusCode")
						+ "   " + aidCode + "198                 A  ~";
				ediList.add(hd021_2); // Check these

				String effectiveDate = effDate;
				String oneMonthAgoDate = getoneMonthAgoDate(effectiveDate);

				String dtp303_3 = "DTP*303*D8*" + oneMonthAgoDate + "~";
				ediList.add(dtp303_3);
				String amtd2_2 = "AMT*D2*76~";
				ediList.add(amtd2_2);
				String refxx1_2 = "REF*XX1*;;;;;;1~";
				ediList.add(refxx1_2);
				String cobp_2 = "COB*P*CFS*5~";
				ediList.add(cobp_2);

				// Repetitive segments
				String oneMonthAgoDate1 = getoneMonthAgoDate(oneMonthAgoDate);
				for (int counter = 1; counter <= 11; counter++) {
					String hd021_3 = "HD*001**HMO*" + aidCode + "301      N22"
							+ inputDataMap.get(key).get("HCPStatusCode") + "   " + aidCode + "198                 A  ~";

					ediList.add(hd021_3);
					String dtp303_4 = "DTP*303*D8*" + oneMonthAgoDate1 + "~";
					ediList.add(dtp303_4);
					String amtd2_3 = "AMT*D2*76~";
					ediList.add(amtd2_3);
					String refxx1_3 = "REF*XX1*;;;;;;1~";
					ediList.add(refxx1_3);
					String cobp_3 = "COB*P*CFS*5~";
					ediList.add(cobp_3);
					oneMonthAgoDate1 = getoneMonthAgoDate(oneMonthAgoDate1);
				}

				len = len + 95;
				String linecount = String.valueOf(len);
				String se = "SE*" + linecount + "*2045~";
				ediList.add(se);
				if (inputDataMap.get(key).get("SFDC").equalsIgnoreCase("Yes")) {
					String sfdc = "\"" + inputDataMap.get(key).get("CIN Number") + "\"|\"" + dob + "\"|\"" + "0015"
							+ "\"|\"" + effDate + "\"|\"" + inputDataMap.get(key).get("SalesForceProviderID") + "\"";
					sfdcList.add(sfdc);
				}

				if (inputDataMap.get(key).get("HCO").equalsIgnoreCase("Yes")) {
					npiMap.putAll(getDataFromQuery("select PRPR_NPI as NPI from fc_cmc_prpr_prov where PRPR_ID = ?",
							inputDataMap.get(key).get("SalesForceProviderID")));

					String hco = "167\t" + lastName + "\t" + firstName + "\t" + midInit + "\t"
							+ inputDataMap.get(key).get("SSN") + "\t" + "019\t" + aidCode + "\t" + "H9QNO43\t" + "8\t"
							+ "81\t" + dob + "\t" + "1\t" + "\t" + addr1 + "\t" + addr2 + "\t" + city + "\t" + zip
							+ "\t" + "CA\t" + telephone + "\t" + gender + "\t" + "7\t" + npiMap.get("NPI") + "\t"
							+ "N/A/N/A\t" + effDate + "\t" + "\t" + "Emergency Enroll\t" + "\t" + "N/A\t"
							+ inputDataMap.get(key).get("CIN Number") + "\t" + "N/A\t" + "N/A/N/A\t" + hicn + "\t"
							+ "47802183\t" + inputDataMap.get(key).get("SalesForceProviderID") + "\t" + "N/A\t"
							+ npiMap.get("NPI") + "\t" + "N/A\t" + "H5928\t" + "005\t" + "20230101";
					hcoList.add(hco);
					npiMap.clear();
				}
			}

			// DisEnrollExistingUser
			if (inputDataMap.get(key).get("ScenarioType").contains("DisEnrollExistingUser")) {
				ediTempList.clear();
				stcount++;

				dbMap.putAll(
						getDataFromQuery(dataMap.get("Subscriber_Details"), inputDataMap.get(key).get("CIN Number")));

				String st = "ST*834*2045*005010X220A1~";
				ediList.add(st);
				String bgn = "BGN*00*2045*" + timestamp + "*2213****2~";
				ediList.add(bgn);
				String dtp007 = "DTP*007*D8*20221201~";
				ediList.add(dtp007);
				String n1p5 = "N1*P5*L.A. CARE*FI*954518790~";
				ediList.add(n1p5);
				String n1in = "N1*IN*CARE1ST*FI*954468482~";
				ediList.add(n1in);

				String insy = "INS*Y*18*024**A*E**TE~";
				ediList.add(insy);

				String ref0f = "REF*0F*" + inputDataMap.get(key).get("SSN") + "*~";
				ediList.add(ref0f);

				Map<String, String> aidMap = new HashMap<>();
				aidMap.putAll(
						getDataFromQuery("select mecd_mctr_aidc as AIDCD from fc_cmc_mecd_medicaid where MEME_CK = ?",
								dbMap.get("MCK")));

				aidCode = aidMap.get("AIDCD");

				String ref1l = "REF*1L*19" + aidCode + "Q8C0418791~"; // Check once
				ediList.add(ref1l);
				String ref3h = "REF*3H*Q8C0418~";
				ediList.add(ref3h);
				String refdx = "REF*DX*CFSTNFAL19~";
				ediList.add(refdx);

				String refq4 = "REF*Q4*" + inputDataMap.get(key).get("MEDSID") + "~"; // Check once
				ediList.add(refq4);

				hicn = dbMap.get("HICN");

				String reff6 = "REF*F6*" + hicn + "~";
				ediList.add(reff6);

				effDate = dbMap.get("EFFECTIVE_DATE");

				refzzMap.putAll(getMultiRowsDataFromPreparedQuery(
						"select a.MEME_MEDCD_NO as CIN_NUMBER,c.CUSTM_DTA_ELEM_KEY AS ELM_KEY, "
								+ "c.CUSTM_DTA_ELEM_VAL_TXT AS ELM_VAL_TXT, To_CHAR(c.CUSTM_DTA_ELEM_VAL_DT,'YYYYMMDD') "
								+ "AS ELM_VAL_DT from FC_CMC_MEME_MEMBER a INNER JOIN fc_cmc_sbsb_subsc b ON "
								+ "b.sbsb_ck = a.meme_ck INNER JOIN CU_EXT_CUSTM_DTA_ELEM_VAL c ON "
								+ "c.FACETS_ID = b.sbsb_ID where  c.CUSTM_DTA_ELEM_KEY in ('486','487','488') and "
								+ "a.MEME_MEDCD_NO = ?",
						inputDataMap.get(key).get("CIN Number")));
				for (String refKeyset : refzzMap.keySet()) {
					if (refzzMap.get(refKeyset).get("ELM_KEY").contains("486")) {
						mcn = refzzMap.get(refKeyset).get("ELM_VAL_TXT");
					}
					if (refzzMap.get(refKeyset).get("ELM_KEY").contains("487")) {
						pbpID = refzzMap.get(refKeyset).get("ELM_VAL_TXT");
					}
					if (refzzMap.get(refKeyset).get("ELM_KEY").contains("488")) {
						pbpSDT = refzzMap.get(refKeyset).get("ELM_VAL_DT");
					}
				}

				String refzz = "REF*ZZ*" + mcn + ";" + pbpID + ";" + pbpSDT + ";S6946;C023;20230101~";
				ediList.add(refzz);
				String dtp303_1 = "DTP*303*D8*" + effDate + "~";
				ediList.add(dtp303_1);
				String dtp357 = "DTP*357*D8~";
				ediList.add(dtp357);
				String dtp356 = "DTP*356*D8*" + effDate + "~";
				ediList.add(dtp356);
				String dtp394 = "DTP*394*D8*" + effDate + "~";
				ediList.add(dtp394);
				String dtp473 = "DTP*473*D8*" + effDate + "~";
				ediList.add(dtp473);

				firstName = dbMap.get("FIRST_NAME");
				lastName = dbMap.get("LAST_NAME");
				midInit = dbMap.get("MIDDLE_INITIAL");

				String nm1il_1 = "NM1*IL*1*" + lastName + "*" + firstName + "*" + midInit + "***ZZ*"
						+ inputDataMap.get(key).get("CIN Number") + "~"; // Check once
				ediList.add(nm1il_1);

				telephone = dbMap.get("TELEPHONE");

				String perip = "PER*IP**TE*" + telephone + "~";
				ediList.add(perip);

				addr1 = dbMap.get("ADDRESS1");
				addr2 = dbMap.get("ADDRESS2");

				String nm1il1_n3 = "N3*" + addr1 + "*" + addr1 + "~"; // Check once
				ediList.add(nm1il1_n3);

				city = dbMap.get("CITY");
				zip = dbMap.get("ZIP");

				String nm1il1_n4 = "N4*" + city + "*CA*" + zip + "**CY*1911~";
				ediList.add(nm1il1_n4);

				dob = dbMap.get("DOB");
				gender = dbMap.get("GENDER");

				String dmgd8 = "DMG*D8*" + dob + "*" + gender + "**>RET>2033-1~"; // Check once
				ediList.add(dmgd8);
				String amtc1 = "AMT*C1*0~";
				ediList.add(amtc1);

				String luile_1 = "LUI*LE*" + inputDataMap.get(key).get("LanguageCode(Spoken)") + "**6~";
				ediList.add(luile_1);
				String luile_2 = "LUI*LE*" + inputDataMap.get(key).get("LanguageCode(Written)") + "*7*~";
				ediList.add(luile_2); // Check these

				String hd021_1 = "HD*024**HMO*" + aidCode + "301      N2200   " + aidCode + "198                 A  ~";
				ediList.add(hd021_1); // Check these

				String dtp303_2 = "DTP*303*D8*" + effDate + "~";
				ediList.add(dtp303_2);
				String amtd2_1 = "AMT*D2*76~";
				ediList.add(amtd2_1);
				String refxx1_1 = "REF*XX1*;;;;;;1~";
				ediList.add(refxx1_1);
				String lx1 = "LX*1~";
				ediList.add(lx1);
				String nm1p3 = "NM1*P3*1******XX*123456789*72~";
				ediList.add(nm1p3);
				String peric = "PER*IC**EM*P0032226       FA0003415001S01Clinic Name~";
				ediList.add(peric);
				String cobp_1 = "COB*P*CFS*5~";
				ediList.add(cobp_1);

				String hd021_2 = "HD*024**HMO*" + aidCode + "301      N2201   " + aidCode + "198                 A  ~";
				ediList.add(hd021_2); // Check these

				String effectiveDate = effDate;
				String oneMonthAgoDate = getoneMonthAgoDate(effectiveDate);

				String dtp303_3 = "DTP*303*D8*" + oneMonthAgoDate + "~";
				ediList.add(dtp303_3);
				String amtd2_2 = "AMT*D2*76~";
				ediList.add(amtd2_2);
				String refxx1_2 = "REF*XX1*;;;;;;1~";
				ediList.add(refxx1_2);
				String cobp_2 = "COB*P*CFS*5~";
				ediList.add(cobp_2);

				// Repetitive segments
				String oneMonthAgoDate1 = getoneMonthAgoDate(oneMonthAgoDate);
				for (int counter = 1; counter <= 11; counter++) {
					String hd021_3 = "HD*024**HMO*" + aidCode + "301      N2201   " + aidCode
							+ "198                 A  ~"; // Check these
					ediList.add(hd021_3);
					String dtp303_4 = "DTP*303*D8*" + oneMonthAgoDate1 + "~";
					ediList.add(dtp303_4);
					String amtd2_3 = "AMT*D2*76~";
					ediList.add(amtd2_3);
					String refxx1_3 = "REF*XX1*;;;;;;1~";
					ediList.add(refxx1_3);
					String cobp_3 = "COB*P*CFS*5~";
					ediList.add(cobp_3);
					oneMonthAgoDate1 = getoneMonthAgoDate(oneMonthAgoDate1);
				}

				len = len + 95;
				String linecount = String.valueOf(len);
				String se = "SE*" + linecount + "*2045~";
				ediList.add(se);
				if (inputDataMap.get(key).get("SFDC").equalsIgnoreCase("Yes")) {
					String sfdc = "\"" + inputDataMap.get(key).get("CIN Number") + "\"|\"" + dob + "\"|\"" + "0015"
							+ "\"|\"" + effDate + "\"|\"" + inputDataMap.get(key).get("SalesForceProviderID") + "\"";
					sfdcList.add(sfdc);
				}

				if (inputDataMap.get(key).get("HCO").equalsIgnoreCase("Yes")) {
					npiMap.putAll(getDataFromQuery("select PRPR_NPI as NPI from fc_cmc_prpr_prov where PRPR_ID = ?",
							inputDataMap.get(key).get("SalesForceProviderID")));

					String hco = "167\t" + lastName + "\t" + firstName + "\t" + midInit + "\t"
							+ inputDataMap.get(key).get("SSN") + "\t" + "019\t" + aidCode + "\t" + "H9QNO43\t" + "8\t"
							+ "81\t" + dob + "\t" + "1\t" + "\t" + addr1 + "\t" + addr2 + "\t" + city + "\t" + zip
							+ "\t" + "CA\t" + telephone + "\t" + gender + "\t" + "7\t" + npiMap.get("NPI") + "\t"
							+ "N/A/N/A\t" + effDate + "\t" + "\t" + "Emergency Enroll\t" + "\t" + "N/A\t"
							+ inputDataMap.get(key).get("CIN Number") + "\t" + "N/A\t" + "N/A/N/A\t" + hicn + "\t"
							+ "47802183\t" + inputDataMap.get(key).get("SalesForceProviderID") + "\t" + "N/A\t"
							+ npiMap.get("NPI") + "\t" + "N/A\t" + "H5928\t" + "005\t" + "20230101";
					hcoList.add(hco);
					npiMap.clear();
				}
			}
		}

		// Standard segments
		String ge = "GE*" + stcount + "*171005~";
		ediList.add(ge);
		String iea = "IEA*1*" + controlNum + "~";
		ediList.add(iea);

		writeToEDI(ediList, fileNameDate);
		if (!sfdcList.isEmpty()) {
			writeToSFDC(sfdcList, fileNameDate);
		}
		if (!hcoList.isEmpty()) {
			writeToHCO(hcoList, fileNameDate);
		}
	}

	// To get previous month date
	public static String getoneMonthAgoDate(String givenDate) {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		try {
			Date date = dateFormat.parse(givenDate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.MONTH, -1);
			Date oneMonthAgo = calendar.getTime();
			return dateFormat.format(oneMonthAgo);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * @param query
	 * @param xmlFileMap
	 * @param subID
	 */

	private static Map<String, String> getDataFromQuery(String query, Object... subID) {
		Map<String, String> finalDbMap = new HashMap<String, String>();
		try {

			finalDbMap = new DBUtils().getDataFromPreparedQuery("facets", query, subID);
		} catch (ArrayIndexOutOfBoundsException e) {

			System.out.println("!!DB Exception in getDataFromQuery method!!");
		}

		return finalDbMap;
	}

	/**
	 * @param query
	 * @param xmlFileMap
	 * @param subID
	 */

	private static SortedMap<String, SortedMap<String, String>> getMultiRowsDataFromPreparedQuery(String query,
			Object... subID) {
		SortedMap<String, SortedMap<String, String>> finalDbMap = new TreeMap<>();
		try {

			finalDbMap = new DBUtils().getMultiRowsFromPreparedQuery("facets", query, subID);
		} catch (ArrayIndexOutOfBoundsException e) {

			System.out.println("!!DB Exception in getDataFromQuery method!!");
		}

		return finalDbMap;
	}
}
