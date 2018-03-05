import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

public class UDP_Communicator implements Communicator {

	DatagramSocket udpSocket;
	InetAddress address; 
	int socket_number;
	UDP_Communicator(String host_name, int socket_number){
		this.socket_number = socket_number;
		try {
			udpSocket = new DatagramSocket();
			address = InetAddress.getByName(host_name);
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
