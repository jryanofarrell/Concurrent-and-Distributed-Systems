import java.lang.Object;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.SynchronousQueue;


public class FairReadWriteLock {
	
	int reading;
	int writing;
	ArrayList<Integer> next_up; 
	
	public FairReadWriteLock(){
		reading = 0;
		writing = 0;  
		next_up = new ArrayList<Integer>(); 
		
	}
                        
	public synchronized void beginRead() throws InterruptedException {
		next_up.add(1);
		while(writing > 0 || next_up.get(0) == -1){
			wait();
		}
		if(next_up.remove(0) != 1){
			System.out.println("Error began read but next was a write");
		}
		reading ++; 
		 
	}
	
	public synchronized void endRead() {
		reading --; 
		notifyAll();
	}
	
	public synchronized void beginWrite() throws InterruptedException {
		next_up.add(-1);
		while(writing >0 || reading>0 || next_up.get(0) == 1){
			wait();
		}
		if(next_up.remove(0) != -1){
			System.out.println("Error began write but next was a read");
		}
		writing++; 
		
	}
	
	public synchronized void endWrite() {
		writing --;
		notifyAll();
	}
}
	
