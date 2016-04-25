package Logistic;
import Utilities.*;
import java.util.Arrays;
import java.util.List;
import Jama.Matrix;
/**
 * Created with IntelliJ IDEA.
 * User: tpeng
 * Date: 6/22/12
 * Time: 11:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class Logistic {

	/** the learning rate */
	private double rate;

	/** the weight to learn */
	private double[] weights;

	private double bias; 

	/** the number of iterations */
	private int ITERATIONS = 20000;

	private double EPSI = Double.longBitsToDouble(971l << 52);

	public Logistic(int n) {
		this.rate =1;// in this implementation, the update rate is 1
		this.weights =  new double[n];
		//initialize weights 
		for(int i=0;i<this.weights.length;i++){
			this.weights[i]=0d;
		}
		this.bias = 0d;
	}

	private double sigmoid(double z) {
		return 1 / (1 + Math.exp(-z));
	}

	/**  @params: trainning data set
	 *  update the weights and bias to maximize the L2 regularized log likelihood function
	 */
	public void train(List<Instance> instances) {

		for (int n=0; n<ITERATIONS; n++) {
			
			//store the previous weights;
			Matrix weightsVector=new Matrix(this.weights,this.weights.length);
			// use betaVector to store both the previous weithts and  bias
			Matrix betaVector=new Matrix(this.weights.length+1,1,20d);
			betaVector.setMatrix(1, betaVector.getRowDimension()-1, 0, 0, weightsVector);
			betaVector.set(0, 0, this.bias);// store the bias as beta_0

			double lik = 0.0;
			double []predictVec = new double[instances.size()];

			for (int i=0; i<instances.size(); i++) {
				double[] x = instances.get(i).getX();
				double predicted = classify(x);
				predictVec[i] = predicted;
			}


			/****************Please Fill Missing Lines Here*****************/
			/* Here,I use L2 regularization, the conduction of the formular for updating parameters
			   are in the solution document, please refer to it */
			double lambda=0.3; // L2 regularization parameter, default value is 0.3

			//1.calculate the gradient of the L2 regularized  log likelihood function with respect to beta
			//store the training example xi=(xi1,xi2..xi6) and add one intercept term xi0=1;
			Matrix extendedX_i=new Matrix(1,this.weights.length+1,1d);
			//the first component of L2Gradient  is the partial derivative of the bias
			Matrix L2Gradient=new Matrix(extendedX_i.getColumnDimension(), 1, 0d);
			//L2Gradient has extendedX_i.getColumnDimension() components, calculate each of them respectively
			for(int j=0;j<=extendedX_i.getColumnDimension()-1;j++){
				//l2_gra_j denots the compoments with index j in l2Gradient, namely the (j+1)th component
				double L2_gra_j=0;
				double beta_j=betaVector.get(j,0);
				for (int i=0; i<=instances.size()-1; i++) {// look all the training examples; 
					Matrix x_i=new Matrix(instances.get(i).getX(),1);//store the x of instance
					// override from x1 to x6, intercept term x0 remians as 1
					extendedX_i.setMatrix(0,0,1,extendedX_i.getColumnDimension()-1,x_i);
					double y_i=(double)instances.get(i).getLabel();
					double sigmoid_i=predictVec[i];// use the previously calculated result
					double x_ij=extendedX_i.get(0, j);

					/* firstly, store the value of the summation of x_ij*(y_i-sigmoid(beta^T*x_i)) 
                       for i=0 to instances.size()-1*/
					L2_gra_j += x_ij*(y_i -sigmoid_i);

				}
				// secondly, substract the value of 2*lambda*beta_j
				// gra_j is the summation of x_ij*(y_i-sigmoid(beta^T*x_i)) then subtract -2*lambda*beta_j
				L2_gra_j-=2*lambda*beta_j;
				L2Gradient.set(j, 0, L2_gra_j);
			}


			//2. calculate the Hessian Matrix of the L2 regularized log likelihood function with respect to beta
			Matrix L2Hessian=new Matrix(extendedX_i.getColumnDimension(),extendedX_i.getColumnDimension(),0d);
			/* L2Hessian has (extendedX_i.getColumnDimension())*(extendedX_i.getColumnDimension()) elements,
			    calculate each of element in L2Hessian respectively	 */
			for(int j=0;j<=extendedX_i.getColumnDimension()-1;j++){
				for(int k=0;k<=extendedX_i.getColumnDimension()-1;k++){
					/* L2_hess_j_k denotes the element with index (j,k) in l2Hessian
            		  , namely the element in (j+1)th row, (k+1)th column*/
					double L2_hess_j_k=0; 
					// look all the training set to calculate l2_hess_j_k
					for(int i=0;i<=instances.size()-1;i++){
						//store the x of instance
						Matrix x_i=new Matrix(instances.get(i).getX(),1);
						// set the value from x1 to x6, intercept term x0 remians as 1
						extendedX_i.setMatrix(0,0,1,extendedX_i.getColumnDimension()-1,x_i);
						double sigmoid_i=predictVec[i];
						double x_ij=extendedX_i.get(0, j);
						double x_ik=extendedX_i.get(0, k);
						// calculate the element with row index of j, column index of k
						/* firstly, store the summation of x_ij*x_ik*sigmoid_i*(1-sigmoid_i) into L2_hess_j_k for all i
						   from 0 to instances.size()-1*/
						L2_hess_j_k -=x_ij*x_ik*sigmoid_i*(1-sigmoid_i);
					}// end for(int i=0....)
					/* secondly, subtract a portion of identity matrix, we only subtract this 
            		   value when we encounter the elements on the diagonal of Hessian matrix*/
					if(k==j) L2_hess_j_k-=2*lambda;
					//set L2_hess_j_k to L2Hessian
					L2Hessian.set(j, k, L2_hess_j_k);
				}// end  for(int k=0...
			}// end for(int j=0....

			//update weights and bias
			Matrix prev_betaVector=betaVector; // store the previous beta
			Matrix negL2Hessian=L2Hessian.inverse();
			Matrix updateTerm=negL2Hessian.times(L2Gradient).times(this.rate); 
			// update the betaVector
			betaVector=prev_betaVector.minus(updateTerm);

			// write the new value into weights and bias
			this.bias=betaVector.get(0, 0);
			Matrix weights_vallue=betaVector.getMatrix(1, betaVector.getRowDimension()-1, 0, 0);
			this.weights=weights_vallue.getColumnPackedCopy();

			//calculate log likelihood  
			/****************Please Fill Missing Lines Here*****************/
			for(int i=0;i<instances.size(); i++){
				double y_i=instances.get(i).getLabel();
				double sigmoid_i=predictVec[i];
				lik += y_i*Math.log(sigmoid_i) + (1-y_i)*Math.log(1-sigmoid_i);
			}

			// calculate the L2 regularized log likelihood
			double l2ReguLogLikelyHood= lik;			
			for(int j=0;j<=betaVector.getRowDimension()-1;j++){
				l2ReguLogLikelyHood -= lambda*Math.pow(betaVector.get(j, 0), 2);
			}


			// evaluate whether terminates the iteration by using one norm of the l2 gradient
			double one_norm=0;
			for (int j=0; j<= L2Gradient.getRowDimension()-1;j++){
				one_norm += Math.abs(L2Gradient.get(j, 0));
			}

			// check if converge
			double precision_threshhold=Math.pow(10, -13);
			if (one_norm<precision_threshhold){
				System.out.println("Iteration number in this fold is: " + n );
				System.out.println("L2-Gradient-One-Norm= " +one_norm+", L2-Gradient="+ Arrays.toString(L2Gradient.getColumnPackedCopy()));
				System.out.println("Log-likelihood= " + lik+ ", L2-regularized-log-likelihood= " + l2ReguLogLikelyHood);
				System.out.println("Trained bias="+this.bias+"Weights= " + Arrays.toString(weights) );
				break; 
			}


		}// end for (int n=0; n<ITERATIONS; n++)...

	}


	public double classify(double[] x) {
		double logit = bias;
		for (int i=0; i<weights.length;i++)  {
			logit += weights[i] * x[i];
		}
		return sigmoid(logit);
	}


}
