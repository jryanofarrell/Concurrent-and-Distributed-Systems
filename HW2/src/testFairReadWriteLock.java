public class testFairReadWriteLock implements Runnable {
	final static int SIZE = 5;
	final FairReadWriteLock lock;
	int readWrite;

	public testFairReadWriteLock(FairReadWriteLock lock, int readWrite) {
		this.lock = lock;
		this.readWrite = readWrite;
	}
	
	public void run() {
		while(true) {
			if(readWrite == 0) {
				System.out.println("Thread " + Thread.currentThread().getId() + " is beginning reading");
				lock.beginRead();
				System.out.println("Thread " + Thread.currentThread().getId() + " is ending reading");
				lock.endRead();
			} else {
				System.out.println("Thread " + Thread.currentThread().getId() + " is beginning writing");
				lock.beginWrite();
				System.out.println("Thread " + Thread.currentThread().getId() + " is ending writing");
				lock.endWrite();
			}

		}
		/*
		for (int round = 0; round < ROUND; ++round) {
			System.out.println("Thread " + Thread.currentThread().getId() + " is reading round:" + lock.beginRead());
			try {
				index = lock.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			System.out.println("Thread " + Thread.currentThread().getId() + " is leaving round:" + round);
		}*/
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