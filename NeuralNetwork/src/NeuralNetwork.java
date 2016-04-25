import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// implementation of a fully connected neural network with one hidden layer and one output node in the output layer
// the input dimension and the number of nodes in the hidden layer should be determined by the user

public class NeuralNetwork {


	//default runing of neural network
	public static void main(String[] args)throws IOException {
		//read the "train.csv", "test.csv"
		List<DataEntry> trainData = Utility.readDataSet("data//train.csv");
		List<DataEntry> testData =Utility.readDataSet("data//test.csv");

		//normalize the data to accelerate the training
		trainData=Utility.minMaxNormalization(trainData);
		testData=Utility.minMaxNormalization(testData);

		
		int nodeNum=100;


		NeuralNetwork neuralNetwork=new NeuralNetwork(trainData,nodeNum);
		System.out.println("Training begins ... ");
		System.out.println("Number of hidden units : "+nodeNum);
		neuralNetwork.training(trainData);
		System.out.println("Training finished!\n");

		// Test on test set
		System.out.println("Test begins...");
		List<Integer> predictedResults=neuralNetwork.getPredictedResults(testData);
		double accuracy=Utility.getAccuracy(testData, predictedResults);
		System.out.println("Test finised! The accuracy is "+accuracy+" percent.");
		

	}

    // learning rate
	private double learningRate;

	//number of nodes in the input layer
	private int inputDimension;

	// number of nodes in the hidden layer
	private int hidLayerNodesNum;

	/* hidLayerWeights[i][j] is the weight connecting the node in the hidden layer with index of i
	   and the node in the input layer with index of j */
	private double[][] hidLayerWeights;

	/* outLayerWeights[i] is the weights connecting the output layer node
	   to the node with index of i in the hidden layer */
	private double[] outLayerWeights;

	/* hidLayerBiases[i] is the bias connecting input layer to the node with index of i 
	   in the hidden layer  */
	private double[] hidLayerBiases;

	/* outLayerBias[i] is the bias connecting the output layer node
	   and all the nodes in the hidden layer */
	private double outLayerBias;


	public NeuralNetwork(List<DataEntry> trainSet,int hidNodesNum){
	
	
		//initialize the input dimension
		inputDimension=trainSet.get(0).getDimension();
	
		// set the number of nodes in the hidden layer
		hidLayerNodesNum=hidNodesNum;
	
		
		Random rand=new Random(System.currentTimeMillis());
		hidLayerWeights=new double[hidLayerNodesNum][];
		for(int i=0;i<hidLayerNodesNum;i++){
			hidLayerWeights[i]=new double[inputDimension];
			for(int j=0;j<inputDimension;j++){
				// randomly initialize the weights between -0.01 and 0.01
				hidLayerWeights[i][j]=-0.01+(rand.nextDouble()*0.02);
			}
	
		}
	
		hidLayerBiases=new double[hidLayerNodesNum];
		for(int i=0;i<hidLayerNodesNum;i++){
			// randomly initialize the weights between -0.5 and 0.5
			hidLayerBiases[i]=-0.5+(rand.nextDouble());
		}
	
		
		outLayerWeights=new double[hidLayerNodesNum];
		hidLayerBiases=new double[hidLayerNodesNum];
		for(int i=0;i<hidLayerNodesNum;i++){
			// randomly initialize the weights between -0.1 and 0.1
			outLayerWeights[i]=-0.1+(rand.nextDouble()*0.2);
		}
	
		outLayerBias=rand.nextDouble();
	
		
		learningRate=0.01;
	
	}


	// Using the feedforward algorithm and backpropagation algorithm to train a fully connected neural network
	public void training(List<DataEntry> trainSet){
		int epoch=1;

		// we assume after 50 epoches, the algorithm converges
		int maxEpoches=50;


		// local variables we need to use
		double[] deltaWeiOutLayer=new double[hidLayerNodesNum];
		double[][] deltaWeiHidLayer=new double[hidLayerNodesNum][inputDimension];
		double[] deltaBiasHidLayer=new double[hidLayerNodesNum];
		double deltaBiasOutLayer;
		double[] hidlayerInput=new double [hidLayerNodesNum];
		double[] hidLayerOutput=new double [hidLayerNodesNum];
		double[] hidLayerErrors=new double[hidLayerNodesNum];
		double outLayerInput;
		double outLayerOutput;
		double outLayerError;

		while(epoch<=maxEpoches){
			System.out.println("Epoch "+epoch+" begins, remaining epoch to go :"+(maxEpoches-epoch));


			for(int i=0;i<trainSet.size();i++){

				double[] inputX=trainSet.get(i).getX();


				//1. feed forward to hidden layer
				for(int j=0;j<hidLayerNodesNum;j++){
					// input for each hidden layer node is (wi*x)+bi
					hidlayerInput[j]=dotProduct(hidLayerWeights[j],inputX)+hidLayerBiases[j];

					// use sigmoid function to produce the output
					hidLayerOutput[j]=sigmoidActivation(hidlayerInput[j]);
				}

				// feed forward to output layer, we only have one node. 
				outLayerInput=dotProduct(hidLayerOutput,outLayerWeights)+outLayerBias;
				outLayerOutput=sigmoidActivation(outLayerInput);


				//2.backpropagate the errors and update the Weights
				//2.1 compute the error in output layer
				double trueLabel=(double) trainSet.get(i).getLabel();
				outLayerError=outLayerOutput*(1-outLayerOutput)*(trueLabel-outLayerOutput);
				//2.2 Backpropagate  the error in the hidden layer
				for(int j=0;j<hidLayerNodesNum;j++){
					hidLayerErrors[j]=hidLayerOutput[j]*(1-hidLayerOutput[j])*outLayerError*outLayerWeights[j];
				}


				//3.update the weights and bias 
				//3.1 update the weights and bias connecting the hidden layer to the output layer
				for(int j=0;j<hidLayerNodesNum;j++){

					deltaWeiOutLayer[j]=learningRate*outLayerError*hidLayerOutput[j];
					outLayerWeights[j] += deltaWeiOutLayer[j];

				}
				deltaBiasOutLayer=learningRate*outLayerError;
				outLayerBias += deltaBiasOutLayer;


				//3.2update the weights connecting the input layer to the hidden layer
				for(int j=0;j<hidLayerNodesNum;j++){
					deltaBiasHidLayer[j]=learningRate*hidLayerErrors[j];
					hidLayerBiases[j] += deltaBiasHidLayer[j];
					for(int k=0;k<inputDimension;k++){
						deltaWeiHidLayer[j][k]=learningRate*hidLayerErrors[j]*inputX[k];
						hidLayerWeights[j][k] += deltaWeiHidLayer[j][k];

					}
				}// end 3.2

			}// end for

			epoch++;
		}//end while


	}

	// return the predicted labels of "testData" by using the trained neural network
	public  List<Integer>  getPredictedResults(List<DataEntry> testData){
		List<Integer> predictedResults=new ArrayList<Integer>();
		for(int i=0;i<testData.size();i++){
			double[] newX=testData.get(i).getX();
			int newY=testData.get(i).getLabel();
			int predictY=this.classify(newX);
			predictedResults.add(predictY);
		}		
		return predictedResults;
	}

	// predict a new example "instanceX" by using the trained neural network
	public int classify(double[] instanceX){
		int label=0;
		// feed forward to hidden layer
		double[] hidLayerOutput=new double [hidLayerNodesNum];
		for(int i=0;i<hidLayerNodesNum;i++){
			// input for each hidden layer node is (wi*x)+bi
			double hidLayerInputI=dotProduct(hidLayerWeights[i],instanceX)+hidLayerBiases[i];

			// use sigmoid function to produce the output
			hidLayerOutput[i]=sigmoidActivation(hidLayerInputI);
		}

		// feed forward to output layer, we only have one node. 
		double outLayerInput=dotProduct(hidLayerOutput,outLayerWeights)+outLayerBias;
		double outLayerOutput=sigmoidActivation(outLayerInput);
		label= (outLayerOutput>0.5? 1 : 0);
		return label;
	}

	// return the results of sigmoid(x)
	private double sigmoidActivation(double x) {
		return 1 / (1 + Math.exp(-x));
	}

	// return the results of dot product of two vectors :  x1*x2
	// we assume the dimension of two product 
	private double dotProduct(double[] x1, double[] x2){
		int Dimension=x1.length;
		double dotProduct=0;
		// dot product
		for(int i=0;i<Dimension;i++){
			dotProduct += x1[i]*x2[i];
		}
		return dotProduct;
	}


}
