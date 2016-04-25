
import Jama.*;
import java.util.Arrays;


public class EigenValueVector {
	public static void main(String[] args) {
		double[][] data1={
				{30,28}
				,{28,30}
		};

		double[][] data2={
				 { 1.5, -0.8, -0.6,  0,   -0.1,  0  }
				,{-0.8,  1.6, -0.8,  0,    0,    0  }
				,{-0.6, -0.8,  1.6, -0.2,  0,    0  }
				,{ 0,    0,   -0.2,  1.7, -0.8, -0.7}
				,{-0.1,  0,    0,   -0.8,  1.7, -0.8}
				,{ 0,    0,    0,   -0.7, -0.8,  1.5}
		};

		double[][] data3={
				 { 7, -4, -3,  0,  -0 }
				,{-4,  6, -2,  0,   0 }
				,{-3, -2,  8, -1,  -2 }
				,{ 0,  0, -1,  1,   0 }
				,{ 0,  0, -2,  0,   2 }
		};

		
		
		
		// input matrix
		Matrix A= new Matrix(data3);
		//System.out.println("\n the matrix A: ");
		//A.print(A.getRowDimension(),A.getColumnDimension());
		
		// get EigenvalueDecomposition
		EigenvalueDecomposition EignDecom=A.eig();
		


		// get eigenvalues
		double[] eigenValues=EignDecom.getRealEigenvalues();
		System.out.println("\nEignValues: ");
		System.out.println(Arrays.toString(eigenValues)+"\n");

		// the eigenvalue diagonal matrix
		// eigenvalues are sorted in diagonal from top left to bottom right in an ascending order 
		Matrix Lambda=EignDecom.getD();
		System.out.println("the eigenvalue diagonal matrix Lambda: ");
		Lambda.print(Lambda.getRowDimension(),Lambda.getColumnDimension());

		double lambda_2=eigenValues[1];
		System.out.println("\n the second smallest eigenvalue lambda_2 ="+lambda_2+"\n");

		// the eigenvector matrix
		// each eigenvector are stored as column vectors.
		// Eigenvectors are sorted from left to right in an ascending order. 
		// i.e. the leftmost column is the eigenvector corresponds to the smallest eigenvalue
		// i.e. the second leftmost column is the eigenvector corresponds to the second smallest eigenvalue
		Matrix X=EignDecom.getV();
		System.out.println("\n the eigenvector matrix X: ");
		X.print(X.getRowDimension(),X.getColumnDimension());

		int index_x2=1;// index starts from 0, so we get the second smallest eigen vector by using index 1
		Matrix x_2=X.getMatrix(0,X.getRowDimension()-1, index_x2, index_x2);
		double[][] x_2_array=x_2.getArrayCopy();

		System.out.println("eigenvector (x_2)^T (rounded to only one digit): ");
		x_2.transpose().print(x_2.getRowDimension(), x_2.getColumnDimension());


		Matrix multi1=A.times(x_2).transpose();
		System.out.println("\n(A*x_2)^T:");
		multi1.print(multi1.getRowDimension(), multi1.getColumnDimension());


		Matrix multi2=x_2.times(lambda_2).transpose();
		System.out.println("(lambda_2*x_2)^T:");
		multi2.print(multi2.getRowDimension(), multi2.getColumnDimension());




		/*		Matrix multi1=A.times(X);
		System.out.println("\nA*X:");
		multi1.print(multi1.getRowDimension(), multi1.getColumnDimension());


	    Matrix multi2=X.times(Lambda);
	    System.out.println("X*Labmda:");
	    multi2.print(multi2.getRowDimension(), multi2.getColumnDimension());*/

		int breakpoint=0;

	}


}




