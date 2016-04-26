import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;


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

public class Utility{

	public static List<Rating> readData(String path) throws FileNotFoundException {

		List<Rating> inputSet = new ArrayList<Rating>();
		File inputFile=new File(path);
		Scanner scanner = new Scanner(inputFile);
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.isEmpty()){
				continue;
			}
			String[] content=line.split("\t");
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

	public static List<Rating> getTestSetAtKFold(List<Rating> inputSet,int fold, int foldNum){
		List<Rating> testSet=new ArrayList<Rating>();
		int testStartIndex=(fold-1)*inputSet.size()/foldNum;
		int testEndIndex=fold*inputSet.size()/foldNum;
		for(int i=testStartIndex;i<testEndIndex;i++){
			Rating r=inputSet.get(i);
			testSet.add(r);
		}
		return testSet;
	}

	public static List<Rating> getTrainSetAtKFold(List<Rating> inputSet,int fold, int foldNum){
		List<Rating> trainSet=new ArrayList<Rating>(inputSet);
		int testStartIndex=(fold-1)*inputSet.size()/foldNum;
		int testEndIndex=fold*inputSet.size()/foldNum;
		for(int i=testStartIndex;i<testEndIndex;i++){
			trainSet.remove(i);
		}
		return trainSet;
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

}

