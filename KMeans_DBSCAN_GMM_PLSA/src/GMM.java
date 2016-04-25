

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;


public class GMM {
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		System.out.println("\n############# GMM on dataset1:###################");
		dataSet = DataPoints.readDataSet("data//dataset1.txt");
		K = DataPoints.getNoOFLabels(dataSet);
		Collections.shuffle(dataSet,new Random(666));
		W = new double[dataSet.size()][K];
		w = new double[K];
		GMM("dataSet1");


		System.out.println("\n########### GMM on dataset2:################# ");
		dataSet = DataPoints.readDataSet("data//dataset2.txt");
		K = DataPoints.getNoOFLabels(dataSet);
		Collections.shuffle(dataSet,new Random(1));
		W = new double[dataSet.size()][K];
		w = new double[K];
		GMM("dataSet2");


		System.out.println("\n############ GMM on dataset3 ###################:");
		dataSet = DataPoints.readDataSet("data//dataset3.txt");
		K = DataPoints.getNoOFLabels(dataSet);
		Collections.shuffle(dataSet,new Random(31415));
		W = new double[dataSet.size()][K];
		w = new double[K];
		GMM("dataSet3");

	}
	public static List<DataPoints> dataSet;
	public static int K=0;
	public static double[][] mean = new double[3][2];
	public static double[][] variance = new double[3][2];
	public static double[][][] coVariance = new double[3][2][2];
	public static double[][] W;
	//class prior
	public static double[] w;



	public static void GMM(String dataSetName) throws IOException {
		List<Set<DataPoints>> clusters = new ArrayList<Set<DataPoints>>();
		int k =0;
		while(k < K) {
			Set<DataPoints> cluster = new HashSet<DataPoints>();
			clusters.add(cluster);
			k++;
		}


		//1. Intialize mean,variance,coVariance randomly
		//1.1 Initially randomly assign points to clusters
		for(int i=0;i<dataSet.size();i++) {
			clusters.get(i%K).add(dataSet.get(i));			
		}
		// Initialize class prior
		for(int m=0; m< K; m++) {
			w[m] = 1.0/K;
		}
		// 1.2 Initialize mean, variance,coVariance according to random clusters
		initilizeMean(clusters);
		initilizeVariance(clusters);
		initilizeCovariance(clusters);

		// Perform EM algorithm to optimize the log-likelihood
		int iteration_num=0;
		double logLikelihood_old =0d, logLikelihood_new = 0d;
		while(true) {

			logLikelihood_old = logLikelyHood();
			Estep();
			Mstep(clusters);
			iteration_num++;
			logLikelihood_new = logLikelyHood();

			//convergence condition
			if((Math.abs(logLikelihood_new - logLikelihood_old)/Math.abs(logLikelihood_old)) < 0.000001)
				break;
		}
		System.out.println("Number of Iterations until converge = "+iteration_num);
		System.out.println("\nAfter Calculations");
		System.out.println("Final mean = ");
		printArray(mean);
		System.out.println("\nFinal covariance = ");
		print3D(coVariance);

		//Assign to new clusters according to 
		for(int j=0; j< K; j++) {
			clusters.get(j).removeAll(clusters.get(j));
		}
		for(int i=0;i<dataSet.size();i++){
			// choose the max probability
			int clusterIndex=0;        	
			for(int j=1;j<K;j++){
				if(W[i][j]>W[i][clusterIndex]) clusterIndex=j;
			}
			clusters.get(clusterIndex).add(dataSet.get(i));
		}

		//Calculate purity
		int[] maxLabelCluster = new int[clusters.size()];
		for (int j = 0; j < clusters.size(); j++) {
			maxLabelCluster[j] = KMeans.getMaxClusterLabel(clusters.get(j));
		}
		double purity = 0d;
		for(int j=0; j< clusters.size(); j++) {
			purity += maxLabelCluster[j];
		}
		purity = purity/dataSet.size();
		System.out.println("Purity is :"+purity);
        
		// calculate NMI
		double[][] nmiMatrix = DataPoints.getNMIMatrix(clusters, DataPoints.getNoOFLabels(dataSet));
		double nmi = DataPoints.calcNMI(nmiMatrix);
		System.out.println("NMI :"+nmi);

		// output the result into file
		//write clusters to file for plotting
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("output//GMM_result_on_"+dataSetName+".csv")));
		//write the header
		bw.write("x_coordinate,y_coordinate,cluster_label\n");
		for(int w=0; w < K; w++) {
			System.out.println("Cluster "+w + " size :"+ clusters.get(w).size());
			//bw.write("\nFor Cluster "+ (w+1) + "\n");
			for(DataPoints point : clusters.get(w))
				bw.write(point.x + "," + point.y + ","+ (w+1)+ "\n");
			//bw.write("\n\n");
		}
		bw.close();
	}



	private static void  initilizeMean(List<Set<DataPoints>> randomClusters){
		for(int j=0;j<K;j++){
			int size=randomClusters.get(j).size();
			DataPoints[] dataPoints = new DataPoints[(int) size]; 
			randomClusters.get(j).toArray(dataPoints);
			// get summation
			for(int i=0;i<size;i++){
				mean[j][0] += dataPoints[i].x;
				mean[j][1] += dataPoints[i].y;
			}

			mean[j][0] = mean[j][0]/size;
			mean[j][1] = mean[j][1]/size;

		}

	}

	private static void  initilizeVariance(List<Set<DataPoints>> randomClusters){
		for(int j=0;j<K;j++){

			int size=randomClusters.get(j).size();
			DataPoints[] dataPoints = new DataPoints[(int) size]; 
			randomClusters.get(j).toArray(dataPoints);

			// get summation of error square 
			for(int i=0;i<size;i++){
				variance[j][0]+=(dataPoints[i].x-mean[j][0])*(dataPoints[i].x-mean[j][0]);
				variance[j][1]+=(dataPoints[i].y-mean[j][1])*(dataPoints[i].y-mean[j][1]);
			}

			// get mean error square
			variance[j][0]=variance[j][0]/size;
			variance[j][1]=variance[j][1]/size;


		}
	}

	private static void  initilizeCovariance(List<Set<DataPoints>> randomClusters){
		for(int j=0;j<K;j++){

			coVariance[j][0][0]=variance[j][0];
			coVariance[j][1][1]=variance[j][1];
			int size=randomClusters.get(j).size();
			DataPoints[] dataPoints = new DataPoints[(int) size]; 
			randomClusters.get(j).toArray(dataPoints);


			double Cov_X_Y=0d;
			for(int i=0;i<size;i++){
				Cov_X_Y += (dataPoints[i].x-mean[j][0])*(dataPoints[i].y-mean[j][1]);
			}
			Cov_X_Y =Cov_X_Y/size;
			coVariance[j][0][1]=Cov_X_Y;
			coVariance[j][1][0]=Cov_X_Y; //Cov[x,y]=Cov[y,x]

		}
	}

	public static void Estep() {
		for(int i=0; i < dataSet.size(); i++) {
			double denominator = 0d;

			for(int j=0; j < K; j++) {
				MultivariateNormalDistribution gaussian;
				gaussian = new MultivariateNormalDistribution(mean[j], coVariance[j]);
				double numerator = w[j] * gaussian.density(new double[]{dataSet.get(i).x, dataSet.get(i).y});
				W[i][j] = numerator;
			}

			//normalize W[i][j] into probabilities
			/****************Please Fill Missing Lines Here*****************/
			for(int j=0;j<K;j++){
				denominator += W[i][j];
			}
			for(int j=0;j<K;j++){
				W[i][j] /= denominator;
			}

		}          
	}

	public static void Mstep(List<Set<DataPoints>> clusters) {
		//get 
		for(int j=0; j < K; j++) {
			double denominator = 0d;
			double numerator = 0d;
			double numerator1 = 0d;
			double cov_xy = 0d;
			double updatedMean1 = 0d, updatedMean2 = 0d;
			for(int i=0; i < dataSet.size(); i++) {
				denominator += W[i][j];
				numerator += W[i][j] * Math.pow((dataSet.get(i).x - mean[j][0]), 2);
				numerator1 += W[i][j] * Math.pow((dataSet.get(i).y - mean[j][1]), 2);
				//cov_xy +=?
				/****************Please Fill Missing Lines Here*****************/
				cov_xy +=  W[i][j]*(dataSet.get(i).x - mean[j][0])*(dataSet.get(i).y - mean[j][1]);


				updatedMean1 += W[i][j] * dataSet.get(i).x;
				updatedMean2 += W[i][j] * dataSet.get(i).y;
			}
			variance[j][0] = numerator / denominator;
			variance[j][1] = numerator1 / denominator;
			//update w[j]
			/****************Please Fill Missing Lines Here*****************/
			w[j]=denominator/dataSet.size();

			//update mean
			mean[j][0] = updatedMean1 / denominator;
			mean[j][1] = updatedMean2 / denominator;

			//update covariance matrix
			coVariance[j][0][0] = variance[j][0];
			coVariance[j][1][1] = variance[j][1];
			coVariance[j][0][1] = coVariance[j][1][0] = cov_xy/denominator;
		}	
	}


	public static double logLikelyHood() {
		double logLiklyhood = 0d;
		for(int i=0; i < dataSet.size(); i++) {
			double numerator = 0d;
			for(int j=0; j < K; j++) {
				MultivariateNormalDistribution gaussian;
				gaussian = new MultivariateNormalDistribution(mean[j], coVariance[j]);
				numerator += w[j] * gaussian.density(new double[]{dataSet.get(i).x, dataSet.get(i).y});
			}
			logLiklyhood += Math.log(numerator);
		}


		return logLiklyhood;
	}

	public static void printArray(double mat[][]) {
		for(int i=0; i < mat.length; i++) {
			for(int j=0; j < mat[i].length; j++) {
				System.out.print(mat[i][j] +  "  ");
			}
			System.out.println();
		}
	}

	public static void print3D(double mat[][][]) {
		for(int i=0; i < mat.length; i++) {
			System.out.println("For Cluster : "+(i+1));
			for(int j=0; j < mat[i].length; j++) {
				for(int k=0; k < mat[i][j].length; k++) {
					System.out.print(mat[i][j][k] +  "  ");
				}
				System.out.println();
			}
			System.out.println();
		}
	}
	public static void convertInputToCsv() throws IOException{
		dataSet = DataPoints.readDataSet("data//dataset1.txt");
		plotInputDataset("dataset1");
		dataSet = DataPoints.readDataSet("data//dataset2.txt");
		plotInputDataset("dataset2");
		dataSet = DataPoints.readDataSet("data//dataset3.txt");
		plotInputDataset("dataset3");
	}

	// helper function to plot the input dataset
	public static void plotInputDataset(String datasetName) throws IOException {
		FileWriter fStream = new FileWriter(datasetName+".csv");
		BufferedWriter out = new BufferedWriter(fStream);
		out.write("x_coordinate,y_coordinate,true_label_in_integer_form\n");
		for (int i = 0; i < dataSet.size(); i++) {
			DataPoints point = dataSet.get(i);
			int label = 0;
			label = point.label;
			out.write(String.valueOf(point.x) + ","
					+ String.valueOf(point.y) + ","
					+ String.valueOf(label));
			out.newLine();
		}
		out.close();
	}

}
