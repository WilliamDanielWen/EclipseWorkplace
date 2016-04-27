import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Utility {
	
	
	
	
	// read rating from csv file then select users which rates all the jokes
	public static void extractDenseMatrixToCSV() throws FileNotFoundException {
		String originalDataPath="data//jester-data-1.csv";
		List<String> inputSet = new ArrayList<String>();
		File inputFile=new File(originalDataPath);
		Scanner scanner = new Scanner(inputFile);
		
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.isEmpty()){
				continue;
			}
			String[] content=line.split(",");
			//rating numer
			int rating_num=Integer.parseInt(content[0]);
			if (rating_num==100) inputSet.add(line);
			
		}
	
		
		
	}
}
