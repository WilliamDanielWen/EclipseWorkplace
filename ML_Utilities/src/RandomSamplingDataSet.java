import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;

import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class RandomSamplingDataSet {
	public static void main(String[] args) throws IOException{
		
		
		String inputSetPath="preprocessedV13//train_preProcessed-V13.csv";
		String samplingSetPath="preprocessedV13//supervisedSet-pv13.csv";
		double samplingProportion=0.15;
				
		//read the whole input set
		List<String> inputDataset = new ArrayList<String>();
		File inputFile=new File(inputSetPath);
		Scanner scanner = new Scanner(inputFile,"ISO-8859-1");
		String header=scanner.nextLine();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("@")||line.isEmpty()) {
				continue;
			}
			inputDataset.add(line);
		}
		scanner.close();

		int inputSize=inputDataset.size();
		int samplingSize=(int) ( ((double)inputSize) * samplingProportion);

		Random rand=new Random(System.currentTimeMillis());
		Collections.shuffle(inputDataset, rand);



		// get the samplingSet
		List<String> samplingSet = new ArrayList<String>();
		for (int i = 0; i < samplingSize; i++) {
			samplingSet.add(inputDataset.get(i));
		}
		//sort according to "id", in an ascending order
		samplingSet.sort(new CSVStringComparator());


		// write the sub training set
		FileOutputStream fstream=new FileOutputStream(samplingSetPath);
		OutputStreamWriter out_ISO_8859_1=new OutputStreamWriter(fstream,"ISO-8859-1");
		BufferedWriter out=new BufferedWriter(out_ISO_8859_1);
		out.write(header);
		out.newLine();
		for (int i = 0; i < samplingSet.size(); i++) {
			out.write(samplingSet.get(i));
			out.newLine();
		}
		out.close();


	}


}


