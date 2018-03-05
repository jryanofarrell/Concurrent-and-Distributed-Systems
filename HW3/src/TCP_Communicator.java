import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCP_Communicator implements Communicator {
    Socket tcpSocket = null;  
    DataOutputStream os = null;
    BufferedReader is = null;
	int socket_number;
	
	TCP_Communicator(int socket_number){
		this.socket_number=socket_number;
	    try {
	        tcpSocket = new Socket("localhost", socket_number);
	        os = new DataOutputStream(tcpSocket.getOutputStream());
	        is = new BufferedReader(new InputStreamReader(tcpSocket.getInputStream()));
	    } catch (UnknownHostException e) {
	        System.err.println("Don't know about host: hostname");
	    } catch (IOException e) {
	        System.err.println("Couldn't get I/O for the connection to: hostname");
	    }
	}

	@Override
	public String send_message(String message) {
		try {
			os.writeBytes(message+"\r");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String response = "";
		String line;
		try {
			//while((line = is.readLine()) != null){
			response = is.readLine();
			//}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return response; 
		// TODO Auto-generated method stub
		
	}
}
