

import Jama.Matrix;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

import java.lang.Math;
/**     
 * Simple Linear Regression implementation
 */

public class LinearRegression {
	public static void main(String[] args) throws Exception{
		LinearRegression lr = new LinearRegression();
        lr.linearRegression();
	}
	
	public  void linearRegression() throws Exception {
		Matrix trainingData = Utility.getDataMatrix("data\\linear_regression\\linear-regression-train.csv");

		// getMatrix(Initial row index, Final row index, Initial column index, Final column index)
		Matrix train_x = trainingData.getMatrix(0, trainingData.getRowDimension() - 1, 0, trainingData.getColumnDimension() - 2);
		Matrix train_y = trainingData.getMatrix(0, trainingData.getRowDimension()-1, trainingData.getColumnDimension()-1, trainingData.getColumnDimension()-1);

		Matrix testData = Utility.getDataMatrix("data\\linear_regression\\linear-regression-test.csv");
		Matrix test_x = testData.getMatrix(0, testData.getRowDimension() - 1, 0, testData.getColumnDimension() - 2);

		//z-score normalization


		// merge matrix
		int train_rows=train_x.getRowDimension();
		int test_rows=test_x.getRowDimension();
		int extended_rows=train_rows+test_rows;
		int cols=train_x.getColumnDimension();
		Matrix whole_x= new Matrix(extended_rows,cols,0); //default value is 0
		whole_x.setMatrix(0,train_rows-1,0,cols-1,train_x);
		whole_x.setMatrix(train_rows,extended_rows-1,0,cols-1,test_x);



		//calculate the mean and the standard deviation
		double[][] data=whole_x.getArrayCopy(); 
		//int r=data.length; ==2000
		// int colum=data[0].length;=100


		double[] means = new double[cols];
		for(int c=0;c<cols;c++){// column
			for(int r=0;r<extended_rows;r++){ //rows
				means[c] += data[r][c];
			}
			means[c] = means[c]/extended_rows; //means of one column
		}

		double[] deviations =new double[cols];
		for (int c=0;c<cols;c++){
			for(int r=0;r<extended_rows;r++){
				deviations[c] += (data[r][c]-means[c])*(data[r][c]-means[c]);
			}
			deviations[c] = deviations[c]/extended_rows;
		}

		//normalize the  x data in training and testing
		for(int c=0;c<cols;c++){

			// the training_x 
			for(int r1=0;r1<train_rows;r1++){
				double val=train_x.get(r1, c);
				val=(val-means[c])/deviations[c];
				train_x.set(r1, c, val);
			}

			// the test sets
			// the training_x 
			for(int r2=0;r2<test_rows;r2++){
				double val=test_x.get(r2, c);
				val=(val-means[c])/deviations[c];
				test_x.set(r2, c, val);
			}

			// end one normalization of one attribute
		}        	


		/* Linear Regression */
		/* 2 step process */
		// 1) find beta
		Matrix beta = getBetaByBatchGradientDescent(train_x, train_y);
		// 2) predict y for test data using beta calculated from train data

		// we should also extend test_x instead of just using Matrix predictedY = test_x.times(beta);
		// I modified these part of code to insert intercept term on test_x
		Matrix extendedTestX = new Matrix(test_x.getRowDimension(),1+test_x.getColumnDimension(),1.0);
		int ext_rows=extendedTestX.getRowDimension();
		int ext_cols=extendedTestX.getColumnDimension();
		extendedTestX.setMatrix(0,ext_rows-1,1,ext_cols-1,test_x);


		Matrix predictedY = extendedTestX.times(beta);

		// Output
		printOutput(predictedY);

		// output beta and MSE
		Matrix test_y = testData.getMatrix(0, testData.getRowDimension()-1, testData.getColumnDimension()-1, testData.getColumnDimension()-1);
		printMseBeta(predictedY,test_y,beta);
	}

	/**  @params: X and Y matrix of training data
	 * returns value of beta calculated using the formula 
	 * closed form: beta = (X^T*X)^ -1)*(X^T*Y)
	 */
	private static Matrix getBetaByClosedForm(Matrix trainX, Matrix trainY) {

		/****************Please Fill Missing Lines Here*****************/


		//construct the intercept term
		Matrix extendedTrainX= new Matrix(trainX.getRowDimension(),1+trainX.getColumnDimension(),1); //default value is 1
		int ext_rows=extendedTrainX.getRowDimension();
		int ext_cols=extendedTrainX.getColumnDimension();
		extendedTrainX.setMatrix(0,ext_rows-1,1,ext_cols-1,trainX);


		//closed form solution (default version)
		Matrix beta=(extendedTrainX.transpose().times(extendedTrainX).inverse()); //(X^T*X)^ -1)
		beta=beta.times(extendedTrainX.transpose().times(trainY)); //(X^T*X)^ -1)*(X^T*Y)
		return beta;




	} 
	/**  @params: X and Y matrix of training data
	 * returns value of beta calculated using the formula 
	 *  batch gradient decent
	 */
	private static Matrix getBetaByBatchGradientDescent(Matrix trainX,Matrix trainY){

		//construct the intercept term
		Matrix extendedTrainX= new Matrix(trainX.getRowDimension(),1+trainX.getColumnDimension(),1); //default value is 1
		int ext_rows=extendedTrainX.getRowDimension();
		int ext_cols=extendedTrainX.getColumnDimension();
		extendedTrainX.setMatrix(0,ext_rows-1,1,ext_cols-1,trainX);

		//2. batch gradient decent
		// initial beta as a ext_cols*1 matrix with all value to be 0.
		Matrix beta=new Matrix(ext_cols,1,0.001);
		Matrix gradient=new Matrix(ext_cols,1,100d);
		//loop to update until converge
		int round=0;
		while(true){
			round++;
			Matrix old_beta=beta.copy(); // store the previous version of beta

			//1.update beta_j. Starts from beta_0 to beta_m, m is the number of attributes(ext_cols);
			for (int j=0;j<ext_cols;j++){

				//1.1 calculate the partial derivative for beta_j by looking through the entire training examples;
				double partial_deriv_j=0;
				for (int i=0;i<ext_rows;i++){
					Matrix x_i= extendedTrainX.getMatrix(i,i,0,ext_cols-1);
					double x_ij =extendedTrainX.get(i,j);
					double y_i= trainY.get(i,0);
					//hypotheses value of y_i is x_i^T*old_beta. 
					//get(0,0): transform from matrix form it into double number.
					double hypo_val_i=x_i.times(old_beta).get(0,0);
					//partial derivative for beta_j is the summation of (beta^T*x_i-y_i)*x_ij
					partial_deriv_j += (hypo_val_i-y_i)*x_ij;
				}// end calculation of  partial_deriv_j

				// 1.2update beta_j
				double learning_rate=0.00001;
				double old_beta_j = old_beta.get(j, 0);
				double new_beta_j =	old_beta_j - learning_rate*	partial_deriv_j;
				beta.set(j,0,new_beta_j);
				gradient.set(j, 0, partial_deriv_j);
			}// end for loop to update from  beta_0 to beta_m

			//2.calculate the euclidean distance between old_beta and beta
			double square_err_sum=0;
			for (int i=0; i< ext_cols;i++){
				square_err_sum += Math.pow(beta.get(i,0)-old_beta.get(i,0),2);
			}
			double euclid_dist=Math.sqrt(square_err_sum);

			// check if converge
			double precision_threshhold=0.002;
			if (euclid_dist<precision_threshhold) 
				return beta; 

			// print some interaction information since the calculation takes some time
			int estimation_rounds=538;
			System.out.println("round_"+round+" : euclid_dist between two betas is "+euclid_dist+ " estmation of rounds to iterate: "+(estimation_rounds-round));


			// if not converge began next round of iteration

		}// end batch gradient decent implementation

	}
	
	/**  @params: X and Y matrix of training data
	 * returns value of beta calculated using the formula 
     * stochastic gradient decent
	 */
	private static Matrix getBetaByStochasticGradientDescent(Matrix trainX,Matrix trainY){

		//construct the intercept term
		Matrix extendedTrainX= new Matrix(trainX.getRowDimension(),1+trainX.getColumnDimension(),1); //default value is 1
		int ext_rows=extendedTrainX.getRowDimension();
		int ext_cols=extendedTrainX.getColumnDimension();
		extendedTrainX.setMatrix(0,ext_rows-1,1,ext_cols-1,trainX);


		//stochastic gradient decent
		// initial beta as a ext_cols*1 matrix with all value to be 0.
		Matrix beta=new Matrix(ext_cols,1,0.001);
		//loop to update until converge
		int round=0;
		while(round<ext_rows){

			Matrix old_beta=beta.copy(); // store the previous version of beta

			//get a new training example
			Matrix x= extendedTrainX.getMatrix(round,round,0,ext_cols-1);
			double y= trainY.get(round,0);
			//hypotheses value of y is x^T*old_beta
			//get(0,0): transform from matrix form it into double number.
			double hypo_val=x.times(old_beta).get(0,0);

			//1. update beta_j. Starts from beta_0 to beta_m, m is the number of attributes(ext_cols);
			for (int j=0;j<ext_cols;j++){
				//1.1calculate the partial derivative for beta_j by looking at the new training example;
				//partial derivative for beta_j is (old_beta^T*x-y)*x_j
				double x_j =extendedTrainX.get(round,j);
				double partial_deriv_j = (hypo_val-y)*x_j;

				// 1.2update beta_j
				double learning_rate=0.001;
				double old_beta_j = old_beta.get(j, 0);
				double new_beta_j = old_beta_j - learning_rate*partial_deriv_j;
				beta.set(j,0,new_beta_j);
			}

			//2.calculate the euclidean distance between old_beta and beta
			double square_err_sum=0;
			for (int i=0; i< ext_cols;i++){
				square_err_sum += Math.pow(beta.get(i,0)-old_beta.get(i,0),2);
			}
			double euclid_dist=Math.sqrt(square_err_sum);

			// check if converge
			double precision_threshhold=0.000001;
			if (euclid_dist<precision_threshhold) return beta; 

			// print some interaction information
			System.out.println("round_"+round+" : euclid_dist between two betas is "+euclid_dist);


			// if not converge began next round of iteration
			round++;
		}

		return beta;
		// end stochastic gradient decent implementation


	}

	
	/**
	 * @params: predicted Y matrix
	 * outputs the predicted y values to the text file named "linear-regression-output"
	 */
	public static void printOutput(Matrix predictedY) throws IOException {
		FileWriter fStream = new FileWriter("output\\linear_regression\\linear-regression-output.txt");     // Output File
		BufferedWriter out = new BufferedWriter(fStream);
		for (int row =0; row<predictedY.getRowDimension(); row++) {
			out.write(String.valueOf(predictedY.get(row, 0)));
			out.newLine();
		}
		out.close();
	}

	/**
	 * @params: predicted Y matrix, test Y matrix, the Matrix beta we calculated  
	 * outputs the trained Beta and MSE values to the text file named "Beta_MSE_Report"
	 */
	public static void printMseBeta(Matrix predictedY, Matrix testY, Matrix Beta) throws IOException{
		double mse=0;
		int predic_rows=predictedY.getRowDimension();
		FileWriter fStream = new FileWriter("output\\linear_regression\\Beta-MSE-Report.txt");     // Output File
		BufferedWriter out = new BufferedWriter(fStream);

		if (predic_rows ==0 | predic_rows != testY.getRowDimension() ) {
			out.write("predictedY didn't have the same dimention as testY"); // error dimension
			out.close();
		}

		else{
			// calculate the sum of square errors
			double sum_sq_errors=0;
			for(int i=0; i<predic_rows; i++){
				sum_sq_errors = sum_sq_errors + Math.pow(testY.get(i, 0)-predictedY.get(i, 0),2);
			}

			// calculate the mean square error(MSE)
			mse =sum_sq_errors/predic_rows;

			//output MSE
			out.write("the mean square error(MSE) between this predictedY and trainY is:  ");
			out.write(String.valueOf(mse));
			out.newLine();out.newLine();

			//output the trained Beta vector
			out.write("The beta vector trained is ");
			out.newLine();
			for (int row =0; row<Beta.getRowDimension(); row++) {
				out.write(String.valueOf(Beta.get(row, 0)));
				out.newLine();
			}
			out.close();
		}


	}
}
