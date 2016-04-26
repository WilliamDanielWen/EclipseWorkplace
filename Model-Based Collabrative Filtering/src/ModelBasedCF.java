import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;

public class ModelBasedCF {

	public static void main(String[] args) throws IOException{
		String inputPath="data//u.data";
		
		// select dimension of 
		//String outputPathSelectLatentDimension=inputPath+"_LatentDimSelection.txt";
		//selectLatentDimension(1, 50,inputPath,outputPathSelectLatentDimension);
		
		
		// best dimension of latent vector is 3, which is suggested 
		// by the result of runnnig parameter selection from 1 to 50
		ModelBasedCF model=new ModelBasedCF(3);
		
		// output path for RMSE and MAE
		String outputPathCrossValidation=inputPath+"_CrossValidationResult.txt";
		FileWriter fwriter=new FileWriter(outputPathCrossValidation);
		BufferedWriter out =new BufferedWriter(fwriter);
		// run a 5-fold cross validation and get the average RMSE and MAE 
		int foldNum=5;
		model.corssValidation(inputPath,foldNum,out);
		out.close();
	}
	
	// parameter for regularization
	private double lambda; 

	// learning rate
	private double eta; 

	// dimension of latent vector
	private int latentDimension;

	// latent user matrix implemented by sparse matrix.  Format: <row number, corresponding row vector>
	private Hashtable<Integer,double[]> userMatrix;

	// latent item matrix implemented by sparse matrix.  Format: <column number, corresponding column vector>
	private Hashtable<Integer,double[]> itemMatrix;

	// upper lower bound for scores in training data
	private double predictionUpperBound;
	private double predictionLowerBound;

	//precision threshhold to determine whether the algorithm converges
	private double convergeThreshold;
	
	// number of iterations between two times of checking whether the algorithm converges
	private double convergeCheckWindowSize;

	//constructor function
	public ModelBasedCF(int dim){
		latentDimension=dim;
	}
	
	
	// function used for selecting proper dimension of latent vector
	// by trying different values between lowerRange and upperRange
	// with each value, a model will be trained and validated by using 5-fold cross validation and output the average RMSE and MAE 
	private static void selectLatentDimension(int lowerRange, int upperRange,String inputPath,String outputPath) throws IOException{
		
		FileWriter fwriter=new FileWriter(outputPath);
		BufferedWriter out =new BufferedWriter(fwriter);
		for(int dim=lowerRange;dim<=upperRange;dim++){
			System.out.println("\n########### Latent Dimension = "+dim+" #########");
			ModelBasedCF model=new ModelBasedCF(dim);
			// run a 5-fold cross validation to select the parameter 
			int foldNum=5;
			out.write("Latent dimension = "+dim+",\t"); 
			model.corssValidation(inputPath,foldNum,out);
		}
		out.close();
	}
	
	// run a k-fold cross validation
	private void corssValidation(String inputPath,int totalFolds,BufferedWriter outWriter) throws IOException{
		// read the data
		List<Rating> inputSet=Utility.readData(inputPath);
		
		double rmseSum=0;
		double maeSum=0;
		for(int k=1;k<=totalFolds;k++){
			System.out.println("######## Fold "+k+" begins!########");
			List<Rating> trainSet=Utility.getTrainSetAtKFold(inputSet, 1, 5);
			List<Rating> testSet=Utility.getTestSetAtKFold(inputSet, 1, 5);
			
			// training
			training(trainSet);
			
			// validating
			rmseSum += getRMSE(testSet);
			maeSum += getMAE(testSet);
		}
		
		//output
		double rmse=rmseSum/(double)totalFolds;
		System.out.println("\nAverage RMSE is "+ rmse);

		double mae=maeSum/(double)totalFolds;
		System.out.println("Average MAE is "+ mae);

		// output to file
		outWriter.write("RMSE = "+rmse+",\t MAE = "+mae);
		outWriter.newLine();
		
	}

	//initilize the parameters
	private void initlization(List<Rating> trainSet){

		lambda=0.0001;
		eta=0.032; // optimal 0.032 const

		predictionLowerBound=1;
		predictionUpperBound=5;

		convergeThreshold=0.0000001;
		
		// for every "convergeCheckWindowSize" number updates
		//  , calculate the cost function to  check if converges
		convergeCheckWindowSize=trainSet.size()*0.3;  

		// init bound for every p_u[] and q_i[]
		double lower_bound_init=1;
		double upper_bound_init=1.15;

		userMatrix=new Hashtable<Integer,double[]>();
		itemMatrix=new Hashtable<Integer,double[]>();

		for(int j=0;j<trainSet.size();j++){

			int u=trainSet.get(j).user_id;
			int i=trainSet.get(j).item_id;

			if(!userMatrix.containsKey(u)){
				double[] p_u=new double[latentDimension];
				for(int k=0;k<p_u.length;k++){
					// a random number between 0 and initUpperBound
					p_u[k]=Utility.randomDouble(lower_bound_init,upper_bound_init);

				}
				userMatrix.put(u, p_u);// put into u_th row 
			}

			if(!itemMatrix.containsKey(i)){
				double[] q_i=new double[latentDimension];
				for(int k=0;k<q_i.length;k++){
					// a random number between 0 and initUpperBound

					q_i[k]=Utility.randomDouble(lower_bound_init,upper_bound_init);
				}
				itemMatrix.put(i, q_i);// put int i_th column
			}

		}

	}

	// train the model
	public void training(List<Rating> trainSet) throws FileNotFoundException {
		System.out.println("Training begins... ");
		
		// initilize parameters
		initlization(trainSet);

		// cost function rated variables
		double cost_old=getCost(trainSet);
		double cost_new;
		double delta_cost;
		boolean converge=false;		
		
		
		int epoch=1;
		int updateNum=0;
		
		// number of updates performed since the last tiem check of converge
		int convergeCheckProgress=0;
		
		// loop to update userMatrix and itemMatrix until converge 
		// by using stochastic gradient descent method
		while(!converge){
			System.out.println("Epoch "+epoch+" begins.....");

			// loop on the entire training example
			for(int j=0;j<trainSet.size();j++){

				int u=trainSet.get(j).user_id;
				int i=trainSet.get(j).item_id;
				double true_score=trainSet.get(j).score;


				double predicted_score=predict(u,i);
				double[] p_u=userMatrix.get(u);
				double[] q_i=itemMatrix.get(i);
				
			
				
				// update p_u
				double[] pu_delta= Utility.vectorMinus(  Utility.vectorScale(q_i,true_score-predicted_score) , Utility.vectorScale(p_u,lambda) );
				double[] pu_new=Utility.vectorPlus( p_u ,Utility.vectorScale(pu_delta,eta) );
				userMatrix.put(u, pu_new);

				
				// update q_i
				double[] qi_delta= Utility.vectorMinus(  Utility.vectorScale(p_u,true_score-predicted_score) , Utility.vectorScale(q_i,lambda) );
				double[] qi_new=Utility.vectorPlus( q_i ,Utility.vectorScale(qi_delta,eta));
				userMatrix.put(i, qi_new);


				// check if converge
				convergeCheckProgress++;
				if(convergeCheckProgress>=convergeCheckWindowSize){
					
					cost_new=getCost(trainSet);
					delta_cost=cost_new-cost_old;
					if(delta_cost<convergeThreshold){
						converge=true;
						break;
					}else{
						cost_old=cost_new;
						convergeCheckProgress=0;
					}
				}

				updateNum++;
				
			}

			epoch++;

		}
		System.out.println("Total updates: "+updateNum);
		System.out.println("Training completed! ");
	}

	// predict the score according to the user_id and item_id
	public double predict(int user_id,int item_id){
		double result=0;
		if(userMatrix.containsKey(user_id)&&itemMatrix.containsKey(item_id)){
			double[] p_u=userMatrix.get(user_id);
			double[] q_i=itemMatrix.get(item_id);
			result=Utility.dotProduct(p_u, q_i);

		}else{
			// cross validation may encounter unseen training examples
			result=randomPredict();
		}

		return result;
	}


	// ranomly generate a score from {1,2,3,4,5}
	private double randomPredict(){
		double predicted_score=0;
		predicted_score=Utility.randomInteger((int)predictionLowerBound,(int)predictionUpperBound);
		return predicted_score;
	}


	// calculate the RMSE according to given validation set
	private double getRMSE(List<Rating> testSet) throws IOException{
		double rmse=0;

		// 1. calculate the square error
		for(int j=0;j<testSet.size();j++){

			int u=testSet.get(j).user_id;
			int i=testSet.get(j).item_id;
			double predicted_score=predict(u,i);
			double true_score=testSet.get(j).score;

			rmse += Math.pow(predicted_score-true_score, 2); 
		}
		//2. get the rmse
		double n=testSet.size();
		rmse = rmse/n;
		rmse = Math.sqrt(rmse);

		return rmse;
	}


	// calculate the MAE according to given validation set
	private double getMAE(List<Rating> testSet) throws IOException{
		double mae=0;

		// 1. calculate the sum 
		for(int j=0;j<testSet.size();j++){

			int u=testSet.get(j).user_id;
			int i=testSet.get(j).item_id;
			double predicted_score=predict(u,i);
			double true_score=testSet.get(j).score;

			mae += Math.abs(predicted_score-true_score); 
		}
		//2. get the mae
		double n=testSet.size();
		mae = mae/n;

		return mae;
	}


	//get the cost function value on the given data set
	private double getCost(List<Rating> dataSet){

		double cost=0;
		for(int j=0;j<dataSet.size();j++){

			int u=dataSet.get(j).user_id;
			int i=dataSet.get(j).item_id;
			double true_score=dataSet.get(j).score;
			double predicted_score=predict(u,i);
			double[] p_u=userMatrix.get(u);
			double[] q_i=itemMatrix.get(i);

			cost += (true_score-predicted_score)*(true_score-predicted_score)+lambda*Utility.vectorSquareSum(p_u)*Utility.vectorSquareSum(q_i);
		}
		return cost;
	}



}

