import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

class Business{
	public String businessID;
	public String businessLabel;
}

class Photo{
	public String photoID;
	public String businessID;
	public Photo(String pID,String bID){
		photoID=pID;
		businessID=bID;
	}
}

public class RandomSamplingYelp {


	public static void main(String[] args) throws IOException{
		//firstSampling();
		//secondSampling();
	}

	public static void firstSampling() throws IOException{
		String original_File_Path="data//train.csv";
		String photoToBusi_Database_FilePath="data//train_photo_to_biz_ids.csv";
		List<Photo> photoToBusi_Database=getPhotoListFromCSV(photoToBusi_Database_FilePath);
		
		String first_sampling_FilePath="data//first_sampled_data.csv";
		String first_remaining_FilePath="data//first_remaining_data.csv";
		int firstSamplingSize=300;
		samplingCSV(original_File_Path,first_sampling_FilePath,first_remaining_FilePath,firstSamplingSize);
		
		
		List<String> businessIDlst_firstSampling=getBusinessIDListFromCSV(first_sampling_FilePath);
		List<Photo> photoList_firstSampling=busiIDLstToPhotoLst(photoToBusi_Database,businessIDlst_firstSampling);
		
		String photoTobiz_firstSampling_Path="data//first_sampled_data_photo_to_biz_ids.csv";
		writePhotoListToCSV(photoList_firstSampling,photoTobiz_firstSampling_Path);
	}
	
	public static void secondSampling() throws IOException{
		// use what remianing after first sampling
		String original_File_Path="data//first_remaining_data.csv";
		String photoToBusi_Database_FilePath="data//train_photo_to_biz_ids.csv";
		List<Photo> photoToBusi_Database=getPhotoListFromCSV(photoToBusi_Database_FilePath);
		
		String second_sampling_FilePath="data//second_sampled_data.csv";
		String second_remaining_FilePath="data//second_remaining_data.csv";
		int secondSamplingSize=200;
		samplingCSV(original_File_Path,second_sampling_FilePath,second_remaining_FilePath,secondSamplingSize);
		
		
		List<String> businessIDlst_secondSampling=getBusinessIDListFromCSV(second_sampling_FilePath);
		List<Photo> photoList_secondSampling=busiIDLstToPhotoLst(photoToBusi_Database,businessIDlst_secondSampling);
		
		String photoTobiz_secondSampling_Path="data//second_sampled_data_photo_to_biz_ids.csv";
		writePhotoListToCSV(photoList_secondSampling,photoTobiz_secondSampling_Path);
	}
	

	
	public static List<Photo> busiIDLstToPhotoLst(List<Photo>  photoToBusiDatabase, List<String> businessIDList){
		List<Photo> photoList=new ArrayList<Photo>();
		for(Photo p: photoToBusiDatabase){
			if (businessIDList.contains(p.businessID))  photoList.add(p);
		}
		
		return photoList;
	}
	
	public static List<String> getBusinessIDListFromCSV(String businessLabelFilePath) throws FileNotFoundException{
		List<String> businessIDList =new ArrayList<String>();
		File inputFile=new File(businessLabelFilePath);
		Scanner scanner = new Scanner(inputFile);
		String header=scanner.nextLine();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("@")||line.isEmpty()) {
				continue;
			}
			String businessID=line.split(",")[0];
			businessIDList.add(businessID);
		}
		scanner.close();
		return businessIDList;
	}


	public static List<Photo> getPhotoListFromCSV(String photoListFilePath) throws IOException{
		List<Photo> photoList = new ArrayList<Photo>();
		File inputFile=new File(photoListFilePath);
		Scanner scanner = new Scanner(inputFile);
		String header=scanner.nextLine();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("@")||line.isEmpty()) {
				continue;
			}
			String photoID=line.split(",")[0];
			String businessID=line.split(",")[1];
			Photo p=new Photo(photoID,businessID);
			photoList.add(p);
		}
		scanner.close();

		return photoList;
	}

	public static List<String> photoLstToBusiIDLst(List<Photo> photoList){
		List<String> businessList=new ArrayList<String>();
		for(int i=0;i<photoList.size();i++){
			String bID=photoList.get(i).businessID;
			if(!businessList.contains(bID)) businessList.add(bID);
		}
		return businessList;
	}


	// WE assume sampledDataSize is less than the data in the originDataPath
	public static void samplingCSV(String originDataPath,String sampledDataPath,String remainingDataPath, int sampledDataSize
			) throws IOException{
		//read the whole original set
		List<String> originDataSet = new ArrayList<String>();
		

		
		File inputFile=new File(originDataPath);
		Scanner scanner = new Scanner(inputFile);
		String header=scanner.nextLine();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("@")||line.isEmpty()) {
				continue;
			}
			originDataSet.add(line);
		}
		scanner.close();

		int originDataSize=originDataSet.size();
		//int sampledDataSize=(int)((double)originDataSize*sampleProportion);
		
		Random rand=new Random(System.currentTimeMillis());
		Collections.shuffle(originDataSet, rand);



		// get the sampled data set
		List<String> sampledDataSet = new ArrayList<String>();
		for (int i = 0; i < sampledDataSize; i++) {
			sampledDataSet.add(originDataSet.get(i));
		}
		// write the sampled set
		FileOutputStream fstream=new FileOutputStream(sampledDataPath);
		OutputStreamWriter outWriter=new OutputStreamWriter(fstream);
		BufferedWriter out=new BufferedWriter(outWriter);
		out.write(header);
		out.newLine();
		for (int i = 0; i < sampledDataSet.size(); i++) {
			out.write(sampledDataSet.get(i));
			out.newLine();
		}
		out.close();

		

		// get the remaining data set
		List<String> remianedDataSet = new ArrayList<String>();
		for (int i = sampledDataSize; i < originDataSize; i++) {
			remianedDataSet.add(originDataSet.get(i));
		}
		// write the remaining data set
		FileOutputStream fstreamRemaining=new FileOutputStream(remainingDataPath);
		outWriter=new OutputStreamWriter(fstreamRemaining);
		out=new BufferedWriter(outWriter);
		out.write(header);
		out.newLine();
		for (int i = 0; i < remianedDataSet.size(); i++) {

			out.write(remianedDataSet.get(i));
			out.newLine();
		}
		out.close();

	}
	
	public static void writePhotoListToCSV(List<Photo> photoList, String outputCSVpath) throws IOException{
		FileOutputStream fstreamDevelopment=new FileOutputStream(outputCSVpath);
		OutputStreamWriter outWriter=new OutputStreamWriter(fstreamDevelopment);
		BufferedWriter out=new BufferedWriter(outWriter);
		String header="photo_id,business_id";
		out.write(header);
		out.newLine();
		for (int i = 0; i < photoList.size(); i++) {

			out.write(photoList.get(i).photoID);
			out.write(",");
			out.write(photoList.get(i).businessID);
			out.newLine();
		}
		out.close();
	}


}

