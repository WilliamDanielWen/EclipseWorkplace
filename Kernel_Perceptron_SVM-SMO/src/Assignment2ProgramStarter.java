import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import Utilities.DataSet;
import Utilities.Instance;
import Perceptron.*;
import SVM.*;

import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.SelectedTag;

public class Assignment2ProgramStarter {


	public static void runPerceptronInPrimal()throws IOException{
		//read the "percep1.txt"
		List<Instance> inputSet = DataSet.readDataSet("data\\perceptron\\percep1.txt");

		PerceptronInPrimal percp=new PerceptronInPrimal(inputSet);

		//train the perceptron
		percp.trainPerceptronInPrimal();

		//normalize the weights
		percp.normalizeWeights();

		// Set the output File
		FileWriter fw = new FileWriter("output\\perceptron\\Report of Primal Perceptron.txt");     
		BufferedWriter bw= new BufferedWriter(fw);
		//output the raw weights 
		bw.write("The raw weights are as followings:");
		bw.newLine();
		bw.write("(");
		for(int i=0;i<percp.getRawWeights().length;i++){
			if(i==percp.getRawWeights().length-1)  {
				bw.write(percp.getRawWeights()[i]+")");
			}else{
				bw.write(percp.getRawWeights()[i]+",  ");
			}
		}
		bw.newLine();
		bw.newLine();

		//output the bias
		bw.write("The bias is "+percp.getBias());
		bw.newLine();
		bw.newLine();

		//output the normalized weights
		bw.write("The normalized weights are as followings:");
		bw.newLine();
		bw.write("(");
		for(int i=0;i<percp.getNormalizedWeights().length;i++){
			if(i==percp.getNormalizedWeights().length-1)  {
				bw.write(percp.getNormalizedWeights()[i]+")");
			}else{
				bw.write(percp.getNormalizedWeights()[i]+",  ");
			}
		}

		bw.close();
	}

	public static void runPerceptronInDual() throws IOException{

		//1. Run dual perceptron with linear kernel  on percep1.txt
		System.out.println("Run dual perceptron with linear kernel on percep1.txt");
		//read the "percep1.txt"
		List<Instance> percep1Data = DataSet.readDataSet("data\\perceptron\\percep1.txt");
		PerceptronInDual percepDualLinearKernel=new PerceptronInDual(percep1Data);
		// train the perceptron
		percepDualLinearKernel.trainPerceptronInDual("Linear Kernel");


		/* since we are using an linear kernel, <x,x'> is <phi(x),phi(x')>, wehre phi(x) is x itself,
		   we can convert from the dual solution by using an linear kernel into primal solution easily*/
		percepDualLinearKernel.convertLinearDualIntoPrimal();
		// Set the output File
		FileWriter fw = new FileWriter("output\\perceptron\\Report of Dual Perceptron with Linear Kernel on percep1.txt");     
		BufferedWriter bw= new BufferedWriter(fw);
		//output the raw weights 
		bw.write("The weights of dual perceptron by using the linear kernel on percep1.txt are as followings:");
		bw.newLine();
		bw.write("(");
		for(int i=0;i<percepDualLinearKernel.getWeights().length;i++){
			if(i==percepDualLinearKernel.getWeights().length-1)  {
				bw.write(percepDualLinearKernel.getWeights()[i]+")");
			}else{
				bw.write(percepDualLinearKernel.getWeights()[i]+",  ");
			}
		}
		bw.newLine();
		bw.newLine();

		//output the bias
		bw.write("The bias is "+percepDualLinearKernel.getBias());
		bw.close();

		//2. Run dual perceptron  on percep2.txt
		//read the "percep1.txt"
		List<Instance> percep2Data = DataSet.readDataSet("data\\perceptron\\percep2.txt");

		// 2.1 trial with polinomial kernel of degree of 3
		System.out.println("");
		System.out.println("Run the dual perceptron with Gaussian kernel on percep2.txt");
		PerceptronInDual percepDualGaussianKernel=new PerceptronInDual(percep2Data);
		percepDualGaussianKernel.trainPerceptronInDual("Gaussian Kernel");

	}

	// runing implementation of problem 4-part(a)
	public static void runSvmLinearKernelSMO() throws IOException{

		//read the "train.csv", "test.csv"
		List<Instance> trainData = DataSet.readP4Csv("data\\digits\\train.csv");
		List<Instance> testData =DataSet.readP4Csv("data\\digits\\test.csv");
		trainData=DataSet.minMaxNormalization(trainData);
		testData=DataSet.minMaxNormalization(testData);


		// randomly select 1/4 training data 
		// shuffle the trainData
		Random rand=new Random(System.currentTimeMillis());
		Collections.shuffle(trainData, rand);

		List<Instance> subTrainData=new ArrayList<Instance>();
		double maxSubIndex=trainData.size()/4;
		for(int i=0;i<maxSubIndex;i++){
			subTrainData.add(trainData.get(i));
		}

		//create the SVM, with the subTrainData
		SvmSMO svm=new SvmSMO(subTrainData);

		// train the SVM
		svm.trainBySimpSMO();

		// Test on test set
		double right_num=0;
		double accuracy=0;
		System.out.println("Test begins");
		for(int i=0;i<testData.size();i++){
			double[] newX=testData.get(i).getX();
			int newY=testData.get(i).getLabel();
			int predictY=svm.classify(newX);
			if (newY==predictY) right_num++;
		}
		accuracy=(double)right_num/testData.size()*100d;
		System.out.println("Accuracy is "+accuracy+" percent.");

	}

	//runing implementation of problem 4-part(b)
	public static void runSvmPackage()throws Exception{
		// variable used for testing
		double right_num=0;
		double accuracy=0;

		//read the train.arff which is converted from train.csv
		BufferedReader trainDataReader = new BufferedReader(new FileReader("data\\digits\\train.arff"));
		Instances trainData = new Instances(trainDataReader);
		Random seed=new Random(System.currentTimeMillis());
		trainData.randomize(seed);
		trainData.setClassIndex(0);

		// we only use 1/3 training set
		Instances subTrainData=new Instances(trainData,0,trainData.numInstances()/2);
		subTrainData.setClassIndex(0); // set label is the first column

		//read the test.arff which is converted from train.csv
		BufferedReader testDataReader = new BufferedReader(new FileReader("data\\digits\\test.arff"));
		Instances testData = new Instances(testDataReader);
		testData.setClassIndex(0);




		//1. Train SVM with linear kernel
		LibSVM svmLinear=new LibSVM();
		// set parameters 
		svmLinear.setSVMType(new SelectedTag(LibSVM.SVMTYPE_C_SVC, LibSVM.TAGS_SVMTYPE));
		svmLinear.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_LINEAR, LibSVM.TAGS_KERNELTYPE));

		System.out.println("Training begins ,kernel type: Linear");
		svmLinear.buildClassifier(subTrainData);
		System.out.println("Training finished");

		// Test on test set
		right_num=0;
	    accuracy=0;
		System.out.println("Test begins");
		for(int i=0;i<testData.numInstances();i++){
			double newY=testData.instance(i).classValue();
			double predictY=svmLinear.classifyInstance(testData.instance(i));
			if (newY==predictY) right_num++;
		}
		accuracy=(double)right_num/testData.numInstances()*100d;
		System.out.println("Test finised, the accuracy is "+accuracy+" percent.");
		System.out.println("");





		//2. Train  svm with polynomial kernel
		LibSVM svmPolyn=new LibSVM();
		// set parameters 
		svmPolyn.setSVMType(new SelectedTag(LibSVM.SVMTYPE_C_SVC, LibSVM.TAGS_SVMTYPE));
		svmPolyn.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_POLYNOMIAL, LibSVM.TAGS_KERNELTYPE));
		svmPolyn.setCoef0(1); //(Coef0+k<xi,xj>^degree)
		svmPolyn.setDegree(3);

		System.out.println("Training begins ,kernel type: POLYNOMIAL");
		svmPolyn.buildClassifier(subTrainData);
		System.out.println("Training finished");
		// Test on test set
		right_num=0;
		accuracy=0;
		System.out.println("Test begins");
		for(int i=0;i<testData.numInstances();i++){
			double newY=testData.instance(i).classValue();
			double predictY=svmPolyn.classifyInstance(testData.instance(i));
			if (newY==predictY) right_num++;
		}
		accuracy=(double)right_num/testData.numInstances()*100d;
		System.out.println("Test finised, the accuracy is "+accuracy+" percent.");
		System.out.println("");


		//3. Train  svm with Gaussian kernel
		LibSVM svmGaussian=new LibSVM();
		// set parameters 
		svmGaussian.setSVMType(new SelectedTag(LibSVM.SVMTYPE_NU_SVC, LibSVM.TAGS_SVMTYPE));
		svmGaussian.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_RBF, LibSVM.TAGS_KERNELTYPE));
		svmGaussian.setGamma(0.05);
		svmGaussian.setEps(1);
		svmGaussian.setNormalize(true);
		
		System.out.println("Training begins ,kernel type: Gaussian RBF kernel");
		svmGaussian.buildClassifier(subTrainData);
		System.out.println("Training finished");
		// Test on test set
		right_num=0;
		accuracy=0;
		System.out.println("Test begins");
		for(int i=0;i<testData.numInstances();i++){
			double newY=testData.instance(i).classValue();
			double predictY=svmGaussian.classifyInstance(testData.instance(i));
			if (newY==predictY) right_num++;
		}
		accuracy=(double)right_num/testData.numInstances()*100d;
		System.out.println("Test finised, the accuracy is "+accuracy+" percent.");
		System.out.println("");
		

        //4. Train  svm with Sigmoid kernel
		LibSVM svmSigmoid=new LibSVM();
		// set parameters 
		svmSigmoid.setSVMType(new SelectedTag(LibSVM.SVMTYPE_C_SVC, LibSVM.TAGS_SVMTYPE));
		svmSigmoid.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_SIGMOID, LibSVM.TAGS_KERNELTYPE));
		svmSigmoid.setNormalize(true);
		System.out.println("Training begins ,kernel type: Sigmoid");
		svmSigmoid.buildClassifier(subTrainData);
		System.out.println("Training finished");
		// Test on test set
		right_num=0;
		accuracy=0;
		System.out.println("Test begins");
		for(int i=0;i<testData.numInstances();i++){
			double newY=testData.instance(i).classValue();
			double predictY=svmSigmoid.classifyInstance(testData.instance(i));
			if (newY==predictY) right_num++;
		}
		accuracy=(double)right_num/testData.numInstances()*100d;
		System.out.println("Test finised, the accuracy is "+accuracy+" percent.");
		System.out.println("");



	}

	
	public static void main(String[] args) throws Exception{


		//choose an algorithm and run it
		System.out.println("Enter the number corresponding to the algorithm you want to run:");
		System.out.println("1) Perceptron in primal form");
		System.out.println("2) Perceptron in dual form with kernel");
		System.out.println("3) SVM with linear kernel trained by using simplfied SMO");
		System.out.println("4) SVM with different kernels");
		System.out.println("0) Exit\n");
		Scanner in = new Scanner(System.in);
		int choice = in.nextInt();
		Assignment2ProgramStarter runner=new Assignment2ProgramStarter();
		switch(choice){
		case 1:         
			runPerceptronInPrimal();
			break;
		case 2: 
			runPerceptronInDual();
			break;
		case 3: 
			runSvmLinearKernelSMO();
			break;
		case 4:
			runSvmPackage();
			break;

		case 0: 
			System.exit(0);
			break;
		default:
			System.out.println("Invalid input, please restart againg");
			break;
		}


	}

}