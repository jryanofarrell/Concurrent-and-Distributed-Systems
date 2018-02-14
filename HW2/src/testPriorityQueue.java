import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class testPriorityQueue implements Runnable {
	final static int SIZE = 5;
	static int count = 20;
	PriorityQueue queue;

	public testPriorityQueue(PriorityQueue queue) {
		this.queue = queue;
	}
	
	public void run() {
		while(true) {
			if(ThreadLocalRandom.current().nextInt(0, 1 + 1) == 1) {
				try {
					System.out.println("Thread " + Thread.currentThread().getId() + " getting first " + queue.getFirst());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} else {
				int priority = ThreadLocalRandom.current().nextInt(0, 9 + 1);
				String str = generateString();
				System.out.println("Thread " + Thread.currentThread().getId() + " adding " + str + " priority " + priority);
				try {
					queue.add(str, priority);
					//System.out.println(queue.search(str));
					queue.print_list();
					Thread.sleep(1000);
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

    public static String generateString() {
        String uuid = UUID.randomUUID().toString();
        return "uuid = " + uuid;
    }

	public static void main(String[] args) {
		Thread[] t = new Thread[SIZE];
		PriorityQueue queue = new PriorityQueue(25);
		Thread test1 = new Thread() {
		      public void run(){
		    	  int priority = ThreadLocalRandom.current().nextInt(0, 9 + 1);
					String str = generateString();
					System.out.println("Thread " + Thread.currentThread().getId() + " adding " + str + " priority " + priority);
					try {
						queue.add(str, priority);
						//System.out.println(queue.search(str));
						queue.print_list();
						Thread.sleep(1000);
					} catch(Exception e) {
						e.printStackTrace();
					}
		        }
		 };
		Thread test2 = new Thread()  {
		      public void run(){
		    	  
		    	  try {
		    		  
						System.out.println("Thread " + Thread.currentThread().getId() + " getting first " + queue.getFirst());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}		        
		      }
		 };
		 test1.start();
		 try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 test2.start();
		 /*
		for (int i = 0; i < SIZE; ++i) {
			t[i] = new Thread(new testPriorityQueue(queue));
		}
		for (int i = 0; i < SIZE; ++i) {
			t[i].start();
		}
		*/
    }

}
