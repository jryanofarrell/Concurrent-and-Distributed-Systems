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
	public void send_message(String message) {
		// TODO Auto-generated method stub
		
	}
	

}
