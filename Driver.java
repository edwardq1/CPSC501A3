import java.io.BufferedOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

public class Driver {

	public static void main(String[] args) throws Exception{
		//Test if the user is a sender or receiver
		Object o = null;
		String info = args[0].toLowerCase();
		if (info.equals("send")){
			Scanner in =  new Scanner(System.in);
			System.out.println("You are a sender...");
			System.out.print("Enter the port: ");
			int port = in.nextInt();
			System.out.print("How many objects would you like to serialize? ");
			int input = in.nextInt();
			ObjectCreator oc = new ObjectCreator();
			for(int i = 0; i < input; i++){
				int temp = oc.objectCreator();
				if (temp ==1){
					Car car = new Car();
					o = oc.initializeObject(car);
				}
				else if (temp ==2){
					Mercedes mercedes = new Mercedes();
					o = oc.initializeObject(mercedes);
				}
				else if (temp ==3){
					Year year = new Year();
					o = oc.initializeObject(year);
				}
				else if (temp ==4){
					Collections collection = new Collections();
					o = oc.initializeObject(collection);
				}
				else if (temp ==5){
					Dealership dealership = new Dealership();
					o = oc.initializeObject(dealership);
				}
				System.out.println("Object has been successfully added.");
			}
			System.out.println("****Objects are now ready to be sent.****");
	        String OUTPUTFILE = "output.xml";
	        Serializer serialize = new Serializer();
			Document doc = serialize.serialize(o);
		    XMLOutputter xmlOutput = new XMLOutputter();
		        
		    xmlOutput.setFormat(Format.getPrettyFormat());
		    xmlOutput.output(doc, new FileWriter(OUTPUTFILE));
		        
			Connection c = new Connection(info, port, OUTPUTFILE);
		}
		else if (info.equals("receive")){
			Scanner in =  new Scanner(System.in);
			System.out.println("You are a receiver...");
			System.out.println("Enter the port: ");
			int port = in.nextInt();
			Connection c = new Connection(info, port, "");

		}
		else{
			System.out.println("Incorrect information...");
		}
	}
}
