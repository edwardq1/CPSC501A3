// Object that will be containing an array of object references
public class Dealership {
	public Car [] carArray;
	
	public Dealership(){
		carArray = new Car[5];
		for(int i = 0; i < carArray.length; i++)
			carArray[i] = new Car();
	}
}
