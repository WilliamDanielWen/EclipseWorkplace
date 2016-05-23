
public class ReverseString344 {
	
	public String reverseString(String s) {
		if(s==null) return s;
		char[] a=s.toCharArray();
		for(int i=0;i<a.length/2;i++){
			char preceding=a[i];
			a[i]=a[a.length-1-i];
			a[a.length-1-i]=preceding;
		}
		s= new String(a);
		return s;
	}

	public String reverseString_TwoPointer(String s) {
		if(s==null) return s;
		char[] a=s.toCharArray();
		int i=0;
		int j=a.length-1;

		while(i<j){
			char preceding =a[i];
			a[i]=a[j];
			a[j]=preceding;
			i++;
			j--;
		}
		return new String(a);
	
	}
}
