import java.util.HashMap;
import java.util.ArrayList;
import java.io.*;

public class BookServer {
  private static HashMap<String, Integer> inventory = new HashMap<String, Integer>();
  private static ArrayList<String> printOrder = new ArrayList<String>();
  private static ArrayList<Record> currentRecordList = new ArrayList<Record>();
  //ServerSocket MyService; 
  private static int currentRecordID = 1;
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
          printOrder.add(bookTitle);
          inventory.put(bookTitle, bookQuantity);
      }
    } catch(Exception e) {
      System.out.println("Error while parsing inventory file.");
      e.printStackTrace();
    }
    // TODO: handle request from clients
    UdpServer udpHandler = new UdpServer();
    TcpServer tcpHandler = new TcpServer();
    udpHandler.start();
    tcpHandler.start();
  }

  public synchronized static int borrow(String studentName, String bookName) {
    System.out.println("borrow " + studentName + " " + bookName);
    if(inventory.get(bookName) == 0)
      return -1;
    Integer reduced = new Integer(inventory.get(bookName).intValue()-1);
    inventory.put(bookName, reduced);
    currentRecordList.add(new Record(studentName, bookName, currentRecordID));
    return currentRecordID++;
  }

  public synchronized static boolean returnBook(int recordID) {
    Record destroy = null;
    for(Record r : currentRecordList) {
      if(r.recordID == recordID) {
        destroy = r;
        break;
      } 
    }
    if(destroy != null) {
      currentRecordList.remove(destroy);
      return true;
    }
    return false;
  }

  public synchronized static String printInventory() {
	String output = "";
    for(String k : printOrder){
    	output += k + " " + inventory.get(k)+"\n";
    }  
    return output; 
  }

  public static int getInventoryLength() {
    return inventory.keySet().size();
  }

  public synchronized static int getListLength(String studentName) {
    ArrayList<Record> studentRecords = new ArrayList<Record>();
    for(Record r : currentRecordList) {
      if(r.studentName.equals(studentName))
        studentRecords.add(r);
    }
    return studentRecords.size();
  }
}
