import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import Utilities.*;
import KNN.KNN;
import Logistic.Logistic;

public class HW2ProgramStarter {

	/**  @params: all the data set converted to List<instance>
	 *  use 5-fold validation to train a logistic regression model and test
	 *  it, respectively
	 */
	public void runLogisticRegression(List<Instance> instances) throws IOException {


		// randomlize the inputSet
		Random rand=new Random();
		Collections.shuffle(instances, rand);

		//parameters used for 5-fold corss validation
		int inputSize=instances.size();
		int foldSize=inputSize/5;


		// Set the output File
		FileWriter fw = new FileWriter("Logistic Regression-output.txt");     
		BufferedWriter bw= new BufferedWriter(fw);
		bw.write("The accuracies of 5-flod cross-validation in each fold are as followings");
		bw.newLine();
		double averageAccuracy=0;
		//use the 5-fold corss validation on the train-set
		for (int fold=1;fold<=5;fold++){
			List<Instance> testSet=new ArrayList<Instance>();
			List<Instance> trainSet=new ArrayList<Instance>();

			// each fold scan the inputSet get different test set and training set
			for(int i=0;i<inputSize;i++){
				if((fold-1)*foldSize<=i && i<=fold*foldSize-1){ 

					testSet.add(instances.get(i));
				}else
				{
					trainSet.add(instances.get(i));
				}
			}

			Logistic logistic = new Logistic(trainSet.get(0).getDimension());
			logistic.train(trainSet);
			int errorCount=0;
			for(int i=0;i<testSet.size();i++){
				double predictProb=logistic.classify(testSet.get(i).getX());
				int predictLabel;

				if(predictProb>=0.5){
					predictLabel=1;
				}else{
					predictLabel=0;
				}

				if(predictLabel!=testSet.get(i).getLabel()) errorCount++;

			}
			// out put the accuracy
			double accuracy= 100*(1- (double)errorCount/testSet.size());
			bw.write("The accuracy of fold "+ fold +" is "+accuracy+" percentage.");
			bw.newLine();
			// sum accuracy in each round
			averageAccuracy +=accuracy;
			System.out.println("Fold "+fold+" accuracy=" +accuracy);
			System.out.println("");
		}
		// calculate the averagy accuracy
		averageAccuracy =averageAccuracy/5;

		bw.write("The average accuracy of 5 folds is "+averageAccuracy+" percentage.");
		bw.newLine();
		bw.newLine();
		bw.close();
		System.out.println("Average accuracy=" +averageAccuracy);

	}


	/**  @params: all the data set converted to List<instance>
	 *  Train and test a KNN classification model from k=1 to k=MaxK=21, k is odd number
	 *  For each k, use 5-fold validation to train a KNN classification model and test
	 *  it, respectively
	 *  
	 */
	public void runKNN(List<Instance> inputSet) throws IOException{
		// randomlize the inputSet
		Random rand=new Random(666);
		Collections.shuffle(inputSet, rand);

		//parameters used for 5-fold corss validation
		int inputSize=inputSet.size();
		int foldSize=inputSize/5;

		// Set the output File
		FileWriter fw = new FileWriter("KNN-output.txt");     
		BufferedWriter bw= new BufferedWriter(fw);

		// run KNN algorithm with different values of k
		int MaxK=21;
		for(int k=1;k<=MaxK;k=k+2){//set k to be 1,3,5...MaxK  
			KNN knn=new KNN(k);
			System.out.println("When K="+k+", the accuracies of 5-flod cross-validation in each fold are as followings");
			bw.write("When K="+k+", the accuracies of 5-flod cross-validation in each fold are as followings");
			bw.newLine();
			double averageAccuracy=0;

			//use the 5-fold corss validation on the train-set
			for (int fold=1;fold<=5;fold++){
				List<Instance> testSet=new ArrayList<Instance>();
				List<Instance> trainSet=new ArrayList<Instance>();

				// each fold scan the inputSet get different test set and training set
				for(int i=0;i<inputSize;i++){
					if((fold-1)*foldSize<=i && i<=fold*foldSize-1){ 

						testSet.add(inputSet.get(i));
					}else
					{
						trainSet.add(inputSet.get(i));
					}
				}

				// train the KNN classifier
				knn.train(trainSet);

				// classify and validate
				int errorCount=0;
				for(Instance testInstance: testSet){
					int predictedLabel=knn.classify(testInstance);
					if( predictedLabel != testInstance.label) errorCount++;
				}
				// out put the accuracy
				double accuracy= 100*(1- (double)errorCount/testSet.size());
				System.out.println("The accuracy of fold "+ fold +" is "+accuracy+" percentage.");
				bw.write("The accuracy of fold "+ fold +" is "+accuracy+" percentage.");
				bw.newLine();
				// sum accuracy in each round
				averageAccuracy +=accuracy;
			}// end one round of 5-fold cross validation

			// calculate the averagy accuracy
			averageAccuracy =averageAccuracy/5;

			System.out.println("When K="+k+",the average accuracy of 5 folds is "+averageAccuracy+" percentage.");
			System.out.println("");
			bw.write("When K="+k+",the average accuracy of 5 folds is "+averageAccuracy+" percentage.");
			bw.newLine();
			bw.newLine();
		}

		bw.close();
	}// end runKNN


	
	public static void main(String[] args) throws IOException{
		//read the data from file
		List<Instance> inputSet = DataSet.readDataSet("data\\data.txt");


		//choose an algorithm and run it
		System.out.println("Enter the number corresponding to the algorithm you want to run:");
		System.out.println("1) Logistic Regression optimized by Newton's method");
		System.out.println("2) KNN");
		System.out.println("3) Exit\n");
		Scanner in = new Scanner(System.in);
		int choice = in.nextInt();
		HW2ProgramStarter runner=new HW2ProgramStarter();
		switch(choice){
		case 1:         
			runner.runLogisticRegression(inputSet);
			break;
		case 2: 
			runner.runKNN(inputSet);
			break;
		case 3: System.exit(0);
		}


	}

}

