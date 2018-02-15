import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class PriorityQueue {
	
	volatile Node head = null;
	volatile Node tail = null;
	Semaphore maxsize; 
	Semaphore bufsize; 
	Semaphore mutex; 
	
	public PriorityQueue(int maxSize) {
		maxsize = new Semaphore(maxSize);
		bufsize = new Semaphore(0);
		//mutex = new Semaphore(1); 
		head = new Node(null,10);
		tail = new Node(null,-1);
		head.next = tail; 
        // Creates a Priority queue with maximum allowed size as capacity
	}

	public int add(String name, int priority) throws InterruptedException {
		System.out.println("add");
		maxsize.acquire();
		if(search(name) != -1){
			maxsize.release();
			return -1;
		}
		Node current = head;
		Node new_node = new Node(name,priority);
		current.lock.lock(); 
		int count = 0; 
		
		while(current.next !=null ){
			current.next.lock.lock();
			if(current.next.priority < new_node.priority){
				new_node.next = current.next;
				current.next.lock.unlock(); 
				current.next = new_node;
				current.lock.unlock();
				//current.next.next.lock.unlock();
				bufsize.release(); 
				return count;
			}
			current.lock.unlock();
			current=current.next; 
			count ++; 
		}
		System.out.println("HUGGGGEEEEE ERRROR");
		return -1;

	}

	public int search(String name) {
		int count = 0;
		Node current = head.next;
		while(current.next != null){
			
			if(current.name.equals(name)){
				return count;
			}
			current = current.next; 
			count++; 
		}
		return -1;
        // Returns the position of the name in the list;
        // otherwise, returns -1 if the name is not found.
	}

	public String getFirst() throws InterruptedException {
		System.out.println("get first");
		bufsize.acquire(); 
		head.lock.lock(); 
		head.next.lock.lock();
		head.next.next.lock.lock();
		String first_name = head.next.name; 
		head.next.lock.unlock();
		head.next = head.next.next;
		head.next.lock.unlock();
		head.lock.unlock();

		maxsize.release();
		
		return first_name;
        // Retrieves and removes the name with the highest priority in the list,
        // or blocks the thread if the list is empty.
	}
	
	public synchronized void print_list(){
		String printout = "";
		Node current = head.next; 
		while(current.next != null){
			printout = printout + "[" + current.name+","+Integer.toString(current.priority)+"]  ";
			current = current.next;
		}
		System.out.println(printout);
	}
	
	class Node{
		String name;
		int priority;
		Node next; 
		private final ReentrantLock lock = new ReentrantLock();
		Node(String name, int priority){
			this.name = name;
			this.priority = priority; 
			this.next = null; 
		}
	}
}

