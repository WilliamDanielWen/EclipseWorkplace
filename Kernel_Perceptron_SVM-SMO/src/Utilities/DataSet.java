package Utilities;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class DataSet {
    
	//read the csv file in assignment2 for problem 3 
    public static List<Instance> readDataSet(String file) throws FileNotFoundException {
        List<Instance> dataset = new ArrayList<Instance>();
        Scanner scanner = new Scanner(new File(file));
        while(scanner.hasNextLine()) {
            String line = scanner.nextLine();
            if (line.startsWith("@")||line.isEmpty()) {
                continue;
            }
            String[] columns = line.split("	");

            double[] data = new double[columns.length-1];
            int i=0;
            for (i=0; i<columns.length-1; i++) {
                data[i] = Double.parseDouble(columns[i]);
            }
            int label = (int)Double.parseDouble(columns[i]);
            Instance instance = new Instance(label, data);
            dataset.add(instance);
        }
        return dataset;
    }
    
    // read the csv file in assignment2 for problem 4
    public static List<Instance> readP4Csv(String file) throws FileNotFoundException {
        List<Instance> dataset = new ArrayList<Instance>();
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
            // transfer label "3" as negtive label "-1", label"5" as positive label "+1"
            int label = (Double.parseDouble(columns[0]) ==3? -1: 1);
            Instance instance = new Instance(label, data);
            dataset.add(instance);
        }
        
        return dataset;
    }
    
    // read the csv file in assignment2 for problem 5    
    public static List<Instance> readP5Csv(String file) throws FileNotFoundException {
        List<Instance> dataset = new ArrayList<Instance>();
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
            Instance instance = new Instance(label, data);
            dataset.add(instance);
        }
        
        return dataset;
    }
    
    public static  List<Instance> zScoreNormlization(List<Instance> dataSet){
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

    public static  List<Instance> minMaxNormalization(List<Instance> dataSet){
    	
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

}
