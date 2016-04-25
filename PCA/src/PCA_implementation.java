
import Jama.*;
import java.util.Arrays;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class PCA_implementation {
	public static void main(String[] args) throws IOException {
		System.out.println("Reading the input file ...\n");
		Matrix trainInput=getDataMatrix("datasets\\digits\\train.csv");
		Matrix X = trainInput.getMatrix(0, trainInput.getRowDimension() - 1, 1, trainInput.getColumnDimension() - 1);
		// input dimension before dimension reduction
		double n=X.getColumnDimension();
		// label in the first column
		Matrix Y = trainInput.getMatrix(0, trainInput.getRowDimension() - 1, 0,0);

		
		
		System.out.println("Calculating S'=(1/N)*X^T*X...\n");
		Matrix S=X.transpose().times(X);
		double divider=1.0/n;
		S=S.times(divider);
		
		System.out.println("Calculating eigienvector of S...\n");
		EigenvalueDecomposition EignDecom=S.eig();

		// get eigenvalues
		double[] eigenValues=EignDecom.getRealEigenvalues();
		
		/* 
		 * the eigenvalue diagonal matrix
		 * eigenvalues are sorted in diagonal from top left to bottom right in an ascending order 
		 */ 
		Matrix Lambda=EignDecom.getD();
		
		/*
		 *  the eigenvector matrix
		 * each eigenvector are stored as column vectors. 
		 * Eigenvectors are sorted from left to right in an ascending order.
		 * i.e. the leftmost column is the eigenvector corresponds to the smallest eigenvalue 
		 * i.e. the second leftmost column is the eigenvector corresponds to the second smallest eigenvalue
	     */
		Matrix E=EignDecom.getV();
		
		
		Matrix principal_first=E.getMatrix(0, E.getRowDimension()-1, E.getColumnDimension()-1, E.getColumnDimension()-1);
		double lamda_1=eigenValues[eigenValues.length-1];
		Matrix multi_left_1=S.times(principal_first);
		Matrix multi_right_1=principal_first.times(lamda_1);
		
		Matrix principal_second=E.getMatrix(0, E.getRowDimension()-1, E.getColumnDimension()-2, E.getColumnDimension()-2);
		double lamda_2=eigenValues[eigenValues.length-2];
		Matrix multi_left_2=S.times(principal_second);
		Matrix multi_right_2=principal_second.times(lamda_2);
		
		
		
		
		
		

		int breakpoint=0;
	}


	
	
	
	
	//Reads the data form CSV files and converts it into Matrix data
	public static Matrix getDataMatrix(String filePath) throws IOException {
		List<double[]> listData = new ArrayList<>();
		String line;
		boolean flag = true;

		BufferedReader br = new BufferedReader(new FileReader(filePath));
		while ((line = br.readLine()) != null) {
			// To skip the first row in the files
			if(flag == true) {
				flag=false;
				continue;
			}
			String rowData[] = line.split(",");
			double rowDoubleData[] = new double[rowData.length];

			// To convert String data into double
			for (int i = 0; i < rowData.length; ++i) {
				rowDoubleData[i] = Double.parseDouble(rowData[i]);
			}
			listData.add(rowDoubleData);
		}
		int data_cols = listData.get(0).length;
		int data_rows = listData.size();

		Matrix matrixData = new Matrix(data_rows, data_cols);

		// storing data in Matrix
		for (int r = 0; r < data_rows; r++) {
			for (int c = 0; c < data_cols ; c++) {
				matrixData.set(r, c, listData.get(r)[c]);
			}
		}
		return matrixData;
	}

}






