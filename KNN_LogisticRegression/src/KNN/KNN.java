package KNN;
import Utilities.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;



public class KNN {

	// the training data for this KNN classifier
	private List<Instance> trainDataSet;

	// number of nearest neighbors
	private int K;


	// constructor 
	public KNN(int num){
		this.K=num;
	}

	/**  @params: a training set for KNN
	 *  stores the training set to trainDataSet
	 */
	public void train(List<Instance> input){
		// store the training data
		this.trainDataSet=input;
	}
	
	/**  @params: tow vectors trainPointX, newPointX
	 *  returns the Euclidean Distance between these two vectors
	 *  The dimension of trainPointX and  newPointX must be equal.
	 */
	private double calcuEuclideanDist(double[] trainPointX, double[] newPointX){
		double distance=0;
		int dimension=trainPointX.length;
		for(int i=0;i<dimension-1;i++){
			// calculate the sum of square
			distance += Math.pow( newPointX[i]-trainPointX[i] , 2);
		}
		Math.sqrt(distance);
		return distance;
	}

	/**  @params: a Instance to be classified: newInstance 
	 *  returns the predicted class label for newInstance
	 *  which is the most frequent lables of the k nearest neighbors
	 */
	public int classify(Instance newInstance){
		int label=0;
		// compute the Euclidean distances from the new Instance to all the trainData
		List<EuclideanDistance> euclideanDistList=new ArrayList<EuclideanDistance>();

		for(Instance trainInstance : trainDataSet){
			double dist=this.calcuEuclideanDist(trainInstance.getX(),newInstance.getX());
			EuclideanDistance ed=new EuclideanDistance(newInstance,trainInstance,dist);
			euclideanDistList.add(ed);
		}

		// sort the euclideanDistList as an ascending order
		euclideanDistList.sort(new EuclideanComparator());		
		// find the most frequent label among the k nearest neighbors
		int posCount=0;
		for(int i=0; i<this.K;i++){
			if(euclideanDistList.get(i).trainPoint.label==1) posCount++;
		}
		if(posCount>this.K-posCount){
			label=1;
		}else{
			label=0;
		}

		return label;
	}



}


/* 
 *  This class is defined to store the Euclidean distance between a new instance(a data to be classified) and 
 * a training instance. 
 */
class EuclideanDistance{
	Instance newPoint;
	Instance trainPoint;
	double euclideanDist;

	EuclideanDistance(Instance np,Instance tp, double ed){
		this.newPoint=np;
		this.trainPoint=tp;
		this.euclideanDist=ed;
	}
}

/* 
 * This Comparator is defined to compare the Euclidean distances from two different training data 
 * to the same new instance to be classified 
 */

class EuclideanComparator implements Comparator<EuclideanDistance>{

	public int compare(EuclideanDistance a, EuclideanDistance b) {
		// a.newPoint and b.newPoint must be the same point
		return a.euclideanDist < b.euclideanDist ? -1 : (a.euclideanDist == b.euclideanDist ? 0 : 1);
	}
}
