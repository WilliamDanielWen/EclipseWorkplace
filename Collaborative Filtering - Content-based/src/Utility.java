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



	public static void extractDenseMatrixToCSV() throws IOException {
		// step1  read rating from csv file then select users which rates all the jokes
		String originalDataPath="data//jester-data-1.csv";
		List<String> fullRatingDataSet = new ArrayList<String>();
		File inputFile=new File(originalDataPath);
		Scanner scanner = new Scanner(inputFile);

		// read the full ratings
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.isEmpty()){
				continue;
			}
			String[] content=line.split(",");
			//rating numer
			int rating_num=Integer.parseInt(content[0]);
			if (rating_num==100) {
				
				fullRatingDataSet.add(line);
			}

		}

		// step2 write to file
		String fullRatingFilePath="data//full-ratings.csv";
		FileWriter fwriter=new FileWriter(fullRatingFilePath);
		BufferedWriter out=new BufferedWriter(fwriter);
		for (int i = 0; i < fullRatingDataSet.size(); i++) {
			out.write(fullRatingDataSet.get(i));
			out.newLine();
		}
		out.close();

		
		// step3 randomly select some ratings from full-ratings
		List<Rating>  ratingsToPredict= new ArrayList<Rating>();
		for(int i=0;i<fullRatingDataSet.size();i++){
			
			int user_id=i+1; // range from 1-7200
			int joke_id=randomInteger(1,100); // range from 1-100
			
			int user_index=i; 
			int joke_index=joke_id-1;
			double true_score=Double.parseDouble(fullRatingDataSet.get(user_index).split(",")[joke_index]);
			ratingsToPredict.add(new Rating(user_id,joke_id,true_score));
		}
	}

	// randomly generate a Integer  between lowerBound and upperBound
	public static int randomInteger(int lowerBound, int upperBound){
		if(lowerBound>upperBound){
			int temp=lowerBound;
			lowerBound=upperBound;
			upperBound=lowerBound;
		}
		Random rand=new Random(System.currentTimeMillis());
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
		Random rand=new Random(System.currentTimeMillis());
		double result= lowerBound+(upperBound-lowerBound)*rand.nextDouble();
		return result;
	}

}
