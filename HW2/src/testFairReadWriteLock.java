public class testFairReadWriteLock implements Runnable {
	final static int SIZE = 5;
	final FairReadWriteLock lock;
	int readWrite;
	static int count = 20;
	public testFairReadWriteLock(FairReadWriteLock lock, int readWrite) {
		this.lock = lock;
		this.readWrite = readWrite;
	}
	
	public void run() {
		while(count >0) {
			if(readWrite == 0) {
				//System.out.println("Thread " + Thread.currentThread().getId() + " is beginning reading");
				try {
					lock.beginRead();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println("Thread " + Thread.currentThread().getId() + " is ending reading");
				lock.endRead();
			} else {
				//System.out.println("Thread " + Thread.currentThread().getId() + " is beginning writing");
				try {
					lock.beginWrite();
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				//System.out.println("Thread " + Thread.currentThread().getId() + " is ending writing");
				lock.endWrite();
			}
			count --; 
		}
	}
	
	public static void main(String[] args) {
		FairReadWriteLock lock = new FairReadWriteLock();
		Thread[] t = new Thread[SIZE];
		
		for (int i = 0; i < SIZE; ++i) {
			t[i] = new Thread(new testFairReadWriteLock(lock, i % 2));
		}
		
		for (int i = 0; i < SIZE; ++i) {
			t[i].start();
		}
    }
}