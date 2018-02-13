/*
 * EID's of group members
 * 
 */
import java.util.concurrent.Semaphore; // for implementation using Semaphores

public class ThreadSynch {
	
	private int parties;
	private int countdown;
	private Semaphore threadsynch;
	
	private Semaphore mutex; 
	
	private int countup; 
	private Semaphore roundsynch;

	public ThreadSynch(int parties) {
		mutex = new Semaphore(1); 
		threadsynch = new Semaphore(0);
		this.parties = parties;
		countdown = parties; 
		countup = 0; 
		
		roundsynch = new Semaphore(0); 

	}
	
	public int await() throws InterruptedException {
		

		int index = 0; 
		
		mutex.acquire();
		countdown--; 
		index = countdown;
		mutex.release();
		while(countup > 0){
			roundsynch.acquire();
		}
		while(countdown>0){
			threadsynch.acquire();
		}
		
		mutex.acquire();
		countup++; 
		threadsynch.release();
		mutex.release();
		if(countup == parties){
			countup = 0;
			countdown = parties;
			roundsynch.release();
		}
		
		
		
	    return index;
	}
}
