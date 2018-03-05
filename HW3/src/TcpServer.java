public class TcpServer extends Thread {
	ServerSocket socket = null;
    String line;
    DataInputStream is;
    PrintStream os;
    Socket clientSocket = null;

    public TcpServer() {
        try {
           socket = new ServerSocket(7000);
        }
        catch (IOException e) {
           System.out.println(e);
        }   
    }
 
    public void run() {
        // Open input and output streams
    	try {
           clientSocket = socket.accept();
           is = new DataInputStream(clientSocket.getInputStream());
           os = new PrintStream(clientSocket.getOutputStream());
			// As long as we receive data, echo that data back to the client.
           while (true) {
             line = is.readLine();
             os.println(line); 
           }
        } catch (IOException e) {
           System.out.println(e);
        }
    }
}