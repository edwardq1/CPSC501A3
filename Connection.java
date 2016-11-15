import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Connection {
	//default port
	private int port = 999;
	
	
	public Connection(String userType, int port) throws IOException{
		this.port = port;
		if (userType.equals("send")){
			send();
		}
		else{
			receive();
		}
	}
	
	public void send() throws IOException{
		ServerSocket server = new ServerSocket(port);
		System.out.println("Waiting for connection...");
		System.out.println(server.getInetAddress());
		Socket senderSocket = server.accept();
		System.out.println("Connection successful.");
		
	}
	
	public void receive() throws UnknownHostException, IOException{
		Scanner in = new Scanner(System.in);
		System.out.println("What is the IP of the server you want to connect to?");
		String ip = in.next(); // only works for internal ip
		Socket receiverSocket = new Socket(ip, port);
		System.out.println("You have successfully connected to " + ip);
	}
}
