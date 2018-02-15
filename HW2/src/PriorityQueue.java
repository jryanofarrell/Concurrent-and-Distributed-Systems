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
//		mutex.acquire();
		Node current = head;
		Node new_node = new Node(name,priority);
//		if(current == null){
//			head = new_node; 
//			mutex.release(); 
//			bufsize.release(); 
//			return 0;
//		}
//		mutex.release();
//		System.out.println("add 1");
		current.lock.lock(); 
//		System.out.println("add 1.1");
//		if(current.priority < new_node.priority){
//			System.out.println("add 1.2");
//			new_node.next = current;
//			head = new_node;
//			current.lock.unlock();
//			bufsize.release(); 
//			return 0;
//		}
		int count = 1; 
//		System.out.println("add 2");
		while(current.next !=null ){
			current.next.lock.lock();
			if(current.next.priority < new_node.priority){
				new_node.next = current.next;
				current.next = new_node;
				current.lock.unlock();
				current.next.next.lock.unlock();
				bufsize.release(); 
				return count;
			}
			current.lock.unlock();
			current=current.next; 
			count ++; 
		}
		System.out.println("HUGGGGEEEEE ERRROR");
		return -1;
//		System.out.println("add 3");
//		current.next = new_node;
//		current.lock.unlock();
//		bufsize.release(); 		
//		return count;
        // Adds the name with its priority to this queue.
        // Returns the current position in the list where the name was inserted;
        // otherwise, returns -1 if the name is already present in the list.
        // This method blocks when the list is full.
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
//		System.out.println("getFirst head " + head.name);
//		System.out.println("get first 1");
//		if(head.next != null){
//			head.next.lock.lock();
//		}
//		
//		String first_name = head.name;
//		//Node temp = head;
//		head.lock.unlock();
//		head = head.next;
//		if(head != null){
//			head.lock.unlock();
//		}
		//temp.lock.unlock();
		maxsize.release();
		//System.out.println("get first 2");
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

