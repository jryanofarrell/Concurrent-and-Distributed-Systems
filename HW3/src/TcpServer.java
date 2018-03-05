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

			while(true) {
				line = is.readLine();
				System.out.println(line);
				os.println(line);
			}
		} catch(Exception e) {
			System.out.println(e);
		}
	}
}