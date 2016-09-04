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
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class DivideDataSet {
	public static void main(String[] args) throws IOException{
		
		
		String datasetPath="ap89_data//data_set.csv";
		String largeSubSetPath="ap89_data//train.csv";
		String smallSubSetPath="ap89_data//test.csv";
		double smallSubSetProportion=0.2;
		
		//read the whole training set
		List<String> supervisedDataset = new ArrayList<String>();
		File inputFile=new File(datasetPath);
		Scanner scanner = new Scanner(inputFile);
		String header=scanner.nextLine();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("@")||line.isEmpty()) {
				continue;
			}
			supervisedDataset.add(line);
		}
		scanner.close();

		int trainSetSize=supervisedDataset.size();
		int largeSubSetSize=(int)((double)trainSetSize*(1-smallSubSetProportion));

		Random rand=new Random(System.currentTimeMillis());
		Collections.shuffle(supervisedDataset, rand);



		// get the largeSubSet
		List<String> largeSubSet = new ArrayList<String>();
		for (int i = 0; i < largeSubSetSize; i++) {
			largeSubSet.add(supervisedDataset.get(i));
		}



		// write the sub training set
		FileOutputStream fstream=new FileOutputStream(largeSubSetPath);
		OutputStreamWriter out_stream=new OutputStreamWriter(fstream);
		BufferedWriter out=new BufferedWriter(out_stream);
		out.write(header);
		out.newLine();
		for (int i = 0; i < largeSubSet.size(); i++) {
			out.write(largeSubSet.get(i));
			out.newLine();
		}
		out.close();


		// get the development set
		List<String> smallSubSet = new ArrayList<String>();
		for (int i = largeSubSetSize; i < trainSetSize; i++) {
			smallSubSet.add(supervisedDataset.get(i));
		}



		// write the small subset
		fstream=new FileOutputStream(smallSubSetPath);
		out_stream=new OutputStreamWriter(fstream);
		out=new BufferedWriter(out_stream);
		out.write(header);
		out.newLine();
		for (int i = 0; i < smallSubSet.size(); i++) {

			out.write(smallSubSet.get(i));
			out.newLine();
		}
		out.close();



	}


}


