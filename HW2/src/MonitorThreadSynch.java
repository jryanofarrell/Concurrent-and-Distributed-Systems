import java.lang.Object;

/*
 * EID's of group members
 * 
 */

public class MonitorThreadSynch {
		
	private int countdown;
	private int countup;
	private int parties;
	public MonitorThreadSynch(int parties) {
		this.parties = parties;
		countdown = parties;
		countup = 0; 
	}
	
	public synchronized int await() throws InterruptedException {
		
   		int index = 0;
   		countdown--; 
   		index = countdown;
   		while(countup > 0){
   			wait();
   		}
   		
   		while(countdown>0){
   			wait();
   		}
   		
   		countup++;
   		if(countup == parties){
   			countup = 0;
   			countdown = parties;
   		}
   		notifyAll(); 
   		
   		
   		
	    return index;
	}
}
