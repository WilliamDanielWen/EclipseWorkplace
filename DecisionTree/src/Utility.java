
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import java.util.Enumeration;
import java.util.List;

public class Utility {

	/*  
	 * @params: testData is the test set
	 * @params: predictedResults must be the predicted results of the test set by using the neural network
	 * the i-th element in "predictedResults" must be the predicted results of the i-th entry in the test set
	 * returns the accuracy of the trained model on the test set 
	 */
    public static double getAccuracy(Instances testSet, List<Double> predictedResults){
    	double right_num=0;
		double accuracy=0;
		for(int i=0;i<testSet.numInstances();i++){
			double trueLabel=testSet.instance(i).classValue();
			if (trueLabel==predictedResults.get(i)) right_num++;
		}
		 accuracy=(double)right_num/testSet.numInstances()*100d;
		 return accuracy;
    }
    
  //Splits a dataset into multiple data sets according to the given attribute.
  	public static Instances[] getSplittedDataSets(Instances dataSet, Attribute attrib) {
  		
  		Instances[] splittedDataSets = new Instances[attrib.numValues()];

  		for(int i = 0; i < attrib.numValues(); i++) {
  			splittedDataSets[i] = new Instances(dataSet, dataSet.numInstances());
  		}

  		// whole instance in the given dataSet
  		Enumeration wholeInstances = dataSet.enumerateInstances();

  		while(wholeInstances.hasMoreElements()) {
  			Instance inst = (Instance)wholeInstances.nextElement();
  			// allocate inst to correponding sub data set according its value of given attribute 
  			splittedDataSets[(int)inst.value(attrib)].add(inst);
  		}
  		
  		return splittedDataSets;
  	}

}
