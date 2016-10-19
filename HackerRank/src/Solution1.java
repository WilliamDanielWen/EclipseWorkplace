/* Implement your classes here. Do not use access modifiers in your class declarations. */
import java.io.*;
import java.util.*;

class Circle{
    
    private float radius;
    
    public Circle(float r){
        this.radius=r;
    }
    public int getArea(){
    	
        return (int)Math.ceil(3.14159265*this.radius*this.radius);        
    }
}


class Rectangle{
    private float width;
    private float height;
    
    public Rectangle(float w,float h){
        this.width=w;
        this.height=h;
        
    }
    
     public int getArea(){
        return (int) Math.ceil(this.width * this.height);        
    }
}

class Square{
    private float width;
    public Square(float w){
       this.width=w;
        
    }
    public int getArea(){
        return (int) Math.ceil(this.width * this.width);        
    }
    
}




public class Solution1 {
	public static void main(String[] args) throws NumberFormatException, IOException {
        Scanner sc = new Scanner(System.in);        
        
        float radius = Float.parseFloat(sc.nextLine());
        Circle c1 = new Circle(radius);
      	System.out.println(c1.getArea());   
        
        float width = sc.nextFloat();
        float height = sc.nextFloat();
        Rectangle r1 = new Rectangle(width, height);
      	System.out.println(r1.getArea());
      	sc.nextLine();
        
        radius = Float.parseFloat(sc.nextLine());
        Circle c2 = new Circle(radius);
      	System.out.println(c2.getArea());
    
        width = Float.parseFloat(sc.nextLine());
        Square s1 = new Square(width);
      	System.out.println(s1.getArea());
        
      	width = sc.nextFloat();
        height = sc.nextFloat();
        Rectangle r2 = new Rectangle(width, height);
      	System.out.println(r2.getArea());
        
        sc.close();
    }

}
