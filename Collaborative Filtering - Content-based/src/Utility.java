import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

//the class used to store the information of the rating from a user to an item
class Rating  {
	public int user_id;
	public int item_id;
	public double score;
	public Rating(int u_id, int i_id, double s){
		this.user_id=u_id;
		this.item_id=i_id;
		this.score=s;		
	}
}
public class Utility {

	
	public static void extract_FullRatingSet_and_TestSet() throws IOException {
		// step1  read rating from csv file then select users which rates all the jokes 
		String originalDataPath="data//jester-data-1.csv";
		List<String> fullRatingDataSet = new ArrayList<String>();
		File inputFile=new File(originalDataPath);
		Scanner scanner = new Scanner(inputFile);

		int user_order=0;
		// read the full ratings
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.isEmpty()){
				continue;
			}
			String[] content=line.split(",");
			int rating_num=Integer.parseInt(content[0]);
			//rating numer equals 100 indicates users has 
			if (rating_num==100) {
				user_order++;
				String ratings="user_"+String.valueOf(user_order);
				// skip the first column, which is the number of scores a user has rated
				for(int i=1;i<content.length;i++){
					ratings += ","+content[i];
				}
				fullRatingDataSet.add(ratings);
			}

		}

		// step2 write full rating set to file
		String fullRatingFilePath="data//full-ratings.csv";
		FileWriter fwriter=new FileWriter(fullRatingFilePath);
		BufferedWriter out=new BufferedWriter(fwriter);
		String fullRatingHeader="	";
		for(int h=1;h<=100;h++){
			fullRatingHeader +=  (","+"movie_"+String.valueOf(h));
		}
		out.write(fullRatingHeader);
		out.newLine();
		for (int i = 0; i < fullRatingDataSet.size(); i++) {
			out.write(fullRatingDataSet.get(i));
			out.newLine();
		}
		out.close();


		// step3 randomly select some ratings from full-ratings as a test set
		List<Rating>  testSet= new ArrayList<Rating>();
		for(int i=0;i<fullRatingDataSet.size();i++){

			int user_id=i+1; // range from 1-7200
			int joke_id=randomInteger(1,100); // range from 1-100

			int user_index=i; 
			double true_score=Double.parseDouble(fullRatingDataSet.get(user_index).split(",")[joke_id]);
			testSet.add(new Rating(user_id,joke_id,true_score));
		}

		//step4 write test set to file
		String testsetPath="data//testSet.csv";
		String header="user_id,item_id,rating_score";
		wirteDataToCSV(testSet,testsetPath,header);

	}


	// write the data to files
	public static void wirteDataToCSV(List<Rating> dataSet,String outputPath,String header) throws IOException{
		FileWriter fwriter=new FileWriter(outputPath);
		BufferedWriter out=new BufferedWriter(fwriter);
		// write the header
		out.write(header);
		out.newLine();
		for (int i = 0; i < dataSet.size(); i++) {
			Rating r=dataSet.get(i);
			out.write(r.user_id+","+r.item_id+","+r.score);
			out.newLine();
		}
		out.close();
	}
	// randomly generate a Integer  between lowerBound and upperBound
	public static int randomInteger(int lowerBound, int upperBound){
		if(lowerBound>upperBound){
			int temp=lowerBound;
			lowerBound=upperBound;
			upperBound=lowerBound;
		}
		Random rand=new Random();
		int result=lowerBound+rand.nextInt(upperBound-lowerBound);
		return result;
	}

	// randomly generate a double  beween lowerBound and upperBound
	public static double randomDouble(double lowerBound, double upperBound){

		if(lowerBound>upperBound){
			double temp=lowerBound;
			lowerBound=upperBound;
			upperBound=lowerBound;
		}
		Random rand=new Random();
		double result= lowerBound+(upperBound-lowerBound)*rand.nextDouble();
		return result;
	}

}
