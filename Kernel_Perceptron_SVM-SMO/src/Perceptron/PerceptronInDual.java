package Perceptron;

import java.util.ArrayList;
import java.util.List;

import Utilities.Instance;

public class PerceptronInDual {

	private int trainSetSize;

	private int wieghtsDimension;

	// training set
	private List<Instance> trainSet;

	//alpha[i]  means number of times the perceptron misclassified the trainSet[i]
	private int[] alpha;

	// used for output
	private double bias;
	private double[] weights;


	// constructor functions
	public PerceptronInDual(List<Instance> inputset){
		// store the train set
		trainSet= new ArrayList<Instance>(inputset);


		// get dimension
		wieghtsDimension=inputset.get(0).getDimension();
		weights= new double[wieghtsDimension];

		// get training set size
		trainSetSize=inputset.size();

		//initialize alpha as 0;
		alpha =new int[trainSetSize];
		for(int i=0;i<trainSetSize;i++){
			alpha[i]=0;
		}

	}

	public void trainPerceptronInDual(String kerName){
		int iterNum=0;
		int mistakeNum=0;
		while(true){
			mistakeNum=0;

			for(int i=0;i<trainSet.size();i++){
				int y_i=trainSet.get(i).getLabel();
				double[] x_i=trainSet.get(i).getX();
				if(y_i*predict(x_i,kerName)<=0){// misclassification detected
					mistakeNum++;
					// update the alphas 
					alpha[i]++;
				}
			}
			iterNum++;
			System.out.println("Iteration "+iterNum+": number of mistakes are: "+mistakeNum);

			if(mistakeNum==0) break;// no mistake detected in this iteration means the algorithm converges
		}



	}

	public double predict(double[] xVector,String kerName){
		double result=0;
		for(int j=0;j<trainSetSize;j++){
			double y_j=trainSet.get(j).getLabel();
			double alpha_j=(double)alpha[j];
			double[] x_j=trainSet.get(j).getX();
			switch(kerName){
			case "Linear Kernel":
				result += y_j*alpha_j*linearKernel(xVector,x_j) + y_j*alpha_j;
				break;
			case "Polynomial Kernel":
				result += y_j*alpha_j*polynomialKernel(xVector,x_j)+ y_j*alpha_j;
				break;
			case "Gaussian Kernel":
				result += y_j*alpha_j*GaussianKernel(xVector,x_j)+ y_j*alpha_j;
				break;
			}
		}

		return result;
	}
	
	
	public double GaussianKernel(double[] x1, double[] x2){
		double result=0;
		for(int i=0;i<wieghtsDimension;i++){
			result += Math.pow(x1[i]-x2[i], 2);
		}
		double sigma_square=2;
		result=Math.exp(-result/(2*sigma_square));
		return result;
	}

	
	public double polynomialKernel(double[] x1, double[] x2){

		double dotProduct=0;
		// dot product
		for(int i=0;i<wieghtsDimension;i++){
			dotProduct += x1[i]*x2[i];
		}

		double result=Math.pow(dotProduct+1,2); 		
		return result;
	}
	public double linearKernel(double[] x1, double[] x2){
		double dotProduct=0;
		// dot product
		for(int i=0;i<wieghtsDimension;i++){
			dotProduct += x1[i]*x2[i];
		}
		return dotProduct;
	}

	public void convertLinearDualIntoPrimal(){
		for(int i=0;i<trainSetSize;i++){
			double[] x_i=trainSet.get(i).getX();
			double y_i=trainSet.get(i).getLabel();
			double alpha_i=(double)alpha[i];
			for(int j=0;j<wieghtsDimension;j++){
				weights[j] += alpha_i*y_i*x_i[j];
			}
			bias +=  y_i*alpha_i;
		}
	}

	public double[] getWeights(){
		return weights;
	}

	public double getBias(){
		return bias;
	}
}
