
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;



public class KMeansML {
	
	public static void main(String[] args) throws IOException {

		List<DataPoints> dataSet = DataPoints.readDataSet("data//MLKmeansDataset.txt");
		K=10;
		kmeans(dataSet,"dataset1");


	}

	static int K = 0;

	private static void kmeans(List<DataPoints> dataSet, String dataSetName) throws IOException {

		List<Set<DataPoints>> clusters = new ArrayList<Set<DataPoints>>();
		int k =0;
		
		// create K clusters
		while(k < K) {
			Set<DataPoints> cluster = new HashSet<DataPoints>();
			clusters.add(cluster);
			k++;
		}


		
		// set the first 10 points as 10 centroid
		for(int i=0;i<10;i++){
			clusters.get(i).add(dataSet.get(i));
		}
			
		//calculate centroid for clusters
		Centroid[] centroids = new Centroid[K];
		for(int j=0; j< K; j++) {
			centroids[j] = getCentroid(clusters.get(j));
		}

		for(int j=0; j< K; j++) {
			clusters.get(j).removeAll(clusters.get(j));
		}

		
		reassignClusters(dataSet, centroids, clusters);

		
		
		//continue till converge
		int iteration = 0;
		while(true) {
			iteration++;
			//calculate centroid for clusters
			Centroid[] centroidsNew = new Centroid[K];
			for(int j=0; j< K; j++) {
				centroidsNew[j] = getCentroid(clusters.get(j));
			}

			// check whether converge
			boolean isConverge = false;
			for(int j=0; j< K; j++) {
				if(!centroidsNew[j].equals(centroids[j])) {
					isConverge = false;
				} else {
					isConverge = true;
				}
			}

			if(isConverge) 
				break;


			// in this part, the algo hasn't converge
			for(int j=0; j< K; j++) {
				clusters.get(j).removeAll(clusters.get(j));
			}

			reassignClusters(dataSet, centroidsNew, clusters);
			for(int j=0; j< K; j++) {
				centroids[j] = centroidsNew[j];
			}
		}

		System.out.println("Numbers of iterations until converge :"+ iteration);


        


		//write clusters to file for plotting
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("output//KmeansResultsMachineLearning.csv")));
		// write the header
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

	public static int getMaxClusterLabel(Set<DataPoints> cluster) {
		Map<Integer, Integer> labelCounts = new HashMap<Integer, Integer>();
		for(DataPoints point : cluster) {
			if(!labelCounts.containsKey(point.label)) {
				labelCounts.put(point.label, 1);
			} else {
				labelCounts.put(point.label, labelCounts.get(point.label) + 1);
			}
		}
		int max = Integer.MIN_VALUE;
		for(int label : labelCounts.keySet()) {
			if(max < labelCounts.get(label)) 
				max = labelCounts.get(label);
		}
		return max;
	}

	private static void reassignClusters(List<DataPoints> dataSet, Centroid[] c, List<Set<DataPoints>> clusters) {
		// reassign points based on cluster and continue till stable clusters found
		double[] dist = new double[K];

		for (DataPoints point : dataSet) {
			for(int i=0; i < K; i++) {
				dist[i] = getEuclideanDist(point.x, point.y, c[i].x, c[i].y);
			}

			double minDist = getMin(dist);
			//assign point to the closest cluster
			/****************Please Fill Missing Lines Here*****************/
			// get the index of the closest cluster
			int closestIndex=0;
			for(int i=0;i<K;i++){
				if(minDist==dist[i]) closestIndex=i;
			}
			clusters.get(closestIndex).add(point);

		}


	}

	private static double getMin(double[] dist) {
		// TODO Auto-generated method stub
		double min = Integer.MAX_VALUE;
		for(int i=0; i<dist.length; i++) {
			if(dist[i] < min)
				min = dist[i];
		}
		return min;
	}

	private static double getEuclideanDist(double x1, double y1, double x2, double y2) {
		double dist = Math.sqrt(Math.pow((x2-x1),2) + Math.pow((y2-y1), 2));
		return dist;
	}

	private static Centroid getCentroid(Set<DataPoints> cluster) {
		// TODO Auto-generated method stub
		Double cx,cy; //mean of x and mean of y
		double size = cluster.size();

		DataPoints[] dataPoints = new DataPoints[(int) size]; 
		cluster.toArray(dataPoints);

		/****************Please Fill Missing Lines Here*****************/
		cx=0.0;
		cy=0.0;
		for(int i=0;i<size;i++){
			cx += dataPoints[i].x;
			cy += dataPoints[i].y;	
		}
		cx = cx/size;
		cy = cy/size;
		return new Centroid(cx, cy);
	}

}


