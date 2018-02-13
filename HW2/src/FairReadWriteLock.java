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
		System.out.println("Thread " + Thread.currentThread().getId() + " is beginning reading");
		//next_up.add(1);
		next_up.add((int) Thread.currentThread().getId());
		while(writing > 0 || next_up.get(0) != Thread.currentThread().getId()){
			wait();
		}
		next_up.remove(0);
//		if(next_up.remove(0) != 1){
//			System.out.println("Error began read but next was a write");
//		}
		reading ++; 
		 
	}
	
	public synchronized void endRead() {
		
		System.out.println("Thread " + Thread.currentThread().getId() + " is ending reading");
		reading --; 
		notifyAll();
	}
	
	public synchronized void beginWrite() throws InterruptedException {
		System.out.println("Thread " + Thread.currentThread().getId() + " is beginning writing");
		//next_up.add(-1);
		next_up.add((int) Thread.currentThread().getId());
		while(writing >0 || reading>0 || next_up.get(0) != Thread.currentThread().getId()){
			wait();
		}
		next_up.remove(0);
//		if(next_up.remove(0) != -1){
//			System.out.println("Error began write but next was a read");
//		}
		writing++; 
		
	}
	
	public synchronized void endWrite() {
		System.out.println("Thread " + Thread.currentThread().getId() + " is ending writing");
		writing --;
		notifyAll();
	}
}
	
