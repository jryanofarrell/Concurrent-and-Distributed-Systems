import java.io.IOException;
import java.net.DatagramPacket;
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
	public String send_message(String message) {
		
		byte[] buf;
        buf = message.getBytes();
        DatagramPacket packet 
          = new DatagramPacket(buf, buf.length, address, 4445);
        try {
        	System.out.println("udp sending packet");
			udpSocket.send(packet);
			System.out.println("udp sent packet");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        packet = new DatagramPacket(buf, buf.length);
        try {
        	System.out.println("udp receiving packet");
			udpSocket.receive(packet);
			System.out.println("udp recieved packet");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        String received = new String(
          packet.getData(), 0, packet.getLength());
        return received;
		// TODO Auto-generated method stub
		
	}
	

}
