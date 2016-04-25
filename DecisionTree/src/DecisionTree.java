import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NoSupportForMissingValuesException;
import weka.core.Utils;

/*
 * Class for constructing an unpruned decision tree based on
 * the ID3 algorithm. Can only deal with categorical attributes.
 * No missing values allowed. Empty leaves may result in unclassified instances.
 */
public class DecisionTree  {

	public static void main(String[] args) throws Exception{
		//String trainSetPath = Utility.readFile("data\\decision_tree\\weather-nominal.arff");
		String trainSetPath="data//decision_tree//voting-records.arff";
		Instances trainSet = new Instances(new BufferedReader(new FileReader(trainSetPath)));

		// set class label according to the class label index
		int cIdx=trainSet.numAttributes()-1;
		trainSet.setClassIndex(cIdx);

		//running the 5-fold cross validation		
		DecisionTree tree=new DecisionTree();
		tree.kFoldsCrossValidation(5,trainSet);

	}

	//Attribute used for splitting.
	private Attribute splittingAttribute;

	//The successors of this decision tree. 
	// The index of successors is determined by the possible values of the splitting attribute 
	private DecisionTree[] successors;

	//Class label if this node/tree is leaf.
	private double leafClassLabel;

	//Class distribution if node is leaf.
	private double[] m_Distribution;

	// Class attribute of data set.
	private Attribute m_ClassAttribute;


	/*  
	 * @params: foldsNum is a number of folds of k folds cross validation
	 * @params: dataSet is the data set used to run the k folds cross validation 
	 * runs a k folds cross validation 
	 */
	public void kFoldsCrossValidation(int foldsNum,Instances dataSet) throws Exception{
		//randomize the data
		Random rand = new Random(168);   // create seeded number generator
		dataSet.randomize(rand);         // randomize data with number generator

		double[] accuracies= new double[foldsNum]; // store accuracies in each round
		// loop for croos-validation
		for (int f = 0; f < foldsNum; f++) {
			Instances trainSet = dataSet.trainCV(foldsNum, f); // training set in fold-f
			Instances testSet = dataSet.testCV(foldsNum, f); // test set in fold-f
			training(trainSet); // begin training
			List<Double> predictedResults=new ArrayList<Double>();
			for(int i =0; i<testSet.numInstances();i++) {
				//make prediction
				double predection =predict(testSet.instance(i));
				predictedResults.add(predection);
			}
			accuracies[f]= Utility.getAccuracy(testSet,predictedResults);
			System.out.println("The accuracy of round-"+(f+1)+" is "+accuracies[f]+" percentage.");
			if(f==(foldsNum-1)){
				double sum_accuracies =0.0;
				for(int i=0;i<foldsNum;i++)
				{
					sum_accuracies += accuracies[i];
				}
				double average_accuracies=(double)sum_accuracies/foldsNum;

				System.out.println("The average accuracy of all the "+foldsNum+" rounds is "+average_accuracies+" percentage.");
			}


		}// end loop for cross-validation
	}

	//Builds a decision tree based on the given train set.
	public void training(Instances trainSet) throws Exception {
		trainSet = new Instances(trainSet);
		// call the build Tree function which recursively build a decision tree 
		this.buildTree(trainSet);
	}

	
	// recursively build a decision tree according to the given data set 
	private void buildTree(Instances dataSet) throws Exception {
		if(dataSet.numInstances() == 0) {//  stop condition for recursion
			this.splittingAttribute = null;
			this.leafClassLabel = Instance.missingValue();
			this.m_Distribution = new double[dataSet.numClasses()];
		} else { //

			// step 1: calculate the information gain/ gain ratio  of all the candidate attribute
			double[] gains = new double[dataSet.numAttributes()];
		
			
			Enumeration possibleAttributes = dataSet.enumerateAttributes();
			while(possibleAttributes.hasMoreElements()){
				
				Attribute candidateAttribute = (Attribute)possibleAttributes.nextElement();
				
				 // use weka's Attribute.index() to map Attribute into a index
				int attribuIndex=candidateAttribute.index();
				
				// we use information gain as default  
				gains[attribuIndex] = this.getInfoGain(dataSet, candidateAttribute);
				
				//alternatively, we can use the gain ratio  by uncomment the following line and comment above line
				//gains[attribuIndex] = this.getInfoGain(dataSet, candidateAttribute);
			}
			
			
			// step 2: select the attribute with maximum information gain/ gain ratio to be the splitting point
			// step2.1 use weka's Utils.maxIndex(double[] array) to find the index of the attribute with maximum information gain/ gain ratio
			int maxIndex=Utils.maxIndex(gains);
			// step2.2 use weka's
			this.splittingAttribute = dataSet.attribute(maxIndex);


			// step 3: recursively build 
			if(Utils.eq(gains[this.splittingAttribute.index()], 0.0D)) {
				this.splittingAttribute = null;
				this.m_Distribution = new double[dataSet.numClasses()];

				Instance j;
				for(Enumeration var6 = dataSet.enumerateInstances(); var6.hasMoreElements(); ++this.m_Distribution[(int)j.classValue()]) {
					j = (Instance)var6.nextElement();
				}

				Utils.normalize(this.m_Distribution);
				this.leafClassLabel = (double)Utils.maxIndex(this.m_Distribution);
				this.m_ClassAttribute = dataSet.classAttribute();
			} else {
				//Instances[] var7 = this.getSplittedDataSets(dataSet, this.splittingAttribute);
				Instances[] var7 = Utility.getSplittedDataSets(dataSet, this.splittingAttribute);
				this.successors = new DecisionTree[this.splittingAttribute.numValues()];

				for(int var8 = 0; var8 < this.splittingAttribute.numValues(); ++var8) {
					this.successors[var8] = new DecisionTree();
					this.successors[var8].buildTree(var7[var8]);
				}
			}
		}
	}

	/*  
	 * @params: dataEntry is a given test data 
	 * returns the predicted class label of dataEntry using the trained decision tree. 
	 */
	public double predict(Instance dataEntry) throws NoSupportForMissingValuesException {
		double predictedLabel;
		if(splittingAttribute == null){ // leaf node detected
			// stop recursion if leaf node is found
			predictedLabel=this.leafClassLabel;

		}else{ //traverse the entire decision tree recursively to find the leaf node 

			// for every subtree
			//step1: find the splitting attribute of this subtree
			//step2: find the value in the given instance according to the splitting attribute in this subtree 
			int splittingAttriValue=(int)dataEntry.value(this.splittingAttribute);

			//step3: use the value found in the previous step as an index to find the proper subtree to traverse 
			//step4: begin next round of recursion by using the subtree found in step3
			predictedLabel=this.successors[splittingAttriValue].predict(dataEntry);
		}

		return predictedLabel;

	}

	//returns the information gain for the given attribute on the given data set .
	private double getInfoGain(Instances dataSet, Attribute attrib) throws Exception {
		//step1: compute the total entropy --Info(D)
		double infoD = this.getEntropy(dataSet);

		//step2: get all data sets splitted from the given dataSet according to the given attribute 
		//Instances[] splittedDataSets = this.getSplittedDataSets(dataSet, attrib);
		Instances[] splittedDataSets = Utility.getSplittedDataSets(dataSet, attrib);

		double dataSetSize=dataSet.numInstances();

		//step3: compute the conditional entropy Info_A_(D)
		double info_A_D=0.0D;
		for(int i=0;i<splittedDataSets.length;i++)
		{
			//probability of splittedDataSets[i]
			double prob_i=  splittedDataSets[i].numInstances()/dataSetSize;

			// entropy of splittedDataSets[i]
			double entropy_i = this.getEntropy(splittedDataSets[i]);

			//info_A_D is summation of all the p_i *entropy_i
			info_A_D += prob_i*entropy_i;

		}

		//step4: get the information gain
		double infoGain = infoD-info_A_D;

		return infoGain;
	}

	//returns the gain ratio for the given attribute on given data set .
	private double getGainRatio(Instances dataSet, Attribute attrib) throws Exception {
		//step1: compute the total entropy --Info(D)
		double infoD = this.getEntropy(dataSet);

		//step2: get all data sets splitted from the given dataSet according to the given attribute 
		//Instances[] splittedDataSets = this.getSplittedDataSets(dataSet, attrib);
		Instances[] splittedDataSets = Utility.getSplittedDataSets(dataSet, attrib);

		double dataSetSize=dataSet.numInstances();

		//step3: compute the conditional entropy Info_A_(D)
		double info_A_D=0.0D;
		//step4: get the SplitInfoA(D)
		double split_info_A_D=0.0D;
		for(int i=0;i<splittedDataSets.length;i++)
		{
			//probability of splittedDataSets[i]
			double prob_i=  splittedDataSets[i].numInstances()/dataSetSize;

			// entropy of splittedDataSets[i]
			double entropy_i = this.getEntropy(splittedDataSets[i]);

			//info_A_D is summation of all the p_i *entropy_i
			info_A_D += prob_i*entropy_i;

			//compute the SplitInfoA(D)
			if(prob_i==0 || prob_i==1){
				split_info_A_D=1.0;
			}else{
				split_info_A_D += (0-prob_i)*(Math.log(prob_i)/Math.log(2));
			}
		}

		//step5: get the information gain
		double infoGain = infoD-info_A_D;
		
		//step6: get the gain ratio
		double gain_ratio=infoGain/split_info_A_D;
		return gain_ratio;
	}
	

	//Computes the entropy of a dataset.
	private double getEntropy(Instances data) throws Exception {
		double[] classCounts = new double[data.numClasses()];

		Instance entropy;
		for(Enumeration instEnum = data.enumerateInstances(); instEnum.hasMoreElements(); ++classCounts[(int)entropy.classValue()]) {
			entropy = (Instance)instEnum.nextElement();
		}

		double totalEntropy = 0.0D;
		int classNum = data.numClasses();
		double [] classProbVec = new double[classNum];

		for(int j = 0; j < classNum; ++j) {
			if(classCounts[j] > 0.0D) {
				classProbVec[j]= classCounts[j]/data.numInstances();
			}
			else
				classProbVec[j]=0;
		}

		for(int i=0; i<classCounts.length;i++){
			if(classProbVec[i]!=0 && classProbVec[i]!=1){
				//(-classProbVec[i])*(Math.log(classProbVec[i])/Math.log(2))=0 or 1,do nothing
				// use the Change of Base Formula (Math.log(2)) to calculate log2(x)
				totalEntropy += (-classProbVec[i])*(Math.log(classProbVec[i])/Math.log(2));
			}
		}

		return totalEntropy;

	}


	private String toString(int level) {
		StringBuffer text = new StringBuffer();
		if(this.splittingAttribute == null) {
			if(Instance.isMissingValue(this.leafClassLabel)) {
				text.append(": null");
			} else {
				text.append(": " + this.m_ClassAttribute.value((int)this.leafClassLabel));
			}
		} else {
			for(int j = 0; j < this.splittingAttribute.numValues(); ++j) {
				text.append("\n");

				for(int i = 0; i < level; ++i) {
					text.append("|  ");
				}

				text.append(this.splittingAttribute.name() + " = " + this.splittingAttribute.value(j));
				text.append(this.successors[j].toString(level + 1));
			}
		}
		return text.toString();
	}

	public String toString() {
		return this.m_Distribution == null && this.successors == null?"DecisionTree: No model built yet.":"DecisionTree\n\n" + this.toString(0);
	}


}
