import java.io.BufferedWriter;
import java.util.Enumeration;


public class decisiontree {

}
/*	 private void printOutput(Instances data) throws IOException, NoSupportForMissingValuesException {
FileWriter fStream = new FileWriter("output\\decision_tree\\decision-tree-output.txt");     // Output File
BufferedWriter out = new BufferedWriter(fStream);
out.write(" The accuracy is reported at the bottom of this file");
out.newLine();
// for validation
int error_count=0;

for(int index =0; index<data.numInstances();index++) {
    Instance testRowInstance = data.instance(index);
    //make prediction
    double prediction =classifyInstance(testRowInstance);
    double class_val = testRowInstance.classValue();

    if(prediction != class_val)
    { 
    	error_count++;
        out.write(data.classAttribute().value((int) prediction)+"(Error prediction on No."+(index+1) +" data record).");
        out.newLine();
    }else{
     out.write(data.classAttribute().value((int) prediction));
     out.newLine();
    }
}
double accura= (double) (data.numInstances()-error_count)*100/data.numInstances();
out.write(" The accuracy is "+accura+" percentage.");
out.newLine();
out.close();
}*/

/*	public void kFoldsCrossValidation(int foldsNum,Instances trainSet) throws Exception{
//randomize the data
Random rand = new Random(168);   // create seeded number generator
trainSet.randomize(rand);         // randomize data with number generator

double[] accuracies= new double[foldsNum]; // store accuracies in each round
// loop for croos-validation
for (int f = 0; f < foldsNum; f++) {
Instances train = trainSet.trainCV(foldsNum, f); // training set in fold-f
Instances test = trainSet.testCV(foldsNum, f); // test set in fold-f
training(train); // begin training
FileWriter fStream = new FileWriter("output\\decision_tree\\decision-tree-output-round-No."+(f+1)+".txt");     // Output File
BufferedWriter out = new BufferedWriter(fStream);
out.write("The accuracy reports are at the bottom of this file");
out.newLine();
out.write("Predicted value on the test set are as followings:");
out.newLine();
out.newLine();
// for validation
int error_count=0;
for(int index =0; index<test.numInstances();index++) {
Instance testRowInstance = test.instance(index);
//make prediction
double prediction =classify(testRowInstance);
double class_val = testRowInstance.classValue();

if(prediction != class_val)
{ 
	//print the error prediction
	error_count++;
	out.write(test.classAttribute().value((int) prediction)+"(Error prediction on No."+(index+1) +" data record).");
	out.newLine();
}else{
	//print the right prediction
	out.write(test.classAttribute().value((int) prediction));
	out.newLine();
}
}
accuracies[f]= (double) (test.numInstances()-error_count)*100/test.numInstances();
out.newLine();
out.write("The accuracy of round-"+(f+1)+" is "+accuracies[f]+" percentage.");
System.out.println("The accuracy of round-"+(f+1)+" is "+accuracies[f]+" percentage.");
out.newLine();
if(f==(foldsNum-1)){
double sum_accuracies =0.0;
for(int i=0;i<foldsNum;i++)
{
	sum_accuracies += accuracies[i];
}
double average_accuracies=(double)sum_accuracies/foldsNum;
out.write("The average accuracy of all the "+foldsNum+" rounds is "+average_accuracies+" percentage.");
System.out.println("The average accuracy of all the "+foldsNum+" rounds is "+average_accuracies+" percentage.");
}
out.close();

}// end loop for croos-validation
}*/

/*    public static String getClass(Instances data, double[] distribution) {
double maxProb = distribution[0];
int maxIndex = 0;
for(int j=0; j<distribution.length;j++){
if(distribution[j] > maxProb) {
maxProb = distribution[j];
maxIndex = j;
}
}
return data.classAttribute().value(maxIndex);
}*/

/*
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

		for(int j = 0; j < splittedDataSets.length; j++) {
			//Compactifies the set
			//Decreases the capacity of the set so that it matches the number of instances in the set.
			splittedDataSets[j].compactify();
		return splittedDataSets;
	}
*/


/*	private String toString(int level) {
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
}*/


/* attribute
 // step 3: build the successors of this tree node if needed to be splitted 
			if(Utils.eq(gains[this.splittingAttribute.index()], 0.0D)) {

				this.splittingAttribute = null;
				this.classLabelProbDistribution = new double[dataSet.numClasses()];

				Instance j;
				for(Enumeration var6 = dataSet.enumerateInstances(); var6.hasMoreElements(); ++this.classLabelProbDistribution[(int)j.classValue()]) {
					j = (Instance)var6.nextElement();
				}

				Utils.normalize(this.classLabelProbDistribution);
				this.leafClassLabel = (double)Utils.maxIndex(this.classLabelProbDistribution);
				//this.m_ClassAttribute = dataSet.classAttribute();
				
			} else {
*/
 


/*				Instance j;
				for(Enumeration var6 = dataSet.enumerateInstances(); var6.hasMoreElements(); ++this.classLabelProbDistribution[(int)j.classValue()]) {
					j = (Instance)var6.nextElement();
				}*/

