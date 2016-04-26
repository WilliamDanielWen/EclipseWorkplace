import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class RandomGuess {
	public static void main(String args[]) throws IOException{
		

		//read the development set
		List<String[]> developmentSet = new ArrayList<String[]>();
        File inputFile=new File("data//developmentSet.csv");
		Scanner scanner = new Scanner(inputFile);
		String header=scanner.nextLine();
		while(scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.startsWith("@")||line.isEmpty()) {
				continue;
			}
			 // get the business id in the development set
			String[] guessLine=new String[2];
			guessLine[0]=line.split(",")[0];
			guessLine[1]=randomLabelsGenerator();
			developmentSet.add(guessLine);
		} 
		
		// output the random guess file
		FileWriter fwriter=new FileWriter("data//randomGuess.csv");
		BufferedWriter out=new BufferedWriter(fwriter);
		
		// write the header
		out.write(header);
		out.newLine();
		for (int i = 0; i < developmentSet.size(); i++) {
			
			String id=developmentSet.get(i)[0];
			String labels=developmentSet.get(i)[1];
			out.write(id+","+labels);
			out.newLine();
		}
		out.close();

		
	}
	
	// generate the random labels
	public static String randomLabelsGenerator(){
		
		String randLabels="";
		for(int i=0;i<9;i++){
			Random rand=new Random();
			int bound=1000000;
			int exists=rand.nextInt(bound); // either 0 or 1 to indicate the existance of ith label
			if(exists<(bound/2)) randLabels += (i+" "); 
		}
		return randLabels;
	}
	
	

}
