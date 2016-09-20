
public class NO_5_LongestPalindromicSubstring {
	public static void main(String[] args){
		
	}
	
	
	public String longestPalindrome(String s) {
        
		char[] s_array=s.toCharArray();
        int s_len=s.length();
        if (s == null || s_len==1) return s;
        
        int max_len=0;
        int max_start=0;
        int max_end=0;
        int[][] dp;
        
        for (int i=s_len-2;i>=0;i--){
        	for (int j=i+1;j<=s_len-1;j++){
        		
        		if((s_array[i]==s_array[j])&&( j-i<3 )){
        			
        			
        		}
        		
        	}
        	
        }
        
        return s.substring(max_start,max_end);
        
        
    }

}
