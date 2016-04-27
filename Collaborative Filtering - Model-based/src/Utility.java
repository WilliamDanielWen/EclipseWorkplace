import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

// the class used to store the information of the rating from a user to an item
class Rating  {
	public int user_id;
	public int item_id;
	public double score;
	public Rating(int u_id, int i_id, double s){
		this.user_id=u_id;
		this.item_id=i_id;
		this.score=s;		
	}
}

// Utilities of ModelBasedCF
public class Utility{

	/*  
	 * @params: inputSet is the whole data set used for K folds cross validation
	 * @params: foldIndex indicates the index of desired fold out of K folds
	 * @params: K indicates the total number of folds
	 * returns  the train set of "foldIndex"-th fold out of K folds
	 */
	public static List<Rating> getTrainSetInKFold(List<Rating> inputSet,int foldIndex, int K){
		List<Rating> trainSet=new ArrayList<Rating>(inputSet);
		
		int foldSize=inputSet.size()/K;
		int testStartIndex=(foldIndex-1)*foldSize;
		int testEndIndex=inputSet.size()-1;  // initilize  testEndIndex as the test set end index for k
		
		// test set end index for fold 1...k-1
		if(foldIndex!=K) testEndIndex=foldIndex*foldSize; 

		
		for(int i=testEndIndex;i>=testStartIndex;i--){
			trainSet.remove(i);
		}
		return trainSet;
	}

	/*  
	 * @params: inputSet is the whole data set used for K folds cross validation
	 * @params: foldIndex indicates the index of desired fold out of K folds
	 * @params: K indicates the total number of folds
	 * returns  the test set of "foldIndex"-th fold out of K folds
	 */
	public static List<Rating> getTestSetAtKFold(List<Rating> inputSet,int foldIndex, int K){
		List<Rating> testSet=new ArrayList<Rating>();
		int foldSize=inputSet.size()/K;
		int testStartIndex=(foldIndex-1)*foldSize;
		int testEndIndex=inputSet.size()-1;  // initilize  testEndIndex as the test set end index for k
		
		// test set end index for fold 1...k-1
		if(foldIndex!=K) testEndIndex=foldIndex*foldSize; 
		for(int i=testStartIndex;i<testEndIndex;i++){
			Rating r=inputSet.get(i);
			testSet.add(r);
		}
		return testSet;
	}
	
	// we assume the dimension of vector_a and vector_b is the same
	// return the dot product of vector_a and vector_b
	public static double dotProduct(double[] vector_a, double[] vector_b){
		double result=0;
		for(int i=0;i<vector_a.length;i++){
			result += vector_a[i]*vector_b[i];
		}
		return result;
	}

	// we assume the dimension of vector_a and vector_b is the same
	// return vector_a + vector_b
	public static double[] vectorPlus(double[] vector_a, double[] vector_b){
		double[] result=new double[vector_a.length];
		for(int i=0;i<vector_a.length;i++){
			result[i]=vector_a[i] +vector_b[i];
		}
		return result;
	}

	// we assume the dimension of vector_a and vector_b is the same
	// return vector_a-vector_b
	public static double[] vectorMinus(double[] vector_a, double[] vector_b){
		double[] result=new double[vector_a.length];
		for(int i=0;i<vector_a.length;i++){
			result[i]=vector_a[i] - vector_b[i];
		}
		return result;
	}


	// scale each component in vector_a by scalar
	public static double[] vectorScale(double[] vector_a, double scalar){
		double[] result=new double[vector_a.length];

		for(int i=0;i<vector_a.length;i++){
			result[i]=vector_a[i]*scalar;
		}
		return result;
	}


	// calculate the squre sum of each component in vector
	public static double vectorSquareSum(double[] vector){
		double result=0;
		for(int i=0;i<vector.length;i++){
			result += Math.pow(vector[i], 2);
		}
		return result;
	}

	// randomly generate a double  beween lowerBound and upperBound
	public static double randomDouble(double lowerBound, double upperBound){

		if(lowerBound>upperBound){
			double temp=lowerBound;
			lowerBound=upperBound;
			upperBound=lowerBound;
		}
		Random rand=new Random(2016);
		double result= lowerBound+(upperBound-lowerBound)*rand.nextDouble();
		return result;
	}

	// randomly generate a Integer  between lowerBound and upperBound
	public static int randomInteger(int lowerBound, int upperBound){
		if(lowerBound>upperBound){
			int temp=lowerBound;
			lowerBound=upperBound;
			upperBound=lowerBound;
		}
		Random rand=new Random(2016);
		int result=lowerBound+rand.nextInt(upperBound-lowerBound);
		return result;
	}

	// read rating from csv file 
	public static List<Rating> readDataFromCSV(String path) throws FileNotFoundException {

		List<Rating> inputSet = new ArrayList<Rating>();
		File inputFile=new File(path);
		Scanner scanner = new Scanner(inputFile);
		scanner.nextLine();// skip the header
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.isEmpty()){
				continue;
			}
			String[] content=line.split(",");
			int user_id=Integer.parseInt(content[0]);
			int item_id=Integer.parseInt(content[1]);
			double score=Double.parseDouble(content[2]);
			Rating r=new Rating(user_id,item_id,score);
			inputSet.add(r);
		}
		Random rand=new Random(999);
		Collections.shuffle(inputSet,rand);

		return inputSet;
	}



	// write a dataSet into a csv file
	public static void wirteDataToCSV(List<Rating> dataSet,String outputPath) throws IOException{
		FileWriter fwriter=new FileWriter(outputPath);
		BufferedWriter out=new BufferedWriter(fwriter);
		// write the header
		out.write("user_id,item_id,rating_score");
		out.newLine();
		for (int i = 0; i < dataSet.size(); i++) {
			Rating r=dataSet.get(i);
			out.write(r.user_id+","+r.item_id+","+r.score);
			out.newLine();
		}
		out.close();
	}

	// split input data set with path of "inputPath" into a training set and a test set
	// training set size= (input data set size)*proportionTrainset
	// test set size= (input data set size)*(1-proportionTrainset)
	public static void generateTestAndTrainData(String inputPath, double proportionTrainset,String trainSetPath,String testSetPath) throws IOException{

		List<Rating> inputSet=readDataFromCSV(inputPath);
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
		wirteDataToCSV(trainSet,trainSetPath);
		wirteDataToCSV(testSet,testSetPath);


	}

}

