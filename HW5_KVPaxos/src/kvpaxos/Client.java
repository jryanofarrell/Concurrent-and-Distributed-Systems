package kvpaxos;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import paxos.State;


public class Client {
    String[] servers;
    int[] ports;
    static int sequence = 1; 
    // Your data here


    public Client(String[] servers, int[] ports){
        this.servers = servers;
        this.ports = ports;
        // Your initialization code here
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
        KVPaxosRMI stub;
        try{
            Registry registry= LocateRegistry.getRegistry(this.ports[id]);
            stub=(KVPaxosRMI) registry.lookup("KVPaxos");
            if(rmi.equals("Get"))
                callReply = stub.Get(req);
            else if(rmi.equals("Put")){
                callReply = stub.Put(req);}
            else
                System.out.println("Wrong parameters!");
        } catch(Exception e){
            return null;
        }
        return callReply;
    }

    // RMI handlers
    public Integer Get(String key){
    	
    	Op operation = new Op("Get",sequence,key,-1);
    	Request req = new Request(operation);
    	sequence ++; 
    	while(true){
	    	for(int id = 0; id < ports.length; id++){
	    		Response res = Call("Put",req,id);
	    		if(res.isOk){
	    			return res.value;
	    		}
	    	}
	    	try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}

    }

    public boolean Put(String key, Integer value){
    	Op operation = new Op("Put",sequence,key,value);
    	Request req = new Request(operation);
    	sequence ++; 
    	while(true){
	    	for(int id = 0; id < ports.length; id++){
	    		Response res = Call("Put",req,id);
	    		if(res.isOk){
	    			return res.isOk;
	    		}
	    	}
	    	try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
        // Your code here
    }
    
//    public Op wait ( int seq ){
//    	int to = 10;
//    	while ( true ){
//    		Paxos.retStatus ret = this.px.Status(seq);
//    		if(ret.state == State.Decided){
//    			return Op.class.cast(ret.v);
//    		}
//    		try{
//    			Thread.sleep(to);
//    			
//    		}
//    		catch(Exception e){
//    			e.printStackTrace();
//    		}
//    		if(to<1000){
//    			to = to*2;
//    		}
//    	}
//    }


}
