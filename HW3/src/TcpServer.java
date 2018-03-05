import java.io.*;
import java.net.*;

public class TcpServer extends Thread {
	ServerSocket socket = null;
	String line;
	BufferedReader is;
	PrintStream os;
	Socket clientSocket = null;

	public TcpServer() {
		try {
			socket = new ServerSocket(7000);
		} catch(IOException e) {
			System.out.println(e);
		}
	}

	public void run() {
		try {
			clientSocket = socket.accept();
	        is = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			os = new PrintStream(clientSocket.getOutputStream());
			boolean running = true;
			while(running) {
				line = is.readLine();
				String[] commandTokens = line.split(":");
				for(String s : commandTokens)
					System.out.println(s);
				switch(commandTokens[0]) {
					case "borrow":
						System.out.println("borrow");
						int recordID = BookServer.borrow(commandTokens[1], commandTokens[2]);
						if(recordID != -1) {
							//request approved
							os.println("Your request has been approved, " + recordID + " " + commandTokens[1] + " " + commandTokens[2]);
						} else {
							//request denied
							os.println("Request Failed - We do not have this book");
						}
						break;
					case "return":
						System.out.println("return");
						break;
					case "list":
						System.out.println("list");
						break;
					case "inventory":
						System.out.println("inventory");
						break;
					case "exit":
						System.out.println("exit");
						running = false;
						break;
				}
				os.println(line+"\r\n");
			}
			socket.close();
		} catch(Exception e) {
			System.out.println(e);
		}
	}
}