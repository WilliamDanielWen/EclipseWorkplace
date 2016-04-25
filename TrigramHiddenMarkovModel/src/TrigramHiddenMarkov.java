import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class TrigramHiddenMarkov {

	public static void main(String[] args) throws IOException{
		training("datasets//UD_English//train.counts");
		inferenceCorpus("datasets//UD_English//test.words","datasets//UD_English//predicted_results_on_test_counts.txt");
	}

	// format: <"WORDTAG->word", frequency>
	private static Hashtable<String, Integer>  WordTagFrequencyTable;

	// format: <"WORDTAG", frequency>
	private static Hashtable<String, Integer>  UnigramFrequencyTable;

	// format: <"1st-WORDTAG->2nd-WORDTAG", frequency>
	private static Hashtable<String, Integer>  BigramFrequencyTable;

	// format: <"1st-WORDTAG->2nd-WORDTAG->3rd-WORDTAG", frequency>
	private static Hashtable<String, Integer>  TrigramFrequencyTable;

	//format: <"word", frequency>
	private static Hashtable<String, Integer> WordFrequencyTable;

	// list of possible Unigram
	private static Set<String> UnigramList;

	// the constant used to map the infrequent words
	private static String RareWord="_RARE_";


	// Read the training data and 
	// construct the EmissionTable,UnigramTable,BigramTable,TrigramTable by using these data	 
	public static  void training(String trainFilePath) throws FileNotFoundException{

		System.out.println("Begining training...");

		buildWordFrequecyTable(trainFilePath);

		File trainFile=new File(trainFilePath);
		Scanner scanner = new Scanner(trainFile);
		//Initialization
		WordTagFrequencyTable=new Hashtable<String, Integer>();
		UnigramFrequencyTable=new Hashtable<String, Integer>();
		BigramFrequencyTable=new Hashtable<String, Integer>();
		TrigramFrequencyTable=new Hashtable<String, Integer>();

		// read data into above table
		int lineNumber=1;
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			if (line.isEmpty()) continue;
			String[] lineArray = line.split(" ");
			int entry_frequency=Integer.parseInt(lineArray[0]);
			String category=lineArray[1];
			String item; //key in the hash table
			int item_frequency;
			switch(category){

			case "WORDTAG":
				String word_tag=lineArray[2];
				String word=lineArray[3].toLowerCase();
				//  check to see wheter encounter a rare word
				if(!WordFrequencyTable.containsKey(word)) word=RareWord;
				//item format:"WORDTAG->word"
				item=word_tag+"->"+word;
				if(WordTagFrequencyTable.containsKey(item)){
					int increased_frequency= entry_frequency + WordTagFrequencyTable.get(item);
					WordTagFrequencyTable.remove(item);
					WordTagFrequencyTable.put(item, increased_frequency);
				}else{
					WordTagFrequencyTable.put(item, entry_frequency);
				}
				break;

			case "1-GRAM":
				// item format: "tag1"
				item=lineArray[2];
				if(UnigramFrequencyTable.containsKey(item)){
					int increased_frequency= entry_frequency + UnigramFrequencyTable.get(item);
					UnigramFrequencyTable.remove(item);
					UnigramFrequencyTable.put(item, increased_frequency);
				}else{
					UnigramFrequencyTable.put(item, entry_frequency);
				}
				break;

			case "2-GRAM":
				// item format "tag1->tag2"
				item=lineArray[2]+"->"+lineArray[3];
				if(BigramFrequencyTable.containsKey(item)){
					int increased_frequency= entry_frequency + BigramFrequencyTable.get(item);
					BigramFrequencyTable.remove(item);
					BigramFrequencyTable.put(item, increased_frequency);
				}else{
					BigramFrequencyTable.put(item, entry_frequency);
				}
				break;

			case "3-GRAM":
				// item="tag1->tag2->tag3"
				item=lineArray[2]+"->"+lineArray[3]+"->"+lineArray[4];
				if(TrigramFrequencyTable.containsKey(item)){
					int increased_frequency= entry_frequency + TrigramFrequencyTable.get(item);
					TrigramFrequencyTable.remove(item);
					TrigramFrequencyTable.put(item, increased_frequency);
				}else{
					TrigramFrequencyTable.put(item, entry_frequency);
				}
				break;
			default :
				System.out.println("Input file error format in line "+lineNumber);
				break;
			}
			lineNumber++;
		}

		// construc TagList according to UnigramTable;
		UnigramList=UnigramFrequencyTable.keySet();
		System.out.println("Training finished!");
	}

	// decoding a corpus
	public static void inferenceCorpus(String inferenceCorpusPath,String outputFilePath ) throws IOException{

		String[] inferencePorpusArgs=inferenceCorpusPath.split("//");
		String corpusFileName=inferencePorpusArgs[inferencePorpusArgs.length-1];
		System.out.println("\nDecoding the corpus file "+corpusFileName+" .........");

		// read the corpus
		List<String> corpusSentences=readInferenceCorpusToSentences(inferenceCorpusPath);
		int corpusSize=corpusSentences.size();
		// inference each sentence and output the result
		FileWriter fStream = new FileWriter(outputFilePath);
		BufferedWriter out = new BufferedWriter(fStream);

		long startTime=(System.currentTimeMillis())/1000;
		// decoding each sentence and output to file
		for(int i=0;i<corpusSize;i++){
			// output the current progress
			if((i+1)%50==0){
				System.out.println("\nNumber of decoded sentences: "+(i+1));
				System.out.println("Number of remaining sentences to be decoded : "+(corpusSize-(i+1))+" .....");
			}

			String[] words=corpusSentences.get(i).split(" ");
			String[] tags=inferenceSingleSentence(words);
			for(int j=0;j<words.length;j++){
				out.write(words[j]);
				out.write(" ");
				out.write(tags[j]);
				out.newLine();
			}
			out.newLine();
		}
		out.close();
		System.out.println("\nDecoding finished, result path: "+outputFilePath);
		long endTime = (System.currentTimeMillis())/1000;
		long runningTime = endTime-startTime;
		long sec= runningTime%60;
		long minutes= runningTime/60;
		System.out.println("\nRunning time for decoding the whole corpus: "+minutes+" minutes, "+sec+" seconds\n");

	}

	// build the WordFrequecyTable by using training data
	private static void buildWordFrequecyTable(String trainFilePath) throws FileNotFoundException{
		//construct the original word frequency table
		File trainFile=new File(trainFilePath);
		Scanner scanner = new Scanner(trainFile);
		WordFrequencyTable=new Hashtable<String, Integer>();
		while(scanner.hasNextLine()){
			String line = scanner.nextLine();
			if (line.isEmpty()) continue;
			String[] lineArray = line.split(" ");
			int entry_frequency=Integer.parseInt(lineArray[0]);// frequency of WORDTAG-word
			String category=lineArray[1];
			if(category.equals("WORDTAG")){
				String word=lineArray[3].toLowerCase();
				if(WordFrequencyTable.containsKey(word)){
					int increased_freqency=WordFrequencyTable.get(word)+entry_frequency;
					WordFrequencyTable.remove(word);
					WordFrequencyTable.put(word, increased_freqency);
				}else{
					WordFrequencyTable.put(word, entry_frequency);
				}

			}
		}

		// map the infrequent word into "_RARE_"
		Set<String> wordsAppeared=new HashSet<String>(WordFrequencyTable.keySet());
		for(String word : wordsAppeared){
			int word_count=WordFrequencyTable.get(word);
			if(word_count<2){
				//infrequent word detected
				WordFrequencyTable.remove(word);

				if(WordFrequencyTable.containsKey(RareWord)){
					int newCount=word_count+WordFrequencyTable.get(RareWord);
					WordFrequencyTable.remove(RareWord);
					WordFrequencyTable.put(RareWord, newCount);
				}else{
					WordFrequencyTable.put(RareWord, word_count);
				}

			}

		}

	}

	// read the sentence data, and return each sentence as a string, each word is converted to lower case
	private static List<String> readInferenceCorpusToSentences(String inferenceCorpusPath) throws FileNotFoundException{
		List<String> testSentences=new ArrayList<String>();
		// read the test data   inferenceCorpusPath
		File trainFile=new File(inferenceCorpusPath);
		Scanner scanner = new Scanner(trainFile);

		String sentenceBuffer="";
		while(scanner.hasNextLine()){
			String word = scanner.nextLine().toLowerCase();

			if (word.isEmpty()){
				// an sentence ends, add the last sentence and empty the buffer
				testSentences.add(sentenceBuffer);
				sentenceBuffer="";
				continue;
			}
			// not an end of sentence, append each word in a string
			sentenceBuffer += word+" ";
		}
		return testSentences;
	}

	// using the viterbi algorithm to inference each tag in sentenceArray
	private static String[] inferenceSingleSentence(String[] inputSentence){

		// map unseen words in inputSentence into ReareWord, store the results in mappedInputWords
		String[] mappedInputSentence=new String[inputSentence.length];
		for(int i=0;i<mappedInputSentence.length;i++){
			mappedInputSentence[i]=(WordFrequencyTable.containsKey(inputSentence[i]) ?  inputSentence[i]:  RareWord);
		}

		// the value for pi(k,u,v): the maximum probability for any sequence of length k, ending in
		// write the table should use the function "setPi"
		// read the table should use the function "getPi"
		Hashtable<String, Double> pi=new Hashtable<String, Double>();

		// the value for bp(k,u,v): the tag w which produce pi(k,u,v)
		// write the table should use the function "setBp"
		// read the table should use the function "getBp"
		Hashtable<String, String> bp=new Hashtable<String, String>();

		// initialization for the dp algorithm
		// set PI(0,*,*)=1.
		setPi(pi,0,"*","*",1d);

		// set PI(0,u,v)=0 for u is not "*", or v is not "*"
		Set<String> extendedTagList=new HashSet(UnigramList);
		extendedTagList.add("*");
		for(String u:extendedTagList){
			for(String v: extendedTagList){
				if( (!u.equals("*")) || (!v.equals("*"))  ) setPi(pi,0,u,v,0d);	
			}
		}

		String[] predictedTags=new String[mappedInputSentence.length];
		// for k=1...n+1 construct pi(k,u,v), bp(k,u,v)
		for(int i=0;i<predictedTags.length;i++){
			String currentWord=mappedInputSentence[i];
			// k is  current length
			int k=i+1;
			if(k==1){
				// u=*, w=* when k=1
				String w="*";
				String u="*";
				for(String v: UnigramList){
					double pi_previous=getPi(pi,k-1,w,u);// this is (pi,0,"*","*")should be 1
					double q=q(v,w,u); //this is transProb(v,"*","*"); 
					double e=e(currentWord,v);					
					double prob=pi_previous*q*e; // 1*transProb(v,*,*)*e_word_v a start probability
					setPi(pi,k,u,v,prob); //here pi is the start probability pi(1,"*",v)=prob
				}
			}else if(k==2){
				//w=* when k=2
				String w="*";
				for(String u: UnigramList){
					for(String v: UnigramList){
						double pi_previous=getPi(pi,k-1,w,u);// previous result, start prob for u
						double q=q(v,w,u); // start prob for trans prob from *,u to v
						double e=e(currentWord,v);
						double prob=pi_previous*q*e; // start porb for *,u,v
						setPi(pi,k,u,v,prob);
					}
				}

			}else{
				//k=3..n+1
				for(String u: UnigramList){
					for(String v: UnigramList){
						List u_v_with_possible_w_list=new ArrayList<ProbabilityEntry>();
						for(String w: UnigramList){
							double pi_previous=getPi(pi,k-1,w,u);
							double q=q(v,w,u);
							double e=e(currentWord,v);
							double prob=pi_previous*q*e;
							ProbabilityEntry candidate=new ProbabilityEntry(w,u,v,prob);
							u_v_with_possible_w_list.add(candidate);
						}
						u_v_with_possible_w_list.sort(new ProbabilityEntryComparator());
						double opt_prob=((ProbabilityEntry) u_v_with_possible_w_list.get(0)).getProb();
						String opt_w=((ProbabilityEntry) u_v_with_possible_w_list.get(0)).getW();
						setPi(pi,k,u,v,opt_prob);
						setBp(bp,k,u,v,opt_w);
					}
				}

			}// end else
		}// end for(i=0..predictedTags.length-1)

		// use the back pointer to decode the tags
		int n=mappedInputSentence.length;
		if(n>=2){
			// decode the last two words
			List possible_optimal_end_list=new ArrayList<ProbabilityEntry>();
			for(String u: UnigramList){
				for(String v: UnigramList){
					double pi_previous=getPi(pi,n,u,v);
					double q=q("STOP",u,v);
					double prob=pi_previous*q;
					ProbabilityEntry candidate=new ProbabilityEntry(u,v,prob);
					possible_optimal_end_list.add(candidate);
				}
			}
			possible_optimal_end_list.sort(new ProbabilityEntryComparator());
			ProbabilityEntry optCandidate=(ProbabilityEntry) possible_optimal_end_list.get(0);
			// set the optimal ending
			predictedTags[n-1]=optCandidate.getV();
			predictedTags[n-2]=optCandidate.getU();
			if(n>=3){
				for(int k=n-3;k>=0;k--){
					predictedTags[k]=getBp(bp,k+3,predictedTags[k+1],predictedTags[k+2]);
				}
			}

		}else{
			
			List possible_optimal_v_list=new ArrayList<ProbabilityEntry>();
			for(String v: UnigramList){
				double pi_previous=getPi(pi,1,"*",v);
				double q=q("STOP","*",v);
				double prob=pi_previous*q;
				ProbabilityEntry candidate=new ProbabilityEntry(v,prob);
				possible_optimal_v_list.add(candidate);
			}
			possible_optimal_v_list.sort(new ProbabilityEntryComparator());
			ProbabilityEntry optCandidate=(ProbabilityEntry) possible_optimal_v_list.get(0);
			predictedTags[0]=optCandidate.getV();
		}

		return predictedTags;
	}


	// emission probability e(word|tag)
	// word should be in lower case or be converted to RareWord
	private static double e(String word, String tag){

		// tag_count will not be zero
		double tag_count=UnigramFrequencyTable.get(tag);

		String item=tag+"->"+word;
		double tag_word_count=(double)(WordTagFrequencyTable.containsKey(item)? WordTagFrequencyTable.get(item): 0);

		double emission_prob= tag_word_count/tag_count;
		return emission_prob;
	}


	// second order markov transition probability q(s|u,v), which indicates the probability
	// of seeing s after u,v
	private static double q(String s, String u,String v){

		String item_u_v=u+"->"+v;
		double u_v_count=(double)(BigramFrequencyTable.containsKey(item_u_v)? BigramFrequencyTable.get(item_u_v) : 0d);
		if(u_v_count==0d) return 0d;


		//for now,u_v_count is not zero
		String item_u_v_s=u+"->"+v+"->"+s; // s appears after bigram u,v
		double u_v_s_count=(double)(TrigramFrequencyTable.containsKey(item_u_v_s)? TrigramFrequencyTable.get(item_u_v_s) : 0d);

		double transi_prob=u_v_s_count/u_v_count;

		return transi_prob;
	}


	// function to read the pi table
	private static double getPi(Hashtable<String, Double> pi,int k,String u, String v){
		String item="pi( "+Integer.toString(k)+","+u+","+v+" )";
		return pi.get(item);
	}

	// function to set the pi table
	private static void setPi(Hashtable<String, Double> pi,int k, String u, String v, double value){
		String item= "pi( "+Integer.toString(k)+","+u+","+v+" )";
		pi.put(item, value);
	}

	// function to read the bp table
	private static String getBp(Hashtable<String, String> bp,int k,String u, String v){
		String item= Integer.toString(k)+" "+u+" "+v;
		return bp.get(item);
	}

	// function to set the bp table
	private static void setBp(Hashtable<String, String> bp,int k, String u, String v, String value){
		String item= Integer.toString(k)+" "+u+" "+v;
		bp.put(item, value);
	}

}


// store the a probability and its related information about tags
class ProbabilityEntry{
	private String w; // the tag before u, v if it exists
	private String u; // the tag before v, if it exists
	private String v; // a tag
	private double probability; 

	ProbabilityEntry(String w, String u,String v,double prob){
		this.w=w;
		this.u=u;
		this.v=v;
		this.probability=prob;
	}

	ProbabilityEntry( String u,String v,double prob){
		this.u=u;
		this.v=v;
		this.probability=prob;
	}
	
	ProbabilityEntry(String v, double prob){
		this.v=v;
		this.probability=prob;
	}
	public String getW(){
		return w;
	}
	public String getU(){
		return u;
	}
	public String getV(){
		return v;
	}

	public double getProb(){
		return probability;
	}
}


//This Comparator is defined to compare the PiEntry. Sort the PiCandidate in a descending order
class ProbabilityEntryComparator implements Comparator<ProbabilityEntry>{

	public int compare(ProbabilityEntry a, ProbabilityEntry b) {
		// a.newPoint and b.newPoint must be the same point
		return a.getProb() < b.getProb() ? 1 : (a.getProb() == b.getProb() ? 0 : -1);
	}
}

