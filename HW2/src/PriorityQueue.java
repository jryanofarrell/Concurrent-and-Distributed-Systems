import java.util.LinkedList;
import java.util.concurrent.Semaphore;

public class PriorityQueue {
	
	LinkedList<node> queue; 
	Semaphore maxsize; 
	Semaphore bufsize; 
	
	
	public PriorityQueue(int maxSize) {
		queue = new LinkedList<node>(); 
		maxsize = new Semaphore(maxSize);
		bufsize = new Semaphore(0);
        // Creates a Priority queue with maximum allowed size as capacity
	}

	public int add(String name, int priority) throws InterruptedException {
		maxsize.acquire();
		bufsize.release(); 
		return priority;
        // Adds the name with its priority to this queue.
        // Returns the current position in the list where the name was inserted;
        // otherwise, returns -1 if the name is already present in the list.
        // This method blocks when the list is full.
	}

	public int search(String name) {
		return 0;
        // Returns the position of the name in the list;
        // otherwise, returns -1 if the name is not found.
	}

	public String getFirst() throws InterruptedException {
		maxsize.release();
		bufsize.acquire(); 
		
		return null;
        // Retrieves and removes the name with the highest priority in the list,
        // or blocks the thread if the list is empty.
	}
	
	class node{
		String name;
		int priority;
		node next; 
		node(String name, int priority){
			this.name = name;
			this.priority = priority; 
			this.next = null; 
		}
	}
}

