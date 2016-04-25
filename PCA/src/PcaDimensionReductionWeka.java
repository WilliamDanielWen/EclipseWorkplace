import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import weka.core.Instances;
//import weka.attributeSelection.PrincipalComponents;
import weka.filters.unsupervised.attribute.PrincipalComponents;
import weka.filters.Filter;
import weka.core.converters.ArffSaver;


public class PcaDimensionReductionWeka {
	public static void main(String[] args) throws Exception {
		
		//estimating running time: 14-15 minutes 
		// majority time is spent on computing the eigen vector of train.arff
		dimesionRedectionByPCA("datasets//digits//train.arff","datasets//digits//test.arff");

	}


	// building the PCA model based on train file
	public static void dimesionRedectionByPCA(String trainFilePath, String testFilePath) throws Exception{

		//read the train.arff which is converted from train.csv
		String[] trainInputArg=trainFilePath.split("//");
		String trainFileName=trainInputArg[trainInputArg.length-1];
		String[] testInputArg=testFilePath.split("//");
		String testFileName=testInputArg[testInputArg.length-1];


		System.out.println("Reading the file "+trainFileName+" ...\n");
		BufferedReader trainReader = new BufferedReader(new FileReader(trainFilePath));
		Instances trainData = new Instances(trainReader);
		trainData.setClassIndex(0);
		
		System.out.println("Reading the file "+testFileName+" ...\n");
		BufferedReader testReader = new BufferedReader(new FileReader(testFilePath));
		Instances testData = new Instances(testReader);
		testData.setClassIndex(0);
		

		// build the PCA by using the top 200 PCs of the training  data
		int maximumRetainDimension=200;
		int retainDimension=maximumRetainDimension;
		System.out.println("Computing the eigenvector matrix of "+trainFileName+" to build the PCA model...");
		long startTime = (System.currentTimeMillis())/1000;	
		PrincipalComponents pca = new PrincipalComponents();
		pca.setInputFormat(trainData);
		pca.setMaximumAttributes(retainDimension);
		Instances transformedTrainData = Filter.useFilter(trainData, pca);
		Instances transformedTestData = Filter.useFilter(testData, pca);
		long endTime = (System.currentTimeMillis())/1000;
		long runningTime = endTime-startTime;
		long sec= runningTime%60;
		long minutes= runningTime/60;
		System.out.println("Running time for building the model: "+minutes+" minutes, "+sec+" seconds\n");

		
		// output to files
		System.out.println("\nReducing the dimension of "+trainFileName+" to "+retainDimension+" dimension...");
		ArffSaver saver=new ArffSaver();
		saver.setInstances(transformedTrainData);
		String[] trainPathPrefix=trainFilePath.split(".arff");
		String trainOutputPath=trainPathPrefix[0]+"_to_"+retainDimension+"_dim.arff";
		System.out.println("Output the result to path: ");
		System.out.println(trainOutputPath);
		saver.setFile(new File(trainOutputPath));
		saver.writeBatch();
		
		System.out.println("\nReducing the dimension of "+testFileName+" to "+retainDimension+" dimension...");		
		saver.setInstances(transformedTestData);
		String[] testPathPrefix=testFilePath.split(".arff");
		String testOutputPath=testPathPrefix[0]+"_to_"+retainDimension+"_dim.arff";
		System.out.println("Output the result to path: ");
		System.out.println(testOutputPath);
		saver.setFile(new File(testOutputPath));
		saver.writeBatch();

		
		
		
		
		// Then select 100,50,20,10,5,2 PCs respectively
		retainDimension=100;
		while(retainDimension!=-1){
			
			pca.setMaximumAttributes(retainDimension);
			
			
			System.out.println("\nReducing the dimension of "+trainFileName+" to "+retainDimension+" dimension...");
			transformedTrainData = Filter.useFilter(trainData, pca); 
			// columns from index of retainDimension to 199 is zero , drop it 
			int numRedunColumn=maximumRetainDimension-retainDimension;
			for(int i=0;i<numRedunColumn;i++) transformedTrainData.deleteAttributeAt(retainDimension); 			
			saver=new ArffSaver();
			saver.setInstances(transformedTrainData);		
			trainOutputPath=trainPathPrefix[0]+"_to_"+retainDimension+"_dim.arff";
			System.out.println("Output the result to path: ");
			System.out.println(trainOutputPath);
			saver.setFile(new File(trainOutputPath));
			saver.writeBatch();

			System.out.println("\nReducing the dimension of "+testFileName+" to "+retainDimension+" dimension...");
			transformedTestData = Filter.useFilter(testData, pca); 
			// columns from index of retainDimension to 199 is zero , drop it 
			for(int i=0;i<numRedunColumn;i++) transformedTestData.deleteAttributeAt(retainDimension);
			saver=new ArffSaver();
			saver.setInstances(transformedTestData);		
			testOutputPath=testPathPrefix[0]+"_to_"+retainDimension+"_dim.arff";
			System.out.println("Output the result to path: ");
			System.out.println(testOutputPath);
			saver.setFile(new File(testOutputPath));
			saver.writeBatch();
			
			// update the retainDimension
			switch(retainDimension){
			case 100: 
				retainDimension -=50;
				break;
			case 50:
				retainDimension -=30;
				break;
			case 20:
				retainDimension -=10;
				break;
			case 10:
				retainDimension -=5;
				break;
			case 5:
				retainDimension -=3;
				break;
			case 2:
				retainDimension =-1;// exits here
				break;
			}

		}


	}




}






