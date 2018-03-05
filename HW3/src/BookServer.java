import java.util.HashMap;
import java.io.*;

public class BookServer {
  private static HashMap<String, Integer> inventory = new HashMap<String, Integer>();
  public static void main (String[] args) {
    int tcpPort;
    int udpPort;
    if (args.length != 1) {
      System.out.println("ERROR: Provide 1 argument: input file containing initial inventory");
      System.exit(-1);
    }
    String fileName = args[0];
    tcpPort = 7000;
    udpPort = 8000;

    // parse the inventory file
    try {
      FileReader input = new FileReader(args[0]);
      BufferedReader bufRead = new BufferedReader(input);
      String myLine = null;

      while ((myLine = bufRead.readLine()) != null) {   
          String[] lineArray = myLine.split(" (?=([^\"]*\"[^\"]*\")*[^\"]*$)");
          System.out.println(lineArray[0] + " " + lineArray[1]);
          String bookTitle = lineArray[0];
          Integer bookQuantity = Integer.valueOf(lineArray[1]);
          inventory.put(bookTitle, bookQuantity);
      }
    } catch(Exception e) {
      System.out.println("Error while parsing inventory file.");
      e.printStackTrace();
    }
    for(String k : inventory.keySet())
      System.out.println(k + " " + inventory.get(k));
    // TODO: handle request from clients
    
  }
}
