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

public class DivideDataSet_ISO_8859_1 {
	public static void main(String[] args) throws IOException{
		
		
		String datasetPath="preprocessedV13//supervisedSet-pv13.csv";
		String subTrainSetPath="preprocessedV13//subTrainSet-pv13.csv";
		String developmentSetPath="preprocessedV13//developmentSet-pv13.csv";
		double developmentSetProportion=0.2;
		
		//read the whole training set
		List<String> supervisedDataset = new ArrayList<String>();
		File inputFile=new File(datasetPath);
		Scanner scanner = new Scanner(inputFile,"ISO-8859-1");
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
		int subTrainSetSize=(int)((double)trainSetSize*(1-developmentSetProportion));

		Random rand=new Random(System.currentTimeMillis());
		Collections.shuffle(supervisedDataset, rand);



		// get the subTrainSet
		List<String> subTrainSet = new ArrayList<String>();
		for (int i = 0; i < subTrainSetSize; i++) {
			subTrainSet.add(supervisedDataset.get(i));
		}
		//sort according to "id", in an ascending order
		subTrainSet.sort(new CSVStringComparator());


		// write the sub training set
		FileOutputStream fstream=new FileOutputStream(subTrainSetPath);
		OutputStreamWriter out_ISO_8859_1=new OutputStreamWriter(fstream,"ISO-8859-1");
		BufferedWriter out=new BufferedWriter(out_ISO_8859_1);
		out.write(header);
		out.newLine();
		for (int i = 0; i < subTrainSet.size(); i++) {
			out.write(subTrainSet.get(i));
			out.newLine();
		}
		out.close();


		// get the development set
		List<String> developmentSet = new ArrayList<String>();
		for (int i = subTrainSetSize; i < trainSetSize; i++) {
			developmentSet.add(supervisedDataset.get(i));
		}
		//sort according to "id", in an ascending order
		developmentSet.sort(new CSVStringComparator());


		// write the development set
		fstream=new FileOutputStream(developmentSetPath);
		out_ISO_8859_1=new OutputStreamWriter(fstream,"ISO-8859-1");
		out=new BufferedWriter(out_ISO_8859_1);
		out.write(header);
		out.newLine();
		for (int i = 0; i < developmentSet.size(); i++) {

			out.write(developmentSet.get(i));
			out.newLine();
		}
		out.close();



	}


}


