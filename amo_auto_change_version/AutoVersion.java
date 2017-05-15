import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

public class AutoVersion {
	private static HashSet<String> fileList = new HashSet<String>();

	public static String read(File src) {
		StringBuffer res = new StringBuffer();
		String line = null;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(src));
			while ((line = reader.readLine()) != null) {
				res.append(line + "\n");
			}
			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return res.toString();
	}

	public static boolean write(String cont, File dist) {
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(dist));
			writer.write(cont);
			writer.flush();
			writer.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void replace(String path, String oldContent,String newContent) {
		//File src = new File("D:/GitHub-Code-Base/AMO_Prod/AMOClient/User Interfaces/Accela.AMO.Client/App.config");
		//System.out.println(path);
		File src = new File(path);

		String cont = AutoVersion.read(src);
		//System.out.println(cont);
		// 
		cont = cont.replaceAll(oldContent, newContent);
		//System.out.println(cont);
		// 
		AutoVersion.write(cont, src);
		//System.out.println(AutoVersion.write(cont, src));
	}
	
	public static void getPath(String path, String content,String fileName) {
		HashSet<String> fileListx = new HashSet<String>();
		File file = new File(path);
		File[] files = file.listFiles();
		for(int x=0; x<files.length; x++)
		{
				if(files[x].isDirectory())
				getPath( files[x].getAbsolutePath() ,content,fileName);
			else
			{
				
				//
				//if( files[x].getName().endsWith( fileType ))
				if( files[x].getName().equals( fileName ))
				{
					//
					 
					fileListx = new AutoVersion().FindPath(files[x].getAbsolutePath(), content);
					
					//FindTxt( files[x],content);
				}
			}
		}
		//System.out.println(fileListx);
		
	}

	public static HashSet<String> FindPath(String path,String content ) 
	{
		//ArrayList<String> fileList = new ArrayList<String>();
	       try {   
	           BufferedReader br = new BufferedReader(new FileReader(path));  
	           
	           String rowtxt = null;
	        
	           while ((rowtxt = br.readLine()) != null) { 
	        	   
	        	   if(rowtxt.contains(content)){
	        		  // System.out.println(rowtxt +"ss");  
	        		   fileList.add(path);
	        		   //System.out.println(fileList);     
	        		       	
	         	  }
	        	   //System.out.println(fileList);        
	           }  
	           	//return fileList;        	 
	       }   
	       catch (Exception e) {   
	            
	           e.printStackTrace();   
	  
	       }
	      // System.out.println(fileList);  
		return fileList;   
	   		
	}

	 /** 
	    *  
	    * @param filePath 
	    * @param oldContent  String 
	    * @param newContent  String 
	    */ 
	public static void main(String[] args) {
		
		AutoVersion av = new AutoVersion();
	    Scanner sc = new Scanner(System.in);

		System.out.println("please input source code path:");
		String path = sc.nextLine();
		
		System.out.println("please input display version number:");
		String displayVersion = sc.nextLine();
		
		System.out.println("please input update version number:");
		String updateVersion = sc.nextLine();
		
	    //replace *.config
		//D:/GitHub-Code-Base/AMO_Prod/AMOClient/User Interfaces/Accela.AMO.Client/App.config
	  //  av.replace(path, "", "");
		String path1 = path + "\\AMOClient\\User Interfaces\\Accela.AMO.Client\\App.config";
	    //System.out.println(x);
		String path2 = path + "\\AMOServer\\WebServices\\Web.config";
		String path3 = path + "\\AMOServer\\WebServices\\client\\AMODLLs\\PC\\x32\\DLLs\\Accela.AMO.Client.exe.config";
		
		
		SimpleDateFormat df = new SimpleDateFormat("yyMMdd");// 151014
		String date = df.format(new Date());
		//System.out.println(date);
		SimpleDateFormat df1 = new SimpleDateFormat("MM/dd/yyyy");//10/14/2015
		String date1 = df1.format(new Date());
		//System.out.println(date1);		
		//av.replace(path1, "151009", date);
	    //8.0
		String version0 = updateVersion.substring(0,1);//8
		String version1 = updateVersion.substring(0,3);//7.3
		//String version2 = version.substring(2,3);//8.0.0
		//System.out.println(version0 +" "+version1 + " " + version2);
	    if(version0.equals("8") || version1.equals("7.3")){
	    	String path0 = path +"\\AMOServer\\WebServices\\Web.config.template";
	    	av.replace(path0, "7.3.0.2033584", displayVersion+"."+date);//8.0.0.0.0
	    	av.replace(path0, "11/08/2010", date1);
	    	
	    	
	    	if(version0.equals("8")){
	    		av.replace(path1, "8.0.0.0.0.151009", displayVersion+"."+date);
		    	av.replace(path2, "8.0.0.0.0.151009", displayVersion+"."+date);
		    	av.replace(path2, "10/09/2015", date1);
	    	}
	    	
	    	else{//version = "7.3.x"
	    		av.replace(path1,"7.3.3.7.0",displayVersion+"."+date);
		    	 av.replace(path2,"7.3.3.7.0",displayVersion+"."+date);
		    	 av.replace(path2, "11/08/2010", date1);
	    	}
	    	   	
	    }
	    	  
	    if(version1.equals("7.2")){//version = "7.2.x"
	    	
	    	 av.replace(path1,"7.2.6.15.151009",displayVersion+"."+date);
	    	 av.replace(path2,"7.2.6.15.151009",displayVersion+"."+date);	    	   	 
	    }
	    
	    av.replace(path3, "2010", "2015");
	    
	    
	    //replace AssemblyInfo.cs
		//
		String versions[] = {"7.2.1.00001",	"7.1.0.1","7.1.0.50008","7.1.0.42","7.0.2.*","7.0.5.30825","7.05.1.0","7.05.2.0","7.3.2.19"};
		
	  //  String versions[] = {"7.2.1.00001"};
		for(int i = 0;i < versions.length;i ++){
			av.getPath(path, versions[i], "AssemblyInfo.cs");
		
			//System.out.println(fileList);
			Iterator<String> iterator=fileList.iterator();
			String tmp = null;
			while(iterator.hasNext()){
				//System.out.println(iterator.next());
				//x.add(iterator.next());
			    // tmp = iterator.next();
			    // System.out.println(iterator.next());
			     
				av.replace(iterator.next(), versions[i],updateVersion);
			}
			
		}
		
		System.out.println("Done!");
		
	
	}

}