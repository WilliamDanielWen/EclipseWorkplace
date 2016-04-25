package Perceptron;
import java.util.ArrayList;
import java.util.List;
import Utilities.Instance;

public class PerceptronInPrimal{

	private int wieghtsDimension;

	// raw weights
	private double[] rawWeights;

	//normalized weights
	private double[] normWeights;

	// W0 ,also referred as bias
	private double bias;

	// training set
	private List<Instance> trainSet;

	// constructor functions
	public PerceptronInPrimal(List<Instance> inputset){
		// store the train set
		trainSet= new ArrayList<Instance>(inputset);
		// get dimension
		wieghtsDimension=inputset.get(0).getDimension();

		//initialize Weights as 0
		rawWeights= new double[wieghtsDimension];
		normWeights= new double[wieghtsDimension];

		for(int i=0;i<wieghtsDimension;i++){
			rawWeights[i]=0;
			normWeights[i]=0;
		}

		//initialize bias as 0
		bias=0;
	}

	public void trainPerceptronInPrimal(){
		int iterNum=0;
		int mistakeNum=0;
		while(true){
			mistakeNum=0;

			for(int i=0;i<trainSet.size();i++){

				int y_i=trainSet.get(i).getLabel();
				double[] x_i=trainSet.get(i).getX();
				if(y_i*predict(x_i)<=0){// misclassification detected
					mistakeNum++;
					// update the weights 
					for(int j=0;j<wieghtsDimension;j++){
						rawWeights[j] += y_i*x_i[j];
					}
					// update the bias
					bias += (double)y_i;

				}

			}

			iterNum++;
			System.out.println("Iteration "+iterNum+": number of mistakes are: "+mistakeNum);
			
			if(mistakeNum==0) break;// no mistake detected in this iteration means the algorithm converges
		}
		

	}

	public double predict(double[] xVector){
		double result=0;
		for(int i=0; i<wieghtsDimension; i++){
			result += rawWeights[i]*xVector[i];
		}
		result += bias;
		return result;
	}

	public void normalizeWeights(){
		for(int i=0;i<wieghtsDimension;i++){
			normWeights[i]=rawWeights[i]/(-bias);
		}
	}

	public double[] getRawWeights(){
		return rawWeights;
	}

	public double getBias(){
		return bias;
	}

	public double[] getNormalizedWeights(){
		return normWeights;
	}

}
