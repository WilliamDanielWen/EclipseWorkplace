import java.io.*;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Random;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NoSupportForMissingValuesException;
import weka.core.UnsupportedAttributeTypeException;
import weka.core.Utils;

/*
 * Class for constructing an unpruned decision tree based on the ID3 and C4.5. 
 * Only categorical attributes are allowed.
 * No missing values allowed.
 */
public class DecisionTree  {

	public static void main(String[] args) throws Exception{
		//String trainSetPath = Utility.readFile("data\\decision_tree\\weather-nominal.arff");
		String trainSetPath="data//voting-records.arff";
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

	//probaility distribution of class label of the leaf node if this node is leaf.
	private double[] classLabelCount;


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

			//By default we use the ID3
			trainingDecisionTreeByID3(trainSet); // begin training

			// Alternatively we can use C4.5 by uncommenting the following line and commenting the above line  
			//trainingDecisionTreeByC45(trainSet); 


			List<Double> predictedResults=new ArrayList<Double>();
			for(int i =0; i<testSet.numInstances();i++) {
				//make prediction
				double predection =classify(testSet.instance(i));
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

	//Builds a decision tree based on the given train set by using ID3.
	public void trainingDecisionTreeByID3(Instances trainSet) throws Exception {
		trainSet = new Instances(trainSet);
		// call the build Tree function which recursively build a decision tree 
		buildTreeNodeByID3(trainSet);
	}

	//Builds a decision tree based on the given train set by using C4.5.
	public void trainingDecisionTreeByC45(Instances trainSet) throws Exception {
		trainSet = new Instances(trainSet);
		// call the build Tree function which recursively build a decision tree 
		buildTreeNodeByC45(trainSet);
	}


	// recursively build a decision node according to the given data set by the ID3  
	private void buildTreeNodeByID3(Instances dataSet) throws Exception {
		if(dataSet.numInstances() == 0) {

			//  stop condition for recursion: 
			//  There are no samples left ¨C use majority voting in the parent partition
			// 	so we do nothing here    

		} else { 

			// there are still samples left 

			// step 1: calculate the information gain of all the candidate attribute
			double[] infoGains = new double[dataSet.numAttributes()];


			Enumeration possibleAttributes = dataSet.enumerateAttributes();
			while(possibleAttributes.hasMoreElements()){

				Attribute candidateAttribute = (Attribute)possibleAttributes.nextElement();

				// use weka's Attribute.index() to map Attribute into a index
				int attribuIndex=candidateAttribute.index();

				// we use information gain as default  
				infoGains[attribuIndex] = this.getInfoGain(dataSet, candidateAttribute);

			}


			// step 2: select the attribute with maximum information gain/ gain ratio to be the splitting point
			// step2.1 use weka's Utils.maxIndex(double[] array) to find the index of the attribute with maximum information gain/ gain ratio
			int maxIndex=Utils.maxIndex(infoGains);
			// step2.2 use weka's Attribute Instances.attribute(int index) to set the best splitting attribute
			this.splittingAttribute = dataSet.attribute(maxIndex);


			// step 3: build the successors of this tree node
			//         or build the leaf node if all 
			if(Utils.eq(infoGains[this.splittingAttribute.index()], 0.0D)) {// all the data has the same class label

				this.splittingAttribute = null;
				this.classLabelCount = new double[dataSet.numClasses()];


				Enumeration leafData = dataSet.enumerateInstances();
				// local variables needed
				Instance j;
				int classLabelIndex;
				while(leafData.hasMoreElements()){
					j = (Instance)leafData.nextElement();

					// map the class label into a index 
					classLabelIndex=(int)j.classValue();

					this.classLabelCount[classLabelIndex]++;
				}

				this.leafClassLabel = (double)Utils.maxIndex(this.classLabelCount);


			} else {

				//  get every sub dataset in "dataSet" with different value of splittingAttribute
				Instances[] set_of_SubDataSet = Utility.splitDataSets(dataSet, this.splittingAttribute);

				// successors number equals to the number of values in the splitting attribute
				this.successors = new DecisionTree[this.splittingAttribute.numValues()];

				// use every sub dataset got previously to build the successors
				for(int i = 0; i < this.splittingAttribute.numValues(); ++i) {
					this.successors[i] = new DecisionTree();

					this.successors[i].buildTreeNodeByID3(set_of_SubDataSet[i]);
				}
			}
		}
	}

	// recursively build a decision node according to the given data set by the C4.5 
	private void buildTreeNodeByC45(Instances dataSet) throws Exception {
		if(dataSet.numInstances() == 0) {
			//  stop condition for recursion: 
			//  There are no samples left ¨C use majority voting in the parent partition
			// 	so we do nothing here  
		} else { 
			// there are still samples left 

			// step 1: calculate the gain ratio  of all the candidate attribute
			double[] gainRatios = new double[dataSet.numAttributes()];


			Enumeration possibleAttributes = dataSet.enumerateAttributes();
			while(possibleAttributes.hasMoreElements()){

				Attribute candidateAttribute = (Attribute)possibleAttributes.nextElement();

				// use weka's Attribute.index() to map Attribute into a index
				int attribuIndex=candidateAttribute.index();

				gainRatios[attribuIndex] = this.getGainRatio(dataSet, candidateAttribute);
			}


			// step 2: select the attribute with maximum information gain/ gain ratio to be the splitting point
			// step2.1 use weka's Utils.maxIndex(double[] array) to find the index of the attribute with maximum information gain/ gain ratio
			int maxIndex=Utils.maxIndex(gainRatios);
			// step2.2 use weka's Attribute Instances.attribute(int index) to set the best splitting attribute
			this.splittingAttribute = dataSet.attribute(maxIndex);


			// step 3: build the successors of this tree node
			//         or build the leaf node if all 
			if(Utils.eq(gainRatios[this.splittingAttribute.index()], 0.0D)) {// all the data has the same class label

				this.splittingAttribute = null;
				this.classLabelCount = new double[dataSet.numClasses()];


				Enumeration leafData = dataSet.enumerateInstances();
				// local variables needed
				Instance j;
				int classLabelIndex;
				while(leafData.hasMoreElements()){
					j = (Instance)leafData.nextElement();

					// map the class label into a index 
					classLabelIndex=(int)j.classValue();

					this.classLabelCount[classLabelIndex]++;
				}

				this.leafClassLabel = (double)Utils.maxIndex(this.classLabelCount);


			} else {

				//  get every sub dataset in "dataSet" with different value of splittingAttribute
				Instances[] set_of_SubDataSet = Utility.splitDataSets(dataSet, this.splittingAttribute);

				// successors number equals to the number of values in the splitting attribute
				this.successors = new DecisionTree[this.splittingAttribute.numValues()];

				// use every sub dataset got previously to build the successors
				for(int i = 0; i < this.splittingAttribute.numValues(); ++i) {
					this.successors[i] = new DecisionTree();

					this.successors[i].buildTreeNodeByC45(set_of_SubDataSet[i]);
				}
			}
		}
	}


	/*  
	 * @params: dataEntry is a given test data 
	 * returns the predicted class label of dataEntry using the trained decision tree. 
	 */
	public double classify(Instance dataEntry) throws NoSupportForMissingValuesException {

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
			predictedLabel=this.successors[splittingAttriValue].classify(dataEntry);
		}

		return predictedLabel;



	}

	//returns the information gain for the given attribute on the given data set .
	private double getInfoGain(Instances dataSet, Attribute attrib) throws Exception {
		//step1: compute the total entropy --Info(D)
		double infoD = this.getEntropy(dataSet);

		//step2: get all data sets split from the given dataSet according to the given attribute 
		//Instances[] splitDataSets = this.getsplitDataSets(dataSet, attrib);
		Instances[] splitDataSets = Utility.splitDataSets(dataSet, attrib);

		double dataSetSize=dataSet.numInstances();

		//step3: compute the conditional entropy Info_A_(D)
		double info_A_D=0.0D;
		for(int i=0;i<splitDataSets.length;i++)
		{
			//probability of splitDataSets[i]
			double prob_i=  splitDataSets[i].numInstances()/dataSetSize;

			// entropy of splitDataSets[i]
			double entropy_i = this.getEntropy(splitDataSets[i]);

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

		//step2: get all data sets split from the given dataSet according to the given attribute 
		//Instances[] splitDataSets = this.getsplitDataSets(dataSet, attrib);
		Instances[] splitDataSets = Utility.splitDataSets(dataSet, attrib);

		double dataSetSize=dataSet.numInstances();

		//step3: compute the conditional entropy Info_A_(D)
		double info_A_D=0.0D;
		//step4: get the SplitInfoA(D)
		double split_info_A_D=0.0D;
		for(int i=0;i<splitDataSets.length;i++)
		{
			//probability of splitDataSets[i]
			double prob_i=  splitDataSets[i].numInstances()/dataSetSize;

			// entropy of splitDataSets[i]
			double entropy_i = this.getEntropy(splitDataSets[i]);

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

}
