import java.net.*;

public class UdpServer extends Thread {
	private DatagramSocket socket;
	private byte[] buf = new byte[256];

	public UdpServer() {
		try {
			socket = new DatagramSocket(8000);
		} catch(Exception e) {
			e.printStackTrace();
		} 
	}

	public void run() {
		while(true) {
			try {
				DatagramPacket packet = new DatagramPacket(buf, buf.length);
				socket.receive(packet);

				InetAddress address = packet.getAddress();
				int port = packet.getPort();
				packet = new DatagramPacket(buf, buf.length, address, port);
				String received = new String(packet.getData(), 0, packet.getLength());
				System.out.println(received);
				socket.send(packet);
			} catch(Exception e) {
				e.printStackTrace();
			}

		}
	}
}