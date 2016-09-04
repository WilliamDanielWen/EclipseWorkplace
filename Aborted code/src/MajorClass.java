
public class MajorClass {
	public static void main(String[] args){
		MinorClass m=new MinorClass();
		m.minorMethod();
	}
}

class MinorClass{
	public void minorMethod(){
		System.out.println("This is a method from Minor Class");
	};
}