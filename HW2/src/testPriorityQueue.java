
public class testPriorityQueue implements Runnable {
	int thread_number; 
	final static int SIZE = 2;
	public testPriorityQueue(int thread_number){
		this.thread_number = thread_number; 
	}
	@Override
	public void run() {
		switch(thread_number){
			case 1: 
				thread1_run();
				break;
				
			case 2:
				thread2_run();
				break;
		}
		
	}
	
	public void thread1_run(){
		
	}
	
	public void thread2_run(){
		
	}
	
	public static void main(){
		PriorityQueue queue = new PriorityQueue(10); 
		Thread[] t = new Thread[SIZE];
		
		for (int i = 0; i < SIZE; ++i) {
			t[i] = new Thread(new testPriorityQueue(i+1));
		}
		
		for (int i = 0; i < SIZE; ++i) {
			t[i].start();
		}
    }
}
