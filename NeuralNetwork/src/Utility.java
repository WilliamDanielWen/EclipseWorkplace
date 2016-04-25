import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utility {
    
	
    // read the csv file   
    public static List<DataEntry> readDataSet(String file) throws FileNotFoundException {
        List<DataEntry> dataset = new ArrayList<DataEntry>();
        Scanner scanner = new Scanner(new File(file));

        //skip the header
        scanner.nextLine();

        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("@")||line.isEmpty()) {
                continue;
            }
            String[] columns = line.split(",");

            double[] data = new double[columns.length-1];
            int i=1;
            for (i=1; i<columns.length; i++) {
                data[i-1] = Double.parseDouble(columns[i]);

            }
            // transfer label "3" as  label "0", label"5" as  label "1"
            int label = (Double.parseDouble(columns[0]) ==3? 0: 1);
            DataEntry instance = new DataEntry(label, data);
            dataset.add(instance);
        }
        scanner.close();
        return dataset;
    }
    
    public static  List<DataEntry> zScoreNormlization(List<DataEntry> dataSet){
        //z-score normalization
        
     
    	int dataDimention=dataSet.get(0).getDimension();
        double rowNum=dataSet.size();
        
        double[] columnSums=new double[dataDimention];
        for(int j=0;j<dataDimention;j++){
        	for (int i=0;i<rowNum;i++){
        		double[] x_i=dataSet.get(i).getX();
        		columnSums[j] += x_i[j];
        	}
        }
        
        double[] columnMeans=new double[dataDimention];
        for (int i=0;i<dataDimention;i++){
        	columnMeans[i]=(double)columnSums[i]/rowNum;
        }
        
        
        double[] columStanDevi=new double[dataDimention];
        for (int j=0;j<dataDimention;j++){
        	
        	// calculate the sum of the deviation
            for (int i=0;i<rowNum;i++){
            	double[] x_i=dataSet.get(i).getX();
            	columStanDevi[j] += (x_i[j]-columnMeans[j])*(x_i[j]-columnMeans[j]);	
            }
            columStanDevi[j] =columStanDevi[j]/rowNum;
            columStanDevi[j]=Math.sqrt(columStanDevi[j]);	
    	}
        
        // normalize data
        for (int i=0;i<rowNum;i++){
        	for (int j=0;j<dataDimention;j++){
        	   if(columStanDevi[j]!=0) dataSet.get(i).x[j]=(dataSet.get(i).x[j]-columnMeans[j])/columStanDevi[j];
        	}
        }
        return dataSet;
    }

    public static  List<DataEntry> minMaxNormalization(List<DataEntry> dataSet){
    	
    	int dataDimention=dataSet.get(0).getDimension();
        double rowNum=dataSet.size();
        
        double[] columnMins=new double[dataDimention];
        double[] columnMaxes=new double[dataDimention];
        
        for(int j=0;j<dataDimention;j++){
        	columnMins[j]=dataSet.get(0).getX()[j];
        	columnMaxes[j]=dataSet.get(0).getX()[j];	
        	for(int i=0;i<rowNum;i++){
        		double x_i_j=dataSet.get(i).getX()[j];
        		if(columnMins[j]>x_i_j) columnMins[j]= x_i_j;
        		if(columnMaxes[j]<x_i_j) columnMaxes[j]= x_i_j;
            }
        	
        }

     // normalize data
        for (int i=0;i<rowNum;i++){
        	for (int j=0;j<dataDimention;j++){
        		double gap=(columnMaxes[j]-columnMins[j]);
        	    if(gap!=0) dataSet.get(i).x[j]=(dataSet.get(i).x[j]-columnMins[j])/gap;
        	}
        }
        return dataSet;
    }

	/*  
	 * @params: List<DataEntry> testData is the test set
	 * @params: predictedResults must be the predicted results of the test set by using the neural network
	 * the ith element in "predictedResults" must be the predicted results of the ith entry in the test set
	 * returns the accuracy of the trained neural network on the test set 
	 */
    public static double getAccuracy(List<DataEntry> testData, List<Integer> predictedResults){
    	double right_num=0;
		double accuracy=0;
		for(int i=0;i<testData.size();i++){
			int trueLabel=testData.get(i).getLabel();
			if (trueLabel==predictedResults.get(i)) right_num++;
		}
		 accuracy=(double)right_num/testData.size()*100d;
		 return accuracy;
    }
}

// the class to represent an image
class DataEntry {
	// label of a image
    public int label;
    
    // the features of a image
    public double[] x;
    
    // number of features of a image
    public int dimension;

    public DataEntry(int label, double[] x) {
        this.label = label;
        this.x = x;
        dimension = x.length;
    }

    public int getLabel() {
        return label;
    }

    public double[] getX() {
        return x;
    }
    
    public int getDimension() {
    	return dimension;
    }
}
