import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class MultiLabelClassificationEvaluation {


	public static void F1_Calculator(String filePathPredictoin,String filePathTestSet) throws IOException{
		//read the development set
		List<Set<String>> predictionList = new ArrayList<Set<String>>();
		List<Set<String>> testSetList = new ArrayList<Set<String>>();


		// scan the file contians prediction into predictionList 
		File predictoinFile=new File(filePathPredictoin);
		Scanner scanner = new Scanner(predictoinFile);
		String header=scanner.nextLine();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("@")||line.isEmpty()) {
				continue;
			}
			Set<String> predictedLabelSet_i;
			// get the business id 
			String[] lineArray=line.split(",");
			if(lineArray.length==1){ // the label are empty(only contain the business id in this line)
				String[] emptyLabel={""};
				predictedLabelSet_i=new HashSet<String>(Arrays.asList(emptyLabel));
			}else{
				predictedLabelSet_i=new HashSet<String>(Arrays.asList(lineArray[1].split(" ")));
			}
			predictionList.add(predictedLabelSet_i);
		}


		// scan the file contians true labels into trueExampleList
		File trueExampleFile=new File(filePathTestSet);
		scanner = new Scanner(trueExampleFile);
		header=scanner.nextLine();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("@")||line.isEmpty()) {
				continue;
			}
			Set<String> trueLabelSet_i;
			// get the business id 
			String[] lineArray=line.split(",");
			if(lineArray.length==1){ // the label are empty(only contain the business id in this line)
				String[] emptyLabel={""};
				trueLabelSet_i=new HashSet<String>(Arrays.asList(emptyLabel));
			}else{
				trueLabelSet_i=new HashSet<String>(Arrays.asList(lineArray[1].split(" ")));
			}
			testSetList.add(trueLabelSet_i);
		}


		// prepare the output file
		FileWriter fwriter=new FileWriter("evaluationResult.txt");
		BufferedWriter out=new BufferedWriter(fwriter);
		if(predictionList.size()!=testSetList.size()){
			out.write("Error! The number of predction didn't eqaul to the number of examples in the test set");
			out.close();
			System.out.println("Error! The number of predction didn't eqaul to the number of examples in the test set");
			return;
		};


		// coupute the precision recall and F1
		double precision=0d;
		double recall=0d;
		double accuracy=0d;
		int testSetSize=testSetList.size();
		for(int i=0;i<testSetSize;i++){

			Set<String> trueLabelSet_i=testSetList.get(i);
			Set<String> predictedLabelSet_i=predictionList.get(i);

			Set<String> intersection = new HashSet<String>(trueLabelSet_i);// initialize as trueLabelSet
			intersection.retainAll(predictedLabelSet_i);// get the intersection;

			Set<String> union=new HashSet<String>(trueLabelSet_i); // initialize as trueLabelSet
			union.addAll(predictedLabelSet_i); // get the union;

			precision += ((double)intersection.size())/((double)predictedLabelSet_i.size());
			recall += ((double)intersection.size())/((double)trueLabelSet_i.size());
			accuracy += ((double)intersection.size())/((double)union.size());

		}
		precision /=testSetSize;
		recall /=testSetSize;
		accuracy /=testSetSize;

		double F1_score;
		if((precision+recall)==0d){
			F1_score=0d;
		}else
		{
			F1_score=2*(precision*recall)/(precision+recall);
		}

		// output the results
		out.write("The evaluation results are as followings:");
		out.newLine();
		out.write("precision : "+precision*100+" percentage ;");
		out.newLine();
		out.write("recall : "+recall*100+" percentage ;");
		out.newLine();
		out.write("accuracy : "+accuracy*100+" percentage ;");
		out.newLine();
		out.write("F1-Score : "+F1_score+" ;");
		out.close();

		System.out.println("The evaluation results are as followings:");
		System.out.println("precision : "+precision*100+" percentage ;");
		System.out.println("recall : "+recall*100+" percentage ;");
		System.out.println("accuracy : "+accuracy*100+" percentage ;");
		System.out.println("F1-Score : "+F1_score+" ;");

	}

	public static void main(String[] args) throws IOException{

		F1_Calculator("randomGuess.csv","developmentSet.csv");


	}

}
