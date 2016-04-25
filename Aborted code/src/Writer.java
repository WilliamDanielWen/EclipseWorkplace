import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Writer {
	/*
	public static void generateTestAndTrainData(String inputPath, double proportionTrainset,String trainSetPath,String testSetPath) throws IOException{

		List<Rating> inputSet=readData(inputPath);
		// randomlize the input set
		Random rand=new Random(System.currentTimeMillis());
		Collections.shuffle(inputSet, rand);

		int inputSetSize=inputSet.size();
		int trainSetSize=(int) (inputSetSize*proportionTrainset);

		// get the trainSet
		List<Rating> trainSet = new ArrayList<Rating>();
		for (int i = 0; i < trainSetSize; i++) {
			trainSet.add(inputSet.get(i));
		}

		// get the testSet
		List<Rating> testSet = new ArrayList<Rating>();
		for (int i = trainSetSize; i < inputSetSize; i++) {
			testSet.add(inputSet.get(i));
		}

		// output to file
		wirteDataToFile(trainSet,trainSetPath);
		wirteDataToFile(testSet,testSetPath);


	}
	
	public static void wirteDataToFile(List<Rating> dataSet,String outputPath) throws IOException{
		FileWriter fwriter=new FileWriter(outputPath);
		BufferedWriter out=new BufferedWriter(fwriter);
		
		for (int i = 0; i < dataSet.size(); i++) {
			Rating r=dataSet.get(i);
			out.write(r.user_id+"\t"+r.item_id+"\t"+r.score);
			out.newLine();
		}
		out.close();
	}
	
		// randomly guess a score for every entry in the given validation set and then get the RMSE
	private double getBaselineRMSE(List<Rating> testSet) throws IOException{
		double rmse=0;		

		// 1. calculate the square error
		for(int j=0;j<testSet.size();j++){
			// radomly guess a number from 1-5
			double predicted_score=randomPredict();
			double true_score=testSet.get(j).score;
			rmse += Math.pow(predicted_score-true_score, 2); 
		}
		//2. get the rmse
		double n=testSet.size();
		rmse = rmse/n;
		rmse = Math.sqrt(rmse);
		return rmse;
	}
	
		// randomly guess a score for every entry in the given validation set and then get the MAE
	private double getBaselineMAE(List<Rating> testSet) throws IOException{
		double mae=0;
		// 1. calculate the sum 
		for(int j=0;j<testSet.size();j++){

			int u=testSet.get(j).user_id;
			int i=testSet.get(j).item_id;
			double predicted_score=randomPredict();
			double true_score=testSet.get(j).score;

			mae += Math.abs(predicted_score-true_score); 
		}
		//2. get the mae
		double n=testSet.size();
		mae = mae/n;

		return mae;
	}
	*/
}
