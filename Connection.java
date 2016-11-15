import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

public class Connection {
	//default port
	private int port = 999;
	
	
	public Connection(String userType, int port, String doc) throws Exception{
		this.port = port;
		if (userType.equals("send")){
			send(doc);
		}
		else{
			receive();
		}
	}
	
	public void send(String doc) throws IOException{
		ServerSocket server = new ServerSocket(port);
		System.out.println("Waiting for connection...");
		System.out.println(server.getInetAddress());
		Socket senderSocket = server.accept();
		System.out.println("Connection successful.");
		sendFile(doc, senderSocket);
		
	}
	
	public void sendFile(String doc, Socket sender) throws IOException{
		File fileToSend = new File(doc);
		byte[] byteArray = new byte[(int)fileToSend.length()];
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileToSend));
		OutputStream os = sender.getOutputStream();
		os = sender.getOutputStream();
		System.out.println("Sending file: " + doc);
		os.write(byteArray, 0, byteArray.length);
		os.flush();
		System.out.println("File sent);");
		sender.close();
	}
	
	public void receive() throws Exception{
		Scanner in = new Scanner(System.in);
		System.out.println("What is the IP of the server you want to connect to?");
		String ip = in.next(); // only works for internal ip
		Socket receiverSocket = new Socket(ip, port);
		System.out.println("You have successfully connected to " + ip);
		String fileReceived = "output.xml";
	    receiveFile(fileReceived, receiverSocket);
	         
	    SAXBuilder saxBuilder = new SAXBuilder();
	    Document doc = saxBuilder.build(fileReceived);
        Deserializer deserialize = new Deserializer();
        Object obj = deserialize.deserializeObject(doc);
        Inspector inspector = new Inspector();
	    inspector.inspect(obj, true);
	}
	
	public void receiveFile(String doc, Socket socket) throws IOException{

		byte [] byteArray = new byte[1000000];
		InputStream is = socket.getInputStream();
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(doc));
		int bytesRead = is.read(byteArray, 0, byteArray.length);
		
		bos.write(byteArray, 0, bytesRead);
		bos.close();
		socket.close();
		System.out.println("File received.");
	}
}
