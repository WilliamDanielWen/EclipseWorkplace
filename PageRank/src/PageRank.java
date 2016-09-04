
import Jama.*;
import java.util.Arrays;


public class PageRank {
	public static void main(String[] args) {
		double beta=0.8d;
		double[][] colStochasticMatrix={
				 { 0,   0,    0, 0.5, 0, 0}
				,{ 0,   0, 0.25,   0, 0, 1}
				,{ 0.5, 1,    0,   0, 0, 0}
				,{ 0.5, 0, 0.25, 0.5, 0, 0}
				,{ 0,   0, 0.25,   0, 0, 0}
				,{ 0,   0, 0.25,   0, 1, 0}
		};


		//pageRankByPowerIterationMethod(colStochasticMatrix,beta);
		
		//pageRankByEigenValueMethod(colStochasticMatrix,beta);
		
		// query node is  the first node
		personalizedPageRankByPowerIterationMethod(colStochasticMatrix,beta,1);
		personalizedPageRankByEigenValueMethod(colStochasticMatrix,beta,1);
	}

	public static void pageRankByPowerIterationMethod(double[][] colStochasticMatrix, double beta){
		System.out.println("\n\n########  page rank by power iteration #######");
		Matrix M=new Matrix(colStochasticMatrix);
		double N=(double)M.getRowDimension();

		Matrix telepoConstanVector=new Matrix((int)N,1,1d/N);

		// initilization of r
		Matrix r_init=telepoConstanVector.copy();

		telepoConstanVector=telepoConstanVector.times(1-beta);

		Matrix r=primalEignVectorByPowerIteration(M,beta,r_init,telepoConstanVector);
		System.out.println("\n page rank by power iteration result");
		System.out.println("r="+Arrays.toString(r.getColumnPackedCopy()));

	}
	
	public static void personalizedPageRankByPowerIterationMethod(double[][] colStochasticMatrix, double beta,int queryNum){
		System.out.println("\n\n######## personalized  page rank by power iteration #######");
		Matrix M=new Matrix(colStochasticMatrix);
		double N=(double)M.getRowDimension();

		// other as 
		Matrix telepoConstanVector=new Matrix((int)N,1,0);
		telepoConstanVector.set(queryNum-1, 0, 1);
		
		// initilization of r
		Matrix r_init=telepoConstanVector.copy();

		telepoConstanVector=telepoConstanVector.times(1-beta);

		Matrix r=primalEignVectorByPowerIteration(M,beta,r_init,telepoConstanVector);
		System.out.println("\n personalized  page rank by power iteration result:");
		System.out.println("r="+Arrays.toString(r.getColumnPackedCopy()));

	}


	
	// get the first eigen vector and then normlized the vector to 
	public static void pageRankByEigenValueMethod(double[][] colStochasticMatrix, double beta){
		System.out.println("\n\n########  page rank by eigenvector decomposition #######");
		Matrix M= new Matrix(colStochasticMatrix);

		double N=(double)M.getRowDimension();

		// the constant n*n Matrix, each element eqaul to 1/n
		Matrix telepoConstanMatrix=new Matrix((int)N,(int)N,1/N).times(1-beta);

		Matrix r=primalEignVectorByEignDecompo(M,beta,telepoConstanMatrix);
		System.out.println("\n  page rank by eigenvector decomposition result");
		System.out.println("r="+Arrays.toString(r.getColumnPackedCopy()));

	}
	
	public static void personalizedPageRankByEigenValueMethod(double[][] colStochasticMatrix, double beta,int queryNum){
		System.out.println("\n\n######## personalized page rank by eigenvector decomposition #######");
		Matrix M= new Matrix(colStochasticMatrix);

		double N=(double)M.getRowDimension();

		// the constant n*n Matrix, each element eqaul to 1/n
		Matrix telepoConstanMatrix=new Matrix((int)N,(int)N,0);
		for(int i=0;i<N;i++){
			// the value of teleport matrix should be 1 at row stands for the query node
			// its correponding constant should multiply (1-beta)
			telepoConstanMatrix.set(queryNum-1, i, (1-beta));
		}

		Matrix r=primalEignVectorByEignDecompo(M,beta,telepoConstanMatrix);
		System.out.println("\n  page rank by eigenvector decomposition result");
		System.out.println("r="+Arrays.toString(r.getColumnPackedCopy()));

	}

	
	
	public static Matrix primalEignVectorByPowerIteration(Matrix M,double beta,Matrix r_init,Matrix telepoConstanVector){
		Matrix r=r_init;
		while(true){
			double error=0d;
			Matrix r_new=M.times(r).times(beta).plus(telepoConstanVector);
			double[] value_new=r_new.getColumnPackedCopy();
			double[] value_old=r.getColumnPackedCopy();
			for (int i=0;i<value_new.length;i++){
				error += Math.abs(value_new[i]-value_old[i]);
			}
			if(error==0) break;
			r=r_new;
		}
		return r;
	}
	
	public static Matrix primalEignVectorByEignDecompo(Matrix M, double beta,Matrix telepoConstanMatrix){
		// input matrix
		Matrix A= M.times(beta).plus(telepoConstanMatrix);
		//System.out.println("\n the matrix A: ");
		//A.print(A.getRowDimension(),A.getColumnDimension());

		// get EigenvalueDecomposition
		EigenvalueDecomposition EignDecom=A.eig();

		// get eigenvalues
		double[] eigenValues=EignDecom.getRealEigenvalues();
		//System.out.println("\nEignValues: ");
		//System.out.println(Arrays.toString(eigenValues)+"\n");

		// the eigenvector matrix
		// each eigenvector are stored as column vectors.
		// Eigenvectors are sorted from left to right in an ascending order. 
		// i.e. the leftmost column is the eigenvector corresponds to the smallest eigenvalue
		// i.e. the second leftmost column is the eigenvector corresponds to the second smallest eigenvalue
		Matrix EignVecMatrix=EignDecom.getV();
		//System.out.println("\n the EignVecMatrix: ");
		//EignVecMatrix.print(EignVecMatrix.getRowDimension(),EignVecMatrix.getColumnDimension());

		// primal eigen vector
		Matrix r=EignVecMatrix.getMatrix(0,EignVecMatrix.getRowDimension()-1, 0, 0);

		Matrix multi=A.times(r).transpose();
		//System.out.print("(Before normalization) A*r=");
		//multi.print(multi.getRowDimension(), multi.getColumnDimension());



		// normalize r
		double[] r_normed=r.getColumnPackedCopy();

		// change into distribution
		double sum=0d;
		for(int i=0;i<r_normed.length;i++){
			sum += r_normed[i];
		}
		for(int i=0;i<r_normed.length;i++){
			r_normed[i] /= sum;
		}
		
		r=new Matrix(r_normed,r_normed.length);
		//overwrite
		multi=A.times(r).transpose();
		System.out.print("\n(After normalization) A*r=");
		multi.print(multi.getRowDimension(), multi.getColumnDimension());
		
		return r;
	}
}




