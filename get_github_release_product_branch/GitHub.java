package com.github;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class GitHub {

	public static String PROD = "AA_Prod,ACA_Prod,AGIS_Prod,AMO_Prod,AdhocReport_Prod";
	public static String LOCAL_REPO = "G:\\RailsFTW423216\\GitHub-Code";

	public static String BRANCH_BAT = System.getProperty("user.dir") + "\\branch.bat";
	public static String BRANCH_OUTPUT_FOLDER = System.getProperty("user.dir") + "\\output";

	public static String GITHUB_URL = "https://github.com/Accela-Inc/AA_Prod/tree/";

	public static void main(String[] args) {
		

		GitHub git = new GitHub();

		String preVersion = args[0];
		String currentVersion = args[1];

		if (currentVersion.startsWith("7.2")) {
			LOCAL_REPO = LOCAL_REPO + "\\7.2.x\\";
		}
		if (currentVersion.startsWith("7.3")) {
			LOCAL_REPO = LOCAL_REPO + "\\7.3.x\\";
		}
		if (currentVersion.startsWith("8")) {
			LOCAL_REPO = LOCAL_REPO + "\\8.0.0\\";
		}
		if (currentVersion.startsWith("9")) {
			LOCAL_REPO = LOCAL_REPO + "\\9.0.0\\";
		}

		git.writeBranchBat();

		Runtime run = Runtime.getRuntime();
		try {
			run.exec("cmd /k " + BRANCH_BAT);
			Thread.sleep(5000);
		} catch (Exception e) {
			System.out.println("read file error!");
			e.printStackTrace();
		}

		
		HashMap<String, ArrayList<String>> branchVersion = git
				.getVersionBranchMap(preVersion, currentVersion);
		if(branchVersion == null){
			System.out
			.println("parse previous version error, please check the previous branch name for AA Product!");
		}
		if(branchVersion.size() > 0){
			git.sendEmail(currentVersion,branchVersion);
		}
		
		
	
	}

	public void writeBranchBat() {

		FileWriter writer = null;

		String[] prods = PROD.split(",");

		try {

			File file = new File(BRANCH_BAT);
			if (!file.exists()) {
				file.createNewFile();
			}
			
			File folder = new File(BRANCH_OUTPUT_FOLDER);
			if (!folder.exists() && !folder.isDirectory())        
			{           
			    folder.mkdir();      
			} 
			
			writer = new FileWriter(BRANCH_BAT, false);

			writer.write("@echo off\n");
			for (int i = 0; i < prods.length; i++) {
				String prod = prods[i];
				String prodBranchFile = BRANCH_OUTPUT_FOLDER + "\\" + prod
						+ ".txt";
				writer.write("cd/d " + LOCAL_REPO + prod + "\n");
				writer.write("git branch > " + prodBranchFile + " \n");
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public String getBranchName(String version) {

		int i = 0;
		String branchName = "false";
		Runtime run = Runtime.getRuntime();
		String branchExist = "cmd /c cd/d " + LOCAL_REPO
				+ "\\AA_Prod &&  git branch | grep " + version;
		
		//String branchExist = "cmd /c cd/d D:\\AA8.0.0\\main-dev &&  git branch | grep " + version;

		try {
			Process process = run.exec(branchExist);

			InputStream input = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input));
			String szline = null;
			while ((szline = reader.readLine()) != null) {
				if((szline.substring(1).trim()).startsWith("AA_") && version.equals(szline.split("_")[1]) && (szline.endsWith("_HF") || szline.endsWith("_SP") || szline.endsWith("_FP"))){
					i++;
					branchName = szline.substring(1).trim();
				}
				
				// System.out.println("d"+szline);
				
			}
			 //System.out.println(i+branchName);
			if (i > 1) {
				return "false";
			} else {
				return branchName;
			}

		} catch (Exception e) {
			System.out.println("read file error!");
			e.printStackTrace();
		}
		return "false";

	}

	public HashMap<String, ArrayList<String>> getVersionBranchMap(
			String preVersion, String currentVersion) {
		HashMap<String, ArrayList<String>> branchVersion = new HashMap<String, ArrayList<String>>();

		String[] prods = PROD.split(",");
		for (int i = 0; i < prods.length; i++) {
			String prod = prods[i];
			String preProd = prod.split("_")[0] + "_";
			if(prod.equals("AdhocReport_Prod")){
				preProd = "ADHOC";
			}
			
			String prodBranchFile = BRANCH_OUTPUT_FOLDER + "\\" + prod + ".txt";
			try {

				File file = new File(prodBranchFile);
				if (file.isFile() && file.exists()) {
					InputStreamReader read = new InputStreamReader(
							new FileInputStream(file));
					BufferedReader bufferedReader = new BufferedReader(read);
					String lineTxt = null;
					while ((lineTxt = bufferedReader.readLine()) != null) {

						if ((lineTxt.substring(1).trim()).startsWith(preProd)
								&& currentVersion.equals(lineTxt.split("_")[1])
								&& (lineTxt.endsWith("_HF")
										|| lineTxt.endsWith("_SP") || lineTxt
											.endsWith("_FP"))) {

							ArrayList<String> branchList = new ArrayList<String>();
							branchList.add(GITHUB_URL
									+ lineTxt.substring(1).trim());
							branchVersion.put(prod, branchList);
							if (prod.equals("AA_Prod")) {
								// db&language
								String preVersionBranch = new GitHub().getBranchName(preVersion);

								if (preVersionBranch.equals("false")) {
									
									return null;
								}
								
								
								ArrayList<String> dbLa = DBandLanguage(
										preVersionBranch, lineTxt.substring(1)
												.trim());

								if(dbLa.contains("db")){
									branchVersion.put("DBUpdate",
											branchList);
								}
								if(dbLa.contains("la")){
									branchVersion.put("LanguagePacks",
											branchList);
								}
								
							}
						}
					}

					read.close();
				} else {
					System.out.println("can not find specific file!");
				}
			} catch (Exception e) {
				System.out.println("read file error!");
				e.printStackTrace();
			}

		}

		// System.out.println(branchVersion);
		return branchVersion;
	}

	public void sendEmail(String version,HashMap<String, ArrayList<String>> branchVersion) {

		String smtp = "smtp.exmail.qq.com";
		String from = "auto_sender@missionsky.com";
		// String to = "abc5206qq.com@qq.com;850389791@qq.com";
		// String copyto = "xyy_xs@163.com";
		String subject = "Relase Branch Detail - "+version;
		String username = "auto_sender@missionsky.com";
		String password = "Ms123456!";

		/*
		 * String tos = args[4]; String ccs = args[5];
		 */
		String tos = "peter.peng@missionsky.com,louis.he@missionsky.com";
		String ccs = "accela-support-team@missionsky.com";
		String[] toList = tos.split(",");
		String[] copyList = ccs.split(",");

		StringBuilder sb = new StringBuilder();

		Iterator<Entry<String, ArrayList<String>>> iter = branchVersion
				.entrySet().iterator();
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			Object prod = entry.getKey();
			Object branchName = entry.getValue();
			sb.append(prod + ":" + branchName);
			sb.append("<br>");
		}

		String content = sb.toString();
		//System.out.println(content);

		
		Mail.sendAndCc(smtp, from, toList, copyList, subject, content,
		  username, password);
		

	}

	public ArrayList<String> DBandLanguage(String perBranch,
			String currentBranch) {//

		ArrayList<String> dbLa = new ArrayList<String>();

		Runtime run = Runtime.getRuntime();
		String dbChanges = "cmd /c cd/d " + LOCAL_REPO
				+ "\\AA_Prod &&  git log --name-status " + perBranch + ".."
				+ currentBranch + " -- sql";
		String laChanges = "cmd /c cd/d " + LOCAL_REPO
				+ "\\AA_Prod &&  git log --name-status " + perBranch + ".."
				+ currentBranch + " -- sql/lang";

		// String dbChanges =
		// "cmd /c cd/d D:/AA8.0.0/AA_Prod &&  git log --name-status AA_8.0.2.0.4_HF..AA_8.0.2.0.5_HF -- sql";
		// String laChanges =
		// "cmd /c cd/d D:/AA8.0.0/AA_Prod &&  git log --name-status AA_8.0.2.0.4_HF..AA_8.0.2.0.5_HF -- sql/lang";
		try {
			Process process = run.exec(dbChanges);
			// Thread.sleep(5000);

			InputStream input = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					input));
			String szline = null;
			while ((szline = reader.readLine()) != null) {
				// System.out.println("d"+szline);
				dbLa.add("db");
				break;
			}

			process = run.exec(laChanges);
			input = process.getInputStream();
			reader = new BufferedReader(new InputStreamReader(input));
			String szline1 = null;
			while ((szline1 = reader.readLine()) != null) {

				dbLa.add("la");
				break;

			}

		} catch (Exception e) {
			System.out.println("read file error!");
			e.printStackTrace();
		}
		// System.out.println(dbLaFlags);
		return dbLa;

	}

}
