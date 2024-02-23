package com.bsc.qa.stt;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.ThreadLocalRandom;

import com.bsc.qa.framework.utility.DBUtils;

/**
 * 
 * @author vkodig01
 *
 */
public class SDCalFileParser {

	// inputFileName :contains full file path
	public String inputFileName = null;
	public String type_ofadimission_codevalue = null;

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
	static DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	static String timestamp1 = LocalDate.ofEpochDay(randomEpochDay).format(dateTimeFormatter);

	// suiteName to store the report location folder
	public static String suiteName = System.getProperty("user.dir")
			.substring(System.getProperty("user.dir").lastIndexOf("\\") + 1);

	// @param ediList

	public static void writeToEDI(List<String> ediList, String regression) {

		// iterating through each code
		try {
			String outputFile = "";
			FileWriter fileWriter = null;
			BufferedWriter bufferedWriter = null;
			outputFile = System.getenv("PHP_834_SD_FILE_OUTPUT") + File.separator + regression + ".dat";
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

	public static void writeToSFDC(List<String> sfdcList, String fileNameDate) {

		// iterating through each code
		try {
			String outputFile = "";
			FileWriter fileWriter = null;
			BufferedWriter bufferedWriter = null;
			String fileNameDate1 = "";

			fileNameDate1 = fileNameDate.split("\\-")[1];
			outputFile = System.getenv("PHP_834_SD_FILE_OUTPUT") + File.separator + "Monthly_Salesforce_PCP_Assign_"
					+ fileNameDate1 + ".txt";
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

	public static void writeToHCO(List<String> hcoList, String fileNameDate) {

		// iterating through each code
		try {
			String outputFile = "";
			FileWriter fileWriter = null;
			BufferedWriter bufferedWriter = null;
			String fileNameDate1 = "";

			fileNameDate1 = fileNameDate.split("\\-")[1];
			outputFile = System.getenv("PHP_834_SD_FILE_OUTPUT") + File.separator + "T_" + fileNameDate1 + "_HCO167_01.167";
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
	 * sdCalParsing method creates SDCal 834 EDI files
	 */
	public void sdCalFileParsing(File SD_Cal) {

		ExcelUtils excelUtils = new ExcelUtils();

		/**
		 * inputDataMap - contains input data from SDCal excel sheet
		 */
		Map<String, Map<String, String>> inputDataMap = new HashMap<String, Map<String, String>>();

		inputDataMap.putAll(excelUtils.cacheAllExcelData(SD_Cal.getPath()));

		inputDataMap.remove("");

		String fileNameDate = SD_Cal.getName().split("\\_")[1];
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
		String reg = "", mcn = "", pbpID = "", pbpSDT = "";
		int inscount = 0;
		int len = 0;
		String effDate = "", aidCode = "", firstName = "", lastName = "", midInit = "", addr1 = "", addr2 = "";
		String city = "", zip = "", dob = "", gender = "", hicn = "", telephone = "";
		String controlNum = "", ssnID = "", cinNum = "", medsID = "", hicnNum = "";
		int controlNum1 = 0, ssnID1 = 0, cinNum1 = 0, cinNum1Al = 0, medsID1 = 0, medsID1Al = 0, hicnNum1 = 0,
				hicnNum2 = 0, hicnNum1Al = 0;

		// Standard segments
		controlNum1 = random.nextInt((999999999 - 100000000) + 1) + 100000000;
		controlNum = String.valueOf(controlNum1);

		String isa = "ISA*00*          *00*          *ZZ*CADHCS_5010_834*30*954468482      *170811*2033*^*00501*"
				+ controlNum + "*0*T* ~";
		ediList.add(isa);

		String gs = "GS*BE*CADHCS_5010_834*954468482*20210501*203354*3414006*X*005010X220A1~";
		ediList.add(gs);
		String st = "ST*834*0001*005010X220A1~";
		ediList.add(st);
		String bgn = "BGN*00*DHCS834-" + fileNameDate + "-Care1st-SanDiego-001*" + timestamp + "*20335400****2~";
		ediList.add(bgn);
		reg = bgn.split("\\*")[2].trim();
		String qty = "QTY*TO*5~";
		ediList.add(qty);
		String n1p5 = "N1*P5*California Department of Health Care Services......*FI*680317191~";
		ediList.add(n1p5);
		String n1in = "N1*IN*Care1st Health Plan*FI*954468482~";
		ediList.add(n1in);

		// EDI File generation
		for (String key : inputDataMap.keySet()) {
			// NewEnrollment
			if (inputDataMap.get(key).get("ScenarioType").contains("NewEnrollment")) {
				ediTempList.clear();
				inscount++;

				String ins = "INS*Y*18*001*AI*E***AC~";
				ediList.add(ins);

				cinNum1 = random.nextInt((99999999 - 10000000) + 1) + 10000000;
				cinNum1Al = random.nextInt((90 - 65) + 1) + 65;
				cinNum = String.valueOf(cinNum1) + (char) (cinNum1Al);

				String ref0f = "REF*0F*" + cinNum + "~";
				ediList.add(ref0f);

				ssnID1 = random.nextInt((999999999 - 100000000) + 1) + 100000000;
				ssnID = String.valueOf(ssnID1);

				String ref1l = "REF*1L*" + ssnID + "~";
				ediList.add(ref1l);
				String ref17_1 = "REF*17*" + inputDataMap.get(key).get("MEDS Renewal Date") + ";;"
						+ inputDataMap.get(key).get("MEDS Renewal Date") + ";~";
				ediList.add(ref17_1);
				String ref23 = "REF*23*7;20220201;~";
				ediList.add(ref23);
				String ref3h = "REF*3H*37;" + inputDataMap.get(key).get("AIDCode") + ";H9QNO43881;;~";
				ediList.add(ref3h);
				String ref60 = "REF*6O*;A;Y;;3;~";
				ediList.add(ref60);

				hicnNum1 = random.nextInt((999999999 - 100000000) + 1) + 10000000;
				hicnNum2 = random.nextInt((9 - 0) + 1);
				hicnNum1Al = random.nextInt((90 - 65) + 1) + 65;
				hicnNum = String.valueOf(hicnNum1) + String.valueOf(hicnNum2) + (char) (hicnNum1Al);

				String reff6 = "REF*F6*" + hicnNum + "~";
				ediList.add(reff6);

				medsID1 = random.nextInt((99999999 - 10000000) + 1) + 10000000;
				medsID1Al = random.nextInt((90 - 65) + 1) + 65;
				medsID = String.valueOf(medsID1) + (char) (medsID1Al);

				String refq4 = "REF*Q4*" + medsID + ";~";
				ediList.add(refq4);
				String refzz_1 = "REF*ZZ*167" + inputDataMap.get(key).get("PlanCode01") + ";;;;;167"
						+ inputDataMap.get(key).get("PlanCode02") + ";;;;~";
				ediList.add(refzz_1);
				String refdx = "REF*DX*" + inputDataMap.get(key).get("MedicareContactNumber") + ";"
						+ inputDataMap.get(key).get("PBPID") + ";" + inputDataMap.get(key).get("PBP Start Date")
						+ ";S6946;C023;20220501;~";
				ediList.add(refdx);
				String nm1il = "NM1*IL*1*" + inputDataMap.get(key).get("LastName") + "*"
						+ inputDataMap.get(key).get("FirstName") + "*" + inputDataMap.get(key).get("MiddleInitial")
						+ "~";
				ediList.add(nm1il);
				String perip = "PER*IP**TE*" + inputDataMap.get(key).get("Telephone") + "~";
				ediList.add(perip);
				String n3nm1il = "N3*" + inputDataMap.get(key).get("Address1") + "*"
						+ inputDataMap.get(key).get("Address2") + "~";
				ediList.add(n3nm1il);
				String n4nm1il = "N4*" + inputDataMap.get(key).get("City") + "*CA*"
						+ inputDataMap.get(key).get("ZipCode") + "**CY*37~";
				ediList.add(n4nm1il);
				String dmgd8 = "DMG*D8*" + inputDataMap.get(key).get("DOB") + "*" + inputDataMap.get(key).get("Gender")
						+ "*:RET:2028-2~";
				ediList.add(dmgd8);
				String luild = "LUI*LD*" + inputDataMap.get(key).get("LanguageCode01") + "*"
						+ inputDataMap.get(key).get("LanguageCode02") + "*7~";
				ediList.add(luild);

				String effectiveDate = inputDataMap.get(key).get("EffectiveDate");
				String hd_1 = "HD*021**HLT*167;" + inputDataMap.get(key).get("HCPStatusCode") + "~";
				ediList.add(hd_1);
				String dtp348_1 = "DTP*348*D8*" + effectiveDate + "~";
				ediList.add(dtp348_1);
				String dtp349_1 = "DTP*349*D8*" + getLastDate(effectiveDate) + "~";
				ediList.add(dtp349_1);
				String amtr_1 = "AMT*R*18~";
				ediList.add(amtr_1);
				String ref17_2 = "REF*17*N;;;;;;;;;;;;;1~";
				ediList.add(ref17_2);
				String ref9v_1 = "REF*9V*2;2;1~";
				ediList.add(ref9v_1);
				String refce_1 = "REF*CE*" + inputDataMap.get(key).get("AIDCode") + ";301;;;;;;~";
				ediList.add(refce_1);
				String refrb_1 = "REF*RB*" + inputDataMap.get(key).get("AIDCode") + "~";
				ediList.add(refrb_1);
				String refzx_1 = "REF*ZX*37~";
				ediList.add(refzx_1);
				String refzz_2 = "REF*ZZ*02;" + effectiveDate + ";10~";
				ediList.add(refzz_2);

				String oneMonthAgoDate = getoneMonthAgoDate(effectiveDate);

				// Repetitive segments
				for (int counter = 1; counter <= 12; counter++) {
					String hd_3 = "HD*021**HLT*167;" + inputDataMap.get(key).get("HCPStatusCode") + "~";
					ediList.add(hd_3);
					String dtp348_3 = "DTP*348*D8*" + oneMonthAgoDate + "~";
					ediList.add(dtp348_3);
					String dtp349_2 = "DTP*349*D8*" + getLastDate(oneMonthAgoDate) + "~";
					ediList.add(dtp349_2);
					String amtr_3 = "AMT*R*18~";
					ediList.add(amtr_3);
					String ref17_4 = "REF*17*N;;;;;;;;;;;;;1~";
					ediList.add(ref17_4);
					String ref9v_3 = "REF*9V*2;2;1~";
					ediList.add(ref9v_3);
					String refce_3 = "REF*CE*" + inputDataMap.get(key).get("AIDCode") + ";301;;;;;;~";
					ediList.add(refce_3);
					String refrb_3 = "REF*RB*" + inputDataMap.get(key).get("AIDCode") + "~";
					ediList.add(refrb_3);
					String refzx_3 = "REF*ZX*37~";
					ediList.add(refzx_3);
					String refzz_4 = "REF*ZZ*02;" + oneMonthAgoDate + ";10~";
					ediList.add(refzz_4);
					oneMonthAgoDate = getoneMonthAgoDate(oneMonthAgoDate);
				}

				len = len + 145;
				if (inputDataMap.get(key).get("SFDC").equalsIgnoreCase("Yes")) {
					String sfdc = "\"" + cinNum + "\"|\"" + inputDataMap.get(key).get("DOB") + "\"|\"" + "0015"
							+ "\"|\"" + inputDataMap.get(key).get("MEDS Renewal Date") + "\"|\""
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
				inscount++;

				dbMap.putAll(
						getDataFromQuery(dataMap.get("Subscriber_Details"), inputDataMap.get(key).get("CIN Number")));

				String ins = "INS*Y*18*001*AI*E***AC~";
				ediList.add(ins);

				String ref0f = "REF*0F*" + inputDataMap.get(key).get("CIN Number") + "~";
				ediList.add(ref0f);

				String ref1l = "REF*1L*" + inputDataMap.get(key).get("SSN") + "~";
				ediList.add(ref1l);
				String ref17_1 = "REF*17*" + inputDataMap.get(key).get("MEDS Renewal Date") + ";;"
						+ inputDataMap.get(key).get("MEDS Renewal Date") + ";~";
				ediList.add(ref17_1);
				String ref23 = "REF*23*7;20220201;~";
				ediList.add(ref23);

				Map<String, String> aidMap = new HashMap<>();
				aidMap.putAll(
						getDataFromQuery("select mecd_mctr_aidc as AIDCD from fc_cmc_mecd_medicaid where MEME_CK = ?",
								dbMap.get("MCK")));
				if (inputDataMap.get(key).get("AIDCode").isEmpty()) {
					aidCode = aidMap.get("AIDCD");
				} else {
					aidCode = inputDataMap.get(key).get("AIDCode");
				}

				String ref3h = "REF*3H*37;" + aidCode + ";H9QNO43881;;~";
				ediList.add(ref3h);

				String ref60 = "REF*6O*;A;Y;;3;~";
				ediList.add(ref60);

				hicn = dbMap.get("HICN");

				String reff6 = "REF*F6*" + hicn + "~";
				ediList.add(reff6);

				String refq4 = "REF*Q4*" + inputDataMap.get(key).get("MEDSID") + ";~";
				ediList.add(refq4);
				String refzz_1 = "REF*ZZ*167" + inputDataMap.get(key).get("PlanCode01") + ";;;;;167"
						+ inputDataMap.get(key).get("PlanCode02") + ";;;;~";
				ediList.add(refzz_1);

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
				String refdx = "REF*DX*" + mcn + ";" + pbpID + ";" + pbpSDT + ";S6946;C023;20220501;~";
				ediList.add(refdx);

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

				String nm1il = "NM1*IL*1*" + lastName + "*" + firstName + "*" + midInit + "~";
				ediList.add(nm1il);

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

				String n3nm1il = "N3*" + addr1 + "*" + addr2 + "~";
				ediList.add(n3nm1il);

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

				String n4nm1il = "N4*" + city + "*CA*" + zip + "**CY*37~";
				ediList.add(n4nm1il);

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

				String dmgd8 = "DMG*D8*" + dob + "*" + gender + "*:RET:2028-2~";
				ediList.add(dmgd8);
				String luild = "LUI*LD*" + inputDataMap.get(key).get("LanguageCode01") + "*"
						+ inputDataMap.get(key).get("LanguageCode02") + "*7~";
				ediList.add(luild);

				if (inputDataMap.get(key).get("EffectiveDate").isEmpty()) {
					effDate = dbMap.get("EFFECTIVE_DATE");
				} else {
					effDate = inputDataMap.get(key).get("EffectiveDate");
				}

				String effectiveDate = effDate;
				String hd_1 = "HD*001**HLT*167;" + inputDataMap.get(key).get("HCPStatusCode") + "~";
				ediList.add(hd_1);
				String dtp348_1 = "DTP*348*D8*" + effDate + "~";
				ediList.add(dtp348_1);
				String dtp349_1 = "DTP*349*D8*" + getLastDate(effectiveDate) + "~";
				ediList.add(dtp349_1);
				String amtr_1 = "AMT*R*18~";
				ediList.add(amtr_1);
				String ref17_2 = "REF*17*N;;;;;;;;;;;;;1~";
				ediList.add(ref17_2);
				String ref9v_1 = "REF*9V*2;2;1~";
				ediList.add(ref9v_1);
				String refce_1 = "REF*CE*" + aidCode + ";301;;;;;;~";
				ediList.add(refce_1);
				String refrb_1 = "REF*RB*" + aidCode + "~";
				ediList.add(refrb_1);
				String refzx_1 = "REF*ZX*37~";
				ediList.add(refzx_1);
				String refzz_2 = "REF*ZZ*02;" + effDate + ";10~";
				ediList.add(refzz_2);

				String oneMonthAgoDate = getoneMonthAgoDate(effectiveDate);

				// Repetitive segments
				for (int counter = 1; counter <= 12; counter++) {
					String hd_3 = "HD*001**HLT*167;" + inputDataMap.get(key).get("HCPStatusCode") + "~";
					ediList.add(hd_3);
					String dtp348_3 = "DTP*348*D8*" + oneMonthAgoDate + "~";
					ediList.add(dtp348_3);
					String dtp349_2 = "DTP*349*D8*" + getLastDate(oneMonthAgoDate) + "~";
					ediList.add(dtp349_2);
					String amtr_3 = "AMT*R*18~";
					ediList.add(amtr_3);
					String ref17_4 = "REF*17*N;;;;;;;;;;;;;1~";
					ediList.add(ref17_4);
					String ref9v_3 = "REF*9V*2;2;1~";
					ediList.add(ref9v_3);
					String refce_3 = "REF*CE*" + aidCode + ";301;;;;;;~";
					ediList.add(refce_3);
					String refrb_3 = "REF*RB*" + aidCode + "~";
					ediList.add(refrb_3);
					String refzx_3 = "REF*ZX*37~";
					ediList.add(refzx_3);
					String refzz_4 = "REF*ZZ*02;" + oneMonthAgoDate + ";10~";
					ediList.add(refzz_4);
					oneMonthAgoDate = getoneMonthAgoDate(oneMonthAgoDate);
				}

				len = len + 145;

				if (inputDataMap.get(key).get("SFDC").equalsIgnoreCase("Yes")) {
					String sfdc = "\"" + inputDataMap.get(key).get("CIN Number") + "\"|\"" + dob + "\"|\"" + "0015"
							+ "\"|\"" + inputDataMap.get(key).get("MEDS Renewal Date") + "\"|\""
							+ inputDataMap.get(key).get("SalesForceProviderID") + "\"";
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
				inscount++;

				dbMap.putAll(
						getDataFromQuery(dataMap.get("Subscriber_Details"), inputDataMap.get(key).get("CIN Number")));

				String ins = "INS*Y*18*024*AI*E***TE~";
				ediList.add(ins);

				String ref0f = "REF*0F*" + inputDataMap.get(key).get("CIN Number") + "~";
				ediList.add(ref0f);

				String ref1l = "REF*1L*" + inputDataMap.get(key).get("SSN") + "~";
				ediList.add(ref1l);
				String ref17_1 = "REF*17*" + inputDataMap.get(key).get("MEDS Renewal Date") + ";;"
						+ inputDataMap.get(key).get("MEDS Renewal Date") + ";~";
				ediList.add(ref17_1);
				String ref23 = "REF*23*7;20220201;~";
				ediList.add(ref23);

				Map<String, String> aidMap = new HashMap<>();
				aidMap.putAll(
						getDataFromQuery("select mecd_mctr_aidc as AIDCD from fc_cmc_mecd_medicaid where MEME_CK = ?",
								dbMap.get("MCK")));

				aidCode = aidMap.get("AIDCD");

				String ref3h = "REF*3H*37;" + aidCode + ";H9QNO43881;;~";
				ediList.add(ref3h);

				String ref60 = "REF*6O*;A;Y;;3;~";
				ediList.add(ref60);

				hicn = dbMap.get("HICN");

				String reff6 = "REF*F6*" + hicn + "~";
				ediList.add(reff6);

				String refq4 = "REF*Q4*" + inputDataMap.get(key).get("MEDSID") + ";~";
				ediList.add(refq4);
				String refzz_1 = "REF*ZZ*167" + inputDataMap.get(key).get("PlanCode01") + ";;;;;167"
						+ inputDataMap.get(key).get("PlanCode02") + ";;;;~";
				ediList.add(refzz_1);

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
				String refdx = "REF*DX*" + mcn + ";" + pbpID + ";" + pbpSDT + ";S6946;C023;20220501;~";
				ediList.add(refdx);

				firstName = dbMap.get("FIRST_NAME");
				lastName = dbMap.get("LAST_NAME");
				midInit = dbMap.get("MIDDLE_INITIAL");

				String nm1il = "NM1*IL*1*" + lastName + "*" + firstName + "*" + midInit + "~";
				ediList.add(nm1il);

				telephone = dbMap.get("TELEPHONE");

				String perip = "PER*IP**TE*" + telephone + "~";
				ediList.add(perip);

				addr1 = dbMap.get("ADDRESS1");
				addr2 = dbMap.get("ADDRESS2");

				String n3nm1il = "N3*" + addr1 + "*" + addr2 + "~";
				ediList.add(n3nm1il);

				city = dbMap.get("CITY");
				zip = dbMap.get("ZIP");

				String n4nm1il = "N4*" + city + "*CA*" + zip + "**CY*37~";
				ediList.add(n4nm1il);

				dob = dbMap.get("DOB");
				gender = dbMap.get("GENDER");

				String dmgd8 = "DMG*D8*" + dob + "*" + gender + "*:RET:2028-2~";
				ediList.add(dmgd8);
				String luild = "LUI*LD*" + inputDataMap.get(key).get("LanguageCode01") + "*"
						+ inputDataMap.get(key).get("LanguageCode02") + "*7~";
				ediList.add(luild);

				effDate = dbMap.get("EFFECTIVE_DATE");

				String effectiveDate = effDate;
				String hd_1 = "HD*024**HLT*167;00~";
				ediList.add(hd_1);
				String dtp348_1 = "DTP*348*D8*" + effDate + "~";
				ediList.add(dtp348_1);
				String dtp349_1 = "DTP*349*D8*" + getLastDate(effectiveDate) + "~";
				ediList.add(dtp349_1);
				String amtr_1 = "AMT*R*18~";
				ediList.add(amtr_1);
				String ref17_2 = "REF*17*N;;;;;;;;;;;;;1~";
				ediList.add(ref17_2);
				String ref9v_1 = "REF*9V*2;2;1~";
				ediList.add(ref9v_1);
				String refce_1 = "REF*CE*" + aidCode + ";301;;;;;;~";
				ediList.add(refce_1);
				String refrb_1 = "REF*RB*" + aidCode + "~";
				ediList.add(refrb_1);
				String refzx_1 = "REF*ZX*37~";
				ediList.add(refzx_1);
				String refzz_2 = "REF*ZZ*02;" + effDate + ";10~";
				ediList.add(refzz_2);

				String oneMonthAgoDate = getoneMonthAgoDate(effectiveDate);

				// Repetitive segments
				for (int counter = 1; counter <= 12; counter++) {
					String hd_3 = "HD*024**HLT*167;01~";
					ediList.add(hd_3);
					String dtp348_3 = "DTP*348*D8*" + oneMonthAgoDate + "~";
					ediList.add(dtp348_3);
					String dtp349_2 = "DTP*349*D8*" + getLastDate(oneMonthAgoDate) + "~";
					ediList.add(dtp349_2);
					String amtr_3 = "AMT*R*18~";
					ediList.add(amtr_3);
					String ref17_4 = "REF*17*N;;;;;;;;;;;;;1~";
					ediList.add(ref17_4);
					String ref9v_3 = "REF*9V*2;2;1~";
					ediList.add(ref9v_3);
					String refce_3 = "REF*CE*" + aidCode + ";301;;;;;;~";
					ediList.add(refce_3);
					String refrb_3 = "REF*RB*" + aidCode + "~";
					ediList.add(refrb_3);
					String refzx_3 = "REF*ZX*37~";
					ediList.add(refzx_3);
					String refzz_4 = "REF*ZZ*02;" + oneMonthAgoDate + ";10~";
					ediList.add(refzz_4);
					oneMonthAgoDate = getoneMonthAgoDate(oneMonthAgoDate);
				}

				len = len + 145;

				if (inputDataMap.get(key).get("SFDC").equalsIgnoreCase("Yes")) {
					String sfdc = "\"" + inputDataMap.get(key).get("CIN Number") + "\"|\"" + dob + "\"|\"" + "0015"
							+ "\"|\"" + inputDataMap.get(key).get("MEDS Renewal Date") + "\"|\""
							+ inputDataMap.get(key).get("SalesForceProviderID") + "\"";
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
		len = len + 6;
		String linecount = String.valueOf(len);
		String se = "SE*" + linecount + "*0001~";
		ediList.add(se);
		String ge = "GE*" + inscount + "*3414006~";
		ediList.add(ge);
		String iea = "IEA*1*" + controlNum + "~";
		ediList.add(iea);

		writeToEDI(ediList, reg);
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

	// To get last date of the month
	public static String getLastDate(String givenDate) {
		DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		try {
			Date date = dateFormat.parse(givenDate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.MONTH, 1);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			calendar.add(Calendar.DATE, -2);
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
	 * @param claimId
	 */

	private static Map<String, String> getDataFromQuery(String query, Object... claimId) {
		Map<String, String> finalDbMap = new HashMap<String, String>();
		try {

			finalDbMap = new DBUtils().getDataFromPreparedQuery("facets", query, claimId);
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
