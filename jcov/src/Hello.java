



public class Hello {

	public Hello(){
		int rand=(int)(Math.random()*100);
		if(rand%2==0){
			System.out.println("Hi,0");
		}else{
			System.out.println("Hi,1");
		}
		System.out.println("End");
	}
}
