package DecisionTree;

import java.io.*;
import java.util.Enumeration;
import java.util.Random;

import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NoSupportForMissingValuesException;
import weka.core.Utils;
import Utility.Utility;

/*Class for constructing an unpruned decision tree based on
the ID3 algorithm. Can only deal with nominal attributes.
No missing values allowed. Empty leaves may result in unclassified instances.
 */
public class DecisionTree  {
    //The node's successors.
    private DecisionTree[] m_Successors;
    //Attribute used for splitting.
    private Attribute m_Attribute;
    //Class value if node is leaf.
    private double m_ClassValue;
    //Class distribution if node is leaf.
    private double[] m_Distribution;
    // Class attribute of data set.
    private Attribute m_ClassAttribute;

    public DecisionTree() {
    }
    //Builds decision tree classifier.
    public void buildClassifier(Instances data) throws Exception {
        data = new Instances(data);
        this.makeTree(data);
    }

    private void makeTree(Instances data) throws Exception {
        if(data.numInstances() == 0) {
            this.m_Attribute = null;
            this.m_ClassValue = Instance.missingValue();
            this.m_Distribution = new double[data.numClasses()];
        } else {
            double[] infoGains = new double[data.numAttributes()];

            Attribute splitData;
            for(Enumeration attEnum = data.enumerateAttributes(); attEnum.hasMoreElements(); infoGains[splitData.index()] = this.computeInfoGain(data, splitData)) {
                splitData = (Attribute)attEnum.nextElement();
            }

            this.m_Attribute = data.attribute(Utils.maxIndex(infoGains));
            if(Utils.eq(infoGains[this.m_Attribute.index()], 0.0D)) {
                this.m_Attribute = null;
                this.m_Distribution = new double[data.numClasses()];

                Instance j;
                for(Enumeration var6 = data.enumerateInstances(); var6.hasMoreElements(); ++this.m_Distribution[(int)j.classValue()]) {
                    j = (Instance)var6.nextElement();
                }

                Utils.normalize(this.m_Distribution);
                this.m_ClassValue = (double)Utils.maxIndex(this.m_Distribution);
                this.m_ClassAttribute = data.classAttribute();
            } else {
                Instances[] var7 = this.splitData(data, this.m_Attribute);
                this.m_Successors = new DecisionTree[this.m_Attribute.numValues()];

                for(int var8 = 0; var8 < this.m_Attribute.numValues(); ++var8) {
                    this.m_Successors[var8] = new DecisionTree();
                    this.m_Successors[var8].makeTree(var7[var8]);
                }
            }
        }
    }
    //Classifies a given test instance using the decision tree.
    public double classifyInstance(Instance instance) throws NoSupportForMissingValuesException {
        if(instance.hasMissingValue()) {
            throw new NoSupportForMissingValuesException("DecisionTree: no missing values, please.");
        } else {
            return this.m_Attribute == null?this.m_ClassValue:this.m_Successors[(int)instance.value(this.m_Attribute)].classifyInstance(instance);
        }
    }

    public String toString() {
        return this.m_Distribution == null && this.m_Successors == null?"DecisionTree: No model built yet.":"DecisionTree\n\n" + this.toString(0);
    }
    //Computes information gain for an attribute.
    private double computeInfoGain(Instances data, Attribute att) throws Exception {
        double infoGain = this.computeEntropy(data);
        Instances[] splitData = this.splitData(data, att);

        
        /****************Please Fill Missing Lines Here*****************/
        double dataSetCounts=data.numInstances();
        //entropy of data given att
        double info_att_data=0.0D;
        
        // implementing ration gain version decision tree
        // SplitInfoA(D)
        double split_info_att_D=0.0D;
        
        for(int i=0;i<splitData.length;i++)
        {
        	//probability of splitData[i]
        	double p_i=  splitData[i].numInstances()/dataSetCounts;
        	// entropy of splitData[i]
        	double entropy_i = this.computeEntropy(splitData[i]);
        	//info_att_data is summation of all the p_i *entropy_i
        	info_att_data += p_i*entropy_i;
        	
        	
        	//compute the SplitInfoA(D)
        	if(p_i==0 || p_i==1){
        		split_info_att_D=1.0;
        	}else{
        		split_info_att_D += (0-p_i)*(Math.log(p_i)/Math.log(2));
        	}
        }
        infoGain -= info_att_data; // Gain(att)
        
      // infoGain /= split_info_att_D; //by default, we use gain ratio version
        
        return infoGain;
    }
    //Computes the entropy of a dataset.
    private double computeEntropy(Instances data) throws Exception {
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

        /****************Please Fill Missing Lines Here*****************/
        for(int i=0; i<classCounts.length;i++){
        	if(classProbVec[i]!=0 && classProbVec[i]!=1){
        	//(-classProbVec[i])*(Math.log(classProbVec[i])/Math.log(2))=0 or 1,do nothing
        	// use the Change of Base Formula (Math.log(2)) to calculate log2(x)
        	totalEntropy += (-classProbVec[i])*(Math.log(classProbVec[i])/Math.log(2));
        	}
        }
        
        return totalEntropy;

    }
    //Splits a dataset according to the values of a nominal attribute.
    private Instances[] splitData(Instances data, Attribute att) {
        Instances[] splitData = new Instances[att.numValues()];

        for(int instEnum = 0; instEnum < att.numValues(); ++instEnum) {
            splitData[instEnum] = new Instances(data, data.numInstances());
        }

        Enumeration var6 = data.enumerateInstances();

        while(var6.hasMoreElements()) {
            Instance i = (Instance)var6.nextElement();
            splitData[(int)i.value(att)].add(i);
        }

        for(int var7 = 0; var7 < splitData.length; ++var7) {
            splitData[var7].compactify();
        }
        return splitData;
    }

    private String toString(int level) {
        StringBuffer text = new StringBuffer();
        if(this.m_Attribute == null) {
            if(Instance.isMissingValue(this.m_ClassValue)) {
                text.append(": null");
            } else {
                text.append(": " + this.m_ClassAttribute.value((int)this.m_ClassValue));
            }
        } else {
            for(int j = 0; j < this.m_Attribute.numValues(); ++j) {
                text.append("\n");

                for(int i = 0; i < level; ++i) {
                    text.append("|  ");
                }

                text.append(this.m_Attribute.name() + " = " + this.m_Attribute.value(j));
                text.append(this.m_Successors[j].toString(level + 1));
            }
        }
        return text.toString();
    }

    public void decisionTree() throws Exception {
        //BufferedReader file = Utility.readFile("data\\decision_tree\\weather-nominal.arff");
    	BufferedReader file = Utility.readFile("data\\decision_tree\\voting-records.arff");
    	Instances data = new Instances(file);
        int cIdx=data.numAttributes()-1;
        data.setClassIndex(cIdx);
        
        //running the 5-fold cross validation
        //randomize the data
        Random rand = new Random(168);   // create seeded number generator
       // Instances randData = new Instances(data);   // create copy of original data
        data.randomize(rand);         // randomize data with number generator
        int folds = 5;
        double[] accuracies= new double[folds]; // store accuracies in each round
        // loop for croos-validation
        for (int f = 0; f < folds; f++) {
        	Instances train = data.trainCV(folds, f); // training set in fold-f
        	Instances test = data.testCV(folds, f); // test set in fold-f
        	buildClassifier(train); // begin training
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
                double prediction =classifyInstance(testRowInstance);
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
            out.newLine();
            if(f==(folds-1)){
            	double sum_accuracies =0.0;
            	for(int i=0;i<folds;i++)
            	{
            		sum_accuracies += accuracies[i];
            	}
            	double average_accuracies=(double)sum_accuracies/folds;
            	out.write("The average accuracy of all the "+folds+" rounds is "+average_accuracies+" percentage.");
            }
            out.close();
           
        }// end loop for croos-validation

    }

    private void printOutput(Instances data) throws IOException, NoSupportForMissingValuesException {
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
    }
}
