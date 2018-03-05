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
	public void borrow_book(String student_name, String book_name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void return_book(String record_id) {
		// TODO Auto-generated method stub

	}

	@Override
	public void list(String student_name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void inventory() {
		// TODO Auto-generated method stub

	}

	@Override
	public void exit() {
		// TODO Auto-generated method stub

	}

}
