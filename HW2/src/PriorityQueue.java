import java.util.LinkedList;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

public class PriorityQueue {
	
	Node head;
	Semaphore maxsize; 
	Semaphore bufsize; 
	private final ReentrantLock lock = new ReentrantLock();
	
	public PriorityQueue(int maxSize) {
		head = null; 
		maxsize = new Semaphore(maxSize);
		bufsize = new Semaphore(0);
        // Creates a Priority queue with maximum allowed size as capacity
	}

	public int add(String name, int priority) throws InterruptedException {
		maxsize.acquire();
		bufsize.release(); 
		if(search(name) != -1){
			return -1;
		}
		Node current = head;
		Node new_node = new Node(name,priority);
		
		if(current == null){
			head = new_node; 
			return 0;
		}
		
		if(current.priority < new_node.priority){
			new_node.next = current;
			head = new_node;
			return 0;
		}
		
		int count = 1; 
		while(current.next !=null ){
			if(current.next.priority < new_node.priority){
				new_node.next = current.next;
				current.next = new_node; 
				return count;
			}
			count ++; 
		}
		
		current.next = new_node;
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
		
		String first_name = head.name;
		head = head.next; 
		return first_name;
        // Retrieves and removes the name with the highest priority in the list,
        // or blocks the thread if the list is empty.
	}
	
	class Node{
		String name;
		int priority;
		Node next; 
		Node(String name, int priority){
			this.name = name;
			this.priority = priority; 
			this.next = null; 
		}
	}
}

