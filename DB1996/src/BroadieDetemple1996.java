
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Math;

public class BroadieDetemple1996 {

	private int N;
	private double K;
	private double T;
	private double delta_t;
	
	private double r;
	private double S0;
	
	private double delta;
	private double sigma;
	
	
	private double u;
	private double d;
	private double q;

	public double[][] V;
	public double[][] S;
	
    
	
	public void setStepsAndCalculate(int steps){
		//Initialize the constants
		N=steps;
		K=105;
		T=0.5;
		delta_t=(double)T/N;
		
		r=0.1;
		S0=100;
		
		//used in BS function to calculate d1 and d2
		delta=0;
		sigma=0.3;
		
		
		u=Math.exp(sigma*Math.sqrt(delta_t));
		d=Math.exp(-sigma*Math.sqrt(delta_t));
		q=(Math.exp(r*delta_t)-d)/(u-d);

		V= new double[N+1][N+1];
		S= new double[N+1][N+1];
		
		// compute the V[j][i] and S[j][i]
	    for(int j=N; j>=0;j--){
	    	for (int i=j;i>=0;i--){
	    		//compute the S[j][i]
	    		S[j][i]=S0*Math.pow(u, i)*(Math.pow(d, j-i));
	    		
	    		//compute the V[j][i]
	    		if(j==N){
	    			
	    			V[N][i]=Math.max(K-S[N][i],0);
	    			
	    		}else if(j==N-1){
	    			
	    			V[N-1][i]=Math.max( BS(S[N-1][i], delta_t)  , (K - S[N-1][i])  );
	    			
	    		}else{//j<N-1
	    			
	    			double V_h_j_i=Math.exp(-r*delta_t)*(q*V[j+1][i+1]+(1-q)*V[j+1][i]);//?
	    		    double V_x_j_i= K-S[j][i];	
	    			V[j][i]=Math.max(V_h_j_i,V_x_j_i);
	    		}
	    		
	    	}
	    }
	    
	}
  

    public double BS(double S, double Tao){
    	double result=0;
    	double d1=(Math.log(S/K)+Tao*(r-delta+0.5*sigma*sigma))/(sigma*Math.sqrt(Tao));
    	double d2=(Math.log(S/K)+Tao*(r-delta-0.5*sigma*sigma))/(sigma*Math.sqrt(Tao));
    	double N_neg_d2=normal_distribution(-d2);
    	double N_neg_d1=normal_distribution(-d1);
    	result=K*Math.exp(-(r*Tao))*N_neg_d2-S*Math.exp(-(delta*Tao))*N_neg_d1;
    	return result;
    }
    
	 public double normal_distribution(double x){
		 //implement the probability density function of standard Gaussian distribution(N~(0,1))
	    	double p=(1/Math.sqrt(2*Math.PI))*Math.exp(-x*x/2);
	    	return p;
	 }
    
	 public static void main(String args[]) throws IOException  {
		 BroadieDetemple1996 demon=new BroadieDetemple1996();
		 
		 FileWriter fStream = new FileWriter("Even Number Steps Report.txt");     // Output File for Even number steps
	     BufferedWriter out = new BufferedWriter(fStream);
	     out.write("Following is even number steps:");
	     out.newLine();
		 for(int stem_num=50;stem_num<=1000;stem_num +=2){
			 demon.setStepsAndCalculate(stem_num);
		     out.write("When N="+stem_num+",  "+"V[0][0]"+" = "+ String.valueOf(demon.V[0][0])+"  ");
		     out.newLine();
		     System.out.println("When N="+stem_num+",  "+"V[0][0]"+" = "+ String.valueOf(demon.V[0][0])+"  ");
		 }
		 out.close();
		 
		 fStream = new FileWriter("Odd Number Steps Report.txt");     // Output File for Odd number steps
		 out = new BufferedWriter(fStream);
		 out.write("Following is odd number steps:");
	     out.newLine();
		 for(int stem_num=51;stem_num<=999;stem_num +=2){
			 demon.setStepsAndCalculate(stem_num);
		     out.write("When N="+stem_num+",  "+"V[0][0]"+" = "+ String.valueOf(demon.V[0][0])+"  ");
		     out.newLine();
		     System.out.println("When N="+stem_num+",  "+"V[0][0]"+" = "+ String.valueOf(demon.V[0][0])+"  ");
		 }
		 out.close();
	 }
    
}
