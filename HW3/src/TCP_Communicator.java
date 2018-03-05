import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCP_Communicator implements Communicator {
    Socket tcpSocket = null;  
    DataOutputStream os = null;
    DataInputStream is = null;
	int socket_number;
	
	TCP_Communicator(int socket_number){
		this.socket_number=socket_number;
	    try {
	        tcpSocket = new Socket("hostname", socket_number);
	        os = new DataOutputStream(tcpSocket.getOutputStream());
	        is = new DataInputStream(tcpSocket.getInputStream());
	    } catch (UnknownHostException e) {
	        System.err.println("Don't know about host: hostname");
	    } catch (IOException e) {
	        System.err.println("Couldn't get I/O for the connection to: hostname");
	    }
	}

	@Override
	public void send_message(String message) {
		// TODO Auto-generated method stub
		
	}

	

}
