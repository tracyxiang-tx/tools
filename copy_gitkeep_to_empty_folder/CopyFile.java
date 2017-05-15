
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import org.apache.commons.io.FileUtils;

public class CopyFile {
	
	
	static ArrayList<String> SVNPaths = new ArrayList<String>();
	public static String GTIKEEP_FOLDER = System.getProperty("user.dir") + "\\git";
	
	public static void main(String[] args){

			CopyFile copyFile  = new CopyFile();
		
			// SVNPaths = new ArrayList<String>();
			//7.0.0
			
			Scanner sc = new Scanner(System.in);

			System.out.println("please input SVNpath:");

			String branch = sc.nextLine();
					
			//String BranchPath = "F:\\SVN-Code-Base\\Aruba";
			//System.out.println("ss");
		
			ArrayList<String> SVNPath = copyFile.getSVNFolderPath(branch);
			//System.out.println(SVNPath);
			/*if(SVNPath.size() != 0){
				copyFile.writeFirst(branch);
			}*/
			for(int i=0; i<SVNPath.size(); i++){
				
				String path = SVNPath.get(i);
				
				//System.out.println(path);
				copyFile.copyFile(path);
				
			}
			
		     
		 System.out.println("success!");
	
		
	}
	
	public ArrayList<String> getSVNFolderPath(String path){
		
		
		File file = new File(path);
		
		File[] folders = file.listFiles(new FileFilter(){

			public boolean accept(File fl) {
				return fl.isDirectory();
			}
			
		});
			
	//	System.out.println(folders.length);
		
		for(int i=0;i<folders.length;i++){
			//System.out.println(folders[i].listFiles().length);
			File[] folder = folders[i].listFiles();
			if(folder.length > 0){
				getSVNFolderPath(folders[i].getAbsolutePath());
				//System.out.println(folders[i].getAbsolutePath());
			}
			else{
				//String SVNPath = folders[i]+"";
				SVNPaths.add(folders[i]+"");
				//return SVNPath;
				//if()
				
				//copyFile(folders[i]);
			}
		} 
		//System.out.println(SVNPath);
		
		return SVNPaths;
		
	}
	
	
	public  void copyFile(String emptyFolderPath){
		File from = new File(GTIKEEP_FOLDER);
        File to = new File(emptyFolderPath);

        try {
            FileUtils.copyDirectory(from, to);
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	
	
}
