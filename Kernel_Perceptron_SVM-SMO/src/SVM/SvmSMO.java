package SVM;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import Utilities.Instance;

public class SvmSMO {

	private int wieghtsDimension;
	private int trainSetSize;

	// the lagrangian multipliers
	private double[] alpha;
	private double bias;

	
	// training set
	private List<Instance> trainSet;

	// constructor function
	public SvmSMO(List<Instance> trainData){

		// store the train set
		trainSet= new ArrayList<Instance>(trainData);

		// get dimension
		wieghtsDimension=trainSet.get(0).getDimension();

		// get training set size
		trainSetSize=trainSet.size();

		//initialize alpha[] and bias as 0;
		alpha =new double[trainSetSize];
		for(int i=0;i<trainSetSize;i++){
			alpha[i]=0;
		}
		bias=0;
		

		
		
	}

	public void trainBySimpSMO(){

		double C=0.001;// regularization parameter 0.001 as default
		double tol=0.8; //numerical tolerance  0.8 as default
		int max_passes=5;// max number of times to iterate over alphas without changing
		int passes=0; // number of times iterated so far over alphas without changing 

		int epoch_num=0;
		
		while(passes<max_passes){
			epoch_num++;
			System.out.println("");
			System.out.println("Epoch "+epoch_num+" begins, trainSetSize="+trainSetSize+"  (If pass this epoch, more epoches to try:"+(max_passes-passes-1)+")");
			int num_changed_alphas=0; // number of components of alpha which changed
			for(int i=0;i<trainSetSize;i++){
				// calculate E_i
				double[] x_i=trainSet.get(i).getX();
				double y_i=trainSet.get(i).getLabel();
				double E_i=decisionFunction(x_i)-y_i;
				
				boolean condition1= y_i*E_i <0-tol && alpha[i]<C ;
				boolean condition2=  y_i*E_i>tol && alpha[i]>0;
				
				if( condition1|| condition2 ){
					// alpha[i] didn't fulfill KTT condition to within some numerical tolerance
					
					// select j randomly
					
					int j=i;
					while (j==i) j=new Random().nextInt(trainSetSize-1);
					
					
					// calculate E_j
					double[] x_j=trainSet.get(j).getX();
					double y_j=trainSet.get(j).getLabel();
					double E_j=decisionFunction(x_j)-y_j;

					//save old alphas
					double alpha_i_old=alpha[i];
					double alpha_j_old=alpha[j];

					//comput L and H
					double L,H;
					if(y_i!=y_j){
						L=Math.max(0, alpha[j]-alpha[i]);
						H=Math.min(C, C+alpha[j]-alpha[i]);
					}else// y_i==y_j
					{
						L=Math.max(0, alpha[i]+alpha[j]-C);
						H=Math.min(C, alpha[i]+alpha[j]);
					}

					if(L==H){
						//System.out.println("Iteration "+iter_num+", no progress, continue for next i, i="+i+", j="+j+" (reason: L==H)");
						continue;
					}


					//compute Eta
					double k_i_j=linearKernel(x_i,x_j);
					double k_i_i=linearKernel(x_i,x_i);
					double k_j_j=linearKernel(x_j,x_j);
					
					double Eta=2*k_i_j-k_i_i-k_j_j;

					if(Eta>=0) {
						//System.out.println("Iteration "+iter_num+", no progress, continue for next i, i="+i+", j="+j+" (reason: Eta>=0)");
						continue;
					}

					// compute and clip new value for alpha[j]
					alpha[j] -= y_j*(E_i-E_j)/Eta;
					
					if(alpha[j]>H){
						alpha[j] =H;
					}else if(alpha[j]<L){
						alpha[j] =L;
					}
					//System.out.println("Iteration "+iter_num+", alpha[j] changed,  i="+i+", j="+j);
					
					if(Math.abs(alpha[j]-alpha_j_old)<0.00001) {
						//System.out.println("Iteration "+iter_num+", no progress, continue for next i, i="+i+", j="+j+" (reason: |alpha[j]-alpha_j_old|<10^-5)");
						continue;
					}
					
					// determine value for alpha[i]
					alpha[i] += y_i*y_j*(alpha_j_old-alpha[j]);
					
					
					//compute b1 and b2
					double b1= bias - E_i - y_i*(alpha[i]-alpha_i_old)*k_i_i - y_j*(alpha[j]-alpha_j_old)*k_i_j;
					double b2= bias - E_j - y_i*(alpha[i]-alpha_i_old)*k_i_j - y_j*(alpha[j]-alpha_j_old)*k_j_j;
					
					if( (0<alpha[i])&&(alpha[i]<C)  ){
						bias=b1;
					}else if( (0<alpha[j])&&(alpha[j]<C) ){
						bias=b2;
					}else{
						bias=(b1+b2)/2.0;
					}

					num_changed_alphas =num_changed_alphas+1;
					System.out.println("a["+i+"] changed,j="+j+", number of a[i]s changed so far:"+num_changed_alphas);
				}// end if 
				//System.out.println("Iteration "+iter_num+", i="+i+", Number of alphas changed: "+num_changed_alphas);
			}// end for


			if(num_changed_alphas==0){
				passes++;
			}else{
				passes=0;
			}
		}// end while

		System.out.println("Training finished");
	}

	public double decisionFunction(double[] new_data){
		double wx_b=bias;
		for(int i=0;i<trainSet.size();i++){
			double[] x_i=trainSet.get(i).getX();
			int y_i=trainSet.get(i).getLabel();
			wx_b += y_i*alpha[i]*linearKernel(x_i,new_data);
		}
		return wx_b; 
	}
	
	public int classify(double[] x_j){
		int label;
		double wx_b=decisionFunction(x_j);
		if(wx_b>=0){
			label=1;
		}else{
			label=-1;
		}
		return label;
	}

	public double linearKernel(double[] x1, double[] x2){
		double dotProduct=0;
		// dot product
		for(int i=0;i<wieghtsDimension;i++){
			dotProduct += x1[i]*x2[i];
		}
		return dotProduct;
	}


}
