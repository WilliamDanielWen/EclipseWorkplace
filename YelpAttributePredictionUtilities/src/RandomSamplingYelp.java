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

public class RandomSamplingYelp {
	public static void main(String[] args) throws IOException{
		//read the whole training set
		List<String> trainSet = new ArrayList<String>();
		File inputFile=new File("train.csv");
		
		Scanner scanner = new Scanner(inputFile);
		String header=scanner.nextLine();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("@")||line.isEmpty()) {
				continue;
			}
			trainSet.add(line);
		}


		int trainSetSize=trainSet.size();
		int subTrainSetSize=trainSetSize*4/5;

		Random rand=new Random(System.currentTimeMillis());
		Collections.shuffle(trainSet, rand);



		// get the subTrainSet
		List<String> subTrainSet = new ArrayList<String>();
		for (int i = 0; i < subTrainSetSize; i++) {
			subTrainSet.add(trainSet.get(i));
		}
		

		// write the sub training set
		FileOutputStream fstream=new FileOutputStream("subTrainSet.csv");
		OutputStreamWriter outWriter=new OutputStreamWriter(fstream);
		BufferedWriter out=new BufferedWriter(outWriter);
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
			developmentSet.add(trainSet.get(i));
		}
		


		// write the development set
		fstream=new FileOutputStream("developmentTestSet.csv");
		outWriter=new OutputStreamWriter(fstream);
		out=new BufferedWriter(outWriter);
		out.write(header);
		out.newLine();
		for (int i = 0; i < developmentSet.size(); i++) {

			out.write(developmentSet.get(i));
			out.newLine();
		}
		out.close();



	}


}

