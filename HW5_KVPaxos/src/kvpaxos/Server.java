package kvpaxos;
import paxos.Paxos;
import paxos.State;
// You are allowed to call Paxos.Status to check if agreement was made.

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.locks.ReentrantLock;

import java.util.HashMap;

public class Server implements KVPaxosRMI {

    ReentrantLock mutex;
    Registry registry;
    Paxos px;
    int me;

    String[] servers;
    int[] ports;
    KVPaxosRMI stub;

    // Your definitions here
    int currentSeq;
    HashMap<Integer, Request> requestsSeen = new HashMap<Integer, Request>();
    HashMap<String, Integer> db = new HashMap<String, Integer>();
    
    public Server(String[] servers, int[] ports, int me){
        this.me = me;
        this.servers = servers;
        this.ports = ports;
        this.mutex = new ReentrantLock();
        this.px = new Paxos(me, servers, ports);
        // Your initialization code here
        

        try{
            System.setProperty("java.rmi.server.hostname", this.servers[this.me]);
            registry = LocateRegistry.getRegistry(this.ports[this.me]);
            stub = (KVPaxosRMI) UnicastRemoteObject.exportObject(this, this.ports[this.me]);
            registry.rebind("KVPaxos", stub);
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public Op wait(int seq) {
    	int to = 10;
    	while(true) {
    		Paxos.retStatus ret = this.px.Status(seq);
    		if(ret.state == State.Decided) {
    			return Op.class.cast(ret.v);
    		}
    		try {
				Thread.sleep(to);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
    		if(to < 1000) {
    			to = to * 2;
    		}
    	}
    }
    
    public int Paxos(Op op) {
    	int seq = currentSeq;
    	int count = 0;
    	Op actualOp;
		do {
			if(count > 0)
				seq++;
			px.Start(seq, op);
			actualOp = wait(seq);
			count++;
    	} while(actualOp != op);
    	return seq;	
    }
     
    public void updateLocalLog(int currentSeq, int seq) {
    	for(int i = currentSeq; i <= seq; i++) {
    		Op op = wait(i);
    		if(op.op.equals("Get")) {
    			requestsSeen.put(op.ClientSeq, null);
    		} else if(op.op.equals("Put")) {
    			requestsSeen.put(op.ClientSeq, null);
    			db.put(op.key, op.value);
    		}
    	}
    }
    
    // RMI handlers
    public Response Get(Request req){
        // Your code here
    	mutex.lock();
    	if(requestsSeen.containsKey(req.operation.ClientSeq)) {
    		//already have seen this request, existing reply sent
    		return null;
    	}
    	//submit operation to paxos
    	int seq = Paxos(req.operation);
    	
    	//update the log to where Paxos currently is
    	updateLocalLog(currentSeq, seq);
    	
    	//update requests seen
    	requestsSeen.put(req.operation.ClientSeq, req);
    	px.Done(seq);
    	currentSeq = seq + 1;
    	mutex.unlock();
    	return null;
    }

    public Response Put(Request req){
        // Your code here
    	mutex.lock();
    	
    	//check to see if already handled
    	if(requestsSeen.containsKey(req.operation.ClientSeq)) {
    		return null;
    	}
    	//submit to paxos
    	int seq = Paxos(req.operation);
    	updateLocalLog(currentSeq, seq);
    	
    	px.Done(seq);
    	currentSeq = seq + 1;
    	mutex.unlock();
    	return null;
    }


}
