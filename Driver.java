import java.io.IOException;
import java.util.Scanner;

public class Driver {

	public static void main(String[] args) throws IOException{
		//Test if the user is a sender or receiver
		String info = args[0].toLowerCase();
		if (info.equals("send")){
			Scanner in =  new Scanner(System.in);
			System.out.println("You are a sender...");
			System.out.print("Enter the port: ");
			int port = in.nextInt();
			Connection c = new Connection(info, port);
		}
		else if (info.equals("receive")){
			Scanner in =  new Scanner(System.in);
			System.out.println("You are a receiver...");
			System.out.println("Enter the port: ");
			int port = in.nextInt();
			Connection c = new Connection(info, port);
		}
		else{
			System.out.println("Incorrect information...");
		}
	}
}
