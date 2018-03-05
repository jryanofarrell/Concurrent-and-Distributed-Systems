import java.util.Scanner;
import java.io.*;
import java.util.*;
public class BookClient {
	
  public static void main (String[] args) {
    String hostAddress;
    int tcpPort;
    int udpPort;
    int clientId;

    if (args.length != 2) {
      System.out.println("ERROR: Provide 2 arguments: commandFile, clientId");
      System.out.println("\t(1) <command-file>: file with commands to the server");
      System.out.println("\t(2) client id: an integer between 1..9");
      System.exit(-1);
    }

    String commandFile = args[0];
    clientId = Integer.parseInt(args[1]);
    hostAddress = "localhost";
    tcpPort = 7000;// hardcoded -- must match the server's tcp port
    udpPort = 8000;// hardcoded -- must match the server's udp port
    Communicator comm;
    UDP_Communicator udp_comm = new UDP_Communicator(hostAddress,udpPort);
    TCP_Communicator tcp_comm = new TCP_Communicator(tcpPort);
    comm = udp_comm;
    String outFile = "out_"+clientId+".txt";
    File yourFile = new File(outFile);
    
    BufferedWriter oFile; 
    FileWriter fw;
    try {
		yourFile.createNewFile();
		fw= new FileWriter(yourFile.getAbsoluteFile(),true);
		oFile = new BufferedWriter(fw);
	} catch (IOException e1) {
		e1.printStackTrace();
	}
    
    try {
        Scanner sc = new Scanner(new FileReader(commandFile));

        while(sc.hasNextLine()) {
          String cmd = sc.nextLine();
          String[] tokens = cmd.split(" ");
          String message = "";
          message += tokens[0];
          if (tokens[0].equals("setmode")) {
        	  if(tokens[1].equals("T"))
        		  comm = tcp_comm;
        	  else
        		  comm = udp_comm;
        	  continue; 
            // TODO: set the mode of communication for sending commands to the server 
          }
          else if (tokens[0].equals("borrow")) {
        	  message += ":"+tokens[1] + ":"+tokens[2];
            // TODO: send appropriate command to the server and display the
            // appropriate responses form the server
          } else if (tokens[0].equals("return")) {
        	  message += ":"+tokens[1];
            // TODO: send appropriate command to the server and display the
            // appropriate responses form the server
          } else if (tokens[0].equals("inventory")) {
            // TODO: send appropriate command to the server and display the
            // appropriate responses form the server
          } else if (tokens[0].equals("list")) {
        	  message += ":"+tokens[1];
            // TODO: send appropriate command to the server and display the
            // appropriate responses form the server
          } else if (tokens[0].equals("exit")) {
            // TODO: send appropriate command to the server 
          } else {
            System.out.println("ERROR: No such command");
            continue;
          }
          String response = comm.send_message(message);
          oFile.write(response);
        }
        
        oFile.close();
        fw.close();
    } catch (FileNotFoundException e) {
	e.printStackTrace();
    }
  }
}
