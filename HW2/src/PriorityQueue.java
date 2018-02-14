import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class PriorityQueue {
	
	Node head;
	Semaphore maxsize; 
	Semaphore bufsize; 
	Semaphore mutex; 
	
	public PriorityQueue(int maxSize) {
		head = null; 
		maxsize = new Semaphore(maxSize);
		bufsize = new Semaphore(0);
		mutex = new Semaphore(1); 
        // Creates a Priority queue with maximum allowed size as capacity
	}

	public int add(String name, int priority) throws InterruptedException {
		maxsize.acquire();
		bufsize.release(); 
		if(search(name) != -1){
			return -1;
		}
		
		mutex.acquire();
		Node current = head;
		Node new_node = new Node(name,priority);
		if(current == null){
			head = new_node; 
			mutex.release(); 
			return 0;
		}
		mutex.release();
		current.lock.lock(); 
		if(current.priority < new_node.priority){
			new_node.next = current;
			head = new_node;
			current.lock.unlock();
			return 0;
		}
		
		int count = 1; 
		while(current.next !=null ){
			current.next.lock.lock();
			if(current.next.priority < new_node.priority){
				new_node.next = current.next;
				current.next = new_node;
				current.lock.unlock();
				current.next.next.lock.unlock();
				return count;
			}
			current.lock.unlock();
			current=current.next; 
			count ++; 
		}
		
		current.next = new_node;
		current.lock.unlock();
		return count;
        // Adds the name with its priority to this queue.
        // Returns the current position in the list where the name was inserted;
        // otherwise, returns -1 if the name is already present in the list.
        // This method blocks when the list is full.
	}

	public int search(String name) {
		int count = 0;
		Node current = head;
		while(current != null){
			
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
		maxsize.release();
		bufsize.acquire(); 
		head.lock.lock(); 
		head.next.lock.lock();
		
		String first_name = head.name;
		
		head.lock.unlock(); 
		head = head.next; 
		head.lock.unlock(); 
		return first_name;
        // Retrieves and removes the name with the highest priority in the list,
        // or blocks the thread if the list is empty.
	}
	
	public synchronized void print_list(){
		String printout = "";
		Node current = head; 
		while(current != null){
			printout = printout + "[" + current.name+","+Integer.toString(current.priority)+"]  ";
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

