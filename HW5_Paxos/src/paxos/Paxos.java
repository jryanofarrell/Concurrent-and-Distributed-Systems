package paxos;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.Registry;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * This class is the main class you need to implement paxos instances.
 */
public class Paxos implements PaxosRMI, Runnable{

    ReentrantLock mutex;
    String[] peers; // hostname
    int[] ports; // host port
    int[] highest_done_seq;
    int me; // index into peers[]
    int num_ports;
    Registry registry;
    PaxosRMI stub;

    AtomicBoolean dead;// for testing
    AtomicBoolean unreliable;// for testing
    static int glob_prop_num = 0; 

    ArrayList<Instance> instances = new ArrayList<Instance>();
    static Hashtable<Integer,Decided_State[]> sequence_status;
    Hashtable<Integer,Request> prepare_seq_req = new Hashtable<Integer,Request>();
    Hashtable<Integer,Request> accept_seq_req = new Hashtable<Integer,Request>();
    // Your data here


    /**
     * Call the constructor to create a Paxos peer.
     * The hostnames of all the Paxos peers (including this one)
     * are in peers[]. The ports are in ports[].
     */
    public Paxos(int me, String[] peers, int[] ports){

        this.me = me;
        this.peers = peers;
        this.ports = ports;
        this.mutex = new ReentrantLock();
        this.dead = new AtomicBoolean(false);
        this.unreliable = new AtomicBoolean(false);

        // Your initialization code here

        num_ports = peers.length;
        highest_done_seq = new int[ports.length];
        for(int i = 0; i<ports.length; i++){
        	highest_done_seq[i] = -1; 
        }
        sequence_status  = new Hashtable<Integer,Decided_State[]>();
        // register peers, do not modify this part
        try{
            System.setProperty("java.rmi.server.hostname", this.peers[this.me]);
            registry = LocateRegistry.createRegistry(this.ports[this.me]);
            stub = (PaxosRMI) UnicastRemoteObject.exportObject(this, this.ports[this.me]);
            registry.rebind("Paxos", stub);
        } catch(Exception e){
        	System.out.print("Fucked up the registry");
            //e.printStackTrace();
        }
    }


    /**
     * Call() sends an RMI to the RMI handler on server with
     * arguments rmi name, request message, and server id. It
     * waits for the reply and return a response message if
     * the server responded, and return null if Call() was not
     * be able to contact the server.
     *
     * You should assume that Call() will time out and return
     * null after a while if it doesn't get a reply from the server.
     *
     * Please use Call() to send all RMIs and please don't change
     * this function.
     */
    public Response Call(String rmi, Request req, int id){
        Response callReply = null;

        PaxosRMI stub;
        try{
            Registry registry=LocateRegistry.getRegistry(this.ports[id]);
            stub=(PaxosRMI) registry.lookup("Paxos");
            if(rmi.equals("Prepare"))
                callReply = stub.Prepare(req);
            else if(rmi.equals("Accept"))
                callReply = stub.Accept(req);
            else if(rmi.equals("Decide"))
                callReply = stub.Decide(req);
            else
                System.out.println("Wrong parameters! --- "+rmi );
        } catch(Exception e){
            return null;
        }
        return callReply;
    }


    /**
     * The application wants Paxos to start agreement on instance seq,
     * with proposed value v. Start() should start a new thread to run
     * Paxos on instance seq. Multiple instances can be run concurrently.
     *
     * Hint: You may start a thread using the runnable interface of
     * Paxos object. One Paxos object may have multiple instances, each
     * instance corresponds to one proposed value/command. Java does not
     * support passing arguments to a thread, so you may reset seq and v
     * in Paxos object before starting a new thread. There is one issue
     * that variable may change before the new thread actually reads it.
     * Test won't fail in this case.
     *
     * Start() just starts a new thread to initialize the agreement.
     * The application will call Status() to find out if/when agreement
     * is reached.
     */
    int seq;
    Object v;
    int prop_num;
    public void Start(int seq, Object value){
    	//Paxos p = new Paxos(me,peers,ports); 
    	mutex.lock();
    	this.seq = seq;
//    	if(seq > highest_seq_seen){
//    		highest_seq_seen = seq;
//    	}
    	this.v = value; 
    	this.prop_num = glob_prop_num;
    	glob_prop_num ++;
    	if(!sequence_status.containsKey(seq)){
    		Decided_State[] ds_list = new Decided_State[num_ports]; 
    		for(int i = 0; i< num_ports; i++){
    			Decided_State ds = new Decided_State(this.prop_num);
    			ds_list[i] = ds;
    		}
    		sequence_status.put(seq, ds_list);
    	
	//    	sequence_status.get(seq).isDecided = false; 
	//    	sequence_status.get(seq).prop_num = prop_num;
	    	Thread t = new Thread(this);
	        Instance i = new Instance(t, seq);
	        instances.add(i);
	        //decided = false;
	        System.out.println("Start me: "+me);
	    	t.start(); 
    	}
    	else{
    		int num_decided = 0;
    		Object loc_val=null;
    		for(Decided_State ds: sequence_status.get(seq)){
    			if(ds.isDecided){
    				loc_val = ds.value;
    				num_decided++;
    			}
    		}
    		
    		if(num_decided>num_ports/2){
    			sequence_status.get(seq)[me].isDecided = true;
    			sequence_status.get(seq)[me].value = loc_val;
    		}
    		else{
    			Thread t = new Thread(this);
    	        Instance i = new Instance(t, seq);
    	        instances.add(i);
    	        //decided = false; 
    	    	t.start();
    		}
    	}
    	try {
            Thread.sleep(100);
        } catch (Exception e){
            e.printStackTrace();
        }
    	mutex.unlock();
        // Your code here
    }

    //boolean decided = false; 
    @Override
    public void run(){
    	System.out.println("Run Paxos["+me+"]  SequenceNumber: "+seq);
    	Request req = new Request(seq, prop_num, v);
    	//int id = 0;
    	
    	while(!sequence_status.get(req.seq)[me].isDecided){
    		boolean prepare_ok = false;
    		int num_prepares = 0;
    		int num_accepts = 0; 
	    	for(int id = 0; id<num_ports; id++){
	    		Response resp = Call("Prepare",req,id);
	    		if(resp == null)
	    			continue;
	    		if(resp.ok){
//	    			if(resp.highest_accept_seen > req.prop_num){
//	    				System.out.println("CHANGING VALUE");
//	    				req.value = resp.value; 
//	    				req.prop_num = resp.highest_accept_seen; 
//	    			}
	    			num_prepares++;
	    			if(num_prepares>num_ports/2){
	    				prepare_ok = true;
	    				//break;			
	    			}
	    		}
	    		for(int i = 0; i<resp.highest_done_seq.length; i++){
	    			if(highest_done_seq[i]<resp.highest_done_seq[i]){
	    				highest_done_seq[i] = resp.highest_done_seq[i];
	    			}
	    		}
	    	}
	    	if(prepare_ok){
	    		for(int id = 0; id<num_ports; id++){
	    			Response resp = Call("Accept",req,id);
	    			if(resp == null)
		    			continue;
	    			if(resp.ok){
	    				num_accepts++;
	    				if(num_accepts > num_ports/2){
	    					sequence_status.get(req.seq)[me].isDecided = true; 
	    					sequence_status.get(req.seq)[me].value = req.value;
	    					//break;
	    				}
	    			}
	    		}
	    	}
	    	if(sequence_status.get(req.seq)[me].isDecided){
	    		for(int id = 0; id<num_ports; id++){
	    			Call("Decide",req,id);
	    		}
	    	}
	    	
    	}
        //Your code here
    }

    
    public Response Prepare(Request req){
    	int inner_seq = req.seq;
    	System.out.println("Prepare Paxos["+me+"]  SequenceNumber: "+req.seq);
    	Response resp = new Response();
    	resp.highest_done_seq = highest_done_seq; 
    	if(prepare_seq_req.containsKey(req.seq) && prepare_seq_req.get(req.seq).prop_num > req.prop_num){
    		return resp;
    	}
    	else{
    		resp.value = req.value;
    		resp.ok = true; 
    		prepare_seq_req.put(req.seq, req);
    	}
    	return resp;
        // your code here

    }

 
    public Response Accept(Request req){
    	System.out.println("Accept Paxos["+me+"]  SequenceNumber: "+req.seq);
//    	if(req.seq > highest_seq_seen){
//    		highest_seq_seen = req.seq;
//    	}
    	Response resp = new Response();
    	if(accept_seq_req.containsKey(req.seq) && accept_seq_req.get(req.seq).prop_num > req.prop_num){
    		return resp;
    	}
    	else{
    		resp.value = req.value;
    		resp.ok = true; 
    		accept_seq_req.put(req.seq, req);
    	}
//    	if(req.prop_num > highest_accept_seen){
//    		//decided = false;
//    		//highest_accept_seen = req.prop_num;
//    		v = req.value;
//    		//highest_prepare_seen = req.prop_num;
//    		resp.ok = true;
//    	}
    	return resp;
        // your code here

    }

    public Response Decide(Request req){
    	System.out.println("Decide Paxos["+me+"]  SequenceNumber: "+req.seq);
    	
    	sequence_status.get(req.seq)[me].isDecided = true; 
		sequence_status.get(req.seq)[me].value = req.value;
    	v = req.value;
    	System.out.println(req.value);
    	return null; 
        // your code here

    }

    /**
     * The application on this machine is done with
     * all instances <= seq.
     *
     * see the comments for Min() for more explanation.
     */
    
   
    public void Done(int seq) {
    	if(seq > highest_done_seq[me])
    		highest_done_seq[me] = seq;
        // Your code here
        for(int count = 0; count < instances.size(); count++) {
            if(instances.get(count).seq <= seq) {
                instances.get(count).t.interrupt();
                instances.remove(count);
                count--;
            }
        }
    }


    /**
     * The application wants to know the
     * highest instance sequence known to
     * this peer.
     */

    public int Max(){
    	return 0;//highest_seq_seen;
        // Your code here
    }

    /**
     * Min() should return one more than the minimum among z_i,
     * where z_i is the highest number ever passed
     * to Done() on peer i. A peers z_i is -1 if it has
     * never called Done().

     * Paxos is required to have forgotten all information
     * about any instances it knows that are < Min().
     * The point is to free up memory in long-running
     * Paxos-based servers.

     * Paxos peers need to exchange their highest Done()
     * arguments in order to implement Min(). These
     * exchanges can be piggybacked on ordinary Paxos
     * agreement protocol messages, so it is OK if one
     * peers Min does not reflect another Peers Done()
     * until after the next instance is agreed to.

     * The fact that Min() is defined as a minimum over
     * all Paxos peers means that Min() cannot increase until
     * all peers have been heard from. So if a peer is dead
     * or unreachable, other peers Min()s will not increase
     * even if all reachable peers call Done. The reason for
     * this is that when the unreachable peer comes back to
     * life, it will need to catch up on instances that it
     * missed -- the other peers therefore cannot forget these
     * instances.
     */
    public int Min(){
    	int min = Integer.MAX_VALUE;
    	for(int num: highest_done_seq){
    		if (num<min){
    			num = min;
    		}
    	}
    	return min+1; 
        // Your code here

    }



    /**
     * the application wants to know whether this
     * peer thinks an instance has been decided,
     * and if so what the agreed value is. Status()
     * should just inspect the local peer state;
     * it should not contact other Paxos peers.
     */
    public retStatus Status(int seq){
        // Your code here
    	//System.out.println("Status Paxos["+me+","+seq+"]");
    	//System.out.println(sequence_status);
//    	if(sequence_status.containsKey(seq))
//    		System.out.println(sequence_status.get(seq).isDecided);
        if(sequence_status.containsKey(seq) && sequence_status.get(seq)[me].isDecided == true) {
            return new retStatus(State.Decided, sequence_status.get(seq)[me].value);
        } else {
            return new retStatus(State.Pending, null);
        }

    }

    /**
     * helper class for Status() return
     */
    public class retStatus{
        public State state;
        public Object v;

        public retStatus(State state, Object v){
            this.state = state;
            this.v = v;
        }
    }

    /**
     * Tell the peer to shut itself down.
     * For testing.
     * Please don't change these four functions.
     */
    public void Kill(){
        this.dead.getAndSet(true);
        if(this.registry != null){
            try {
                UnicastRemoteObject.unexportObject(this.registry, true);
            } catch(Exception e){
                System.out.println("None reference");
            }
        }
    }

    public boolean isDead(){
        return this.dead.get();
    }

    public void setUnreliable(){
        this.unreliable.getAndSet(true);
    }

    public boolean isunreliable(){
        return this.unreliable.get();
    }

    class Instance {
        Thread t;
        int seq;
        
        Instance(Thread t, int seq) {
            this.t = t;
            this.seq = seq;
        }
    }
    class Decided_State{
    	public Decided_State(int prop_num) {
			this.prop_num = prop_num;
			isDecided = false; 
		}
		boolean isDecided;
    	int prop_num;
    	Object value;
    }
}
